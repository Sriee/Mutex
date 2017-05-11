package com.aos.work;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;

import com.aos.config.Configuration;
import com.aos.config.Message;
import com.aos.log.Logger;

public class Listener implements Runnable {

	private Configuration resource;
	private Logger logger;
	private int remoteId;
	private ObjectInputStream in;

	public Listener() {
		super();
		this.resource = null;
		this.logger = null;
	}

	/**
	 * @param resource
	 * @param logger
	 * @param serverSocket
	 */
	public Listener(Configuration resource, Logger logger, int remoteId) {
		super();
		this.resource = resource;
		this.logger = logger;
		this.remoteId = remoteId;
		this.in = null;
	}

	/**
	 * @param resource
	 * @param logger
	 * @param serverSocket
	 */
	public Listener(Configuration resource, Logger logger, int remoteId, ObjectInputStream in) {
		super();
		this.resource = resource;
		this.logger = logger;
		this.remoteId = remoteId;
		this.in = in;
	}

	@Override
	public void run() {
		int thisId = this.resource.getNodeId();
		int numNodes = this.resource.getNumOfNodes();
		// Wait till the network is setup
		while (this.resource.getSocketMap().keySet().size() != (numNodes - 1))
			;

		try {
			if (this.in == null) {
				this.in = new ObjectInputStream(this.resource.getSocketMap(remoteId).getInputStream());
			}
			while (!this.resource.isTerminate()) {

				Message msg = (Message) this.in.readObject();

				if (msg == null)
					throw new NullPointerException(thisId + " received 'null' message ");

				this.logger.writeLog("Received " + msg);
				synchronized (resource) {
					
					// Receive events for logical clock and vector clock
					this.resource.getMyClock().receiveEvent(msg.getClockValue());	
					this.resource.getVectorClock().receiveEvent(msg.getVectorClock());
					
					// Request Message
					if (msg.getMessageType().equals("request")) {
						if (this.resource.getTimeStamp() == Integer.MIN_VALUE
								|| this.resource.getTimeStamp() > msg.getClockValue()
								|| ((this.resource.getTimeStamp() == msg.getClockValue())
										&& (thisId > msg.getSender()))) {
							Message okayMsg = new Message(thisId, "okay", 
									this.resource.getMyClock().getC(), this.resource.getVectorClock());
							this.sendMsg(msg.getSender(), okayMsg);
						} else {
							this.resource.getPendingQueue().add(msg.getSender());
							this.logger.writeLog("Adding " + msg.getSender() + " to Pending Queue.");
						}

					} else if (msg.getMessageType().equals("okay")) {
						this.resource.incrementNumOkay();
						this.logger.writeLog("numOkay = " + this.resource.getNumOkay());
					} else if (msg.getMessageType().equals("terminate")) {
						this.resource.setReceivedTerminate(msg.getSender(), true);

						// Check termination
						if (this.resource.sentReqComplete() && this.isDone())
							this.resource.setTerminate(true);
					}
				}
			}

		} catch (EOFException e) {
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.logger.writeLog("Listener " + remoteId + " quiting..");
		}

	}

	public synchronized boolean isDone() {
		int numNodes = this.resource.getNumOfNodes();

		for (int i = 0; i < numNodes; i++) {
			if (!this.resource.getReceivedTerminate(i))
				return false;
		}
		this.logger.writeLog(Arrays.toString(this.resource.getReceivedTerminate()));
		return true;
	}

	private synchronized void sendMsg(int dst, Message msg) throws IOException {
		Socket to = null;

		to = this.resource.getSocketMap(dst);

		if (to == null) {
			this.logger.writeLog("'null' socket for " + dst);
			throw new NullPointerException("Error in sendMsg.");
		}

		this.logger.writeLog("Sending " + msg.getMessageType() + " to " + dst + " : '" + msg.getClockValue() + "'");
		ObjectOutputStream out = this.resource.hashOs.get(dst);
		out.reset();
		out.writeObject(msg);
		out.flush();
	}
}

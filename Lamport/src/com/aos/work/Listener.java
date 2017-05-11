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
					// Receive Event
					this.resource.getMyVC().receiveEvent(msg.getVectorClock());
					
					if (msg.getMessageType().equals("request")) {
						
						this.resource.setQueueValue(msg.getSender(), msg.getClockValue());
						this.logger.writeLog("Queue: " + Arrays.toString(this.resource.getQueue()));
						
						// Sending reply message back 
						Message replyMsg = new Message(thisId, "reply", this.resource.getMyClockValue(), this.resource.getMyVC());
						this.sendMsg(msg.getSender(), replyMsg);
						
					} else if (msg.getMessageType().equals("reply")) {
						// Reply Message
						this.resource.incrementNumReply();
						this.logger.writeLog("numReply = " + this.resource.getNumReply());
						
					} else if (msg.getMessageType().equals("release")) {
						// Release Message 
						this.resource.setQueueValue(msg.getSender(), Integer.MIN_VALUE);	
						this.logger.writeLog("Queue: " + Arrays.toString(this.resource.getQueue()));
						
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

		this.logger.writeLog("Sending " + msg.getMessageType() + " to " + dst + " : '" + this.resource.getMyVC() + "'");
		ObjectOutputStream out = this.resource.hashOs.get(dst);
		out.reset();
		out.writeObject(msg);
		out.flush();
	}
}

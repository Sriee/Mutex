package com.aos.service;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.aos.config.Configuration;
import com.aos.config.Message;
import com.aos.log.Logger;

public class RAMutex implements Lock {

	private Configuration resource;
	private Logger logger;

	/**
	 * @param resource
	 * @param logger
	 */
	public RAMutex(Configuration resource, Logger logger) {
		super();
		this.resource = resource;
		this.logger = logger;
	}

	@Override
	public synchronized void csEnter() {
		int numNodes = this.resource.getNumOfNodes();

		this.resource.getMyClock().sendEvent(); // Update local clock
		this.resource.getVectorClock().sendEvent(); // Update vector clock
		this.resource.setTimeStamp(this.resource.getMyClock().getC());
		Message requestMsg = new Message(this.resource.getNodeId(), "request", 
				this.resource.getTimeStamp(), this.resource.getVectorClock());
		try {
			this.logger.writeLog("Broadcasting request");
			this.resource.resetNumOkay();
			this.broadCast(requestMsg);

			while (this.resource.getNumOkay() < (numNodes - 1)) { // Comment
																	// wait

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public synchronized void csExit() {
		this.resource.resetTimeStamp();
		try {
			while (!this.resource.getPendingQueue().isEmpty()) {
				int to = this.resource.getPendingQueue().remove();
				Message okayMsg = new Message(this.resource.getNodeId(), "okay", 
						this.resource.getMyClock().getC(), this.resource.getVectorClock());
				this.sendMsg(to, okayMsg);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private synchronized void broadCast(Message msg) throws IOException {
		Socket to = null;
		int numNodes = this.resource.getNumOfNodes();
		for (int i = 0; i < numNodes; i++) {
			if (i == this.resource.getNodeId())
				continue;

			to = this.resource.getSocketMap(i);

			if (to == null) {
				this.logger.writeLog("'null' socket for " + i);
				throw new NullPointerException("Error in broadCast.");
			}

			this.logger.writeLog(
					"Sending " + msg.getMessageType() + " message to " + i + " : '" + msg.getClockValue() + "'");
			ObjectOutputStream out = this.resource.hashOs.get(i);
			out.reset();
			out.writeObject(msg);
			out.flush();
		}
	}

	private synchronized void sendMsg(int dst, Message msg) throws IOException {
		Socket to = null;

		to = this.resource.getSocketMap(dst);

		if (to == null) {
			this.logger.writeLog("'null' socket for " + dst);
			throw new NullPointerException("Error in sendMsg.");
		}

		this.logger.writeLog(
				"Sending " + msg.getMessageType() + " message to " + dst + " : '" + msg.getClockValue() + "'");

		ObjectOutputStream out = this.resource.hashOs.get(dst);
		out.reset();
		out.writeObject(msg);
		out.flush();
	}
}

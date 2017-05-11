package com.aos.service;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.aos.config.Configuration;
import com.aos.config.Message;
import com.aos.log.Logger;

public class LAMutex implements Lock {

	private Configuration resource;
	private Logger logger;
	
	private int thisId;

	/**
	 * @param resource
	 * @param logger
	 */
	public LAMutex(Configuration resource, Logger logger) {
		super();
		this.resource = resource;
		this.logger = logger;
		this.thisId = this.resource.getNodeId();
		this.resource.initQueue();
	}

	@Override
	public synchronized void csEnter() {
		try {
			this.resource.getMyVC().sendEvent(); // Update local clock
			this.resource.setQueueValue(this.thisId, this.resource.getMyClockValue());
			this.resource.resetNumReply();
			Message requestMsg = new Message(this.thisId, "request", this.resource.getQueueValue(this.thisId), this.resource.getMyVC());

			this.logger.writeLog("Broadcasting request");
			this.broadCast(requestMsg);

			while (!this.condition()) {}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public synchronized void csExit() {
		try {
			this.resource.setQueueValue(this.thisId, Integer.MIN_VALUE);
			Message releaseMsg = new Message(this.thisId, "release", this.resource.getMyClockValue(), this.resource.getMyVC());
			this.broadCast(releaseMsg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private synchronized boolean condition() {
		int numNodes = this.resource.getNumOfNodes();
		for (int j = 0; j < numNodes; j++) {
			if (j == this.thisId) continue;
			
			// Is my request time stamp lesser than time stamp of other processes
			if (this.resource.getQueue()[j] != Integer.MIN_VALUE){
				if (this.resource.getQueue()[this.thisId] > this.resource.getQueue()[j]
						|| ((this.resource.getQueue()[this.thisId] == this.resource.getQueue()[j]) && (this.thisId > j)))
					return false;
			}  
			if (this.resource.getQueue()[this.thisId] > this.resource.getMyVC().getVectorClockValue(j)
					|| ((this.resource.getQueue()[this.thisId] == this.resource.getMyVC().getVectorClockValue(j)) && (this.thisId > j)))
				return false;
		}

		// L2 is satisfied
		return true;
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
}

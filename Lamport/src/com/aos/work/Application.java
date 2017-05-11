package com.aos.work;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;

import com.aos.config.Configuration;
import com.aos.config.Message;
import com.aos.log.Logger;
import com.aos.service.LAMutex;


public class Application {

	private Configuration resource;
	private Logger logger;
	private LAMutex lock;

	/**
	 * @param config
	 * @param logger
	 * @param vectorClock
	 * @throws IOException
	 */
	public Application(Configuration config, Logger logger) throws IOException {
		super();
		this.resource = config;
		this.logger = logger;
		this.lock = new LAMutex(this.resource, this.logger);
	}

	public synchronized void start() {
		int numReq = this.resource.getNumRequest();
		int numNodes = this.resource.getNumOfNodes();
		int thisId = this.resource.getNodeId();

		// Wait till the network is setup
		while (this.resource.getSocketMap().keySet().size() != (numNodes - 1))
			;
		try {
			this.resource.resetSentRequest();
			for (int i = 0; i < numReq; i++) {

				Thread.sleep(this.resource.getInterRequestDelay());
				this.lock.csEnter();
				this.logger.writeLog("Entered CS for req-" + (i + 1));
				this.logger.writeClock(Arrays.toString(this.resource.getMyVC().getVectorClock()));
				for (int j = 0; j < 2; j++)
					this.logger.writeEntry(thisId + " in CS.");
				
				Thread.sleep(this.resource.getCsExecutionTime());

				for (int j = 0; j < 2; j++)
					this.logger.writeEntry(thisId + " in CS.");
				
				this.lock.csExit();
				this.logger.writeLog("Exited CS for req-" + (i + 1));
				this.resource.incrementSentRequest();
			}

			// Broadcast Terminate message after generating requests
			this.resource.getMyVC().sendEvent(); // Update local clock
			Message terminateMsg = new Message(thisId, "terminate", 
					this.resource.getMyVC().getVectorClockValue(thisId), this.resource.getMyVC());
			this.broadCast(terminateMsg);

		} catch (InterruptedException e) {
			e.printStackTrace();
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

			this.logger.writeLog("Sending " + msg.getMessageType() + " message to " + i);
			ObjectOutputStream out = this.resource.hashOs.get(i);
			out.reset();
			out.writeObject(msg);
			out.flush();
		}
	}
}

package com.aos.config;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author sriee
 *
 */
public class Message implements Serializable{
	
	private static final long serialVersionUID = 8442183641921041945L;
	
	private int sender;
	private String messageType;
	private int clockValue;
	private int[] vectorClock;
	
	public Message() {
		super();
		this.sender = -1;
		this.messageType = null;
		this.clockValue = -1;
	}

	
	/**
	 * @param sender
	 * @param messageType
	 */
	public Message(int sender, String messageType) {
		super();
		this.sender = sender;
		this.messageType = messageType;
	}


	/**
	 * @param sender
	 * @param msgTag
	 * @param messageType
	 * @param logicalClock
	 */
	public Message(int sender, String messageType, int logicalClock) {
		super();
		this.sender = sender;
		this.messageType = messageType;
		this.clockValue = logicalClock;
	}

	/**
	 * @param sender
	 * @param messageType
	 * @param clockValue
	 * @param vectorClock
	 */
	public Message(int sender, String messageType, int clockValue, VectorClock vectorClock) {
		super();
		this.sender = sender;
		this.messageType = messageType;
		this.clockValue = clockValue;
		this.vectorClock = vectorClock.getVectorClock();
	}

	/**
	 * @return the sender
	 */
	public synchronized int getSender() {
		return sender;
	}


	/**
	 * @param sender the sender to set
	 */
	public synchronized void setSender(int sender) {
		this.sender = sender;
	}


	/**
	 * @return the messageType
	 */
	public String getMessageType() {
		return messageType;
	}

	/**
	 * @param messageType the messageType to set
	 */
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	/**
	 * @return the clockValue
	 */
	public synchronized int getClockValue() {
		return clockValue;
	}


	/**
	 * @param clockValue the clockValue to set
	 */
	public synchronized void setClockValue(int logicalClock) {
		this.clockValue = logicalClock;
	}

	/**
	 * @return the vectorClock
	 */
	public int[] getVectorClock() {
		return vectorClock;
	}

	/**
	 * @param vectorClock the vectorClock to set
	 */
	public void setVectorClock(VectorClock vectorClock) {
		this.vectorClock = vectorClock.getVectorClock();
	}

	@Override
	public String toString() {
		return "Message [sender=" + sender + ", Type=" + messageType + ", Clock="
				+ clockValue + ", vectorClock="	+ Arrays.toString(vectorClock) + "]";
	}
	
}

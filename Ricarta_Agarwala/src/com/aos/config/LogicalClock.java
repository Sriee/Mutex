package com.aos.config;

public class LogicalClock {

	private int c;

	public LogicalClock() {
		super();
		this.c = 1;
	}
	
	/**
	 * @return the c
	 */
	public synchronized int getC() {
		return c;
	}


	public synchronized void sendEvent(){
		this.c = this.c + 1;
	}
	
	public synchronized void receiveEvent(int received){
		this.c = this.max(this.c, received) + 1;
	}
	
	private int max(int a, int b){ return (a > b) ? a : b; }

	@Override
	public String toString() {
		return "LogicalClock [" + c + "]";
	}
	
}

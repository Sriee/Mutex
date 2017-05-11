package com.aos.config;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import com.aos.log.Logger;
import com.aos.work.Listener;


public class Configuration {

	private String CONFIG_FILE = "config.txt";

	private int numOfNodes = -1;
	
	private int interRequestDelay = -1;
	
	private int csExecutionTime = -1;
	
	private int numRequest = -1; 

	private int nodeId = -1;

	private String[] machines = null;

	private int[] ports = null;

	public static HashMap<Integer, Socket> nodeSocketMap = null;

	public HashMap<Integer, ObjectOutputStream> hashOs = new HashMap<>(); 

	public static boolean terminate = false;

	private boolean[] receivedTerminate;
	
	private LogicalClock myClock;
	
	private VectorClock vectorClock;
	
	// Ricarta & Agarwala Mutex Parameters 
	private int timeStamp = -1;
	
	private int numOkay = -1;
	
	private Queue<Integer> pendingQueue = new LinkedList<Integer>();
	
	private int sentRequest = -1;
	
	public Configuration() {
		nodeSocketMap = new HashMap<>();
		numOkay = 0;
		timeStamp = Integer.MIN_VALUE;
	}

	/**
	 * @return the numOfNodes
	 */
	public int getNumOfNodes() {
		return numOfNodes;
	}

	/**
	 * @param numOfNodes
	 *            the numOfNodes to set
	 */
	public void setNumOfNodes(int numOfNodes) {
		this.numOfNodes = numOfNodes;
	}

	
	/**
	 * @return the interRequestDelay
	 */
	public synchronized int getInterRequestDelay() {
		return interRequestDelay;
	}

	/**
	 * @param interRequestDelay the interRequestDelay to set
	 */
	public synchronized void setInterRequestDelay(int interRequestDelay) {
		this.interRequestDelay = interRequestDelay;
	}

	/**
	 * @return the csExecutionTime
	 */
	public synchronized int getCsExecutionTime() {
		return csExecutionTime;
	}

	/**
	 * @param csExecutionTime the csExecutionTime to set
	 */
	public synchronized void setCsExecutionTime(int csExecutionTime) {
		this.csExecutionTime = csExecutionTime;
	}

	/**
	 * @return the numRequest
	 */
	public synchronized int getNumRequest() {
		return numRequest;
	}

	/**
	 * @param numRequest the numRequest to set
	 */
	public synchronized void setNumRequest(int numRequest) {
		this.numRequest = numRequest;
	}

	/**
	 * @return the nodeId
	 */
	public int getNodeId() {
		return nodeId;
	}

	/**
	 * @param nodeId
	 *            the nodeId to set
	 */
	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}

	/**
	 * @return the machines
	 */
	public String[] getMachines() {
		return machines;
	}

	/**
	 * @param id
	 *            machine id
	 * @param value
	 *            machine name
	 */
	public void setMachine(int id, String value) {
		this.machines[id] = value;
	}


	/**
	 * @return the ports
	 */
	public int[] getPorts() {
		return ports;
	}

	/**
	 * @param id
	 *            machine id
	 * @param value
	 *            machine port number
	 */
	public void setPort(int id, int value) {
		this.ports[id] = value;
	}

	/**
	 * @param cONFIG_FILE the cONFIG_FILE to set
	 */
	public void setCONFIG_FILE(String cONFIG_FILE) {
		CONFIG_FILE = cONFIG_FILE;
	}

	/**
	 * @return the CONFIG_FILE
	 */
	public String getCONFIG_FILE() {
		return CONFIG_FILE;
	}

	/**
	 * Initialize machines and ports array
	 */
	private void initMachineAndPort() {
		try {
			this.machines = new String[this.getNumOfNodes()];
			this.ports = new int[this.getNumOfNodes()];
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the terminate
	 */
	public synchronized boolean isTerminate() {
		return terminate;
	}

	/**
	 * @param terminate the terminate to set
	 */
	public synchronized void setTerminate(boolean terminate) {
		Configuration.terminate = terminate;
	}
	
	/**
	 * @return the receivedTerminate
	 */
	public synchronized boolean getReceivedTerminate(int idx) {
		return receivedTerminate[idx];
	}

	/**
	 * @return the receivedTerminate
	 */
	public synchronized boolean[] getReceivedTerminate() {
		return receivedTerminate;
	}

	/**
	 * @param receivedTerminate the receivedTerminate to set
	 */
	public synchronized void setReceivedTerminate(int idx, boolean value) {
		this.receivedTerminate[idx] = value;
	}

	public synchronized void updateSocketMap(int neighbor, Socket hopSocket){
		nodeSocketMap.put(neighbor, hopSocket);
	}

	public synchronized Socket getSocketMap(int key){
		return nodeSocketMap.get(key);
	}

	public synchronized HashMap<Integer, Socket> getSocketMap(){
		return nodeSocketMap;
	}
		
	/**
	 * @return the myVC
	 */
	public synchronized LogicalClock getMyClock() {
		return myClock;
	}

	/**
	 * @param myVC the myVC to set
	 */
	public synchronized void setMyClock(LogicalClock myClock) {
		this.myClock = myClock;
	}

	
	/**
	 * @return the vectorClock
	 */
	public synchronized VectorClock getVectorClock() {
		return vectorClock;
	}

	/**
	 * @param vectorClock the vectorClock to set
	 */
	public synchronized void setVectorClock(VectorClock vectorClock) {
		this.vectorClock = vectorClock;
	}

	/**
	 * @return the timeStamp
	 */
	public synchronized int getTimeStamp() {
		return timeStamp;
	}

	/**
	 * reset timeStamp
	 */
	public synchronized void resetTimeStamp() {
		this.timeStamp = Integer.MIN_VALUE;
	}

	/**
	 * @param timeStamp the timeStamp to set
	 */
	public synchronized void setTimeStamp(int timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	/**
	 * @return the numOkay
	 */
	public synchronized int getNumOkay() {
		return this.numOkay;
	}

	/**
	 * reset numOkay
	 */
	public synchronized void resetNumOkay() {
		this.numOkay = 0;
	}

	public synchronized void incrementNumOkay() {
		this.numOkay += 1;
	}

	/**
	 * @return the pendingQueue
	 */
	public synchronized Queue<Integer> getPendingQueue() {
		return pendingQueue;
	}

	/**
	 * @param pendingQueue the pendingQueue to set
	 */
	public synchronized void setPendingQueue(Queue<Integer> pendingQueue) {
		this.pendingQueue = pendingQueue;
	}

	public synchronized boolean sentReqComplete() {
		return (sentRequest == this.getNumRequest());
	}

	/**
	 * @param reset sentRequest
	 */
	public synchronized void resetSentRequest() {
		this.sentRequest = 0;
	}
	
	/**
	 * @param increment sentRequest
	 */
	public synchronized void incrementSentRequest() {
		this.sentRequest += 1;
	}
	
	/**
	 * Parse 'config.txt'
	 * 
	 * Section 1 - <Number of nodes><minPerActive><maxPerActive><minSendDelay><snapshotDelay><maxNumber>
	 * 
	 * Section 2 - <Identifier> <Hostname> <Port>
	 * 
	 */
	public void parseConfigFile() {
		String inputLine = null;
		String line = null;
		int section = 0, locationStored = 0;
		try {
			BufferedReader br = new BufferedReader(new FileReader(this.getCONFIG_FILE()));

			while ((inputLine = br.readLine()) != null) {

				if (inputLine.startsWith("#"))
					continue;

				if (inputLine.contains("#")) {
					line = inputLine.substring(0, inputLine.indexOf('#'));
					line = line.trim();
				} else {
					line = inputLine.trim();
				}

				if (line.length() == 0)
					continue;

				String[] tokens = line.split("\\s+");

				switch (section) {
				case 0:

					if (tokens[0].isEmpty())
						throw new IllegalArgumentException("Error in getting number of nodes.");

					if (tokens[1].isEmpty())
						throw new IllegalArgumentException("Error in getting inter-request delay.");

					if (tokens[2].isEmpty())
						throw new IllegalArgumentException("Error in getting csExecutionTime.");

					if (tokens[3].isEmpty())
						throw new IllegalArgumentException("Error in getting number of requests.");

					this.setNumOfNodes(Integer.parseInt(tokens[0]));	// No of nodes 
					this.setInterRequestDelay(Integer.parseInt(tokens[1]));	// inter-request delay 
					this.setCsExecutionTime(Integer.parseInt(tokens[2]));	// csExecutionTime 
					this.setNumRequest(Integer.parseInt(tokens[3]));	// No of requests 

					this.initMachineAndPort();
					section += 1;
					break;
				case 1:
					/* Store machine and port array for each node */
					if (tokens.length > 3 || tokens[0].isEmpty() || tokens[1].isEmpty() || tokens[2].isEmpty())
						throw new IllegalArgumentException("Parsing <id><hostname><port>");

					this.setMachine(Integer.parseInt(tokens[0]), tokens[1]);
					this.setPort(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[2]));

					locationStored += 1;

					if (locationStored == this.getNumOfNodes())
						section += 1;
					break;
				}
			}
			
			// Initialize logical clock 
			this.myClock = new LogicalClock();
			
			// Initialize vector clock 
			this.vectorClock = new VectorClock(this.getNodeId(), this.getNumOfNodes());
						
			// Initialize Received Terminate
			this.receivedTerminate = new boolean[this.numOfNodes];
			this.receivedTerminate[this.nodeId] = true;
			
			br.close();
		} catch (FileNotFoundException e) {
			System.out.println("Unable to find " + this.getCONFIG_FILE());
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException i) {
			i.printStackTrace();
		}
	}

	public void setUpNetwork(Logger logger) {
		int thisId = this.getNodeId();
		int neighborSize = this.getNumOfNodes();
		int k = 0;
		try {

			for(; k  < neighborSize; k++){
				
				if( k < thisId ){
					String machine = this.getMachines()[k];
					int port = this.getPorts()[k];

					InetAddress ipAddress = InetAddress.getByName(machine);
			
					Socket hopSocket = new Socket(ipAddress, port);
					
					// Persist the client socket connection
					updateSocketMap(k, hopSocket);
					
					new Thread(new Listener(this, logger, k)).start();

					// Set only the origin
					Message first = new Message();
					first.setSender(this.getNodeId());

					ObjectOutputStream out = new ObjectOutputStream( hopSocket.getOutputStream() );
					out.writeObject(first);
					out.flush();

					this.hashOs.put(k, out);
				}
			}

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch(ConnectException c){
			System.out.println("Exception at " + k);
			c.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		String currentMachine = machines[nodeId];
		currentMachine = currentMachine.replaceAll(".utdallas.edu", "");
		return "Configuration [numOfNodes=" + numOfNodes + ", nodeId=" + nodeId + 
				", machine=" + currentMachine + ", port=" + ports[nodeId] + "]";
	}
	
}

package com.aos.work;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.UnknownHostException;

import com.aos.config.Configuration;
import com.aos.log.FileLogger;
import com.aos.log.Logger;


public class AOS {

	public static void main(String[] args) throws UnknownHostException {

		if (args.length == 0)
			throw new IllegalArgumentException("Argument missing.");

		if (args[0].isEmpty())
			throw new IllegalArgumentException("Config file missing.");

		if (args[1].isEmpty())
			throw new ArrayIndexOutOfBoundsException("Id missing.");

		// Object Instance
		Logger logger = null;
		Configuration resource = new Configuration();
		resource.setCONFIG_FILE(args[0]);
		resource.setNodeId( Integer.parseInt(args[1]) );
	
		/* Parse config.txt file and store data */
		resource.parseConfigFile();
		
		ServerSocket serverSocket = null;

		try {

			serverSocket = new
					ServerSocket(resource.getPorts()[resource.getNodeId()]);

			logger = new FileLogger(resource.getCONFIG_FILE(), resource.getNodeId());

			// Test for Configuration
			logger.writeLog(resource.toString());

			// Start Server Execution 
			new Thread(new Server(resource, logger, serverSocket)).start();

			// Network setup
			resource.setUpNetwork(logger);
			
			// Start the Application
			new Application(resource, logger).start();

		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

}		 


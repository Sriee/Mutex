package com.aos.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

public class FileLogger implements Logger {

	private String fileName = null; 
	private File logFile = null;
	private final String SHARED_FILE_NAME = "SharedResource.txt";
	private final String CLOCK_FILE_NAME = "clock.txt";
	
	/**
	 * 
	 * @param configFileName
	 * @param originId
	 * @throws IOException
	 */
	public FileLogger(String configFileName, int originId) throws IOException {
		super();
		this.fileName = "log_" + originId + ".txt";
		this.logFile = new File(fileName);
		if(configFileName.indexOf('.') != -1)
			configFileName = configFileName.substring(0, configFileName.indexOf('.'));
		
		this.setup(originId);
	}

	@Override
	public synchronized void writeLog(String entry){
		if( logFile == null )
			throw new NullPointerException("Log file not initialized.");
		
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter( new FileWriter(this.logFile.getCanonicalPath(), true));
			bw.write(this.getTimeStamp() + entry + "\n");
			bw.close();
		} catch(FileNotFoundException e){
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public synchronized void writeLog(Collection<String> entry){
		Iterator<String> iter = entry.iterator();
	
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter( new FileWriter(this.logFile.getCanonicalPath(), true));
			while( iter.hasNext() )
				bw.write(this.getTimeStamp() + iter.next() + "\n");
			
			bw.close();
		} catch(FileNotFoundException e){
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public synchronized void writeEntry(String entry){		
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter( new FileWriter(SHARED_FILE_NAME, true));
			bw.write(entry + "\n");
			bw.close();
		} catch(FileNotFoundException e){
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void writeClock(String entry) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter( new FileWriter(CLOCK_FILE_NAME, true));
			bw.write(entry + "\n");
			bw.close();
		} catch(FileNotFoundException e){
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void setup(int id) {
		
		if (id < 0)
			throw new IllegalArgumentException("Origin id is null or Empty.");

		this.logFile = new File(this.fileName);

		try {
			
			if( this.logFile.exists() ){
				logFile.delete();
			}
			
			if( ! logFile.createNewFile() )
				throw new NullPointerException(fileName + " file not created.");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String getTimeStamp(){
		String timeStamp = "";
		SimpleDateFormat dateFormat = new SimpleDateFormat("M-dd-yyyy hh:mm:ss a");
		Date date = new Date();
		timeStamp = dateFormat.format(date.getTime()) + ": ";
		return timeStamp;
	}
}

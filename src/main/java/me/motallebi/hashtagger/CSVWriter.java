package me.motallebi.hashtagger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class CSVWriter {

	private CSVWriter() {
		// TODO Auto-generated constructor stub
	}
	
	private static final CSVWriter INSTANCE = new CSVWriter();
	private static String fileName = null;
	private static FileOutputStream fos = null;
	private static FileWriter bw = null;
	
	public void writeToFile(List<Object> data) throws Exception {		
		if(fileName == null){
			throw new Exception("Please specify a name for the file");
		}
		String join = StringUtils.join(data, ",");
		bw.write(join + "\n");
		bw.flush();
	}
	
	public final static CSVWriter getInstance(String nameOfFile ) throws IOException{
		fileName = nameOfFile;
		//fos = new FileOutputStream(new File(fileName));
		//bw = new Writer(new OutputStreamWriter(fos));
		 bw = new FileWriter(new File(fileName));
		return INSTANCE;
	}
	
	


}

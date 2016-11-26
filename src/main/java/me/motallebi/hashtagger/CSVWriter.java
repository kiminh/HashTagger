package me.motallebi.hashtagger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class CSVWriter {

	private CSVWriter() {
		// TODO Auto-generated constructor stub
	}
	
	private static final CSVWriter INSTANCE = new CSVWriter();
	private static String fileName = null;
	private static FileOutputStream fos = null;
	private static BufferedWriter bw = null;
	
	public void writeToFile(List<Float> data) throws Exception {		
		if(fileName == null){
			throw new Exception("Please specify a name for the file");
		}
		String join = StringUtils.join(data, ",");
		bw.append(join + "\n");
	}
	
	public final static CSVWriter getInstance(String nameOfFile ) throws FileNotFoundException{
		fileName = nameOfFile;
		fos = new FileOutputStream(new File(fileName));
		bw = new BufferedWriter(new OutputStreamWriter(fos));
		return INSTANCE;
	}
	
	


}

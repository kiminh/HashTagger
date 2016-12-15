package me.motallebi.hashtagger;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class CSVWriter {

	private final FileWriter writer;

	public CSVWriter(String filename) throws IOException {
		if (filename == null || filename.isEmpty()) {
			throw new IOException("Please specify a name for the file");
		}
		this.writer = new FileWriter(filename);
	}

	public void write(List<?> data) throws IOException {
		if (writer == null)
			throw new IOException("FileWriter not initialized.");
		if (data == null)
			throw new NullPointerException("Input list was null.");
		try {
			String join = StringUtils.join(data, ",");
			writer.write(join);
			writer.write("\n");
			writer.flush();
		} catch (IOException e) {
			throw new IOException("Could not write to file", e);
		}
	}

	public void close() throws IOException {
		writer.close();
	}
}

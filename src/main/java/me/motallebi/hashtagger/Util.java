package me.motallebi.hashtagger;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

/**
 * Utility class
 * 
 * @author mrmotallebi
 *
 */
public class Util {

	/**
	 * Taken from : http://stackoverflow.com/a/17024943/475200
	 * 
	 * @param fileIn
	 * @return bufferedReader
	 * @throws FileNotFoundException
	 * @throws CompressorException
	 */
	public static BufferedReader getBufferedReaderForCompressedFile(
			File fileIn, Charset charset) throws FileNotFoundException,
			CompressorException {
		if (charset == null)
			charset = StandardCharsets.US_ASCII;
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(
				fileIn));
		CompressorInputStream decompressedInputStream = new CompressorStreamFactory()
				.createCompressorInputStream(bis);
		return new BufferedReader(new InputStreamReader(
				decompressedInputStream, charset));
	}
}

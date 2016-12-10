package me.motallebi.hashtagger;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
		if (null == charset)
			charset = StandardCharsets.US_ASCII;
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(
				fileIn));
		CompressorInputStream decompressedInputStream = new CompressorStreamFactory()
				.createCompressorInputStream(bis);
		return new BufferedReader(new InputStreamReader(
				decompressedInputStream, charset));
	}

	/**
	 * Split the text using space. Then remove the FREQUENT_WORD_SET from it.
	 * 
	 * @param text
	 * @return array of unique words
	 */
	public static String[] getMainWords(String text) {
		if (null == text)
			return new String[0];
		text = text.toLowerCase();
		String[] words = text.split(" ");
		Set<String> wordSet = new HashSet<>(Arrays.asList(words));
		// wordSet.removeAll(FREQUENT_WORD_SET);
		return wordSet.toArray(new String[wordSet.size()]);
	}
}

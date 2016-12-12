/**
 * 
 */
package me.motallebi.hashtagger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.compress.compressors.CompressorException;

import twitter4j.HashtagEntity;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;

/**
 * Simple implementation of a file-based Tweet source. Currently, uses Twitter4J
 * library to read Tweets from JSON files. Loads all tweets into memory (as an
 * ArrayList), so if you have a lot of files, it is better and recommended that
 * you set -Xmx to a large value, otherwise JVM will be calling GC frequently.
 * In future revisions, might consider changing the object that is maintained in
 * memory to have less memory footprint. Currently filters out all tweets that
 * are not English. This is the first version - not optimized at all!
 * 
 * @author mrmotallebi
 *
 */
public class FileTweetSource implements TweetSource {

	private String filePath = Constants.TWEET_SAVE_LOCATION;
	private int numToLoad = Constants.TWEET_RANGE_END
			- Constants.TWEET_RANGE_START + 1;
	private List<Status> tweetList = new ArrayList<>(this.numToLoad);
	private volatile boolean loaded = false;

	/**
	 * 
	 */
	public FileTweetSource() {
	}

	/**
	 * @param filePath
	 *            relative filePath if other than specified in properties file
	 * @param date
	 *            currently not used
	 */
	public FileTweetSource(String filePath, Date date) {
		if (filePath == null || filePath.isEmpty())
			return;
		if (date == null)
			this.filePath = filePath;
		else {
			// Integrate the date into the path
			this.filePath = filePath;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<Status> iterator() {
		return this.tweetList.iterator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see me.motallebi.hashtagger.TweetSource#loadTweets()
	 */
	@Override
	public synchronized void loadTweets() {
		this.loaded = false;

		File tweetFilePath = new File(this.filePath);
		File[] files = tweetFilePath.listFiles();

		if (files == null)
			throw new UncheckedIOException(new IOException(
					"Path does not exist"));

		for (File f : files) {
			List<Status> statuses = null;
			try {
				statuses = readFromFile(f);
			} catch (CompressorException | IOException e) {
				System.err.println("Error reading from tweet file "
						+ f.getName());
				e.printStackTrace();
				continue;
			}
			System.out.println(f.getName() + " -- " + statuses.size());
			for (Status status : statuses) {
				// Yes, this is very inefficient. Slow, grows by making size *
				// 1.5
				if ("en".equals(status.getLang()))// ???
					this.tweetList.add(status);
			}
			// To make sure no handles are left over
			statuses.clear();
		}
		System.out.println(this.tweetList.size());
		// Suggest it can GC now
		System.gc();
		this.loaded = true;
		notifyAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see me.motallebi.hashtagger.TweetSource#loadTweets(java.util.Date)
	 */
	@Override
	public synchronized void loadTweets(Date date) {
		// TODO
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	/**
	 * @param file
	 * @return
	 * @throws CompressorException
	 * @throws IOException
	 */
	private static List<Status> readFromFile(File file)
			throws CompressorException, IOException {
		List<Status> retList = new ArrayList<>(4000);
		try (BufferedReader bfr = file.getName().endsWith(".bz2") ? Util
				.getBufferedReaderForCompressedFile(file, null)
				: new BufferedReader(new InputStreamReader(new FileInputStream(
						file)));) {
			String line;
			while ((line = bfr.readLine()) != null) {
				Status s;
				try {
					s = TwitterObjectFactory.createStatus(line);
					retList.add(MTweet.asMTweet(s));
				} catch (TwitterException e) {
					// pass
					continue;
				}
			}
		} catch (IOException e) {
			throw new IOException("Error while reading tweet file.", e);
		}
		return retList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see me.motallebi.hashtagger.TweetSource#getStream()
	 */
	@Override
	public OutputStream getStream() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * To be replaced with getStream()
	 * @return computed Tweet list
	 */
	public List<Status> getTweetList(){
		return this.tweetList;
	}

	/**
	 * Method to notify that the data has loaded.
	 */
	public synchronized void waitUntilLoad() {
		while (!this.loaded) {
			System.out.println("Waiting for news to load");
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
	}
	
	public static void main(String[] args) {
		FileTweetSource fts = new FileTweetSource();
		fts.loadTweets();
		fts.waitUntilLoad();
		for(Status s : fts){
			HashtagEntity[] hes = s.getHashtagEntities();
			for (HashtagEntity he : hes){
				System.out.print("#" + he.getText() + " ");
			}
			if(hes.length > 0)
				System.out.println();
		}
		
		HashtagFinder hf = new SimpleHashtagFinder(fts.tweetList);
		System.out.println(hf.getStatusWithHT("travel").length);
		System.out.println("Max tweets for hashtag : " + hf.getMaxTweetsForHashtags());
		System.out.println("Min tweets for hashtag : " + hf.getMinTweetsForHashtags());
		System.out.println("\"#travel\" was mentioned in this many tweets: " + hf.getTweetCountForHashtag("travel"));
	}

}

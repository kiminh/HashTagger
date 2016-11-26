/**
 * 
 */
package me.motallebi.hashtagger;

import java.io.OutputStream;
import java.util.Date;

import twitter4j.Status;

/**
 * @author mrmotallebi
 *
 */
public interface TweetSource extends Iterable<Status> {
	
	public void loadTweets();
	
	public void loadTweets(Date date);
	
	public OutputStream getStream();
	
}

package me.motallebi.hashtagger;

import twitter4j.Status;

/**
 * @author mrmotallebi
 *
 */
public interface HashtagFinder {
	
	/**
	 * Get Tweets having a Hashtag
	 * @param hashtag
	 * @return List of Tweets
	 */
	public Status[] getStatusWithHT(String hashtag);
	
	/**
	 * Get Tweets having a word
	 * @param word
	 * @return List of Tweets
	 */
	public Status[] getStatusWithWord(String word);
	
	/**
	 * Get Hashtags for Tweets having a word
	 * @param word
	 * @return List of Hashtags
	 */
	public String[] getHTWithWord(String word);

}

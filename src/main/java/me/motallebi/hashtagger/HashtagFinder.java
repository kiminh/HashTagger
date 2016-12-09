package me.motallebi.hashtagger;

import twitter4j.Status;

/**
 * @author mrmotallebi
 *
 */
public interface HashtagFinder {

	/**
	 * Get Tweets having a Hashtag
	 * 
	 * @param hashtag
	 * @return List of Tweets
	 */
	public Status[] getStatusWithHT(String hashtag);

	/**
	 * Get Tweets having a word
	 * 
	 * @param word
	 * @return List of Tweets
	 */
	public Status[] getStatusWithWord(String word);

	/**
	 * Get Hashtags for Tweets having a word
	 * 
	 * @param word
	 * @return List of Hashtags
	 */
	public String[] getHTWithWord(String word);

	/**
	 * Get hashtags for tweets having both words
	 * 
	 * @param word1
	 * @param word2
	 * @return List of Hashtags
	 */
	public String[] getHTWithWord(String word1, String word2);

	/**
	 * Get all hashtags discovered
	 * 
	 * @return array of hashtags, preferably unique set
	 */
	public String[] getAllHashtags();

	/**
	 * Number of tweets mentioning this hashtag
	 * 
	 * @param hashtag
	 * @return
	 */
	public int getTweetCountForHashtag(String hashtag);

	/**
	 * Get max number of tweets mentioning some hashtag
	 * 
	 * @return
	 */
	public int getMaxTweetsForHashtags();

	/**
	 * Get min number of tweets mentioning some hashtag
	 * 
	 * @return
	 */
	public int getMinTweetsForHashtags();

}

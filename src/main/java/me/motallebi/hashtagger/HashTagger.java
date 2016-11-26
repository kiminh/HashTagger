/**
 * 
 */
package me.motallebi.hashtagger;

import twitter4j.Status;

/**
 * Program entry point. Main class.
 * 
 * @author mrmotallebi
 *
 */
public class HashTagger {

	/**
	 * Main method
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		FileTweetSource fts = new FileTweetSource();

		fts.loadTweets();
		
		HashtagFinder hashtagFinder = new SimpleHashtagFinder(fts.getTweetList());
		
		Status[] result = hashtagFinder.getStatusWithHT("cnn");
		for (Status s : result)
			System.out.println(s.getText());
	}


}

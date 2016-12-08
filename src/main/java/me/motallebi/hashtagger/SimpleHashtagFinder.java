/**
 * 
 */
package me.motallebi.hashtagger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import twitter4j.HashtagEntity;
import twitter4j.Status;

/**
 * @author mrmotallebi
 *
 */
public class SimpleHashtagFinder implements HashtagFinder {

	protected final List<Status> statusList;
	protected final Map<String, List<Status>> invertedIndex;

	public SimpleHashtagFinder(List<Status> statusList) {
		this.statusList = statusList;
		// Assume each two tweets has one unique word
		this.invertedIndex = new HashMap<String, List<Status>>(
				statusList.size() / 2);
		for (Status status : this.statusList) {
			String text = status.getText();
			String[] mainWords = Util.getMainWords(text);

			HashtagEntity[] hashtags = status.getHashtagEntities();
			String[] hashtagWords = new String[hashtags.length];
			int counter = 0;
			for (HashtagEntity hashtag : hashtags) {
				hashtagWords[counter++] = hashtag.getText().toLowerCase();
			}
			// Add tweet for each word
			for (String word : mainWords) {
				if (!invertedIndex.containsKey(word)) {
					List<Status> statuses = new ArrayList<Status>();
					invertedIndex.put(word, statuses);
					statuses.add(status);
				} else {
					List<Status> statuses = invertedIndex.get(word);
					statuses.add(status);
				}
			}
			// Add tweet for hashtags
			for (String word : hashtagWords) {
				if (!invertedIndex.containsKey(word)) {
					List<Status> statuses = new ArrayList<Status>();
					invertedIndex.put(word, statuses);
					statuses.add(status);
				} else {
					List<Status> statuses = invertedIndex.get(word);
					statuses.add(status);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * me.motallebi.hashtagger.HashTagFinder#getStatusWithHT(java.lang.String)
	 */
	@Override
	public Status[] getStatusWithHT(String hashtag) {
		if (this.invertedIndex.get(hashtag)==null){
			Status d[] =  new Status[0];
			return  d;
		}
		return this.invertedIndex.get(hashtag).toArray(new Status[0]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * me.motallebi.hashtagger.HashTagFinder#getStatusWithWord(java.lang.String)
	 */
	@Override
	public Status[] getStatusWithWord(String word) {
		return (Status[]) this.invertedIndex.get(word).toArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * me.motallebi.hashtagger.HashTagFinder#getHTWithWord(java.lang.String)
	 */
	@Override
	public String[] getHTWithWord(String word) {
		// TODO Auto-generated method stub
		return null;
	}

}

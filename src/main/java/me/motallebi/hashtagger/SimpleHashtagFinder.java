/**
 * 
 */
package me.motallebi.hashtagger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import twitter4j.HashtagEntity;
import twitter4j.Status;

/**
 * @author mrmotallebi
 *
 */
public class SimpleHashtagFinder implements HashtagFinder {

	protected final List<Status> statusList;
	protected final Map<String, List<Status>> invertedIndex;
	protected final Map<String, List<Status>> htInvertedIndex;

	public SimpleHashtagFinder(List<Status> statusList) {
		this.statusList = statusList;
		// Assume each two tweets has one unique word
		this.invertedIndex = new HashMap<String, List<Status>>(
				statusList.size() / 2);
		this.htInvertedIndex = new HashMap<String, List<Status>>(
				statusList.size() / 10);
		for (Status status : this.statusList) {
			String text = status.getText();
			String[] mainWords = Util.getMainWords(text);
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
			for (HashtagEntity hashtag : status.getHashtagEntities()) {
				String word = hashtag.getText().toLowerCase();
				if (!htInvertedIndex.containsKey(word)) {
					List<Status> statuses = new ArrayList<Status>();
					htInvertedIndex.put(word, statuses);
					statuses.add(status);
				} else {
					List<Status> statuses = htInvertedIndex.get(word);
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
		if (this.htInvertedIndex.get(hashtag) == null) {
			Status d[] = new Status[0];
			return d;
		}
		// .toArray(new Status[0]) automatically recreates an array to match the
		// size
		return this.htInvertedIndex.get(hashtag).toArray(new Status[0]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * me.motallebi.hashtagger.HashTagFinder#getStatusWithWord(java.lang.String)
	 */
	@Override
	public Status[] getStatusWithWord(String word) {
		try{
		return this.invertedIndex.get(word).toArray(new Status[0]);
		}catch(NullPointerException e){
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * me.motallebi.hashtagger.HashTagFinder#getHTWithWord(java.lang.String)
	 */
	@Override
	public String[] getHTWithWord(String word) {
		if (!this.invertedIndex.containsKey(word))
			return new String[0];
		Set<String> hashtagSet = new HashSet<>();
		for (Status status : this.invertedIndex.get(word)) {
			for (HashtagEntity hashtag : status.getHashtagEntities()) {
				hashtagSet.add(hashtag.getText().toLowerCase());
			}
		}
		return hashtagSet.toArray(new String[0]);
	}

	/* (non-Javadoc)
	 * @see me.motallebi.hashtagger.HashtagFinder#getHTWithWord(java.lang.String, java.lang.String)
	 */
	@Override
	public String[] getHTWithWord(String word1, String word2) {
		if (word2 == null || word2.isEmpty())
			return getHTWithWord(word1);
		if (!this.invertedIndex.containsKey(word1)
				&& !this.invertedIndex.containsKey(word2))
			return new String[0];
		Set<String> hashtagSet1 = new HashSet<>();
		for (Status status : this.invertedIndex.get(word1)) {
			for (HashtagEntity hashtag : status.getHashtagEntities()) {
				hashtagSet1.add(hashtag.getText().toLowerCase());
			}
		}
		Set<String> hashtagSet2 = new HashSet<>();
		for (Status status : this.invertedIndex.get(word2)) {
			for (HashtagEntity hashtag : status.getHashtagEntities()) {
				hashtagSet2.add(hashtag.getText().toLowerCase());
			}
		}
		hashtagSet1.retainAll(hashtagSet2);
		return hashtagSet1.toArray(new String[0]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see me.motallebi.hashtagger.HashtagFinder#getAllHashtags()
	 */
	@Override
	public String[] getAllHashtags() {
		Set<String> hashtagSet = this.htInvertedIndex.keySet();
		return hashtagSet.toArray(new String[0]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see me.motallebi.hashtagger.HashtagFinder#getMaxTweetsForHashtags()
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public int getMaxTweetsForHashtags() {
		return Collections.max(this.htInvertedIndex.values(),
				new Comparator<List>() {
					@Override
					public int compare(List o1, List o2) {
						return o1.size() - o2.size();
					}
				}).size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see me.motallebi.hashtagger.HashtagFinder#getMinTweetsForHashtags()
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public int getMinTweetsForHashtags() {
		return Collections.min(this.htInvertedIndex.values(),
				new Comparator<List>() {
					@Override
					public int compare(List o1, List o2) {
						return o1.size() - o2.size();
					}
				}).size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * me.motallebi.hashtagger.HashtagFinder#getTweetCountForHashtag(java.lang
	 * .String)
	 */
	@Override
	public int getTweetCountForHashtag(String hashtag) {
		if (!this.htInvertedIndex.containsKey(hashtag))
			return 0;
		return this.htInvertedIndex.get(hashtag).size();
	}

}

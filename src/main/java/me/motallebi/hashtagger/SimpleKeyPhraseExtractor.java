/**
 * 
 */
package me.motallebi.hashtagger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * @author mrmotallebi
 *
 */
public class SimpleKeyPhraseExtractor extends AbstractKeyPhraseExtractor {

	private SimpleKeyPhraseExtractor() {
	}

	public static final SimpleKeyPhraseExtractor INSTANCE = new SimpleKeyPhraseExtractor();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * me.motallebi.hashtagger.KeyPhraseExtractor#extractKeyPhrases(me.motallebi
	 * .hashtagger.NewsArticleInterface)
	 */
	@Override
	public List<String> extractKeyPhrases(NewsArticle newsArticle) {
		String[] words = newsArticle.getNewsBody().split(" ", 0);
		Map<String, Integer> wordCount = new HashMap<String, Integer>();
		for (String word : words) {
			int val = wordCount.getOrDefault(word, 0);
			wordCount.put(word, ++val);
		}
		Queue<String> countOrder = new PriorityQueue<String>(wordCount.keySet()
				.size(), new Comparator<String>() {

			@Override
			public int compare(String s1, String s2) {
				if (s1 == null || !wordCount.containsKey(s1))
					return Integer.MIN_VALUE;
				if (s2 == null || !wordCount.containsKey(s2))
					return Integer.MAX_VALUE;
				return wordCount.get(s1).compareTo(wordCount.get(s2));
			}

		});
		for (String word : wordCount.keySet()) {
			countOrder.add(word);
		}
		List<String> returnStringList = new ArrayList<String>(
				this.keyPhraseCount);
		for (int i = 0; i < this.keyPhraseCount; i++) {
			returnStringList.add(countOrder.poll());
		}
		return returnStringList;
	}

}

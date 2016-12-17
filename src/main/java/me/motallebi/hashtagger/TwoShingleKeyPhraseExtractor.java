/**
 * 
 */
package me.motallebi.hashtagger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author mrmotallebi
 *
 */
public class TwoShingleKeyPhraseExtractor extends AbstractKeyPhraseExtractor {

	private TwoShingleKeyPhraseExtractor() {
	}

	private static final TwoShingleKeyPhraseExtractor instance = new TwoShingleKeyPhraseExtractor();
	
	public static final TwoShingleKeyPhraseExtractor getInstance() {
		return instance;
	}
	
	private static final Pattern splitPattern = Pattern.compile("\\W+");
	private static final List<String> frequentWordSet = Arrays.asList("a",
			"the", "i", "me", "my", "he", "she", "his", "her", "here", "there",
			"by", "and", "its", "of", "in", "for", "on", "it", "be", "if",
			"did", "not", "as", "to", "a", "an", "have", "has", "had", "with",
			"over", "no", "will", "against", "say", "says", "such", "any",
			"all", "some", "many", "what", "over", "out", "up", "down", "go",
			"going", "went", "into", "after", "before", "back", "ahead", "but",
			"been", "who", "when", "where", "how", "why", "other", "");
	
	/* (non-Javadoc)
	 * @see me.motallebi.hashtagger.AbstractKeyPhraseExtractor#extractKeyPhrases(me.motallebi.hashtagger.NewsArticle)
	 */
	@Override
	public List<String> extractKeyPhrases(NewsArticle newsArticle) {
		String newsTitle = newsArticle.getTitle();
		String[] words = splitPattern.split(newsTitle);
		List<String> wordList = new ArrayList<>(Arrays.asList(words));
		wordList.removeAll(frequentWordSet);
		List<String> returnList = new ArrayList<>(wordList.size());
		String word1 = null;
		String word2 = null;
		Iterator<String> wordIter = wordList.iterator();
		if(wordIter.hasNext())
			word1 = wordIter.next();
		while(wordIter.hasNext()){
			word2 = wordIter.next();
			returnList.add(word1.toLowerCase() + " " + word2.toLowerCase());
			word1 = word2;
		}
		return returnList;
	}

}

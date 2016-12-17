/**
 * 
 */
package me.motallebi.hashtagger;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author mrmotallebi
 *
 */
public class PredefinedKeyPhraseExtractor extends AbstractKeyPhraseExtractor {

	private PredefinedKeyPhraseExtractor() {
	}

	private static final PredefinedKeyPhraseExtractor instance = new PredefinedKeyPhraseExtractor();

	public static final PredefinedKeyPhraseExtractor getInstance() {
		return instance;
	}

	protected Pattern pattern = Pattern
			.compile("<strong>(S|s)tream (K|k)eywords: (.*?)</strong>");

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * me.motallebi.hashtagger.AbstractKeyPhraseExtractor#extractKeyPhrases(
	 * me.motallebi.hashtagger.NewsArticle)
	 */
	@Override
	public List<String> extractKeyPhrases(NewsArticle newsArticle) {
		String newsBody = newsArticle.getBody();
		Matcher matcher = pattern.matcher(newsBody);
		if (!matcher.find())
			return Collections.emptyList();
		String unparsed = matcher.group(3);
		String[] pairs = unparsed.split(",");
		return Arrays.asList(pairs);
	}

}

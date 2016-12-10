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

	private static final PredefinedKeyPhraseExtractor INSTANCE = new PredefinedKeyPhraseExtractor();

	public static final PredefinedKeyPhraseExtractor getInstance() {
		return INSTANCE;
	}

	protected Pattern pattern = Pattern
			.compile("<strong>Stream Keywords: (.*?)</strong>");

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
		String unparsed = matcher.group(1);
		String[] pairs = unparsed.split(",");
		return Arrays.asList(pairs);
	}

}

/**
 * 
 */
package me.motallebi.hashtagger;

import java.util.List;

/**
 * @author mrmotallebi
 *
 */
public abstract class AbstractKeyPhraseExtractor implements KeyPhraseExtractor {
	
	protected int keyPhraseCount = 5;

	/* (non-Javadoc)
	 * @see me.motallebi.hashtagger.KeyPhraseExtractor#extractKeyPhrases(me.motallebi.hashtagger.NewsArticleInterface)
	 */
	@Override
	public abstract List<String> extractKeyPhrases(NewsArticle newsArticle);

	/* (non-Javadoc)
	 * @see me.motallebi.hashtagger.KeyPhraseExtractor#extractKeyPhrases(me.motallebi.hashtagger.NewsArticleInterface, int)
	 */
	@Override
	public List<String> extractKeyPhrases(NewsArticle newsArticle,
			int keyPhraseCount) {
		this.keyPhraseCount = keyPhraseCount;
		return extractKeyPhrases(newsArticle);
	}
	
	@Override
	public void setKeyPhraseCount(int keyPhraseCount) {
		this.keyPhraseCount = keyPhraseCount;
	}

}

/**
 * 
 */
package me.motallebi.hashtagger;

import java.util.List;

/**
 * @author mrmotallebi
 *
 */
public interface KeyPhraseExtractor {
		
	public List<String> extractKeyPhrases(NewsArticle newsArticle);
	
	public List<String> extractKeyPhrases(NewsArticle newsArticle, int keyPhraseCount);
	
	public void setKeyPhraseCount(int keyPhraseCount);

}

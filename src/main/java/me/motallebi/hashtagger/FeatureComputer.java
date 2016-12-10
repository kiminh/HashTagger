/**
 * 
 */
package me.motallebi.hashtagger;

import java.util.List;

import me.motallebi.hashtagger.SimpleFeatureComputer.DurationEnum;
import twitter4j.HashtagEntity;

/**
 * @author mhmotallebi
 *
 */
public interface FeatureComputer {

	// void TR(NewsArticle a, HashtagEntity h, Date t); // TRending hashtags
	/*
	 * needs a function tell how many times this HashtagEntity has occured in
	 * the past time window t
	 */

	void HE(NewsArticle a, HashtagEntity h); // HashtagEntity in hEadLine

	void LS(NewsArticle article, HashtagEntity h, DurationEnum lambda2,
			List<NewsArticle> articles);

	void LF(NewsArticle a, HashtagEntity h, DurationEnum LAMBDA);

	void GS(NewsArticle article, HashtagEntity h, DurationEnum GAMMA);

	void GF(HashtagEntity h, DurationEnum GAMMA);

	void TR(NewsArticle a, HashtagEntity h, DurationEnum LAMBDA);

	void UC(NewsArticle article, HashtagEntity h, DurationEnum LAMBDA);

	void UR(NewsArticle article, HashtagEntity h, DurationEnum LAMBDA);

	void EG(Float TR, NewsArticle article, HashtagEntity h, DurationEnum lambda3);

}

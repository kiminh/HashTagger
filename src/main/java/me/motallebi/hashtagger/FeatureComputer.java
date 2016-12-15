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

	float HE(NewsArticle a, HashtagEntity h); // HashtagEntity in hEadLine

	float LS(NewsArticle article, HashtagEntity h, DurationEnum lambda2,
			List<NewsArticle> articles);

	float LF(NewsArticle a, HashtagEntity h, DurationEnum LAMBDA);

	float GS(NewsArticle article, HashtagEntity h, DurationEnum GAMMA);

	float GF(NewsArticle article, HashtagEntity h, DurationEnum gamma);

	float TR(NewsArticle a, HashtagEntity h, DurationEnum LAMBDA);

	float UC(NewsArticle article, HashtagEntity h, DurationEnum LAMBDA);

	float UR(NewsArticle article, HashtagEntity h, DurationEnum LAMBDA);

	float EG(NewsArticle article, HashtagEntity h, DurationEnum lambda3,
			Float TR);

}

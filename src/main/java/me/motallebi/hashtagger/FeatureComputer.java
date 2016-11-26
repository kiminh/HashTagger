/**
 * 
 */
package me.motallebi.hashtagger;

import java.util.Date;
import java.util.List;

/**
 * @author mhmotallebi
 *
 */
public interface FeatureComputer {

	public Float LS(NewsArticle a, Hashtag h, Date LAMBDA, List<NewsArticle> articles); // Local Simiarity
	
	public Float LF(NewsArticle a, Hashtag h, Date LAMBDA ); // Local Frequency
	
	public Float GS(NewsArticle a, Hashtag h, Date GAMMA ); // Global Similarity
	
	public Float GF(Hashtag h, Date GAMMA ); // Global Frequency
	
	public Float TR(NewsArticle a, Hashtag h, Date t); // TRending hashtags
	/* needs a function tell how many times this hashtag has occured in the past time window t*/

	public Float EG(Float TR, NewsArticle article, Hashtag h, Date timeFrame); // Expected Gain
	
	public Float HE(NewsArticle a, Hashtag h); // Hashtag in hEadLine
	
	public Float UR(NewsArticle a, Hashtag h, Date LAMBDA ); // Unique user Ratio
	
	public int UC(NewsArticle a, Hashtag h, Date LAMBDA ); // User Credibility

}

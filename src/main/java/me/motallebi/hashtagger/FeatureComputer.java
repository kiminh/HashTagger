/**
 * 
 */
package me.motallebi.hashtagger;

import java.util.Date;

/**
 * @author mhmotallebi
 *
 */
public interface FeatureComputer {

	public Float LS(NewsArticle a, Hashtag h, Date LAMBDA ); // Local Simiarity
	
	public Float LF(NewsArticle a, Hashtag h, Date LAMBDA ); // Local Frequency
	
	public Float GS(NewsArticle a, Hashtag h, Date GAMMA ); // Global Similarity
	
	public Float GF(Hashtag h, Date GAMMA ); // Global Frequency
	
	public Float TR(NewsArticle a, Hashtag h, Date t); // TRending hashtags
	/* needs a function tell how many times this hashtag has occured in the past time window t*/
	
	public Float EG(); // Expected Gain
	
	public Float HE(NewsArticle a, Hashtag h); // Hashtag in hEadLine
	
	public Float UR(NewsArticle a, Hashtag h, Date LAMBDA ); // Unique user Ratio
	
	public Float UC(NewsArticle a, Hashtag h, Date LAMBDA ); // User Credibility

}

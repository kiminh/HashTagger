/**
 * 
 */
package me.motallebi.hashtagger;

import java.io.OutputStream;
import java.util.Date;

/**
 * @author mhmotallebi
 *
 */
public interface FeatureComputer {

	public Float LS(NewsArticle a, Hashtag h, Time LAMBDA ); // Local Simiarity
	
	public Float LF(NewsArticle a, Hashtag h, Time LAMBDA ); // Local Frequency
	
	public Float GS(NewsArticle a, Hashtag h, Time GAMMA ); // Global Similarity
	
	public Float GF(Hashtag h, Time GAMMA ); // Global Frequency
	
	public Float TR(NewsArticle a, Hashtag h, Time t); // TRending hashtags
	/* needs a function tell how many times this hashtag has occured in the past time window t*/
	
	public Float EG(); // Expected Gain
	
	public Float HE(NewsArticle a, Hashtag h); // Hashtag in hEadLine
	
	public Float UR(NewsArticle a, Hashtag h, Time LAMBDA ); // Unique user Ratio
	
	public Float UC(NewsArticle a, Hashtag h, Time LAMBDA ); // User Credibility

/*
	public void loadNews();

	public void loadNews(Date date);

	public void loadNews(Integer count);

	public void loadNews(Date date, Integer count);

	public OutputStream getStream();

	public NewsArticle getNextNews();
*/

}

/**
 * 
 */
package me.motallebi.hashtagger;

/**
 * @author mrmotallebi
 *
 */
public interface NewsArticle {

	/**
	 * @param newsTitle
	 */
	public void setNewsTitle(String newsTitle);

	/**
	 * @param newsBody
	 */
	public void setNewsBody(String newsBody);

	/**
	 * @return
	 */
	public String getNewsBody();

	/**
	 * @return
	 */
	public String getNewsTitle();

}

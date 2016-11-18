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
	 * Assume ID is an int
	 * @param id
	 */
	public void setId(int id);
	
	/**
	 * @return Id
	 */
	public int getId();

	/**
	 * @param newsTitle
	 */
	public void setTitle(String newsTitle);

	/**
	 * @param newsBody
	 */
	public void setBody(String newsBody);

	/**
	 * @return
	 */
	public String getBody();

	/**
	 * @return
	 */
	public String getTitle();

}

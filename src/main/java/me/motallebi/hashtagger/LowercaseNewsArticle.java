/**
 * 
 */
package me.motallebi.hashtagger;

/**
 * @author mrmotallebi
 *
 */
public class LowercaseNewsArticle extends SimpleNewsArticle {

	/* (non-Javadoc)
	 * @see me.motallebi.hashtagger.SimpleNewsArticle#setBody(java.lang.String)
	 */
	@Override
	public void setBody(String newsBody) {
		if(newsBody == null)
			throw new NullPointerException("newsBody is null.");
		super.setBody(newsBody.toLowerCase());
	}

	/* (non-Javadoc)
	 * @see me.motallebi.hashtagger.SimpleNewsArticle#setTitle(java.lang.String)
	 */
	@Override
	public void setTitle(String newsTitle) {
		if (newsTitle == null)
			throw new NullPointerException("newsTitle is null.");
		super.setTitle(newsTitle.toLowerCase());
	}

}

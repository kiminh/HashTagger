/**
 * 
 */
package me.motallebi.hashtagger;

/**
 * @author mrmotallebi
 *
 */
public class SimpleNewsArticle implements NewsArticle {

	protected String newsTitle;
	protected String newsBody;
	protected int id;

	/* (non-Javadoc)
	 * @see me.motallebi.hashtagger.NewsArticle#setId(int)
	 */
	@Override
	public void setId(int id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see me.motallebi.hashtagger.NewsArticle#getId()
	 */
	@Override
	public int getId() {
		return this.id;
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see me.motallebi.hashtagger.NewsArticleInterface#getNewsBody()
	 */
	@Override
	public String getBody() {
		return this.newsBody;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see me.motallebi.hashtagger.NewsArticleInterface#getNewsTitle()
	 */
	@Override
	public String getTitle() {
		return this.newsTitle;
	}

	@Override
	public void setTitle(String newsTitle) {
		// Check encoding etc. when setting
		this.newsTitle = newsTitle;

	}

	@Override
	public void setBody(String newsBody) {
		// Check encoding etc. when setting
		this.newsBody = newsBody;

	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 * Generate HashCode by concatenating two Strings. 
	 * Hashcode computation taken from the algorithm for String.
	 */
	@Override
	public int hashCode() {
		return (int) Math.pow(31, this.newsTitle.length()) * this.newsTitle.hashCode() + this.newsBody.hashCode(); 
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.newsTitle + " : " + this.newsBody;
	}


}

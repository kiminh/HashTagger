/**
 * 
 */
package me.motallebi.hashtagger;

import java.util.Date;
import java.util.regex.Pattern;

/**
 * @author mrmotallebi
 *
 */
public class SimpleNewsArticle implements NewsArticle {

	protected String newsTitle;
	protected String newsBody;
	protected Date newsDate;
	protected int id;

	/*
	 * (non-Javadoc)
	 * 
	 * @see me.motallebi.hashtagger.NewsArticle#setId(int)
	 */
	@Override
	public void setId(int id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
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
		if (this.newsBody == null)
			throw new NullPointerException(
					"newsBody is not initialized for this NewsArticle");
		return this.newsBody;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see me.motallebi.hashtagger.NewsArticleInterface#getNewsTitle()
	 */
	@Override
	public String getTitle() {
		if (this.newsTitle == null)
			throw new NullPointerException(
					"newsTitle is not initialized for this NewsArticle");
		return this.newsTitle;
	}

	private static Pattern tagPattern = Pattern.compile("<.*?>");

	/*
	 * (non-Javadoc)
	 * 
	 * @see me.motallebi.hashtagger.NewsArticle#setTitle(java.lang.String)
	 */
	@Override
	public void setTitle(String newsTitle) {
		// Check encoding etc. when setting
		if (newsTitle != null)
			this.newsTitle = tagPattern.matcher(newsTitle).replaceAll("");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see me.motallebi.hashtagger.NewsArticle#setBody(java.lang.String)
	 */
	@Override
	public void setBody(String newsBody) {
		// Check encoding etc. when setting
		this.newsBody = newsBody;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode() Generate HashCode by concatenating two
	 * Strings. Hashcode computation taken from the algorithm for String.
	 */
	@Override
	public int hashCode() {
		return (int) Math.pow(31, this.newsTitle.length())
				* this.newsTitle.hashCode() + this.newsBody.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.newsTitle + " : " + this.newsBody;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see me.motallebi.hashtagger.NewsArticle#getDate()
	 */
	@Override
	public Date getDate() {
		if (this.newsDate == null)
			throw new NullPointerException(
					"Date is not defined for this NewsArticle");
		// make safe copy
		return (Date) this.newsDate.clone();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see me.motallebi.hashtagger.NewsArticle#setDate(java.util.Date)
	 */
	@Override
	public void setDate(Date date) {
		if (date != null)
			this.newsDate = new Date(date.getTime());
	}

}

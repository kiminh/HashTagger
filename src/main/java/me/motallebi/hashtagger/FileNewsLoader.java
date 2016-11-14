/**
 * 
 */
package me.motallebi.hashtagger;

import java.io.OutputStream;
import java.util.Date;

/**
 * @author mrmotallebi
 *
 */
public class FileNewsLoader implements NewsLoader {

	private String filePath;

	public FileNewsLoader(String filePath) {
		if (filePath == null || filePath.isEmpty()) {
			throw new IllegalArgumentException("File path invalid.");
		}
		this.filePath = filePath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see me.motallebi.hashtagger.NewsLoaderInterface#loadNews()
	 */
	@Override
	public void loadNews() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see me.motallebi.hashtagger.NewsLoaderInterface#loadNews(java.util.Date)
	 */
	@Override
	public void loadNews(Date date) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * me.motallebi.hashtagger.NewsLoaderInterface#loadNews(java.lang.Integer)
	 */
	@Override
	public void loadNews(Integer count) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see me.motallebi.hashtagger.NewsLoaderInterface#loadNews(java.util.Date,
	 * java.lang.Integer)
	 */
	@Override
	public void loadNews(Date date, Integer count) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see me.motallebi.hashtagger.NewsLoaderInterface#getStream()
	 */
	@Override
	public OutputStream getStream() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see me.motallebi.hashtagger.NewsLoaderInterface#getNextNews()
	 */
	@Override
	public NewsArticle getNextNews() {
		// TODO Auto-generated method stub
		return null;
	}

}

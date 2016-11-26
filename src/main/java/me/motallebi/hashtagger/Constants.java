package me.motallebi.hashtagger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class Constants {

	private Constants() {
	}

	// TODO: Add Ant/Maven script to make sure the config file is exported upon
	// build
	private static final String propFilePath = "/config/config.prop";

	private static final Properties properties = new Properties();
	static {
		try {
			assert (properties != null);
			InputStream configFileStream = Constants.class
					.getResourceAsStream(propFilePath);
			assert (configFileStream != null);
			properties.load(configFileStream);
		} catch (IOException e) {
			throw new RuntimeException(
					"Problem while reading properties file.", e);
		}
	}

	public static final String NEWS_URL_PATTERN = properties
			.getProperty("news.url.pattern");

	public static final int NEWS_RANGE_START = Integer.valueOf(properties
			.getProperty("news.range.start"));

	public static final int NEWS_RANGE_END = Integer.valueOf(properties
			.getProperty("news.range.end"));

	public static final String NEWS_SAVE_LOCATION = properties
			.getProperty("news.save.location");

	public static final int CONCURRENT_DOWNLOADS = 5;

	public static final String NEWS_FILE_REGEX = properties
			.getProperty("news.file.regex");

	public static final int NEWS_TITLE_GROUP = Integer.valueOf(properties
			.getProperty("news.title.group"));

	public static final int NEWS_BODY_GROUP = Integer.valueOf(properties
			.getProperty("news.body.group"));

	public static final String TWEET_SAVE_LOCATION = properties.getProperty("tweet.save.location");

	public static final int TWEET_RANGE_END = Integer.valueOf(properties.getProperty("tweet.range.end"));

	public static final int TWEET_RANGE_START = Integer.valueOf(properties.getProperty("tweet.range.start"));

}

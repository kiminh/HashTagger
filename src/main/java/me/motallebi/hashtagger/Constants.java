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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static final String NEWS_URL_PATTERN = properties
			.getProperty("news.url.pattern");

	public static final int NEWS_RANGE_START = Integer.valueOf(properties
			.getProperty("news.range.start"));

	public static final int NEWS_RANGE_END = Integer.valueOf(properties
			.getProperty("news.range.end"));

}

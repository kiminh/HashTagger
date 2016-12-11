/**
 * 
 */
package me.motallebi.hashtagger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * Should be very buggy. Especially the synchronization part. Needs a lot of
 * refactoring.
 * 
 * @author mrmotallebi
 *
 */
public class FileNewsSource implements NewsSource {

	private String filePath = Constants.NEWS_SAVE_LOCATION;
	private boolean download = false;
	private int numToLoad = Constants.NEWS_RANGE_END
			- Constants.NEWS_RANGE_START + 1;
	private List<NewsArticle> newsList = new ArrayList<>(this.numToLoad);
	private volatile boolean loaded = false;

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			Constants.NEWS_DATETIME_FORMAT);

	private static final Pattern NEWS_PATTERN = Pattern
			.compile(Constants.NEWS_FILE_REGEX);

	/**
	 * Create new FileNewsLoader object
	 */
	public FileNewsSource() {
	}

	/**
	 * Create new FileNewsLoader object
	 * 
	 * @param filePath
	 *            Location to store or look for news files
	 */
	public FileNewsSource(String filePath) {
		if (filePath == null || filePath.isEmpty()) {
			// throw new IllegalArgumentException("File path invalid.");
			this.filePath = Constants.NEWS_SAVE_LOCATION;
		} else {
			this.filePath = filePath;
		}
	}

	/**
	 * Create new FileNewsLoader object
	 * 
	 * @param filePath
	 *            Location to store or look fore news files
	 * @param download
	 *            To download or look for files on disk
	 */
	public FileNewsSource(String filePath, boolean download) {
		this(filePath);
		this.download = download;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see me.motallebi.hashtagger.NewsLoaderInterface#loadNews()
	 */
	@Override
	public synchronized void loadNews() {
		this.loaded = false;
		if (this.download) {
			downloadNews();
		}

		File newsFilePath = new File(this.filePath);
		File[] files = newsFilePath.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (name == null)
					return false;
				// TODO: something about this...
				return name.endsWith(".html") || name.endsWith(".htm");
			}
		});

		if (files == null)
			throw new UncheckedIOException(new IOException(
					"Path does not exist"));

		for (File f : files) {
			NewsArticle news;
			try {
				news = parseNewsfile(f, NEWS_PATTERN);
			} catch (NewsLoadException e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
				// Something weird happened. Just return.
				return;
			}
			if (news == null) {
				System.err.println("Skipping file. No news returned for : "
						+ f.getName());
				continue;
			}
			this.newsList.add(news);
		}
		this.loaded = true;
		notifyAll();
	}

	/**
	 * Just a helper method. Replace it with xml/html parser or DOM. Consider
	 * making a new class for it to remove unnecessary parts and normalize news.
	 * 
	 * @param f
	 * @param pattern
	 * @return
	 * @throws NewsLoadException
	 */
	private static NewsArticle parseNewsfile(File f, Pattern pattern)
			throws NewsLoadException {
		if (pattern == null) {
			pattern = Pattern.compile(Constants.NEWS_FILE_REGEX);
		}
		Matcher matcher;
		try {
			matcher = pattern
					.matcher(new String(Files.readAllBytes(f.toPath())));
		} catch (IOException e) {
			throw new NewsLoadException(
					"Problem reading from news from file : " + f.getName(), e);
		}
		if (!matcher.matches())
			return null;
		NewsArticle news = new SimpleNewsArticle();
		try {
			news.setTitle(matcher.group(Constants.NEWS_TITLE_GROUP));
			news.setBody(matcher.group(Constants.NEWS_BODY_GROUP));
			// Yeah this is naive and stupid..
			news.setId(Integer.valueOf(f.getName().split("\\.")[0]));
			// Code to get around the stupid custom date format
			// ReplaceAll can be optimized if a Pattern is created in advance
			String newsDate = matcher.group(Constants.NEWS_TIME_GROUP)
					.replace("a.m.", "AM").replace("p.m.", "PM")
					.replace("June", "Jun.").replace("March", "Mar.")
					.replace("May", "May.").replace("July", "Jul.")
					.replaceAll(" (\\d\\d??) AM", " $1\\:00 AM")
					.replaceAll(" (\\d\\d??) PM", " $1\\:00 PM");
			// System.out.println(f.getName() + "-- " + newsDate);
			Date parsedDate = FileNewsSource.DATE_FORMAT.parse(newsDate);
			news.setDate(parsedDate);
		} catch (ParseException | IllegalStateException
				| IndexOutOfBoundsException e) {
			throw new NewsLoadException("Problem with parsing file : "
					+ f.getName(), e);
		}
		return news;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see me.motallebi.hashtagger.NewsLoaderInterface#loadNews(java.util.Date)
	 */
	@Override
	public synchronized void loadNews(Date date) {
		throw new UnsupportedOperationException(
				"Not supported. Can't read files for specified dates.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * me.motallebi.hashtagger.NewsLoaderInterface#loadNews(java.lang.Integer)
	 */
	@Override
	public synchronized void loadNews(Integer count) {
		this.numToLoad = count;
		loadNews();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see me.motallebi.hashtagger.NewsLoaderInterface#loadNews(java.util.Date,
	 * java.lang.Integer)
	 */
	@Override
	public synchronized void loadNews(Date date, Integer count) {
		throw new UnsupportedOperationException(
				"Not supported. Can't read files for specified dates.");
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
	 * @see java.lang.Iterable#iterator() It should behave strangely while the
	 * List is being loaded and iterator is called by another thread.
	 * Whatever...
	 */
	@Override
	public Iterator<NewsArticle> iterator() {
		return newsList.iterator();
	}

	/**
	 * Helper method for downloading and saving news files.
	 */
	private void downloadNews() {
		ExecutorService threadPool = Executors
				.newFixedThreadPool(Constants.CONCURRENT_DOWNLOADS);
		for (int newsId = Constants.NEWS_RANGE_START; newsId
				- Constants.NEWS_RANGE_START < this.numToLoad; newsId++) {
			// URL encoded?
			String downloadUrl = String.format(Constants.NEWS_URL_PATTERN,
					newsId);
			// Must make sure the URL doesn't end with a slash...
			// If there's a problem in Constants.NEWS_URL_PATTERN , throw
			// exception on first iteration and don't continue with other
			// threads
			// Will files being downloaded be closed if exception occurs here?
			String filename = downloadUrl.substring(downloadUrl
					.lastIndexOf('/') + 1);
			filename = "news-" + filename;
			if (!filename.endsWith(".htm") && !filename.endsWith(".html"))
				filename += ".html";
			// Should use a better way to build file path and create dir
			// eg, Paths.get()
			FileDownloader fd = new FileDownloader(this.filePath
					+ File.separator + filename, downloadUrl);
			threadPool.submit(fd);
		}
		try {
			threadPool.shutdown();
			boolean successful = threadPool.awaitTermination(this.numToLoad
					* 500 / Constants.CONCURRENT_DOWNLOADS + 5000,
					TimeUnit.MILLISECONDS);
			if (!successful)
				throw new InterruptedException(
						"Not all files were downloaded. ");
		} catch (InterruptedException e) {
			// Don't mind if all files weren't downloaded
			System.err.println(e.getMessage());
			e.printStackTrace();
		}

	}

	/**
	 * Method to notify that the data has loaded.
	 */
	public synchronized void waitUntilLoad() {
		while (!this.loaded) {
			System.out.println("Waiting for news to load");
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
	}

	/**
	 * @author mrmotallebi
	 *
	 */
	static class FileDownloader implements Runnable {
		private File downloadedFile;
		private URL downloadUrl;

		public FileDownloader(String filePath, String downloadUrl) {
			this.downloadedFile = new File(filePath);
			try {
				this.downloadUrl = new URL(downloadUrl);
			} catch (MalformedURLException e) {
				throw new IllegalArgumentException("Malformed URL", e);
			}
		}

		/**
		 * @throws IOException
		 */
		protected void download() throws IOException {
			String inputLine = null;
			try (BufferedReader reader = new BufferedReader(
					new InputStreamReader(this.downloadUrl.openStream()));
					BufferedWriter writer = new BufferedWriter(new FileWriter(
							this.downloadedFile))) {
				// TODO: Replace with logging
				System.out.println("Thread No. "
						+ Thread.currentThread().getId() + " downloading file "
						+ this.downloadUrl);
				while ((inputLine = reader.readLine()) != null) {
					writer.write(inputLine);
				}
			} catch (IOException e) {
				// TODO: to be replaced with logging
				System.err
						.println("Error reading from server and writing to file.");
				System.err.println("Last line read was : " + inputLine);
				throw e;
			}

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			try {
				this.download();
			} catch (IOException e) {
				// TODO: Handle this in a better way
				System.err.println(e.getMessage());
				throw new RuntimeException("Problem while downloading.", e);
			}

		}

	}

	public static void main(String[] args) {

		FileNewsSource fnl = new FileNewsSource(null, false);

		new Thread() {
			public void run() {
				fnl.waitUntilLoad();
				for (NewsArticle na : fnl) {
					System.out.println(na.getTitle());
				}
			};
		}.start();
		try {
			Thread.sleep(1000l);
			fnl.loadNews();
		} catch (InterruptedException e) {
		}
		fnl.waitUntilLoad();
		PredefinedKeyPhraseExtractor pkpe = PredefinedKeyPhraseExtractor
				.getInstance();
		TwoShingleKeyPhraseExtractor tskpe = TwoShingleKeyPhraseExtractor
				.getInstance();
		for (NewsArticle news : fnl) {
			List<String> result1 = pkpe.extractKeyPhrases(news);
			List<String> result2 = tskpe.extractKeyPhrases(news);
			System.out.println(Arrays.toString(result1.toArray()));
			System.out.println(Arrays.toString(result2.toArray()));
			System.out.println(news.getDate());
		}

	}

}

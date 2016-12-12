/**
 * 
 */
package me.motallebi.hashtagger;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import twitter4j.HashtagEntity;
import twitter4j.Status;

/**
 * @author mhmotallebi
 *
 */
public class SimpleFeatureComputer implements FeatureComputer {

	private List<Float> featureList;
	private static final int numberOfFeatures = 9;
	private NewsArticle article;
	private List<NewsArticle> articles;
	private HashtagFinder hf;
	private FileTweetSource fts;
	private int minArticleSpecificTweetBagSize;
	private int maxArticleSpecificTweetBagSize;
	private static int maxTweetBagSize = -1;
	private static int minTweetBagSize = -1;
	private float maxFreq;
	private List<HashtagEntity> allHashtagsOfArticle;
	private Set<String> bagOfWords;
	private static Duration lambda = Duration.ofHours(2);
	private static Duration gamma = Duration.ofHours(6);
	private static Duration lambda2 = Duration.ofHours(4);
	private static final Pattern SPACE_PATTERN = Pattern.compile("\\s");

	public static enum DurationEnum {
		LAMBDA, GAMMA, LAMBDA2
	}

	public SimpleFeatureComputer(NewsArticle article, FileTweetSource fts,
			FileNewsSource fns, HashtagFinder hashtagFinder,
			List<HashtagEntity> hashtags) {
		// this.featureList = new ArrayList<Float>(numberOfFeatures);
		this.article = article;
		this.fts = fts;
		this.hf = hashtagFinder;
		this.allHashtagsOfArticle = hashtags;
		this.articles = getAllArticles(fns);
		this.maxFreq = calcMaxFreq();
		this.maxArticleSpecificTweetBagSize = calcArticleSpecificMaxTweetBagSize();
		this.minArticleSpecificTweetBagSize = calcArticleSpecificMinTweetBagSize();
		calcMaxTweetBagSize();// this.maxTweetBagSize
		calcMinTweetBagSize();// this.minTweetBagSize
		// this.NEWSTIME = this.article.getDate();
	}

	private int calcArticleSpecificMaxTweetBagSize() {
		int maxBagSize = 0;
		for (HashtagEntity hashtag : allHashtagsOfArticle) {
			Status[] tweets = hf.getStatusWithHT(hashtag.getText());
			// We have a hashtag and all of its tweets. Now, we want only tweets
			// that are part related to this article
			// get keyphrase of this article
			List<String> keyPhrases = PredefinedKeyPhraseExtractor
					.getInstance().extractKeyPhrases(this.article);
			// for each keyphrase, find related tweets
			List<List<Long>> allValidIDs = new ArrayList<List<Long>>();
			for (String kph : keyPhrases) {
				String[] parts;
				if ((parts = SPACE_PATTERN.split(kph)).length < 2) {
					continue;
				}
				String part1 = parts[0];
				String part2 = parts[1];
				List<Long> validIDs = new ArrayList<Long>();
				for (Status tweet : tweets) {
					if (tweet.getText().contains(part1)
							&& tweet.getText().contains(part2))
						validIDs.add(tweet.getId());// I only keep IDs to reduce
													// space used.
				}
				allValidIDs.add(validIDs);
			}
			// find union of tweets related to keyphrases
			List<Long> unionIDs = new ArrayList<Long>();
			for (List<Long> v : allValidIDs) {
				unionIDs.addAll(v);
			}
			HashSet<Long> set = new HashSet<Long>();
			set.addAll(unionIDs);
			if (maxBagSize < set.size())
				maxBagSize = set.size();
		}
		return maxBagSize;
	}

	private int calcArticleSpecificMinTweetBagSize() {
		int minBagSize = 9999;
		for (HashtagEntity hashtag : allHashtagsOfArticle) {
			Status[] tweets = hf.getStatusWithHT(hashtag.getText());
			// int count = 0;
			// for(Status tweet: tweets){
			// if()
			// count++;
			// }
			// if(count>maxBagSize)
			// maxBagSize = count;
			// We have a hashtag and all of its tweets. Now, we want only tweets
			// that are part related to this article
			// get keyphrase of this article
			List<String> keyPhrases = PredefinedKeyPhraseExtractor
					.getInstance().extractKeyPhrases(this.article);
			// for each keyphrase, find related tweets
			List<List<Long>> allValidIDs = new ArrayList<List<Long>>();
			for (String kph : keyPhrases) {
				String[] parts;
				if ((parts = SPACE_PATTERN.split(kph)).length < 2) {
					continue;
				}
				String part1 = parts[0];
				String part2 = parts[1];
				List<Long> validIDs = new ArrayList<Long>();
				for (Status tweet : tweets) {
					if (tweet.getText().contains(part1)
							&& tweet.getText().contains(part2))
						validIDs.add(tweet.getId());// I only kep IDs to reduce
													// space used.
				}
				allValidIDs.add(validIDs);
			}
			// find union of tweets related to keyphrases
			List<Long> unionIDs = new ArrayList<Long>();
			for (List<Long> v : allValidIDs) {
				unionIDs.addAll(v);
			}
			HashSet<Long> set = new HashSet<Long>();
			set.addAll(unionIDs);
			if (minBagSize > set.size())
				minBagSize = set.size();
		}
		return minBagSize;
	}

	private void calcMaxTweetBagSize() {
		if (maxTweetBagSize != -1)
			return;
		maxTweetBagSize = hf.getMaxTweetsForHashtags();
	}

	private void calcMinTweetBagSize() {
		if (minTweetBagSize != -1)
			return;
		minTweetBagSize = hf.getMinTweetsForHashtags();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see me.motallebi.hashtagger.FeatureComputer#LS(me.motallebi.hashtagger.
	 * NewsArticle, me.motallebi.hashtagger.Hashtag, java.util.Date)
	 */
	@Override
	public void LS(NewsArticle article, HashtagEntity h, DurationEnum lambda2,
			List<NewsArticle> articles) {
		List<Status> tweets = new ArrayList<Status>();
		List<Float> articleVector = new ArrayList<Float>();
		List<Float> hashtagsVector = new ArrayList<Float>();
		List<String> strArticles = new ArrayList<String>();
		// find all tweets of article a that contain this hashtag in this time.
		tweets = findArticleSpecificTweets(article, h, lambda2);

		StringBuilder tweetsText = new StringBuilder();// convert list of tweets
														// to a string
		// TODO: I think it needs removal of stopwords!
		for (Status t : tweets) {
			tweetsText.append(t.getText()); // text of one tweet.
			tweetsText.append(" ");
		}
		// create list of bag-of-words that contains terms of article and all
		// tweets
		Set<String> bagOfWords = createBagOfWords(article, tweets);
		// create list of stings of articles -needed for idf-
		for (NewsArticle ns : articles) {
			strArticles.add(ns.getTitle() + " " + ns.getBody());
		}
		// compute vectors of tf-idf for each term
		for (String str : bagOfWords) {
			articleVector.add(termFrequency(str, article.getTitle() + " "
					+ article.getBody())
					* inverseDocumentFrequency(str, strArticles));
			hashtagsVector.add(termFrequency(str, tweetsText.toString()));
		}
		try {
			this.featureList
					.add(cosineSimilarity(articleVector, hashtagsVector));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception in Cosine Similarity!");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see me.motallebi.hashtagger.FeatureComputer#LF(me.motallebi.hashtagger.
	 * NewsArticle, me.motallebi.hashtagger.Hashtag, java.util.Date)
	 */
	@Override
	public void LF(NewsArticle a, HashtagEntity h, DurationEnum lambda) {
		// find all tweets that contain this hashtag in this time.
		List<Status> tweets = findArticleSpecificTweets(a, h, lambda);
		if (maxArticleSpecificTweetBagSize == minArticleSpecificTweetBagSize)
			featureList.add(0.0f);
		else
			featureList
					.add((float) (tweets.size() - this.minArticleSpecificTweetBagSize)
							/ (maxArticleSpecificTweetBagSize - minArticleSpecificTweetBagSize));
	}

	/*
	 * (non`-Javadoc)
	 * 
	 * @see me.motallebi.hashtagger.FeatureComputer#GS(me.motallebi.hashtagger.
	 * NewsArticle, me.motallebi.hashtagger.Hashtag, java.util.Date)
	 */
	@Override
	public void GS(NewsArticle article, HashtagEntity h, DurationEnum gamma) {
		List<Float> articleVector = new ArrayList<Float>();
		List<Float> hashtagsVector = new ArrayList<Float>();
		List<String> strArticles = new ArrayList<String>();

		// find all tweets of this article that contain this hashtag in this
		// time.
		List<Status> tweets = findTweets(h, gamma);
		// convert list of tweets to list of strings
		StringBuilder tweetsText = new StringBuilder();
		for (Status t : tweets) {
			tweetsText.append(t.getText());
			tweetsText.append(" ");// text of one tweet.
		}
		// create list of bag-of-words that contains terms of article and all
		// tweets
		Set<String> bagOfWords = createBagOfWords(article, tweets);
		// create list of stings of articles -needed for idf-
		for (NewsArticle ns : this.articles) {
			strArticles.add(ns.getTitle() + " " + ns.getBody());
		}
		// compute vectors of tf-idf for each term
		for (String str : bagOfWords) {
			articleVector.add(termFrequency(str, article.getTitle() + " "
					+ article.getBody())
					* inverseDocumentFrequency(str, strArticles));
			hashtagsVector.add(termFrequency(str, tweetsText.toString()));
		}
		try {
			this.featureList
					.add(cosineSimilarity(articleVector, hashtagsVector));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception in Cosine Similarity!");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * me.motallebi.hashtagger.FeatureComputer#GF(me.motallebi.hashtagger.Hashtag
	 * , java.util.Date)
	 */
	@Override
	public void GF(HashtagEntity h, DurationEnum gamma) {
		List<Status> tweets = new ArrayList<Status>();
		tweets = findTweets(h, gamma);// fin all tweets that contain this
										// hashtag in this time.
		featureList.add((float) (tweets.size() - minTweetBagSize)
				/ (maxTweetBagSize - minTweetBagSize));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see me.motallebi.hashtagger.FeatureComputer#TR(me.motallebi.hashtagger.
	 * NewsArticle, me.motallebi.hashtagger.Hashtag, java.util.Date)
	 */
	@Override
	public void TR(NewsArticle a, HashtagEntity h, DurationEnum lambda) {
		List<Status> tweetsNewer = new ArrayList<Status>();
		List<Status> tweetsOlder = new ArrayList<Status>();

		// find all tweets that contain this hashtag in this time.
		tweetsNewer = findArticleSpecificTweets(a, h, lambda);
		// find all tweets of article a that contain this hashtag in this time.
		tweetsOlder = findArticleSpecificTweets(a, h, DurationEnum.LAMBDA2);
		if (tweetsOlder.size() == tweetsNewer.size())
			featureList.add(0.0f);
		else
			featureList.add((float) (2 * tweetsNewer.size() - tweetsOlder
					.size()) / (tweetsOlder.size() - tweetsNewer.size()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see me.motallebi.hashtagger.FeatureComputer#EG()
	 */
	@Override
	public void EG(Float TR, NewsArticle article, HashtagEntity h,
			DurationEnum lambda3) {// TODO
		// TODO time frame is unclear for me!
		// temporary:///////////////
		featureList.add(1.0f);
		if (true)
			return;
		// /////////////////////////
		/*
		 * List<Status> tweets = new ArrayList<Status>(); tweets =
		 * findArticleSpecificTweets(article, h, lambda3);
		 * featureList.add((float) (1.0 + TR) * tweets.size());
		 */
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see me.motallebi.hashtagger.FeatureComputer#HE(me.motallebi.hashtagger.
	 * NewsArticle, me.motallebi.hashtagger.Hashtag)
	 */
	@Override
	public void HE(NewsArticle a, HashtagEntity h) {// Hashtag in Headline
		float inText = 0.0f;
		String temp = a.getTitle() + " " + a.getBody();
		temp = temp.replaceAll("\\s+", "");
		if (temp.contains(h.getText()))
			inText = 1.0f;
		featureList.add(inText);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see me.motallebi.hashtagger.FeatureComputer#UR(me.motallebi.hashtagger.
	 * NewsArticle, me.motallebi.hashtagger.Hashtag, java.util.Date)
	 */
	@Override
	public void UR(NewsArticle article, HashtagEntity h, DurationEnum lambda) {
		List<Status> tweets = new ArrayList<Status>();
		HashSet<Long> hs = new HashSet<Long>();
		tweets = findArticleSpecificTweets(article, h, lambda);
		for (Status tweet : tweets) {
			hs.add(tweet.getUser().getId());
		}
		featureList.add((float) hs.size() / tweets.size());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see me.motallebi.hashtagger.FeatureComputer#UC(me.motallebi.hashtagger.
	 * NewsArticle, me.motallebi.hashtagger.Hashtag, java.util.Date)
	 */
	@Override
	public void UC(NewsArticle article, HashtagEntity h, DurationEnum lambda) {
		List<Status> tweets = new ArrayList<Status>();
		// HashSet<String> hs = new HashSet<String>();
		tweets = findArticleSpecificTweets(article, h, lambda);
		int maxFollowers = 0;
		for (Status tweet : tweets) {
			int temp = tweet.getUser().getFollowersCount();
			if (temp > maxFollowers)
				maxFollowers = temp;
		}
		featureList.add((float) maxFollowers);
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////////////

	private Float termFrequency(String w, String text) {
		return (float) (0.4 + ((1 - 0.4) * wordFreq(w, text)) / (this.maxFreq));
	}

	private float inverseDocumentFrequency(String w, List<String> strings) {
		int corpusSize = strings.size();
		int count = 0;
		w = w.toLowerCase();
		for (String ns : strings) {
			if (ns.toLowerCase().contains(w))
				count++;
		}
		if (count == 0)
			return 0.0f;
		return (float) Math.log10(corpusSize / count);
	}

	private float wordFreq(String t, String text) {
		int count = StringUtils.countMatches(text.toLowerCase(),
				t.toLowerCase());
		int totalCount = SPACE_PATTERN.split(text).length;
		return (float) count / totalCount;
	}

	private Set<String> getBagOfWords() {
		if (this.bagOfWords != null)
			return this.bagOfWords;
		this.bagOfWords = createBagOfWords(this.fts.getTweetList());
		return this.bagOfWords;
	}

	private Set<String> createBagOfWords(List<Status> tweets) {
		Set<String> bag = new HashSet<>();
		for (Status t : tweets) {
			for (String str : SPACE_PATTERN.split(t.getText())) {
				if (str.length() > 1)
					bag.add(str);
			}
		}
		return bag;
	}

	@SuppressWarnings("unused")
	private Set<String> createBagOfWords(NewsArticle article) {
		Set<String> bag = new HashSet<String>();
		String temp = article.getTitle() + " " + article.getBody();
		for (String str : SPACE_PATTERN.split(temp)) {
			if (str.length() > 1)
				bag.add(str);
		}
		// Add all words from default tweets
		bag.addAll(this.getBagOfWords());
		return bag;
	}

	private Set<String> createBagOfWords(NewsArticle article,
			List<Status> tweets) {
		Set<String> bag = new HashSet<String>();
		String temp = article.getTitle() + " " + article.getBody();
		for (String str : SPACE_PATTERN.split(temp)) {
			if (str.length() > 1)
				bag.add(str);
		}
		for (Status t : tweets) {
			for (String str : SPACE_PATTERN.split(t.getText())) {
				if (str.length() > 1)
					bag.add(str);
			}
		}
		return bag;
	}

	private float cosineSimilarity(List<Float> a, List<Float> b)
			throws IllegalArgumentException {
		float sum = 0.0f;
		if (a.size() != b.size())
			throw new IllegalArgumentException(
					"Size of vectors in cosineSimilarity does not match");
		int size = a.size();
		if (size == 0)
			return 0;
		for (int i = 0; i < size; i++) {
			sum += a.get(i) * b.get(i);
		}
		return sum / (size * size);
	}

	public void computeFeatures(HashtagEntity hashtag) {
		this.featureList = new ArrayList<Float>(numberOfFeatures);
		LS(this.article, hashtag, DurationEnum.LAMBDA, this.articles);
		LF(this.article, hashtag, DurationEnum.LAMBDA);
		GS(this.article, hashtag, DurationEnum.GAMMA);
		GF(hashtag, DurationEnum.GAMMA);
		TR(this.article, hashtag, DurationEnum.LAMBDA);
		EG(this.featureList.get(4), this.article, hashtag, DurationEnum.LAMBDA);// TODO
		HE(this.article, hashtag);
		UR(this.article, hashtag, DurationEnum.LAMBDA);
		UC(this.article, hashtag, DurationEnum.LAMBDA);
	}

	public List<Float> getFeaturesList() {
		return this.featureList;
	}

	private float calcMaxFreq() {
		// calculate maximum frequency for all terms we have so we can have the
		// maximum in the beginning.
		String text = this.article.getTitle().toLowerCase() + " "
				+ this.article.getBody().toLowerCase();
		String[] words = SPACE_PATTERN.split(text);
		int max = -1;
		for (String word : words) {
			int t = StringUtils.countMatches(text, word);
			if (t > max) {
				max = t;
			}
		}
		if (words.length == 0)
			return 0;
		return (float) max / words.length;
	}

	private boolean isTweetRelatedToArticle(NewsArticle article, Status tweet) {
		List<String> keyPhrases = PredefinedKeyPhraseExtractor.getInstance()
				.extractKeyPhrases(article);
		for (String kph : keyPhrases) {
			String[] parts;
			if ((parts = SPACE_PATTERN.split(kph)).length < 2) {
				continue;
			}
			return tweet.getText().contains(parts[0])
					&& tweet.getText().contains(parts[1]);
		}
		return false;
	}

	private List<Status> findArticleSpecificTweets(NewsArticle article2,
			HashtagEntity h, DurationEnum de) {
		Status[] tweets = hf.getStatusWithHT(h.getText());
		List<Status> validTweets = new ArrayList<>();
		for (Status tweet : tweets) {
			LocalDateTime timeTweet = LocalDateTime.ofInstant(tweet
					.getCreatedAt().toInstant(), ZoneId.of("GMT"));
			// System.out.println(this.article.getDate());
			LocalDateTime timeArticle = LocalDateTime.ofInstant(this.article
					.getDate().toInstant(), ZoneId.of("GMT"));
			Duration duration = Duration.between(timeTweet, timeArticle);
			if (isTweetRelatedToArticle(this.article, tweet)) {
				switch (de) {
				case GAMMA:
					if (duration.compareTo(gamma) > 0)
						validTweets.add(tweet);
					break;
				case LAMBDA:
					if (duration.compareTo(lambda) > 0)
						validTweets.add(tweet);
					break;
				case LAMBDA2:
					if (duration.compareTo(lambda2) > 0)
						validTweets.add(tweet);
					break;
				}
			}
		}
		return validTweets;
	}

	private List<Status> findTweets(HashtagEntity h, DurationEnum de) {
		Status[] tweets = hf.getStatusWithHT(h.getText());
		List<Status> validTweets = new ArrayList<Status>();
		for (Status tweet : tweets) {
			LocalDateTime timeTweet = LocalDateTime.ofInstant(tweet
					.getCreatedAt().toInstant(), ZoneId.of("GMT"));
			LocalDateTime timeArticle = LocalDateTime.ofInstant(this.article
					.getDate().toInstant(), ZoneId.of("GMT"));
			Duration duration = Duration.between(timeTweet, timeArticle);
			switch (de) {
			case GAMMA:
				if (duration.compareTo(gamma) > 0)
					validTweets.add(tweet);
				break;
			case LAMBDA:
				if (duration.compareTo(lambda) > 0)
					validTweets.add(tweet);
				break;
			case LAMBDA2:
				if (duration.compareTo(lambda2) > 0)
					validTweets.add(tweet);
				break;
			}
		}
		return validTweets;
	}

	private List<NewsArticle> getAllArticles(FileNewsSource fns) {
		List<NewsArticle> nas = new ArrayList<NewsArticle>();
		Iterator<NewsArticle> f = fns.iterator();
		while (f.hasNext()) {
			nas.add(f.next());
		}
		return nas;
	}
}

/*
 * lambda o gamma ro interval begiram. ke gamma masalan 24 saat bashe o lambda 4
 * hala time ha ro moghayese konam, male news ro ba tweet ha va interval besazam
 * o ba ina moghayese konam -- az dadash bekham ke bara newsArticle time besaze.
 * dar natije beshe moghayese kard shayad lazem bashe ke gamma ro kheili vaghta
 * infinity ina begiram!
 */
/**
 * 
 */
package me.motallebi.hashtagger;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

	private static final int numberOfFeatures = 9;
	private List<NewsArticle> articles;
	private HashtagFinder hf;
	// private FileTweetSource fts;
	private static Integer maxTweetBagSize;
	private static Integer minTweetBagSize;
	private static Duration lambda = Duration.ofHours(4);
	private static Duration gamma = Duration.ofHours(24);
	private static Duration lambda2 = Duration.ofHours(6);
	private static final Pattern SPACE_PATTERN = Pattern.compile("\\W+");
	private Map<String, Integer> wordOccurences;
	private final int corpusSize;
	private int maxArticleSpecificTweetBagSize = 1;
	private int minArticleSpecificTweetBagSize = 1;

	public static enum DurationEnum {
		LAMBDA, GAMMA, LAMBDA2
	}

	public SimpleFeatureComputer(FileNewsSource fns, HashtagFinder hashtagFinder) {
		// this.featureList = new ArrayList<Float>(numberOfFeatures);
		// this.article = article;
		// this.fts = fts;
		this.hf = hashtagFinder;
		this.articles = getAllArticles(fns);

		calcMaxTweetBagSize();// this.maxTweetBagSize
		calcMinTweetBagSize();// this.minTweetBagSize
		// this.NEWSTIME = this.article.getDate();
		computeInverseDocumentFrequencyMap();
		this.corpusSize = this.articles.size();
	}

	private void computeInverseDocumentFrequencyMap() {
		List<String> strArticles = new ArrayList<>(this.articles.size());
		for (NewsArticle ns : this.articles) {
			strArticles.add(ns.getTitle() + " " + ns.getBody());
		}
		this.wordOccurences = Util.computeWordOccurences(strArticles, false);
	}

	private int calcArticleSpecificMaxTweetBagSize(NewsArticle article,
			List<HashtagEntity> allHashtagsOfArticle) {
		int maxBagSize = 0;
		for (HashtagEntity hashtag : allHashtagsOfArticle) {
			Status[] tweets = hf.getStatusWithHT(hashtag.getText());
			// We have a hashtag and all of its tweets. Now, we want only tweets
			// that are part related to this article
			// get keyphrase of this article
			List<String> keyPhrases = PredefinedKeyPhraseExtractor
					.getInstance().extractKeyPhrases(article);
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

	private int calcArticleSpecificMinTweetBagSize(NewsArticle article,
			List<HashtagEntity> allHashtagsOfArticle) {
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
					.getInstance().extractKeyPhrases(article);
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
		if (maxTweetBagSize != null)
			return;
		maxTweetBagSize = hf.getMaxTweetsForHashtags();
	}

	private void calcMinTweetBagSize() {
		if (minTweetBagSize != null)
			return;
		minTweetBagSize = hf.getMinTweetsForHashtags();
	}

	public void prepareForArticle(NewsArticle na, List<HashtagEntity> hashtags) {
		this.minArticleSpecificTweetBagSize = calcArticleSpecificMinTweetBagSize(
				na, hashtags);
		this.maxArticleSpecificTweetBagSize = calcArticleSpecificMaxTweetBagSize(
				na, hashtags);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see me.motallebi.hashtagger.FeatureComputer#LS(me.motallebi.hashtagger.
	 * NewsArticle, me.motallebi.hashtagger.Hashtag, java.util.Date)
	 */
	@Override
	public float LS(NewsArticle article, HashtagEntity h, DurationEnum lambda2,
			List<NewsArticle> articles) {
		// System.out.print("LS...");
		List<Status> tweets = new ArrayList<Status>();
		List<Float> articleVector = new ArrayList<Float>();
		List<Float> hashtagsVector = new ArrayList<Float>();

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

		// precompute max frequency for article bag and tweets
		float maxFreqArticle = calcMaxFreq(article.getTitle() + " "
				+ article.getBody());
		float maxFreqHashtag = calcMaxFreq(tweetsText.toString());

		// compute vectors of tf-idf for each term
		for (String str : bagOfWords) {
			articleVector.add(termFrequency(str, article.getTitle() + " "
					+ article.getBody(), maxFreqArticle)
					* inverseDocumentFrequency(str));
			hashtagsVector.add(termFrequency(str, tweetsText.toString(),
					maxFreqHashtag));
		}
		try {
			return cosineSimilarity(articleVector, hashtagsVector);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			System.out.println("Exception in Cosine Similarity!");
		}
		return 0.0f;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see me.motallebi.hashtagger.FeatureComputer#LF(me.motallebi.hashtagger.
	 * NewsArticle, me.motallebi.hashtagger.Hashtag, java.util.Date)
	 */
	@Override
	public float LF(NewsArticle article, HashtagEntity h, DurationEnum lambda) {
		// System.out.print("LF...");
		// find all tweets that contain this hashtag in this time.
		List<Status> tweets = findArticleSpecificTweets(article, h, lambda);
		if (maxArticleSpecificTweetBagSize == minArticleSpecificTweetBagSize) {
			return 0.0f;
		} else
			return (float) (tweets.size() - minArticleSpecificTweetBagSize)
					/ (maxArticleSpecificTweetBagSize - minArticleSpecificTweetBagSize);
	}

	/*
	 * (non`-Javadoc)
	 * 
	 * @see me.motallebi.hashtagger.FeatureComputer#GS(me.motallebi.hashtagger.
	 * NewsArticle, me.motallebi.hashtagger.Hashtag, java.util.Date)
	 */
	@Override
	public float GS(NewsArticle article, HashtagEntity h, DurationEnum gamma) {
		// System.out.print("GS...");
		List<Float> articleVector = new ArrayList<Float>();
		List<Float> hashtagsVector = new ArrayList<Float>();

		// find all tweets of this article that contain this hashtag in this
		// time.
		List<Status> tweets = findTweets(h, article.getDate(), gamma);
		// convert list of tweets to list of strings
		StringBuilder tweetsText = new StringBuilder();
		for (Status t : tweets) {
			tweetsText.append(t.getText());
			tweetsText.append(" ");// text of one tweet.
		}
		// create list of bag-of-words that contains terms of article and all
		// tweets
		Set<String> bagOfWords = createBagOfWords(article, tweets);

		// precompute max frequency for article bag and tweets
		float maxFreqArticle = calcMaxFreq(article.getTitle() + " "
				+ article.getBody());
		float maxFreqHashtag = calcMaxFreq(tweetsText.toString());

		// compute vectors of tf-idf for each term
		for (String str : bagOfWords) {
			articleVector.add(termFrequency(str, article.getTitle() + " "
					+ article.getBody(), maxFreqArticle)
					* inverseDocumentFrequency(str));
			hashtagsVector.add(termFrequency(str, tweetsText.toString(),
					maxFreqHashtag));
		}
		try {
			return cosineSimilarity(articleVector, hashtagsVector);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			System.out.println("Exception in Cosine Similarity!");
		}
		return 0.0f;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * me.motallebi.hashtagger.FeatureComputer#GF(me.motallebi.hashtagger.Hashtag
	 * , java.util.Date)
	 */
	@Override
	public float GF(NewsArticle article, HashtagEntity h, DurationEnum gamma) {
		// System.out.print("GF...");
		List<Status> tweets = new ArrayList<Status>();
		tweets = findTweets(h, article.getDate(), gamma);// fin all tweets that
		// contain this
		// hashtag in this time.
		return (float) (tweets.size() - minTweetBagSize)
				/ (maxTweetBagSize - minTweetBagSize);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see me.motallebi.hashtagger.FeatureComputer#TR(me.motallebi.hashtagger.
	 * NewsArticle, me.motallebi.hashtagger.Hashtag, java.util.Date)
	 */
	@Override
	public float TR(NewsArticle a, HashtagEntity h, DurationEnum lambda) {
		// System.out.print("TR...");
		List<Status> tweetsNewer = new ArrayList<Status>();
		List<Status> tweetsOlder = new ArrayList<Status>();

		// find all tweets that contain this hashtag in this time.
		tweetsNewer = findArticleSpecificTweets(a, h, lambda);
		// find all tweets of article a that contain this hashtag in this time.
		tweetsOlder = findArticleSpecificTweets(a, h, DurationEnum.LAMBDA2);
		if (tweetsOlder.size() == tweetsNewer.size())
			return 0.0f;
		else
			return (float) (2 * tweetsNewer.size() - tweetsOlder.size())
					/ (tweetsOlder.size() - tweetsNewer.size());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see me.motallebi.hashtagger.FeatureComputer#EG()
	 */
	@Override
	public float EG(NewsArticle article, HashtagEntity h, DurationEnum lambda3,
			Float TR) {// TODO
		// System.out.print("EG...");
		// TODO time frame is unclear for me!
		// temporary:///////////////
		return 1.0f;
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
	public float HE(NewsArticle a, HashtagEntity h) {// Hashtag in Headline
		// System.out.print("HE...");
		float inText = 0.0f;
		String temp = a.getTitle() + " " + a.getBody();
		temp = SPACE_PATTERN.matcher(temp).replaceAll(" ");
		if (temp.contains(h.getText()))
			inText = 1.0f;
		return inText;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see me.motallebi.hashtagger.FeatureComputer#UR(me.motallebi.hashtagger.
	 * NewsArticle, me.motallebi.hashtagger.Hashtag, java.util.Date)
	 */
	@Override
	public float UR(NewsArticle article, HashtagEntity h, DurationEnum lambda) {
		// System.out.print("UR...");
		List<Status> tweets = new ArrayList<Status>();
		HashSet<Long> hs = new HashSet<Long>();
		tweets = findArticleSpecificTweets(article, h, lambda);
		if (tweets.size() == 0)
			return 0.0f;
		for (Status tweet : tweets) {
			hs.add(tweet.getUser().getId());
		}
		return (float) hs.size() / tweets.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see me.motallebi.hashtagger.FeatureComputer#UC(me.motallebi.hashtagger.
	 * NewsArticle, me.motallebi.hashtagger.Hashtag, java.util.Date)
	 */
	@Override
	public float UC(NewsArticle article, HashtagEntity h, DurationEnum lambda) {
		// System.out.print("UC...");
		List<Status> tweets = new ArrayList<Status>();
		// HashSet<String> hs = new HashSet<String>();
		tweets = findArticleSpecificTweets(article, h, lambda);
		int maxFollowers = 0;
		for (Status tweet : tweets) {
			int temp = tweet.getUser().getFollowersCount();
			if (temp > maxFollowers)
				maxFollowers = temp;
		}
		return (float) maxFollowers;
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////////////

	public float[] computeFeatures(NewsArticle article, HashtagEntity hashtag) {
		float[] featureArray = new float[numberOfFeatures];
		featureArray[0] = LS(article, hashtag, DurationEnum.LAMBDA,
				this.articles);
		featureArray[1] = LF(article, hashtag, DurationEnum.LAMBDA);
		featureArray[2] = GS(article, hashtag, DurationEnum.GAMMA);
		featureArray[3] = GF(article, hashtag, DurationEnum.GAMMA);
		featureArray[4] = TR(article, hashtag, DurationEnum.LAMBDA);
		featureArray[5] = EG(article, hashtag, DurationEnum.LAMBDA,
				featureArray[4]);// TODO
		featureArray[6] = HE(article, hashtag);
		featureArray[7] = UR(article, hashtag, DurationEnum.LAMBDA);
		featureArray[8] = UC(article, hashtag, DurationEnum.LAMBDA);
		return featureArray;
	}

	@SuppressWarnings("unused")
	private float termFrequency(String word, String text) {
		float maxFreq = calcMaxFreq(text);
		if (maxFreq == 0.0f)
			return 0.0f;
		float wf = wordFreq(word, text);
		float retValue = (float) (0.4 + ((1 - 0.4) * wf) / (maxFreq));
		return retValue;
	}

	private float termFrequency(String word, String text, float maxFreq) {
		if (maxFreq == 0.0f)
			return 0.0f;
		float wf = wordFreq(word, text);
		float retValue = (float) (0.4 + ((1 - 0.4) * wf) / (maxFreq));
		return retValue;
	}

	private float inverseDocumentFrequency(String w) {
		Integer count = this.wordOccurences.get(w);
		if (count == null || count == 0)
			return 0.0f;
		if (corpusSize < count) {
			// How can this be possible?
			return 1.0f;
		}
		float retValue = (float) Math.log10((float) corpusSize / count);
		return retValue;
	}

	private float wordFreq(String word, String text) {
		int count = StringUtils.countMatches(text.toLowerCase(),
				word.toLowerCase());
		int totalCount = SPACE_PATTERN.split(text).length;
		return (float) count / totalCount;
	}

	@SuppressWarnings("unused")
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
		if (a.size() != b.size())
			throw new IllegalArgumentException(
					"Size of vectors in cosineSimilarity does not match");
		int size = a.size();
		if (size == 0)
			return 0;
		float sum = 0.0f;
		float aSum = 0.0f;
		float bSum = 0.0f;
		float a_i, b_i;
		for (int i = 0; i < size; i++) {
			a_i = a.get(i);
			b_i = b.get(i);
			sum += a_i * b_i;
			aSum += a_i * a_i;
			bSum += b_i * b_i;
		}
		return (float) (sum / Math.sqrt(aSum * bSum));
	}

	private float calcMaxFreq(String text) {
		// calculate maximum frequency for all terms we have so we can have the
		// maximum in the beginning.
		Map<String, Integer> countMap = new HashMap<>();
		String[] words = SPACE_PATTERN.split(text);
		for (String word : words) {
			int count = countMap.getOrDefault(word, 0);
			countMap.put(word, ++count);
		}
		int max = Collections.max(countMap.values());
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
			if (tweet.getText().contains(parts[0])
					&& tweet.getText().contains(parts[1]))
				return true;
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
			LocalDateTime timeArticle = LocalDateTime.ofInstant(article2
					.getDate().toInstant(), ZoneId.of("GMT"));
			Duration duration = Duration.between(timeTweet, timeArticle).abs();
			if (isTweetRelatedToArticle(article2, tweet)) {
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

	private List<Status> findTweets(HashtagEntity h, Date fromDateTime,
			DurationEnum de) {
		Status[] tweets = hf.getStatusWithHT(h.getText());
		List<Status> validTweets = new ArrayList<Status>();
		for (Status tweet : tweets) {
			LocalDateTime timeTweet = LocalDateTime.ofInstant(tweet
					.getCreatedAt().toInstant(), ZoneId.of("GMT"));
			LocalDateTime timeArticle = LocalDateTime.ofInstant(
					fromDateTime.toInstant(), ZoneId.of("GMT"));
			Duration duration = Duration.between(timeTweet, timeArticle).abs();
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
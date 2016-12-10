/**
 * 
 */
package me.motallebi.hashtagger;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

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
	private Date NEWSTIME;
	private static Duration lambda = Duration.ofHours(4);
	private static Duration gamma = Duration.ofHours(24);
	private static Duration lambda2 = Duration.ofHours(8);

	public enum DurationEnum{
		LAMBDA, GAMMA, LAMBDA2
	}
	public SimpleFeatureComputer(NewsArticle article, FileTweetSource fts, FileNewsSource fns, HashtagFinder hashtagFinder, List<HashtagEntity> hashtags) {
		featureList = new ArrayList<Float>(numberOfFeatures);
		this.article = article;
		this.fts = fts;
		this.hf = hashtagFinder;
		this.allHashtagsOfArticle = hashtags;
		this.articles = getAllArticles(fns);
		this.maxFreq = calcMaxFreq();
		this.maxArticleSpecificTweetBagSize = calcArticleSpecificMaxTweetBagSize();
		this.minArticleSpecificTweetBagSize = calcArticleSpecificMinTweetBagSize();
		calcMaxTweetBagSize();//this.maxTweetBagSize
		calcMinTweetBagSize();//this.minTweetBagSize
		this.NEWSTIME = this.article.getDate();
	}



	private int calcArticleSpecificMaxTweetBagSize() {
		int maxBagSize = 0;
		for(HashtagEntity hashtag:allHashtagsOfArticle){
			Status[] tweets = hf.getStatusWithHT(hashtag.getText());
			// We have a hashtag and all of its tweets. Now, we want only tweets that are part related to this article
			// get keyphrase of this article
			List<String> keyPhrases = PredefinedKeyPhraseExtractor.getInstance().extractKeyPhrases(this.article);
			// for each keyphrase, find related tweets
			List<List<Long>> allValidIDs = new ArrayList<List<Long>>();
			for(String kph:keyPhrases){
				String part1 = kph.split(" ")[0];
				String part2 = kph.split(" ")[1];
				List<Long> validIDs = new ArrayList<Long>();
				for(Status tweet:tweets){
					if(tweet.getText().contains(part1) && tweet.getText().contains(part2))
						validIDs.add(tweet.getId());// I only keep IDs to reduce space used.
				}
				allValidIDs.add(validIDs);
			}
			// find union of tweets related to keyphrases
			List<Long> unionIDs = new ArrayList<Long>();
			for(List<Long> v:allValidIDs){
				unionIDs.addAll(v);
			}
			HashSet<Long> set = new HashSet<Long>();
			set.addAll(unionIDs);
			if(maxBagSize< set.size())
				maxBagSize = set.size();
		}
		return maxBagSize;
	}

	private int calcArticleSpecificMinTweetBagSize() {
		int minBagSize = 9999;
		for(HashtagEntity hashtag:allHashtagsOfArticle){
			Status[] tweets = hf.getStatusWithHT(hashtag.getText());
			//			int count = 0;
			//			for(Status tweet: tweets){
			//				if()
			//					count++;
			//			}
			//			if(count>maxBagSize)
			//				maxBagSize = count;
			// We have a hashtag and all of its tweets. Now, we want only tweets that are part related to this article
			// get keyphrase of this article
			List<String> keyPhrases = PredefinedKeyPhraseExtractor.getInstance().extractKeyPhrases(this.article);
			// for each keyphrase, find related tweets
			List<List<Long>> allValidIDs = new ArrayList<List<Long>>();
			for(String kph:keyPhrases){
				String part1 = kph.split(" ")[0];
				String part2 = kph.split(" ")[1];
				List<Long> validIDs = new ArrayList<Long>();
				for(Status tweet:tweets){
					if(tweet.getText().contains(part1) && tweet.getText().contains(part2))
						validIDs.add(tweet.getId());// I only kep IDs to reduce space used.
				}
				allValidIDs.add(validIDs);
			}
			// find union of tweets related to keyphrases
			List<Long> unionIDs = new ArrayList<Long>();
			for(List<Long> v:allValidIDs){
				unionIDs.addAll(v);
			}
			HashSet<Long> set = new HashSet<Long>();
			set.addAll(unionIDs);
			if(minBagSize> set.size())
				minBagSize = set.size();
		}
		return minBagSize;
	}

	private void calcMaxTweetBagSize() {
		if(maxTweetBagSize != -1)
			return;
		maxTweetBagSize = hf.getMaxTweetsForHashtags();
	}

	private void calcMinTweetBagSize() {
		if(minTweetBagSize != -1)
			return;
		minTweetBagSize = hf.getMinTweetsForHashtags();
	}


	/* (non-Javadoc)
	 * @see me.motallebi.hashtagger.FeatureComputer#LS(me.motallebi.hashtagger.NewsArticle, me.motallebi.hashtagger.Hashtag, java.util.Date)
	 */
	@Override
	public void LS(NewsArticle article, HashtagEntity h, DurationEnum lambda2, List<NewsArticle> articles) {
		List<Status> tweets = new ArrayList<Status>();
		List<String> bagOfWords = new ArrayList<String>();
		List<Float> articleVector = new ArrayList<Float>();
		List<Float> hashtagsVector = new ArrayList<Float>();
		List<String> strArticles = new ArrayList<String>();
		tweets = findArticleSpecificTweets(article, h,lambda2);// find all tweets of article a that contain this hashtag in this time.

		String tweetsText = "";//convert list of tweets to a string
		for(Status t:tweets){
			tweetsText+=t.getText() + " ";// text of one tweet.
		}
		bagOfWords = createBagOfWords(article, tweets);//create list of bag-of-words that contains terms of article and all tweets
		for(NewsArticle ns: articles){// create list of stings of articles -needed for idf-
			strArticles.add(ns.getTitle() + " " + ns.getBody());
		}
		for(String str: bagOfWords){//compute vectors of tf-idf for each term
			articleVector.add(termFrequency(str, article.getTitle() + " " + article.getBody())*inverseDocumentFrequency(str, strArticles));
			hashtagsVector.add(termFrequency(str, tweetsText));
		}		
		try {
			featureList.set(0,cosineSimilarity(articleVector, hashtagsVector));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception in Cosine Similarity!");
		}
	}

	/* (non-Javadoc)
	 * @see me.motallebi.hashtagger.FeatureComputer#LF(me.motallebi.hashtagger.NewsArticle, me.motallebi.hashtagger.Hashtag, java.util.Date)
	 */
	@Override
	public void LF(NewsArticle a, HashtagEntity h, DurationEnum LAMBDA) {
		List<Status> tweets = new ArrayList<Status>();
		tweets = findArticleSpecificTweets(a, h, LAMBDA);// fin all tweets that contain this hashtag in this time.
		featureList.set(1,(float) (tweets.size()-this.minArticleSpecificTweetBagSize)/(maxArticleSpecificTweetBagSize-minArticleSpecificTweetBagSize));
	}

	/* (non`-Javadoc)
	 * @see me.motallebi.hashtagger.FeatureComputer#GS(me.motallebi.hashtagger.NewsArticle, me.motallebi.hashtagger.Hashtag, java.util.Date)
	 */
	@Override
	public void GS(NewsArticle article, HashtagEntity h, DurationEnum GAMMA) {
		List<Status> tweets = new ArrayList<Status>();
		List<String> bagOfWords = new ArrayList<String>();
		List<Float> articleVector = new ArrayList<Float>();
		List<Float> hashtagsVector = new ArrayList<Float>();
		List<String> strArticles = new ArrayList<String>();

		tweets = findTweets(h, GAMMA);// find all tweets of this article that contain this hashtag in this time.
		String tweetsText = "";//convert list of tweets to list of strings
		for(Status t:tweets){
			tweetsText+=t.getText() + " ";// text of one tweet.
		}
		bagOfWords = createBagOfWords(article, tweets);//create list of bag-of-words that contains terms of article and all tweets
		for(NewsArticle ns: articles){// create list of stings of articles -needed for idf-
			strArticles.add(ns.getTitle() + " " + ns.getBody());
		}
		for(String str: bagOfWords){//compute vectors of tf-idf for each term
			articleVector.add(termFrequency(str, article.getTitle() + " " + article.getBody())*inverseDocumentFrequency(str, strArticles));
			hashtagsVector.add(termFrequency(str, tweetsText));
		}
		try {
			featureList.set(2, cosineSimilarity(articleVector, hashtagsVector));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception in Cosine Similarity!");
		}
	}

	/* (non-Javadoc)
	 * @see me.motallebi.hashtagger.FeatureComputer#GF(me.motallebi.hashtagger.Hashtag, java.util.Date)
	 */
	@Override
	public void GF(HashtagEntity h, DurationEnum GAMMA) {
		List<Status> tweets = new ArrayList<Status>();
		tweets = findTweets(h,GAMMA);// fin all tweets that contain this hashtag in this time.
		featureList.set(3, (float) (tweets.size()-minTweetBagSize)/(maxTweetBagSize-minTweetBagSize));
	}

	/* (non-Javadoc)
	 * @see me.motallebi.hashtagger.FeatureComputer#TR(me.motallebi.hashtagger.NewsArticle, me.motallebi.hashtagger.Hashtag, java.util.Date)
	 */
	@Override
	public void TR(NewsArticle a, HashtagEntity h, DurationEnum LAMBDA) {
		List<Status> tweetsNewer = new ArrayList<Status>();
		List<Status> tweetsOlder = new ArrayList<Status>();

		tweetsNewer = findArticleSpecificTweets(a, h,LAMBDA);// find all tweets that contain this hashtag in this time.
		tweetsOlder = findArticleSpecificTweets(a, h,DurationEnum.LAMBDA2);// find all tweets of article a that contain this hashtag in this time.
		featureList.set(4, (float) (2*tweetsNewer.size() - tweetsOlder.size())/(tweetsOlder.size()-tweetsNewer.size()));
	}

	/* (non-Javadoc)
	 * @see me.motallebi.hashtagger.FeatureComputer#EG()
	 */
	@Override
	public void EG(Float TR, NewsArticle article, HashtagEntity h, DurationEnum lambda3) {//TODO
		// TODO time frame is unclear for me!
		//temporary:///////////////
		featureList.set(5, 1.0f);
		if(true)return;
		///////////////////////////
		List<Status> tweets = new ArrayList<Status>();
		tweets = findArticleSpecificTweets(article, h, lambda3);
		featureList.set(5, (float)(1.0+TR)*tweets.size());
	}

	/* (non-Javadoc)
	 * @see me.motallebi.hashtagger.FeatureComputer#HE(me.motallebi.hashtagger.NewsArticle, me.motallebi.hashtagger.Hashtag)
	 */
	@Override
	public void HE(NewsArticle a, HashtagEntity h) {// Hashtag in Headline
		float inText= 0.0f;
		String temp = a.getTitle() + " " + a.getBody();
		temp = temp.replaceAll("\\s+", "");
		if(temp.contains(h.getText()))
			inText = 1.0f;
		featureList.set(6, inText);
	}

	/* (non-Javadoc)
	 * @see me.motallebi.hashtagger.FeatureComputer#UR(me.motallebi.hashtagger.NewsArticle, me.motallebi.hashtagger.Hashtag, java.util.Date)
	 */
	@Override
	public void UR(NewsArticle article, HashtagEntity h, DurationEnum LAMBDA) {
		List<Status> tweets = new ArrayList<Status>();
		HashSet<Long> hs = new HashSet<Long>();
		tweets = findArticleSpecificTweets(article, h, LAMBDA);
		for(Status tweet:tweets){
			hs.add(tweet.getUser().getId());
		}
		featureList.set(7,  (1.0f * hs.size())/tweets.size());
	}

	/* (non-Javadoc)
	 * @see me.motallebi.hashtagger.FeatureComputer#UC(me.motallebi.hashtagger.NewsArticle, me.motallebi.hashtagger.Hashtag, java.util.Date)
	 */
	@Override
	public void UC(NewsArticle article, HashtagEntity h, DurationEnum LAMBDA) {
		List<Status> tweets = new ArrayList<Status>();
		//HashSet<String> hs = new HashSet<String>();
		tweets = findArticleSpecificTweets(article, h, LAMBDA);
		int maxFollowers = 0;
		for(Status tweet:tweets){
			int temp = tweet.getUser().getFollowersCount();
			if(temp>maxFollowers)
				maxFollowers = temp;
		}
		featureList.set(8, (float) maxFollowers);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////	

	private Float termFrequency(String w, String text){
		return (float) (0.4 + ((1-0.4)*wordFreq(w,text))/(this.maxFreq));
	}

	private Float inverseDocumentFrequency(String w, List<String> strings ){
		int corpusSize = strings.size();
		int count = 0;
		for(String ns:strings){
			if(ns.contains(w))
				count++;
		}
		return (float) Math.log10(corpusSize/count);
	}

	private double wordFreq(String t, String text) {
		int count = StringUtils.countMatches(text,t);
		int totalCount = text.split("\\s").length;
		return (1.0f * count)/totalCount;
	}

	private List<String> createBagOfWords(NewsArticle article, List<Status> tweets){
		HashSet<String> hs = new HashSet<String>();
		String temp= article.getTitle() + " " + article.getBody();
		for(String str:temp.split("\\s") ){
			hs.add(str);
		}
		for(Status t:tweets)
			for(String str:t.getText().split("\\s")){
				hs.add(str);
			}
		List<String> ret = new ArrayList<String>(hs);
		return ret;

	}

	private Float cosineSimilarity(List<Float> a, List<Float> b) throws Exception{
		float sum =  0.0f;
		if(a.size()!=b.size())
			throw new Exception("Size of vectors in cosineSimilarity does not match");
		for(int i=0;i<a.size();i++){
			sum+=a.get(i)*b.get(i);
		}
		return sum/(a.size()*b.size());
	}

	public void computeFeatures(HashtagEntity hashtag) {
		LS(this.article, hashtag, DurationEnum.LAMBDA, this.articles);
		LF(this.article, hashtag, DurationEnum.LAMBDA);
		GS(this.article, hashtag, DurationEnum.GAMMA);
		GF(hashtag, DurationEnum.GAMMA);
		TR(this.article, hashtag, DurationEnum.LAMBDA);
		EG(this.featureList.get(4), this.article, hashtag, DurationEnum.LAMBDA);//TODO
		HE(this.article, hashtag);
		UR(this.article, hashtag, DurationEnum.LAMBDA);
		UC(this.article, hashtag, DurationEnum.LAMBDA);
	}

	public List<Float> getFeaturesList() {
		return this.featureList;
	}

	private Float calcMaxFreq() {
		// calculate maximum frequency for all terms we have so we can have the maximum in
		// in the beginning.
		String text = this.article.getTitle() + " " + this.article.getBody();
		String[] words = text.split(" ");
		int max = -1;
		@SuppressWarnings("unused")
		String maxWord;
		for(String word:words){
			int t = StringUtils.countMatches(text, word);
			if(t>max){
				max = t;
				maxWord = word;
			}
		}
		return (1.0f * max)/words.length;
	}

	private boolean isTweetRelatedToArticle(NewsArticle article2, Status tweet) {
		List<String> keyPhrases = PredefinedKeyPhraseExtractor.getInstance().extractKeyPhrases(article2);
		for(String kph:keyPhrases){
			String part1 = kph.split(" ")[0];
			String part2 = kph.split(" ")[1];
			if(tweet.getText().contains(part1) && tweet.getText().contains(part2))
				return true;
		}
		return false;
	}

	private List<Status> findArticleSpecificTweets(NewsArticle article2,
			HashtagEntity h, DurationEnum de) {
		Status[] tweets = hf.getStatusWithHT(h.getText());
		List<Status> validTweets = new ArrayList<Status>();
		for(Status tweet:tweets){
			LocalDateTime timeTweet = LocalDateTime.ofInstant(tweet.getCreatedAt().toInstant(), ZoneId.of("GMT"));
			LocalDateTime timeArticle = LocalDateTime.ofInstant(this.article.getDate().toInstant(), ZoneId.of("GMT"));
			Duration duration = Duration.between(timeTweet, timeArticle);
			if(isTweetRelatedToArticle(this.article, tweet)){
				if(de.toString().equals("GAMMA")){
					if(duration.compareTo(gamma)>0)
						validTweets.add(tweet);
				}
				else if(de.toString().equals("LAMBA")){
					if(duration.compareTo(lambda)>0)
						validTweets.add(tweet);		    	
				}
				else if(de.toString().equals("LAMBA2")){
					if(duration.compareTo(lambda2)>0)
						validTweets.add(tweet);		    	
				}
			}
		}
		return validTweets;
	}

	private List<Status> findTweets(HashtagEntity h, DurationEnum de) {
		Status[] tweets = hf.getStatusWithHT(h.getText());
		List<Status> validTweets = new ArrayList<Status>();
		for(Status tweet:tweets){
			LocalDateTime timeTweet = LocalDateTime.ofInstant(tweet.getCreatedAt().toInstant(), ZoneId.of("GMT"));
			LocalDateTime timeArticle = LocalDateTime.ofInstant(this.article.getDate().toInstant(), ZoneId.of("GMT"));
			Duration duration = Duration.between(timeTweet, timeArticle);
			if(de.toString().equals("GAMMA")){
				if(duration.compareTo(gamma)>0)
					validTweets.add(tweet);
			}
			else if(de.toString().equals("LAMBA")){
				if(duration.compareTo(lambda)>0)
					validTweets.add(tweet);		    	
			}
			else if(de.toString().equals("LAMBA2")){
				if(duration.compareTo(lambda2)>0)
					validTweets.add(tweet);		    	
			}

		}
		return validTweets;
	}

	private List<NewsArticle> getAllArticles(FileNewsSource fns) {
		List<NewsArticle> nas = new ArrayList<NewsArticle>();
		Iterator<NewsArticle> f = fns.iterator();
		while(fns.iterator().hasNext()){
			nas.add(f.next());
		}
		return nas;
	}
}

/*
 * lambda o gamma ro interval begiram. ke gamma masalan 24 saat bashe o lambda 4
 * hala time ha ro moghayese konam, male news ro ba tweet ha va interval besazam o ba ina moghayese konam
 * -- az dadash bekham ke bara newsArticle time besaze.
 * dar natije beshe moghayese kard
 * shayad lazem bashe ke gamma ro kheili vaghta infinity ina begiram!
 * */
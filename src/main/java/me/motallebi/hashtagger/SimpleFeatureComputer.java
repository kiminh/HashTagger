/**
 * 
 */
package me.motallebi.hashtagger;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;


/**
 * @author mhmotallebi
 *
 */
public class SimpleFeatureComputer implements FeatureComputer {

	/**
	 * 

	
	// to be used in tf calculation
	private Float maxFreq = (float) 0.0;
	private int maxArticleSpecificTweetBagSize = 0;
	private int minArticleSpecificTweetBagSize = 0;
	private int maxTweetBagSize = 0;
	private int minTweetBagSize = 0;
	/*
	 * TODO1: I have to find a way to calculate maxFreq before calling the LS()
	 * TODO2: Need to calc max and min article specific tweet bag size for feature 2
	 * TODO3: the same as previous one, for general tweets, feature 4
	 * */
	private List<Float> featureList;
	public SimpleFeatureComputer() {
		// TODO Auto-generated constructor stub
		maxFreq = calcMaxFreq();
		maxArticleSpecificTweetBagSize = calcArticleSpecificMaxTweetBagSize();
		minArticleSpecificTweetBagSize = calcArticleSpecificMinTweetBagSize();
		maxTweetBagSize = calcMaxTweetBagSize();
		minTweetBagSize = calcMinTweetBagSize();
	}

	private Float calcMaxFreq() {
		// TODO Auto-generated method stub
		// calculate maximum frequency for all terms we have so we can have the maximum in
		// in the beginning.
		return null;
	}

	/* (non-Javadoc)
	 * @see me.motallebi.hashtagger.FeatureComputer#LS(me.motallebi.hashtagger.NewsArticle, me.motallebi.hashtagger.Hashtag, java.util.Date)
	 */
	@Override
	public Float LS(NewsArticle article, Hashtag h, Date LAMBDA, List<NewsArticle> articles) {
		List<Tweet> tweets = new ArrayList<Tweet>();
		List<String> bagOfWords = new ArrayList<String>();
		List<Float> articleVector = new ArrayList<Float>();
		List<Float> hashtagsVector = new ArrayList<Float>();
		List<String> strArticles = new ArrayList<String>();

		tweets = findArticleSpecificTweets(article, h,LAMBDA);// find all tweets of article a that contain this hashtag in this time.
		String tweetsText = "";//convert list of tweets to list of strings
		for(Tweet t:tweets){
			tweetsText+=t.getContent() + " ";// text of one tweet.
		}
		bagOfWords = createBagOfWords(article, tweets);//create list of bag-of-words that contains terms of article and all tweets
		for(NewsArticle ns: articles){// create list of stings of articles -needed for idf-
			strArticles.add(article.getTitle() + " " + article.getBody());
		}
		for(String str: bagOfWords){//compute vectors of tf-idf for each term
			articleVector.add(termFrequency(str, article.getTitle() + " " + article.getBody())*inverseDocumentFrequency(str, strArticles));
			hashtagsVector.add(termFrequency(str, tweetsText));
		}		
		return cosineSimilarity(articleVector, hashtagsVector);
	}

	/* (non-Javadoc)
	 * @see me.motallebi.hashtagger.FeatureComputer#LF(me.motallebi.hashtagger.NewsArticle, me.motallebi.hashtagger.Hashtag, java.util.Date)
	 */
	@Override
	public Float LF(NewsArticle a, Hashtag h, Date LAMBDA) {
		List<Tweet> tweets = new ArrayList<Tweet>();
		tweets = findArticleSpecificTweets(a, h,LAMBDA);// fin all tweets that contain this hashtag in this time.
		return (float) (tweets.size()-minArticleSpecificTweetBagSize)/(maxArticleSpecificTweetBagSize-minArticleSpecificTweetBagSize);
	}

	/* (non-Javadoc)
	 * @see me.motallebi.hashtagger.FeatureComputer#GS(me.motallebi.hashtagger.NewsArticle, me.motallebi.hashtagger.Hashtag, java.util.Date)
	 */
	@Override
	public Float GS(NewsArticle article, Hashtag h, Date GAMMA) {
		List<Tweet> tweets = new ArrayList<Tweet>();
		List<String> bagOfWords = new ArrayList<String>();
		List<Float> articleVector = new ArrayList<Float>();
		List<Float> hashtagsVector = new ArrayList<Float>();
		List<String> strArticles = new ArrayList<String>();

		tweets = findTweets(h, GAMMA);// find all tweets of this article that contain this hashtag in this time.
		String tweetsText = "";//convert list of tweets to list of strings
		for(Tweet t:tweets){
			tweetsText+=t.getContent() + " ";// text of one tweet.
		}
		bagOfWords = createBagOfWords(article, tweets);//create list of bag-of-words that contains terms of article and all tweets
		for(NewsArticle ns: articles){// create list of stings of articles -needed for idf-
			strArticles.add(article.getTitle() + " " + article.getBody());
		}
		for(String str: bagOfWords){//compute vectors of tf-idf for each term
			articleVector.add(termFrequency(str, article.getTitle() + " " + article.getBody())*inverseDocumentFrequency(str, strArticles));
			hashtagsVector.add(termFrequency(str, tweetsText));
		}
		return cosineSimilarity(articleVector, hashtagsVector);
	}

	/* (non-Javadoc)
	 * @see me.motallebi.hashtagger.FeatureComputer#GF(me.motallebi.hashtagger.Hashtag, java.util.Date)
	 */
	@Override
	public Float GF(Hashtag h, Date GAMMA) {
		List<Tweet> tweets = new ArrayList<Tweet>();
		tweets = findTweets(h,GAMMA);// fin all tweets that contain this hashtag in this time.
		return (float) (tweets.size()-minTweetBagSize)/(maxTweetBagSize-minTweetBagSize);
	}

	/* (non-Javadoc)
	 * @see me.motallebi.hashtagger.FeatureComputer#TR(me.motallebi.hashtagger.NewsArticle, me.motallebi.hashtagger.Hashtag, java.util.Date)
	 */
	@Override
	public Float TR(NewsArticle a, Hashtag h, Date t) {
		List<Tweet> tweetsNewer = new ArrayList<Tweet>();
		List<Tweet> tweetsOlder = new ArrayList<Tweet>();

		tweetsNewer = findArticleSpecificTweets(a, h,LAMBDA);// find all tweets that contain this hashtag in this time.
		tweetsOlder = findArticleSpecificTweets(a, h,2*LAMBDA);// find all tweets of article a that contain this hashtag in this time.
		return (float) (2*tweetsNewer.size() - tweetsOlder.size())/(tweetsOlder.size()-tweetsNewer.size());
	}

	/* (non-Javadoc)
	 * @see me.motallebi.hashtagger.FeatureComputer#EG()
	 */
	@Override
	public Float EG(Float TR, NewsArticle article, Hashtag h, Date timeFrame) {
		// TODO time frame is unclear for me!
		List<Tweet> tweets = new ArrayList<Tweet>();
		tweets = findArticleSpecificTweets(article, h, timeFrame);
		return (float)(1.0+TR)*tweets.size();
	}

	/* (non-Javadoc)
	 * @see me.motallebi.hashtagger.FeatureComputer#HE(me.motallebi.hashtagger.NewsArticle, me.motallebi.hashtagger.Hashtag)
	 */
	@Override
	public Float HE(NewsArticle a, Hashtag h) {// Hashtag in Headline
		float inText= (float)0.0;
		String temp = a.getTitle() + " " + a.getBody();
		temp = temp.replaceAll("\\s+", "");
		if(temp.contains(h.content()))
			inText = (float)1.0;
		return inText;
	}

	/* (non-Javadoc)
	 * @see me.motallebi.hashtagger.FeatureComputer#UR(me.motallebi.hashtagger.NewsArticle, me.motallebi.hashtagger.Hashtag, java.util.Date)
	 */
	@Override
	public Float UR(NewsArticle article, Hashtag h, Date LAMBDA) {
		List<Tweet> tweets = new ArrayList<Tweet>();
		HashSet<String> hs = new HashSet<String>();
		tweets = findArticleSpecificTweets(article, h, LAMBDA);
		for(Tweet tweet:tweets){
			hs.add(tweet.getAuthor());
		}
		return (float) hs.size()/tweets.size();
	}

	/* (non-Javadoc)
	 * @see me.motallebi.hashtagger.FeatureComputer#UC(me.motallebi.hashtagger.NewsArticle, me.motallebi.hashtagger.Hashtag, java.util.Date)
	 */
	@Override
	public int UC(NewsArticle article, Hashtag h, Date LAMBDA) {
		List<Tweet> tweets = new ArrayList<Tweet>();
		HashSet<String> hs = new HashSet<String>();
		tweets = findArticleSpecificTweets(article, h, LAMBDA);
		int maxFollowers = 0;
		for(Tweet tweet:tweets){
			int temp = tweet.getAuthor().getFollowersNumber();
			if(temp>maxFollowers)
				maxFollowers = temp;
		}
		return maxFollowers;
	}

	
	
	private Float termFrequency(String w, String text){
		return (float) (0.4 + ((1-0.4)*wordFreq(w,text))/(maxFreq));
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
		return (float)count/totalCount;
	}
	
	private List<String> createBagOfWords(NewsArticle article, List<Tweet> tweets){
		HashSet<String> hs = new HashSet<String>();
		String temp= article.getTitle() + " " + article.getBody();
		for(String str:temp.split("\\s") ){
			hs.add(str);
		}
		for(Tweet t:tweets)
			for(String str:t.toString().split("\\s")){
				hs.add(str);
			}
		List<String> ret = new ArrayList<String>(hs);
		return ret;
		
	}
	
	private Float cosineSimilarity(List<Float> a, List<Float> b) throws Exception{
		float sum = (float) 0.0;
		if(a.size()!=b.size())
			throw new Exception("Size of vectors in cosineSimilarity does not mathc");
		for(int i=0;i<a.size();i++){
			sum+=a.get(i)*b.get(i);
		}
		return sum/(a.size()*b.size());
	}

	public List<Float> computeFeatures() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Float> getFeaturesList() {
		return this.featureList;
	}
}

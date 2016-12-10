package me.motallebi.hashtagger;


import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import twitter4j.HashtagEntity;
import twitter4j.Status;

/**
 * Program entry point. Main class.
 * 
 * @author mrmotallebi and mhmotallebi
 *
 */
public class HashTagger {

	/**
	 * Main method
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
//		/*
//		 * I Train classifier
//		 * II use classifier
//		 * 1. Load all documents (or one by one streamed)
//		 * 2. for each document do this:
//		 * 	2.1 calculate features
//		 *  2.2 run classifier on its features.
//		 *  2.3 print the result.
//		 *  ???????? this is not good at all!
//		 * 
//		 * 
//		 * 1. article khunde she az file.
//		 * 2. ghesmat haye mohemesh joda she.(yani ye pseudo-article she)
//		 * 3. kalamate kilidish moshakhas shan.(ham raveshe taraf ham raveshe ma)
//		 * 4. az ru kalamate kilidi tu twitter search shan va tweet bargardune.
//		 * 5. feature ha sakhte shan.
//		 * 6. random forest run she ru in data ha.
//		 * 7. be natije miresim!
//		 * */
//		SimpleNewsLoader snl = new SimpleNEwsLoader();
//		SimpleKeyPhraseExtractor extractor = SimpleKeyPhraseExtractor.getInstance();
//		int ARTICLE_NUMBER = 10;
//		CSVWriter csvWriter = CSVWriter.getInstance("training.csv");
//		for(int i=0;i<ARTICLE_NUMBER;i++){
//			NewsArticle article = snl.loadAnArticle();
//			List<String> keyphrases = extractor.extractKeyPhrases(article);
//			SimpleTweetExtractor tweetExtractor = new SimpleTweetExtractor();
//			SimpleHashtagExtractor hashtagExtractor new SimpleTweetExtractor();
//			List<Tweet> tweets = new List<Tweet>();
//			List<Hashtag> hashtags = new List<Hashtag>();
//			for(String keyphrase:keyphrases){
//				tweets = tweetExtractor.extractTweets(keyphrase);
//				hashtags = hashtagExtractor.extractHashtags(tweets);
//			}
//			SimpleFeatureComputer featureCompute = new SimpleFeatureComputer();
//			
//			for(Hashtag hashtag: hashtags){
//				List<Float> featuresValues = new ArrayList<Float>();
//				//compute all features
//				featuresValues = featureCompute.computeFeatures();
//				csvWriter.writeToFile(featuresValues);
//				
//			}
//			
//		}
		
		//1. Read and load tweets of one day
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String dateInString = "06-07-2016";
		Date date=null;
		try {
			date = sdf.parse(dateInString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		FileTweetSource fts = new FileTweetSource("/media/hossein/2020202020202020/06/all-1",date);
		fts.loadTweets();
		HashtagFinder hashtagFinder = new SimpleHashtagFinder(fts.getTweetList());
		
		//2. Load News articles
		FileNewsSource fns = new FileNewsSource("/home/hossein/Downloads/CMPUT692/twitter-data/news-test/");
		fns.loadNews();
		Iterator<NewsArticle> itNA = fns.iterator();//iterator containing news articles
		
		//3. iterate over news articles
		while(itNA.hasNext()){
			NewsArticle na = itNA.next();
			System.out.println("Start of news article: " + na.getId());
			
			//3.1 find keyphrases of article with method 1
			//SimpleKeyPhraseExtractor skpe = SimpleKeyPhraseExtractor.getInstance();
			//List<String> keyPhrases = skpe.extractKeyPhrases(na);
			List<String> keyPhrases = PredefinedKeyPhraseExtractor.getInstance().extractKeyPhrases(na);
			
			//3.2 find tweets and hashtags of keyphrases
			List<HashtagEntity> hashtags = new ArrayList<HashtagEntity>();
			for(String str: keyPhrases){
				str = str.replaceAll("<.*?>", "");// in early version some of them contained HTML tags inside! had to remove them
				ArrayList<Status> result1 = (ArrayList<Status>) Arrays.asList(hashtagFinder.getStatusWithWord(str.split(" ")[0]));
				ArrayList<Status> result2 = (ArrayList<Status>) Arrays.asList(hashtagFinder.getStatusWithWord(str.split(" ")[1]));
				result1.retainAll(result2);//get intersection since each keyphase has 2 strings and tweet should contain both of them.
				for (Status s : result1){
					hashtags.addAll(Arrays.asList(s.getHashtagEntities()));
				}
			}
//	        System.out.println("Hashtags for news " + na.getTitle() + "\nare: ");
//			for(HashtagEntity he: hashtags){
//				System.out.println(he.getText());
//			}
			
			//3.3 Now, for each hashtag, compute feature vector
			List<List<Float>> allFeatures = new ArrayList<List<Float>>();
			SimpleFeatureComputer sfc = new SimpleFeatureComputer(na,fts,fns, hashtagFinder, hashtags);
			for(HashtagEntity h:hashtags){
				List<Float> featuresValues = sfc.getFeaturesList();
				allFeatures.add(featuresValues);
				
			}
			//3.4 write them into a CSV file (to use it in Random Forest of WEKA)
			CSVWriter csv = null;
			try {
				csv = CSVWriter.getInstance(na.getId() + ".csv");
				for (List<Float> fv:allFeatures)
					csv.writeToFile(fv);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					System.out.println("CSV file cannot be generated!");
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("CSVWriter was not able to write into the file!");
			}		
		}
	}
}

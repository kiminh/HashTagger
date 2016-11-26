/**
 * 
 */
package me.motallebi.hashtagger;

import java.util.ArrayList;
import java.util.List;

/**
 * Program entry point. Main class.
 * 
 * @author mrmotallebi
 *
 */
public class HashTagger {

	/**
	 * Main method
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*
		 * I Train classifier
		 * II use classifier
		 * 1. Load all documents (or one by one streamed)
		 * 2. for each document do this:
		 * 	2.1 calculate features
		 *  2.2 run classifier on its features.
		 *  2.3 print the result.
		 *  ???????? this is not good at all!
		 * 
		 * 
		 * 1. article khunde she az file.
		 * 2. ghesmat haye mohemesh joda she.(yani ye pseudo-article she)
		 * 3. kalamate kilidish moshakhas shan.(ham raveshe taraf ham raveshe ma)
		 * 4. az ru kalamate kilidi tu twitter search shan va tweet bargardune.
		 * 5. feature ha sakhte shan.
		 * 6. random forest run she ru in data ha.
		 * 7. be natije miresim!
		 * */
		SimpleNewsLoader snl = new SimpleNEwsLoader();
		SimpleKeyPhraseExtractor extractor = SimpleKeyPhraseExtractor.getInstance();
		int ARTICLE_NUMBER = 10;
		CSVWriter csvWriter = CSVWriter.getInstance("training.csv");
		for(int i=0;i<ARTICLE_NUMBER;i++){
			NewsArticle article = snl.loadAnArticle();
			List<String> keyphrases = extractor.extractKeyPhrases(article);
			SimpleTweetExtractor tweetExtractor = new SimpleTweetExtractor();
			SimpleHashtagExtractor hashtagExtractor new SimpleTweetExtractor();
			List<Tweet> tweets = new List<Tweet>();
			List<Hashtag> hashtags = new List<Hashtag>();
			for(String keyphrase:keyphrases){
				tweets = tweetExtractor.extractTweets(keyphrase);
				hashtags = hashtagExtractor.extractHashtags(tweets);
			}
			SimpleFeatureComputer featureCompute = new SimpleFeatureComputer();
			
			for(Hashtag hashtag: hashtags){
				List<Float> featuresValues = new ArrayList<Float>();
				//compute all features
				featuresValues = featureCompute.computeFeatures();
				csvWriter.writeToFile(featuresValues);
				
			}
			
		}
		
	}


}

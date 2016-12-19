package me.motallebi.hashtagger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import me.motallebi.hashtagger.MTweet.MHashtag;
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
		// /*
		// * I Train classifier
		// * II use classifier
		// * 1. Load all documents (or one by one streamed)
		// * 2. for each document do this:
		// * 2.1 calculate features
		// * 2.2 run classifier on its features.
		// * 2.3 print the result.
		// * ???????? this is not good at all!
		// *
		// *
		// * 1. article khunde she az file.
		// * 2. ghesmat haye mohemesh joda she.(yani ye pseudo-article she)
		// * 3. kalamate kilidish moshakhas shan.(ham raveshe taraf ham raveshe
		// ma)
		// * 4. az ru kalamate kilidi tu twitter search shan va tweet
		// bargardune.
		// * 5. feature ha sakhte shan.
		// * 6. random forest run she ru in data ha.
		// * 7. be natije miresim!
		// * */
		// SimpleNewsLoader snl = new SimpleNEwsLoader();
		// SimpleKeyPhraseExtractor extractor =
		// SimpleKeyPhraseExtractor.getInstance();
		// int ARTICLE_NUMBER = 10;
		// CSVWriter csvWriter = CSVWriter.getInstance("training.csv");
		// for(int i=0;i<ARTICLE_NUMBER;i++){
		// NewsArticle article = snl.loadAnArticle();
		// List<String> keyphrases = extractor.extractKeyPhrases(article);
		// SimpleTweetExtractor tweetExtractor = new SimpleTweetExtractor();
		// SimpleHashtagExtractor hashtagExtractor new SimpleTweetExtractor();
		// List<Tweet> tweets = new List<Tweet>();
		// List<Hashtag> hashtags = new List<Hashtag>();
		// for(String keyphrase:keyphrases){
		// tweets = tweetExtractor.extractTweets(keyphrase);
		// hashtags = hashtagExtractor.extractHashtags(tweets);
		// }
		// SimpleFeatureComputer featureCompute = new SimpleFeatureComputer();
		//
		// for(Hashtag hashtag: hashtags){
		// List<Float> featuresValues = new ArrayList<Float>();
		// //compute all features
		// featuresValues = featureCompute.computeFeatures();
		// csvWriter.writeToFile(featuresValues);
		//
		// }
		//
		// }

		// 1. Read and load tweets of one day
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String dateInString = "06-07-2016";
		Date date = null;
		try {
			date = sdf.parse(dateInString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		FileTweetSource fts = new FileTweetSource("/media/hossein/2020202020202020/06/all-12", date);
		///media/hossein/2020202020202020/06/all-6
		fts.loadTweets();

		// 2. Load News articles
		FileNewsSource fns = new FileNewsSource("/home/hossein/Downloads/CMPUT692/twitter-data/news-06-jul-16");
		///home/hossein/Downloads/CMPUT692/twitter-data/news-06-jul-16
		fns.loadNews();
		Iterator<NewsArticle> itNA = fns.iterator();// iterator containing news
													// articles

		fns.waitUntilLoad();
		fts.waitUntilLoad();

		HashtagFinder hashtagFinder = new SimpleHashtagFinder(
				fts.getTweetList());

		SimpleFeatureComputer sfc = new SimpleFeatureComputer(fns,
				hashtagFinder);

		// 3. iterate over news articles
		while (itNA.hasNext()) {
			NewsArticle na = itNA.next();
			System.out.println("Start of news article: " + na.getId());
			if(na.getId()==39174)
				continue;
			try {
				// System.out.println(na.getDate());
				// System.out.println("--- Date is working");
			} catch (Exception e) {
				System.out.println("ERROR in Date");
				continue;
			}
			// 3.1 find keyphrases of article with method 1
			// SimpleKeyPhraseExtractor skpe =
			// SimpleKeyPhraseExtractor.getInstance();
			// List<String> keyPhrases = skpe.extractKeyPhrases(na);
			List<String> keyPhrases = PredefinedKeyPhraseExtractor
					.getInstance().extractKeyPhrases(na);

			// 3.2.1 find hashtags of keyphrases
			List<HashtagEntity> hashtags1 = findHashtagsFromKeyphrases(
					hashtagFinder, keyPhrases, 1);
			// 3.2.2 find hashtags of hashtags improvement-1
			List<String> keyphrasesFromHashtags = new ArrayList<String>();
			for (int i = 0; i < hashtags1.size() - 1; i += 2) {
				keyphrasesFromHashtags.add(hashtags1.get(i).getText() + " "
						+ hashtags1.get(i + 1).getText());
			}
//<<<<<<< HEAD
			//List<HashtagEntity> hashtags2 = findHashtagsFromKeyphrases(hashtagFinder, keyphrasesFromHashtags,2);
			//3.2.3 find hashtags of hashtag-keywor combination
			List<MHashtag> hashtags3 = findHashtagsFromCombination2(hashtagFinder, hashtags1,keyPhrases);
			HashSet<MHashtag> set = new HashSet<MTweet.MHashtag>(hashtags3);
			set.removeAll(hashtags1);
			List<HashtagEntity> hashtags = new ArrayList<HashtagEntity>(set);
			// printing different approaches. first one is the default, second is hashtag in hashtag, third is hashtag+keyword together
			//	        System.out.println("Hashtags for news " + na.getTitle() + "\nare: ");
			for(HashtagEntity he: hashtags1){
				System.out.print(he.getText() + " ");
			}
			if(hashtags1.size()>0)
				System.out.println();
			//for(HashtagEntity he: hashtags2){
			//	System.out.print(he.getText() + " ");
			//}
			//if(hashtags2.size()>0)
			//	System.out.println();
			for(HashtagEntity he: hashtags){
				System.out.print(he.getText() + " ");
			}
			if(hashtags.size()>0)
				System.out.println();

			hashtags.addAll(hashtags1);
			
			//if(true) continue;
			//3.3 Now, for each hashtag, compute feature vector
//=======
//			List<HashtagEntity> hashtags = findHashtagsFromKeyphrases(
//					hashtagFinder, keyphrasesFromHashtags, 2);

			// System.out.println(Arrays.toString(hashtags1.toArray()));
			// System.out.println(Arrays.toString(hashtags.toArray()));

			sfc.prepareForArticle(na, hashtags);

			// 3.3 Now, for each hashtag, compute feature vector
//>>>>>>> dev
			List<List<Object>> allFeatures = new ArrayList<List<Object>>();

			for (HashtagEntity h : hashtags) {
				// sfc.computeFeatures(h);
				// List<Float> featuresValues = sfc.getFeaturesList();
				float[] featuresValues = sfc.computeFeatures(na, h);
				List<String> temp = new ArrayList<String>(featuresValues.length);
				temp.add(h.getText());
				for (Float f : featuresValues)
					temp.add(f.toString());
				allFeatures.add((new ArrayList<Object>(temp)));

			}
			// 3.4 write them into a CSV file (to use it in Random Forest of
			// WEKA)
			CSVWriter csv = null;
			try {
				csv = new CSVWriter(na.getId() + ".csv");
				for (List<Object> objs : allFeatures)
					csv.write(objs);
				csv.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.out.println("CSV file cannot be generated!");
			} catch (IOException e) {
				e.printStackTrace();
				System.out
						.println("CSVWriter was not able to write into the file!");
			}
		}
	}

	private static List<HashtagEntity> findHashtagsFromCombination(
			HashtagFinder hashtagFinder, List<HashtagEntity> hashtags1,
			List<String> keyPhrases) {
		HashSet<String> keywords = new HashSet<String>();
		for(String str:keyPhrases){
			keywords.add(str.split(" ")[0]);
			keywords.add(str.split(" ")[1]);
		}
		//List<HashtagEntity> results = new ArrayList<HashtagEntity>();
		HashSet<HashtagEntity> s = new HashSet<>();
		for(HashtagEntity h:hashtags1){
			Status[] tweets = hashtagFinder.getStatusWithHT(h.getText());
			for(Status tweet:tweets){
				boolean flag = false;
				for(String str:keywords){
					if(tweet.getText().toLowerCase().contains(str.toLowerCase())){
						flag = true;
						break;
					}
				}
				if(flag == true){
					s.addAll(Arrays.asList(tweet.getHashtagEntities()));
					//results.addAll(Arrays.asList(tweet.getHashtagEntities()));
				}
			}
		}
		return new ArrayList<HashtagEntity>(s);
	}
	
	private static List<MHashtag> findHashtagsFromCombination2(
			HashtagFinder hashtagFinder, List<HashtagEntity> hashtags1,
			List<String> keyPhrases) {
		HashSet<String> keywords = new HashSet<String>();
		for(String str:keyPhrases){
			if(str.length()<2)
				continue;
			keywords.add(str.split(" ")[0]);
			keywords.add(str.split(" ")[1]);
		}
		//List<HashtagEntity> results = new ArrayList<HashtagEntity>();
		HashSet<MHashtag> s = new HashSet<MHashtag>();
		for(HashtagEntity h:hashtags1){
			Status[] tweets = hashtagFinder.getStatusWithHT(h.getText());
			for(Status tweet:tweets){
				int flag = 0;
				for(String str:keywords){
					if(tweet.getText().toLowerCase().contains(str.toLowerCase())){
						flag++;
						if(flag>=2)
							break;
					}
				}
				if(flag >=2){
					s.addAll((Collection<? extends MHashtag>) Arrays.asList(tweet.getHashtagEntities()));
					//results.addAll(Arrays.asList(tweet.getHashtagEntities()));
				}
			}
		}
		
		return new ArrayList<MHashtag>(s);
	}

	/**
	 * @param hashtagFinder
	 * @param keyPhrases
	 * @param i
	 * @return
	 */
	private static List<HashtagEntity> findHashtagsFromKeyphrases(
			HashtagFinder hashtagFinder, List<String> keyPhrases, int reqType) {
		List<HashtagEntity> hashtags = new ArrayList<HashtagEntity>();
		for (String str : keyPhrases) {
			// = null, result2 = null;
			// str = str.replaceAll("<.*?>", "");// in early version some of
			// them contained HTML tags inside! had to remove them
			if (str.split(" ").length < 2) {
				System.err.println("KEyword does not contain two words!: "
						+ str);
				continue;
			}
			Status[] temp1 = null, temp2 = null;
			if (reqType == 1) {
				temp1 = hashtagFinder.getStatusWithWord(str.split(" ")[0]);
				temp2 = hashtagFinder.getStatusWithWord(str.split(" ")[1]);
			} else {
				temp1 = hashtagFinder.getStatusWithHT(str.split(" ")[0]);
				temp2 = hashtagFinder.getStatusWithHT(str.split(" ")[1]);
			}
			if (temp1 == null || temp2 == null)
				continue;
			// List<Status> result1 = new ArrayList<Status>();
			// result1 = (List<Status>) Arrays.asList(temp1);
			List<Status> result1 = new ArrayList<>(Arrays.asList(temp1));
			List<Status> result2 = (List<Status>) Arrays.asList(temp2);
			result1.retainAll(result2);// get intersection since each keyphase
										// has 2 strings and tweet should
										// contain both of them.
			for (Status s : result1) {
				// hashtags.addAll(Arrays.asList(s.getHashtagEntities()));
				for (HashtagEntity he : s.getHashtagEntities()) {
					if (isNotRedundant(he, hashtags)
							&& he.getText().length() > 1)
						hashtags.add(he);
				}
			}
		}
		return hashtags;
	}

	private static boolean isNotRedundant(HashtagEntity he,
			List<HashtagEntity> hashtags) {
		for (HashtagEntity h : hashtags)
			if (h.getText().toLowerCase().equals(he.getText().toLowerCase()))
				return false;
		return true;
	}
}

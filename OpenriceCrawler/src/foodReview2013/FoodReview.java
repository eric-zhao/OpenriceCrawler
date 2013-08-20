package foodReview2013;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Vector;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;
import fileIO.FileIOImplementation;
import fileIO.IfileIO;
/*This class is used to crawl the data from openrice, in the format of UserID, shopID, visitTime, shopDistrict, cuisonType
 * amenity Type, review score. To crawl, there has several things to be considered
 * 1. The crawler is done based on the analysis of web page HTML tag. Since the website will updated from time to time,
 * the HTML tag may be changed, thus the program may not be able to run, then the program has to be changed accordingly.
 * 2. The crawling rate should also be tuned. Frequently crawling will make website reject the http request. Thus how to
 * the sleep time has to be tuned manually
 * 3. Some user may have thousands of reviews. My setting is, once the user has more than 500 reviews, I only get the first 500
 * reviews of the user.
 * 4. The site has both english and chinese version. Sometimes english version are easy to be crawled, and sometimes chinese. */
public class FoodReview {
	private int startUser;  //The starting point to crawl.
	private int endUser;	//End point to crawl.
//	private int userUpperLimit;
	private String outputDir = "";
	public static final String REVIEWURL_ENG = "http://www.openrice.com/english/gourmet/reviews.htm?userid=";
	public static final String REVIEWURL_CHI = "http://www.openrice.com/gourmet/reviews.htm?userid=";
	
	
	public FoodReview(int start, int end){
		this.startUser = start;
		this.endUser = end;
	}
	
	public void setOutputDir(String reviewStorage){
		this.outputDir = reviewStorage;
	}
	
	public int getStartUser (){
		return startUser;
	}
	
	public int getEndUser (){
		return endUser;
	}
	
	public String getUserLivePlace(Source source){
		List<Element> userInfo = source.getAllElements("div class=\"info\"");
		String userDistrict = "";
		if(userInfo.isEmpty()){
			return "";
		}
		else{
			Element info = userInfo.get(0);
			List<Element> v_ahref = info.getAllElements(HTMLElementName.A);
			if(!v_ahref.isEmpty()){
				Vector<String> vprofile = new Vector<String>();
				for(Element ele: v_ahref){
					String str = ele.getAttributeValue("href");
					String[] str_split = str.split("\\?");
					vprofile.add(str_split[1]);  //Since the number of cuisineID and dishID is not sure, store all the info into a vector;
				}
				for(String str_pro: vprofile){
					String[] str_dis = str_pro.split("=");
					String attr = str_dis[0];
					String id = str_dis[1];
					if(attr.equals("userhomedistrict_id") || attr.equals("userworkdistrict_id") || attr.equals("userdistrict_id")){
						userDistrict = userDistrict + id + ",";
					}
				}
			}
			return userDistrict;
		}
		
	}
	public Vector<String> getTime(Source source){
		List<Element> reviewTimes = source.getAllElements("div class=\"face\" ");
		Vector<String> vs = new Vector<String>();
		for(Element time: reviewTimes){
			vs.add(time.getTextExtractor().toString());
		}
		return vs;
	}
	
	//In this function we can obtain shopID.
	public Vector<String> getShopID(Source source){
		List<Element> shopID = source.getAllElements("div class=\"f010 lightgrey\"");
		Vector<String> vs = new Vector<String>();
		for(Element ids: shopID){
			List<Element> shopIDURLs = ids.getAllElements(HTMLElementName.A);
			String url = shopIDURLs.get(0).getAttributeValue("href");
			String[] idArray = url.split("=");
			vs.add(idArray[1]);
		}
		return vs;
	}
	//In this function we can obtain shop district vector.
	public Vector<String> getShopDistrict(Source source){
		List<Element> shopID = source.getAllElements("div class=\"f010 lightgrey\"");
		Vector<String> vs = new Vector<String>();
		for(Element ids: shopID){
			List<Element> shopIDURLs = ids.getAllElements(HTMLElementName.A);
			String url_shop_dist = shopIDURLs.get(1).getAttributeValue("href");
			String[] idArray = url_shop_dist.split("=");
			vs.add(idArray[1]);
		}
		return vs;
	}
	//This function is used to extract the shop's cuison type id.
	public Vector<String> getShopCuisionType(Source source){
		List<Element> v_table = source.getAllElements("td width=\"99%\"");
		Vector<String> vs = new Vector<String>();
		for(Element cuisonURL: v_table){
			List<Element> cuisons = cuisonURL.getAllElements(HTMLElementName.A);
			Element cuison_url = cuisons.get(0);
			
			String cuison_text = cuison_url.getAttributeValue("href");
			String[] split1 = cuison_text.split("&");
			String[] split2 = split1[0].split("=");
			String cuisonid = split2[1];
			vs.add(cuisonid);
//			System.out.println("Cuison url is: "+ cuisonid);
		}
//		System.out.println("Table size is: "+ v_table.size());
		
		return vs;
	}
	
	//This function is used to extract the shop's amenity type id.
		public Vector<String> getShopAmenityType(Source source){
			List<Element> v_table = source.getAllElements("td width=\"99%\"");
			Vector<String> vs = new Vector<String>();
			for(Element cuisonURL: v_table){
				List<Element> amenitys = cuisonURL.getAllElements(HTMLElementName.A);
				try{
					Element url = amenitys.get(1);
					String text = url.getAttributeValue("href");
					String[] split1 = text.split("&");
					String[] split2 = split1[0].split("=");
					vs.add(split2[1]);
				}
				catch(IndexOutOfBoundsException e){
					vs.add("");
					continue;
				}	
			}
			return vs;
		}
	
	//Return the number of food review customer has. Since the reviews are displayed 5 per page, once I obtain the total number
	//of review, I can visit each review page.
	public int getNumofReview(Source source){
		List<Element> v_review = source.getAllElements("div class=\"hk_fontsize16 eng_fontsize\"");
		int numReview = 0;
		try{	//If the customer has food reviews, we can extract the text in the form of "Showing 1 to 5 of 10 Reviews"
			Element reviewNum = v_review.get(0);
			String strNum = reviewNum.getTextExtractor().toString();
			String[] arrayNum = strNum.split(" ");
			numReview = Integer.parseInt(arrayNum[5]);
		}
		catch(IndexOutOfBoundsException e){	//If the customer has no food review, return 0 review;
//			System.out.println("Number of review is: "+ numReview);
			return numReview;
		}
		return numReview;
	}
	/************************************Get five types of score on a restaurant********************************************/
	//This function is used to retrieve the review score.
	public Vector<Integer> getPosStrongTag(Source source){
		List<Element> v_score = source.getAllElements("div class=\"orangetit\"");
		Vector<Integer> v_int = new Vector<Integer>();		
		for(int i=0; i<v_score.size(); i++){  //For each review in five reviews per page;
			List<Element> v_strong = v_score.get(i).getAllElements(HTMLElementName.STRONG);
			if(v_strong.size() < 3) {v_int.add(-1); continue;}
			int counter = 1;
			for(Element sub_ele: v_strong){
				
				if(!sub_ele.getContent().getTextExtractor().toString().equals("Other Ratings:")){
					counter++;
				}
				else{
					v_int.add(counter);
					break;
				}
			}
			
		}
		return v_int;
	}
	
	public Vector<String> getReviewScore(Source source){
		List<Element> v_score = source.getAllElements("div class=\"orangetit\"");
		Vector<String> vs = new Vector<String>();
		Vector<Integer> v_strong_pos= getPosStrongTag(source);
		for(int i=0; i< v_score.size(); i++){
			List<Element> v_strong = v_score.get(i).getAllElements(HTMLElementName.STRONG);
			String score = "";
			try{
			
				score = score + v_strong.get(v_strong_pos.get(i)).getContent().getTextExtractor().toString() + ",";
				score = score + v_strong.get(v_strong_pos.get(i)+1).getContent().getTextExtractor().toString() + ",";
				score = score + v_strong.get(v_strong_pos.get(i)+2).getContent().getTextExtractor().toString() + ",";
				score = score + v_strong.get(v_strong_pos.get(i)+3).getContent().getTextExtractor().toString() + ",";
				score = score + v_strong.get(v_strong_pos.get(i)+4).getContent().getTextExtractor().toString();
				vs.add(score);
//				System.out.println("Score is: "+ v_strong.get(v_strong_pos.get(i)).getContent().getTextExtractor().toString());
			}
			catch(IndexOutOfBoundsException e){
				vs.add("");
				continue;
			}
		}
		return vs;
	}
	
	public void getReview(int sleepMark) throws Exception{
		int pointer = startUser;
		int counter_scannedUser = 0;
		Vector<Vector<String>> vvtime = new Vector<Vector<String>>();  //Visit time of the shop
		Vector<Vector<String>> vvShopID = new Vector<Vector<String>>();  
		Vector<Vector<String>> vvShopDistrict = new Vector<Vector<String>>();
		Vector<Vector<String>> vvCuison = new Vector<Vector<String>>();
		Vector<Vector<String>> vvAmenity = new Vector<Vector<String>>();
		Vector<Vector<String>> vvReviewScore = new Vector<Vector<String>>();  //Review score, 5 items: Taste, service, etc.
		
		IfileIO fileIO = new FileIOImplementation();
		FileOutputStream fout_review = fileIO.fileOutputStream(outputDir);
		try{
			while(pointer < endUser){ //Loop each user;
				counter_scannedUser++;
				String urlReviewPage_eng = REVIEWURL_ENG + pointer;
				URL url_eng_home = new URL(urlReviewPage_eng);
				HttpURLConnection conn_eng_home = (HttpURLConnection)url_eng_home.openConnection();
				Source source_eng=new Source(conn_eng_home);
				int numReview = getNumofReview(source_eng);
				System.out.println("Number of review is: "+ numReview);
				if(numReview == 0) {
					pointer++; 
					System.out.println("User: "+ pointer);
					if(counter_scannedUser>sleepMark){
						Thread.sleep(6000);
						System.out.println("I am sleeping now!");
						counter_scannedUser = 0;
						conn_eng_home.disconnect();
					}
					continue;
				}   //If the user has no review, then jump the user;
				if(numReview > 1000){numReview = 1000;}  //Restrict the upper bound of review per user.
				int numPages = numReview/5;
				int counter = 0;
				for(int page=1; page<=numPages+1; page++){
					String url_chi_page = REVIEWURL_CHI + pointer+ "&page="+ page;
					String url_eng_page = REVIEWURL_ENG + pointer+ "&page="+ page;
					counter++;
					System.out.println("Page: "+ counter);
					URL url_eng = new URL(url_eng_page);
					URL url_chi = new URL(url_chi_page);
					HttpURLConnection conn_eng = (HttpURLConnection)url_eng.openConnection();
					HttpURLConnection conn_chi = (HttpURLConnection)url_chi.openConnection();
					Source source_to_page_chi=new Source(conn_chi);
					Source source_to_page_eng=new Source(conn_eng);
					
					vvtime.add(getTime(source_to_page_chi));  //Get the visiting time;
					vvShopID.add(getShopID(source_to_page_eng));
					vvShopDistrict.add(getShopDistrict(source_to_page_eng));
					vvCuison.add(getShopCuisionType(source_to_page_eng));
					vvAmenity.add(getShopAmenityType(source_to_page_eng));
					vvReviewScore.add(getReviewScore(source_to_page_eng));
					
					conn_eng.disconnect();
					conn_chi.disconnect();
					Thread.sleep(4000);
				}
				
				for(int i=0; i<vvShopID.size(); i++){
					Vector<String> vShopID = vvShopID.elementAt(i);
					Vector<String> vTime = vvtime.elementAt(i);
					Vector<String> vShopDistrict = vvShopDistrict.elementAt(i);
					Vector<String> vCuison = vvCuison.elementAt(i);
					Vector<String> vAmenity = vvAmenity.elementAt(i);
					Vector<String> vReviewScore = vvReviewScore.elementAt(i);
					for(int j=0; j<vShopID.size(); j++){
						String shopID = vShopID.elementAt(j);
						String time = vTime.elementAt(j);
						System.out.println("time is: "+ time);
						if(time.length() < 10){continue;}
						if(time.length() > 10){ time = time.substring(0, 10); }
						String ShopDistrict = vShopDistrict.elementAt(j);
						String cuison = vCuison.elementAt(j);
						String amenity = vAmenity.elementAt(j);
						String ReviewScore = vReviewScore.elementAt(j);
						String output = pointer+ "	"+ shopID+ "	"+time+ "	"+ ShopDistrict+ "	"+ cuison+ "	"+ amenity+ "	"+ ReviewScore;
						new PrintStream(fout_review).println(output);
						System.out.println(output);
					}
				}
//					for(Integer in: vv){
//						System.out.println("String is: "+ in);
//					}
//				for(Vector<String> vstring: vvReviewScore)
//					for(String str: vstring){
//						System.out.println("String is: "+ str);
//					}
				vvShopID.clear(); vvtime.clear(); vvShopDistrict.clear(); vvCuison.clear(); vvAmenity.clear(); vvReviewScore.clear();
				
//				if(counter_scannedUser>sleepMark){
//					Thread.sleep(100);
//					System.out.println("I am sleeping now!");
//					counter_scannedUser = 0;
//				}
				System.out.println("User: "+ pointer);
				pointer++;
			}
		}
		catch(IOException e){
			Thread.sleep(5000);
		}
	}
	
	public static void main(String[] args) throws Exception{
		FoodReview review = new FoodReview(10604, 20000);
		review.setOutputDir("./Data/data2013Aug/userReview_2013.txt");
//		FoodReview review = new FoodReview(16346, 16347);
		review.getReview(3);
		
		

	}
}

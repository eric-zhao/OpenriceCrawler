package Entity;
import java.net.URL;
import java.util.List;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;
import java.util.*;
import java.lang.Thread;
import fileIO.*;
import java.io.FileOutputStream;
import java.io.PrintStream;
public class userProfile {
	private int userLimit;  //Set how many users will be crawled.
	private int rvewPerUser;
	private int userUpperLimit;
	
	private String outputDir_user = "";
	private String outputDir_Review = "";
	private String outputDir_shop = "";
	//Constructor. UserLimit: Number of users to be crawled. RevewPerUser: the lower boundary of the number of food review user needs to write;
	//limit: the upper bound of the number of review. E.g. if user wrote more 1000 reviews, we only pick part of it.
	public userProfile(int userLimit, int revewPerUser, int upper_limit){
		this.userLimit = userLimit;
		this.rvewPerUser = revewPerUser;
		this.userUpperLimit = upper_limit;
	}
	
	public int get_userLimit(){
		return userLimit;
	}
	
	public void setOutputDir(String usr, String review, String shop){
		this.outputDir_user = usr;
		this.outputDir_Review = review;
		this.outputDir_shop = shop;
	}
	
	public int getFoodReviewPerUser(){
		return rvewPerUser;
	}
	
	public int getUserUpperLimit(){
		return userUpperLimit;
	}
	
	public void setUserUpperLimit(int limit){
		this.userUpperLimit = limit;
	}
	
	public List<Element> getProfileList(Source source){
//		List<Element> userLeftNav = source.getAllElements("div id=\"gourmetLeftNav\"");
//		List<Element> userInfo = userLeftNav.get(0).getAllElements("div class=\"info\"");
		List<Element> userInfo = source.getAllElements("div class=\"info\"");
		
		if(userInfo.isEmpty()){
			return userInfo;
		}
		else{
			Element info = userInfo.get(0);
			List<Element> v_ahref = info.getAllElements(HTMLElementName.A);
			
//			for(Element ele: v_ahref){
//				System.out.println(ele.getAttributeValue("href"));
//			}
			return v_ahref;
//			return "lalala";
		}
	}
	

	
	public String getHomeDist(Vector<String> v_pro){
		String homeDistrID = "";
		for(String str_pro: v_pro){		
			String[] str = str_pro.split("=");
			String attr = str[0];
			String id = str[1];
			if(attr.equals("userhomedistrict_id")){
				homeDistrID = homeDistrID+ id+ " ";
			}
		}
		return homeDistrID;
	}
	
	public String getWorkingDist(Vector<String> v_pro){
		String workDistrID = "";
		for(String str_pro: v_pro){		
			String[] str = str_pro.split("=");
			String attr = str[0];
			String id = str[1];
			if(attr.equals("userworkdistrict_id")){
				workDistrID = workDistrID+ id+ " ";
			}
		}
		return workDistrID;
	}
	
	public String getUserDist(Vector<String> v_pro){
		String userDistrID = "";
		for(String str_pro: v_pro){		
			String[] str = str_pro.split("=");
			String attr = str[0];
			String id = str[1];
			if(attr.equals("userdistrict_id")){
				userDistrID = userDistrID+ id+ " ";
			}
		}
		return userDistrID;
	}
	
	public String getCuisine(Vector<String> v_pro){
		String cuisineID = "";
		for(String str_pro: v_pro){		
			String[] str = str_pro.split("=");
			String attr = str[0];
			String id = str[1];
			if(attr.equals("cuisine_id")){
				cuisineID = cuisineID+ id+ " ";
			}
		}
		return cuisineID;
	}
	
	public String getDish(Vector<String> v_pro){
		String dishes_id = "";
		for(String str_pro: v_pro){		
			String[] str = str_pro.split("=");
			String attr = str[0];
			String id = str[1];
			if(attr.equals("dishes_id")){
				dishes_id = dishes_id+ id+ " ";
			}
		}
		return dishes_id;
	}
	
	//Get the number of food review per user;
	public int numOfReviews(Source source){
		List<Element> reviewInfo = source.getAllElements("div class=\"info\"");
		List<Element> ahref = reviewInfo.get(1).getAllElements(HTMLElementName.A);
		Element a = ahref.get(0);
		int num = Integer.parseInt(a.getTextExtractor().toString());
		return num;
	}
	
	//Given userID, get user's profile;
	public String getSingleUserProfile(int userStartPointer) throws Exception{
		int reviewLowLimit = getFoodReviewPerUser(); //User at least should have a number reviews,say 20.
		String userProfilePrefix = "http://www.openrice.com/gourmet/reviews.htm?userid=";
		String userProfile = "";
		String userPage = userProfilePrefix+ userStartPointer;
		Source source=new Source(new URL(userPage));
		List<Element> list = getProfileList(source);
		if(!list.isEmpty()){  //If the user's profile list is not null;
			Vector<String> vprofile = new Vector<String>();
			for(Element ele: list){
				String str = ele.getAttributeValue("href");
				String[] str_split = str.split("\\?");
				vprofile.add(str_split[1]);  //Since the number of cuisineID and dishID is not sure, store all the info into a vector;
			}
            int numReview = numOfReviews(source);
            if(numReview>=reviewLowLimit){  //Check current user's number of food review. Only choose the user writting more than 10 review;
            	String homeDistr = getHomeDist(vprofile);
    		    String workDistr = getWorkingDist(vprofile);
    		    String userDistr = getUserDist(vprofile);
    		    String cuisineID = getCuisine(vprofile);
    		    String dishID = getDish(vprofile);
    		    userProfile = userStartPointer+","+ homeDistr+","+ workDistr+ ","+userDistr+ ","+cuisineID+ ","+dishID+ ","+numReview;
            }	    
		}
		return userProfile;
	}
	
	//userStartPointer: the start point to crawl. sleepMark: after crawling a number of users, sleep the system for a while;
	public void getUserProfile(int userStartPointer, int sleepMark) throws Exception{
		int counter = 0;
		int counter_scannedUser = 0;
		int userLimit = get_userLimit();
		int reviewLowLimit = getFoodReviewPerUser();
		String userProfilePrefix = "http://www.openrice.com/gourmet/reviews.htm?userid=";
		IfileIO fileIO = new FileIOImplementation();
		FileOutputStream fout_userProfile = fileIO.fileOutputStream(outputDir_user); 
		FileOutputStream fout_review = fileIO.fileOutputStream(outputDir_Review); 
		FileOutputStream fout_shopProfile = fileIO.fileOutputStream(outputDir_shop); 
		
		userFoodReviewCrawler foodReview = new userFoodReviewCrawler( fout_review, fout_shopProfile);  //Get user's review info;
		while(counter < userLimit){  //First loop, for each user;
			try{   //If the server complains, sleep for a while;
				userStartPointer++;
				counter_scannedUser++;
				String currentUserProfile = getSingleUserProfile(userStartPointer);
				if(currentUserProfile == ""){  //If there is no userProfile info, then pick another user;
					continue;
				}
				else{
					System.out.println(counter_scannedUser+ "	"+currentUserProfile); //Output user profile info;
					new PrintStream(fout_userProfile).println(currentUserProfile); //Print user profile into files;
					System.out.println("**************************");
//				    userFoodReviewCrawler foodReview = new userFoodReviewCrawler( fout_review, fout_shopProfile);  //Get user's review info;
			    	foodReview.getFoodReview(Integer.toString(userStartPointer), getUserUpperLimit()); //First argument is current userID; Second is the maximum number reviews per user.

				}
				if(counter_scannedUser>sleepMark){
					Thread.sleep(30000);
					counter_scannedUser = 0;
				}
			}catch(java.io.IOException e){
				Thread.sleep(30000);
			}
			
		}
	}
	
	public static void main(String[] args) throws Exception{
		userProfile usr = new userProfile(20, 10, 30);
		usr.getUserProfile(16346, 70);
		
//		String userInfo = usr.getSingleUserProfile(16326);
//		System.out.println(userInfo);
	}


}

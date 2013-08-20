package Entity;

public class main {
	
	public static void main(String[] args) throws Exception{
		String outputDir_user = "./Data/userProfile_2013.txt";
		String outputDir_Review = "./Data/userReview_2013.txt";
		String outputDir_Shop = "./Data/shopInfo_2013.txt";
		//First argument: UserLimit: Number of users to be crawled. 
		//Second argument: RevewPerUser: the lower boundary of the number of food review user needs to write;
		//Third argument: limit: the upper bound of the number of review. E.g. if user wrote more 1000 reviews, we only pick part of it.
		userProfile usr = new userProfile(20, 10, 30);
		usr.setOutputDir(outputDir_user, outputDir_Review, outputDir_Shop);
		//First argument: the start user to crawl;
		//Second argument: the time to sleep when server complains;
		usr.getUserProfile(16346, 70);
		
	}
	
}

package Entity;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.List;

import fileIO.FileIOImplementation;
import fileIO.IfileIO;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;
//import fileIO.FileIOImplementation;
//import fileIO.IfileIO;

public class userFoodReviewCrawler {
    private String userID;
//    private FileOutputStream  usr_outputStream;
    private FileOutputStream review_outputStream;
    private FileOutputStream shop_outputStream;
    
    public userFoodReviewCrawler( FileOutputStream review_dir, FileOutputStream shopDir){
//    	this.usr_outputStream = usr_dir;
    	this.review_outputStream = review_dir;
    	this.shop_outputStream = shopDir;
    }
    
    public String getShopID(Source source,String userID, int hrefID) throws Exception{
    	List<Element> lTable = source.getAllElements(HTMLElementName.TABLE);
		Element ele = lTable.get(hrefID);			
		List<Element> ahref = ele.getAllElements(HTMLElementName.A);	
		Element shopURLElement = ahref.get(4);
		String shopURL = shopURLElement.getAttributeValue("href");
		String[] shopIDSplit = shopURL.split("=");
		String shopID = shopIDSplit[1];
//		System.out.println(shopID);
		return shopID; 
    }
    
    	
    public String getCommentShopDistrictID(Source source,String userID, int hrefID) throws Exception{	
		    List<Element> lTable = source.getAllElements(HTMLElementName.TABLE);
			Element ele = lTable.get(hrefID);			
			List<Element> ahref = ele.getAllElements(HTMLElementName.A);	
			Element comment = ahref.get(3);
			Element shopURLElement = ahref.get(4);			
			Element shopDistrictElement = ahref.get(5);
			
			String commentURL = comment.getAttributeValue("href");
			String shopURL = shopURLElement.getAttributeValue("href");
			String shopDistrictURL = shopDistrictElement.getAttributeValue("href");

//			System.out.println(shopURL);
			String[] commentIDSplit = commentURL.split("commentid=");
            String[] shopIDSplit = shopURL.split("=");
            String[] shopDistrictIDSplit = shopDistrictURL.split("=");
            String commentID = commentIDSplit[1];
            String shopID = shopIDSplit[1];
            String shopDisID = shopDistrictIDSplit[1];            
            if(commentID.contains("&")){
            	String[] split = commentID.split("&");
            	commentID = split[0];
            }
//            System.out.println(ele.getTextExtractor().toString());
//            System.out.println("");
            return commentID+ ","+userID+","+shopID+","+shopDisID+ ",";
	}
    
    private void fileOutput(String review, String shop){
    	new PrintStream(review_outputStream).println(review);
    	new PrintStream(shop_outputStream).println(shop);
    }
    
    public void getFoodReview(String uID, int numOfReview) throws Exception{
    	   int numOfPage = numOfReview / 15 +1;  //One page has 15 review;
    	   for(int page=1; page<numOfPage; page++){
 //   		   System.out.println("This is page"+ page);
        	   String sourceURL = "http://www.openrice.com/english/gourmet/reviews.htm?userid="+ uID+ "&OrderType=ScoreOverall"+ "&page="+ page;
        	   Source source=new Source(new URL(sourceURL));
        	   
        	   List<Element> lTable = source.getAllElements(HTMLElementName.TABLE);
    		   double size = lTable.size();
    		   int num = (int)Math.floor((size-7)/4);

    		   for(int i=0; i<=num; i++){   //for each restaurant of the lists;
    			   int j = 4+4*i;
    			   String CommentShopDistrictID = "";
    			   String shopReview = "";
    			   try{  //If there has some problems during the extraction of food review, or restaurant info, abandon it;
    				    CommentShopDistrictID = getCommentShopDistrictID(source, uID, j);  //commentID+ ","+userID+","+shopID+","+shopDisID
    				    shopReview = rating.getReview(source, uID, j);
    				    String review = CommentShopDistrictID+ shopReview;
	    	            if(!Character.isDigit(shopReview.charAt(0))){    //Check if the review is digit. If not, abandon the review;
	    	                continue;
	    	            }    	            
	    	            /****************************Get the shop information********************/
	    	            String shopID = getShopID(source, uID, j); //Get the shopID for retrieving shopInfo.
	    	            System.out.println("The shopid is"+ shopID);
	        			restaurantInfo rest = new restaurantInfo();
	        			String shopProfile = rest.getShopInfo(shopID);
	        			
	        			System.out.println(review);
	        			System.out.println(shopProfile);
	        			
	        			//new PrintStream(review_outputStream).println(review);
	        	    	//new PrintStream(shop_outputStream).println(shopProfile);
//	        			fileOutput(review, shopProfile);
    			   }catch(Exception e){
    				   continue;
    			   }
    			   
    		   }
    	   }

    }
    
    public static void main(String[] args) throws Exception{
    	String uID = "16326";
    	String outputDir_Review = "./Data/userReview_2013.txt";
		String outputDir_Shop = "./Data/shopInfo_2013.txt";
		IfileIO fileIO = new FileIOImplementation();
    	FileOutputStream fout_review = fileIO.fileOutputStream(outputDir_Review); 
		FileOutputStream fout_shopProfile = fileIO.fileOutputStream(outputDir_Shop);
    	userFoodReviewCrawler foodReview = new userFoodReviewCrawler(fout_review, fout_shopProfile);
    	foodReview.getFoodReview(uID, 400);
    	
    }
}

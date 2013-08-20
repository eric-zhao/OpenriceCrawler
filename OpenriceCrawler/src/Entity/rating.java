package Entity;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.List;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;
//import fileIO.FileIOImplementation;
//import fileIO.IfileIO;

//Extract the rating in review page;
public class rating {
//	public static String getReview(Source source, FileOutputStream fout, String userID, int StrongID) throws Exception{
	public static String getReview(Source source, String userID, int StrongID) throws Exception{

		List<Element> lTable = source.getAllElements(HTMLElementName.TABLE);
//		int counter = 1;
//		double size = lTable.size();
//		System.out.println(lTable.size());
//		int num = (int)Math.floor((size-7)/4);
//		for(int i=0; i<=num; i++){
//			int j = 4+4*i;    //Find the position of "Strong" tag.
			Element ele = lTable.get(StrongID);	
			List<Element> ratingStrong = ele.getAllElements(HTMLElementName.STRONG);
//			System.out.println(i+ "-------------------------");
			int ratNum = ratingStrong.size();
//			System.out.println("The size of rating is: "+ ratNum);
			int rat_start = 0;
			if(ratNum == 9){
				rat_start = 3;
			}
			if(ratNum == 10){
				rat_start = 4;
			}
			if(ratNum == 11){
				rat_start = 5;
			}
			else if(ratNum == 12){
				rat_start = 6;
			}
			else if(ratNum == 13){
				rat_start = 7;
			}
			else if(ratNum == 14){
				rat_start = 8;
			}
			String rating = "";
			for(int k=rat_start; k<ratNum - 1; k++){
				Element rate = ratingStrong.get(k);
				String strRate = rate.getTextExtractor().toString();
				if(k!=ratNum-2){
					strRate += ",";
				}			
//				System.out.println(strRate);
				rating += strRate;
			}
//			new PrintStream(fout).println(rating);
//			counter++;
//		}
			return rating;
	}
	public static void main(String[] args) throws Exception {
		String outputDir = "./data/output.txt";
//		IfileIO fileIO = new FileIOImplementation();
//	    FileOutputStream fout = fileIO.fileOutputStream(outputDir); 
		String sourceUrlString = "http://www.openrice.com/english/gourmet/reviews.htm?userid=37777&OrderType=ScoreOverall";
		Source source=new Source(new URL(sourceUrlString));
		List<Element> lTable = source.getAllElements(HTMLElementName.TABLE);
		int counter = 1;
		double size = lTable.size();
		System.out.println(lTable.size());
		int num = (int)Math.floor((size-7)/4);
		System.out.println("num is: "+num);
		for(Element ele: lTable){
//			new PrintStream(fout).println(counter+"----------------------------------------------------");
			List<Element> ahref = ele.getAllElements(HTMLElementName.STRONG);
			for(Element href: ahref){
				String link = href.getTextExtractor().toString();
//				new PrintStream(fout).println(link);
			}
			counter++;
		}
		for(int i=0; i<=num; i++){
			int j = 4+4*i;    //Find the position of "Strong" tag.
			Element ele = lTable.get(j);	
			List<Element> ratingStrong = ele.getAllElements(HTMLElementName.STRONG);
			System.out.println(i+ "-------------------------");
			int ratNum = ratingStrong.size();
			System.out.println("The size of rating is: "+ ratNum);
			int rat_start = 0;
			if(ratNum == 9){
				rat_start = 3;
			}
			if(ratNum == 10){
				rat_start = 4;
			}
			if(ratNum == 11){
				rat_start = 5;
			}
			else if(ratNum == 12){
				rat_start = 6;
			}
			else if(ratNum == 13){
				rat_start = 7;
			}
			else if(ratNum == 14){
				rat_start = 8;
			}
			for(int k=rat_start; k<ratNum - 1; k++){
				Element rate = ratingStrong.get(k);
				String strRate = rate.getTextExtractor().toString();
				System.out.println(strRate);
			}
			counter++;
		}
	}
}

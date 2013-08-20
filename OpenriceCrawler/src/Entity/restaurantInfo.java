package Entity;

import java.net.URL;
import java.util.List;

import net.htmlparser.jericho.CharacterReference;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

//Only need one parameter: shopID;
public class restaurantInfo {
	
	public String getShopPrice(Source source){
		String price = "";	
		try{
//			List<Element> vPri = source.getAllElements("div class=\"FL ML10\"");
			List<Element> vPri = source.getAllElements("div class=\"FL\"");
			System.out.println("haha"+ vPri.size());
			Element titleElement=source.getFirstElement(HTMLElementName.TITLE);
			String title = CharacterReference.decodeCollapseWhiteSpace(titleElement.getContent());
			System.out.println("title is "+title);
			String priceRaw = vPri.get(0).getTextExtractor().toString();
			System.out.println("raw price is: "+ priceRaw);
			if(priceRaw.length()<11){  //Below 40;
				price = "Below"+ priceRaw.substring(6, 8);			
			}
			else{
				String lowPrice = priceRaw.substring(6,8);
				String highPrice = priceRaw.substring(10,13);
				price = lowPrice+ "to"+ highPrice;
//				System.out.println(lowPrice+"	"+highPrice);
			}
		}catch(Exception e){
			return "";
		}	
		return price;
	}
	
	public String getDistrictID(Source source){
		String distID = "";
		try{
			List<Element> vPri = source.getAllElements("a class=\"main_color2\"");
			String disURL = vPri.get(0).getAttributeValue("href");
			String[] split = disURL.split("=");
			distID = split[1];
//			System.out.println(distID);
		}catch(Exception e){
			return "";
		}		
		return distID;
	}
	
	public String getCuisineID(Source source){
		String cuisineID = "";
		try{
			List<Element> vPri = source.getAllElements("span class=\"blacklink\"");
			List<Element> vEle = vPri.get(0).getAllElements(HTMLElementName.A);
			String cuisineURL = vEle.get(0).getAttributeValue("href");
			String[] splitInfo = cuisineURL.split("cuisine_id=");

			String[] splitInfo1 = splitInfo[1].split("&");
		    cuisineID = splitInfo1[0];
//			System.out.println(cuisineID);
		}catch(Exception e){
			return "";
		}
		
		return cuisineID;
	}
	
	public String getDishID(Source source){
		String dishID = "";
		List<Element> vPri = source.getAllElements("span class=\"blacklink\"");
		List<Element> vEle = vPri.get(0).getAllElements(HTMLElementName.A);
		for(Element ele: vEle){
			String dishIDURL = ele.getAttributeValue("href");
			if(dishIDURL.contains("dishes_id")){
				String[] splitInfo = dishIDURL.split("dishes_id=");
				String[] splitInfo1 = splitInfo[1].split("&");
				dishID = dishID+ " "+splitInfo1[0];   //If there are more than 1 dishID, then use space seperate. " dish1 dish2"
			}
		}
//		System.out.println("Number of like is: "+dishID);
		return dishID;
	}
	
	//Get how many user likes the shop;
	public int getNumLike(Source source){
		List<Element> vPri = source.getAllElements("span class=\"number\"");
		String numLike_str = vPri.get(0).getTextExtractor().toString();
		int numLike = Integer.parseInt(numLike_str);
//		System.out.println(numLike);
		return numLike;
	}
	//Get how many user thinks the shop is OK;
	public int getNumOK(Source source){
		List<Element> vPri = source.getAllElements("span class=\"number\"");
		String numOK_str = vPri.get(1).getTextExtractor().toString();
		int numOK = Integer.parseInt(numOK_str);
//		System.out.println(numOK);
		return numOK;
	}
	
	//Get how many user dislikes the shop;
	public int getNumDislike(Source source){
		List<Element> vPri = source.getAllElements("span class=\"number\"");
		String numDislike_str = vPri.get(2).getTextExtractor().toString();
		int numDislike = Integer.parseInt(numDislike_str);
//		System.out.println(numDislike_str);
		return numDislike;
	}
	//Get the score of attribute "Taste";
	public double getTasteAvg(Source source){
		List<Element> vPri = source.getAllElements("div class=\"bar\"");
		if(vPri.size() == 0){
			return -1;
		}
		String taste_str = vPri.get(0).getAllElements("nobr").get(0).getAttributeValue("title");
//		System.out.println(taste_str);
		return Double.parseDouble(taste_str);
	}
	//Get the score of attribute "Environment";
	public double getEnvAvg(Source source){
		List<Element> vPri = source.getAllElements("div class=\"bar\"");
		if(vPri.size() == 0){
			return -1;
		}
		String env_str = vPri.get(0).getAllElements("nobr").get(1).getAttributeValue("title");
//		System.out.println(env_str);
		return Double.parseDouble(env_str);
	}
	//Get the score of attribute "Service";
	public double getServiceAvg(Source source){
		List<Element> vPri = source.getAllElements("div class=\"bar\"");
		if(vPri.size() == 0){
			return -1;
		}
		String ser_str = vPri.get(0).getAllElements("nobr").get(2).getAttributeValue("title");
//		System.out.println(ser_str);
		return Double.parseDouble(ser_str);
	}
	//Get the score of attribute "Hygene";
	public double getHygeneAvg(Source source){
		List<Element> vPri = source.getAllElements("div class=\"bar\"");
		if(vPri.size() == 0){
			return -1;
		}
		String hyg_str = vPri.get(1).getAllElements("nobr").get(0).getAttributeValue("title");
//		System.out.println(hyg_str);
		return Double.parseDouble(hyg_str);
	}
	//Get the score of attribute "value for money";
	public double getMoneyAvg(Source source){
		List<Element> vPri = source.getAllElements("div class=\"bar\"");
		if(vPri.size() == 0){
			return -1;
		}
		String money_str = vPri.get(1).getAllElements("nobr").get(1).getAttributeValue("title");
//		System.out.println(money_str);
		return Double.parseDouble(money_str);
	}
	//Get the overall average score of a restaurant;
	public double getScoreWhole(Source source){
		List<Element> vPri = source.getAllElements("td width=\"64\"");
		if(vPri.size() == 0){
			return -1;
		}
		String overallScore = vPri.get(0).getAllElements(HTMLElementName.SPAN).get(0).getAttributeValue("title");
		double score = Double.parseDouble(overallScore);
//		System.out.println(score);
		return score;
	}
	
	
	public String getShopInfo(String shopID) throws Exception{
		String sourceURL = "http://www.openrice.com/restaurant/sr2.htm?shopid="+shopID;
		Source source=new Source(new URL(sourceURL));
		restaurantInfo rest = new restaurantInfo();
		String shopInfo = "";
		String price = rest.getShopPrice(source);
		System.out.println("Shop price is"+ price);
		String districtID = rest.getDistrictID(source);
		System.out.println("district id is"+ districtID);
		String cuisineID = rest.getCuisineID(source);
		String dishID = rest.getDishID(source);  //dishID may have more than one.
		int numLike = rest.getNumLike(source);
		int numOK = rest.getNumOK(source);
		int numDislike = rest.getNumDislike(source);
		double tasteAvg = rest.getTasteAvg(source);
		double envAvg = rest.getEnvAvg(source);
		double serviceAvg = rest.getServiceAvg(source);	
		double hygeneAvg = rest.getHygeneAvg(source);
		double moneyAvg = rest.getMoneyAvg(source);
		double scoreWhole = rest.getScoreWhole(source);
		shopInfo = shopID+","+price+","+districtID+","+dishID+","+numLike+","+numOK+","+numDislike+","+tasteAvg+","+envAvg+","+serviceAvg+","
		+hygeneAvg+","+moneyAvg+","+scoreWhole;
		return shopInfo;
	}

	public static void main(String[] args) throws Exception{
		String shopID = "14029";
		restaurantInfo restInfo = new restaurantInfo();
		String shopInfo = restInfo.getShopInfo(shopID);
		System.out.println(shopInfo);
	}
}

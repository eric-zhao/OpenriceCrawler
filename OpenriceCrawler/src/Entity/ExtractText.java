package Entity;

import net.htmlparser.jericho.*;

import java.util.*;
import java.io.*;
import java.net.*;

public class ExtractText {
	public static void main(String[] args) throws Exception {
		String sourceUrlString="http://www.openrice.com/restaurant/sr2.htm?shopid=7784";
//		String sourceUrlString= "http://www.openrice.com/gourmet/reviews.htm?userid=16346";
//		String sourceUrlString="data/test.html";
		if (args.length==0)
		  System.err.println("Using default argument of \""+sourceUrlString+'"');
		else
			sourceUrlString=args[0];
		if (sourceUrlString.indexOf(':')==-1) sourceUrlString="file:"+sourceUrlString;
		MicrosoftConditionalCommentTagTypes.register();
		PHPTagTypes.register();
		PHPTagTypes.PHP_SHORT.deregister(); // remove PHP short tags for this example otherwise they override processing instructions
		MasonTagTypes.register();
		Source source=new Source(new URL(sourceUrlString));
		
//		List<Element> priceElements = source.getAllElements("id", "district_id", false);
//		List<Element> priceElements = source.getAllElements("div class=\"iedropdownlistfixdiv\"");
//		if(priceElements.isEmpty()){
//			System.out.println("I am empty");
//		}
//		else{
//			Element info = priceElements.get(0);
//			List<Element> v_option = info.getAllElements(HTMLElementName.OPTION);
//			System.out.println("Number of options are"+ v_option.size());
//			for (Element option : v_option) {
//				String str = option.getAttributeValue("lat");
//				System.out.println(str);
//			}
//		}
//		
		List<Element> spanElements=source.getAllElements(HTMLElementName.SPAN);
		System.out.println("Number of span is"+ spanElements.size());
		for (Element spanElement : spanElements) {
			String label=spanElement.getContent().getTextExtractor().toString();
			System.out.println("labe "+ label);
		}
		
		
		
		
		
		
		// Call fullSequentialParse manually as most of the source will be parsed.
		source.fullSequentialParse();

		System.out.println("Document title:");
		String title=getTitle(source);
		System.out.println(title==null ? "(none)" : title);
		
		
		
	
		System.out.println("\nLinks to other documents:");
		List<Element> linkElements=source.getAllElements(HTMLElementName.A);
		for (Element linkElement : linkElements) {
			String href=linkElement.getAttributeValue("href");
			
			if (href==null) continue;
			// A element can contain other tags so need to extract the text from it:
			String label=linkElement.getContent().getTextExtractor().toString();
			System.out.println(label+" <"+href+'>');
		}

		System.out.println("\nAll text from file (exluding content inside SCRIPT and STYLE elements):\n");
		System.out.println(source.getTextExtractor().setIncludeAttributes(true).toString());

		System.out.println("\nSame again but this time extend the TextExtractor class to also exclude text from P elements and any elements with class=\"control\":\n");
		
  }

	private static String getTitle(Source source) {
		Element titleElement=source.getFirstElement(HTMLElementName.TITLE);
		if (titleElement==null) return null;
		// TITLE element never contains other tags so just decode it collapsing whitespace:
		return CharacterReference.decodeCollapseWhiteSpace(titleElement.getContent());
	}

	private static String getMetaValue(Source source, String key) {
		for (int pos=0; pos<source.length();) {
			StartTag startTag=source.getNextStartTag(pos,"name",key,false);
			if (startTag==null) return null;
			if (startTag.getName()==HTMLElementName.META)
				return startTag.getAttributeValue("content"); // Attribute values are automatically decoded
			pos=startTag.getEnd();
		}
		return null;
	}
}
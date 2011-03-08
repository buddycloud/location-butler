package com.buddycloud.utils;

public class HtmlUtils {
	
	public static String removeTags(String html){
		int safeguard = 0;
	    while (html.contains("<") && safeguard<50){
	    	int i1 = html.indexOf("<");
	    	int i2 = html.indexOf(">")+1;
	    	html = html.substring(0,i1)+html.substring(i2, html.length());
	    	safeguard++;
	    }
	    return html;

	}
	
	public static String html2xhtml(String html){
		html = html.replace("< ", "<");
		html = html.replace(" >", ">");
		html = html.replace(" />", "/>");
		html = closeElements(html, "br");
		html = closeElements(html, "img");
		html = assertRequiredAttribute(html, "img", "alt");
		
		return html;
		
	}
	
	private static String closeElements(String html, String elementName){
		
		int openStartIndex = html.indexOf("<"+elementName);
		int safeGuard = 0;
		while(openStartIndex>=0 && safeGuard <1e5){
			int openEndIndex = html.indexOf(">", openStartIndex);
			int closeStartIndex = html.indexOf("</"+elementName, openStartIndex);
			
			// if no </tag> found
			if(closeStartIndex<0){
				// check if it is closed with />
				if(html.charAt(openEndIndex-1)!='/'){
					html = html.substring(0, openEndIndex)+"/"+html.substring(openEndIndex);
					System.out.println("closed tag <"+elementName+"> at pos "+openEndIndex);
				}
			}
			openStartIndex = html.indexOf("<"+elementName, openEndIndex);
		}
		if(safeGuard>=1e5) throw new RuntimeException("Infinite loop detected while closing elemnts of html string: "+html);
		
		return html;
		
	}
	
	public static String assertRequiredAttribute(String html, String elementName, String atrName){
		
		int openStartIndex = html.indexOf("<"+elementName);
		int safeGuard = 0;
		while(openStartIndex>=0 && safeGuard <1e5){
			int openEndIndex = html.indexOf(">", openStartIndex);
			
			String attributes = html.substring(openStartIndex+elementName.length()+2, openEndIndex-1);
			String[] atrs = attributes.split(" ");
			boolean found = false;
			for(String atr : atrs){
				String[] nameValue = atr.split("=");
				if(clearLeadingAndTrailingWhitespace(nameValue[0]).equals(atrName)) found = true;
			}
			
			if(!found){
				html = html.substring(0, openEndIndex-1) + " "+atrName+"=\"\"" + html.substring(openEndIndex-1);
			}
			
			openStartIndex = html.indexOf("<"+elementName, openEndIndex);
		}
		if(safeGuard>=1e5) throw new RuntimeException("Infinite loop detected while closing elemnts of html string: "+html);
		
		return html;
	}
	
	private static String clearLeadingAndTrailingWhitespace(String s){
		while(s.startsWith(" ")) s = s.substring(1);
		while(s.endsWith(" "))s = s.substring(0, s.length()-1);
		return s;
	}
	
	
	public static void main(String[] args){
		String test = "";
		test += "<a href=\"http://m.buddycloud.com/ads\">\n";
		test += "  <img src=\"http://m.buddycloud.com/default.png\" ><br>\n";
		test += "</a>";
		
		String xhtml = html2xhtml(test);
		System.out.println(test);
		System.out.println();
		System.out.println(xhtml);
	}
	
	
	
}

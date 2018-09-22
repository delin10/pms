package pms.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternUtil {
	public static int getStart(String source,String regex) {
		Pattern pattern=Pattern.compile(regex);
		Matcher matcher=pattern.matcher(source);
		
		if (matcher.find()) {
			return matcher.start();
		}
		return -1;
	}
	
	public static String getSubString(String source,String regex) {
		Pattern pattern=Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
		Matcher matcher=pattern.matcher(source);
		//System.out.println(regex);
		if (matcher.find()) {
			//System.out.println(matcher.groupCount());
			return matcher.groupCount()==0?matcher.group(0):matcher.group(1);
		}
		return "";
	}
	
	public static String match(String regex,String text) {
		Pattern pattern=Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
		Matcher matcher=pattern.matcher(text);
		
		//System.out.println(regex);
		if (matcher.find()) {
			return matcher.group(0);
		}
		
		return "";
	}
	
	public static boolean isNumber(String str) {
		Pattern pattern=Pattern.compile("^-?[\\d]*\\.?[0-9]*$",Pattern.CASE_INSENSITIVE);
		Matcher matcher=pattern.matcher(str);
		return matcher.find();
	}
}

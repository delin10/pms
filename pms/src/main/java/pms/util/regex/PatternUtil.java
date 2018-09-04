package pms.util.regex;

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
		Pattern pattern=Pattern.compile(regex);
		Matcher matcher=pattern.matcher(source);
		
		if (matcher.find()) {
			return matcher.group(1);
		}
		return "";
	}
}

package pms.util.regex;

import java.util.ArrayList;
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
	
	public static ArrayList<String> groups(String source,String regex) {
		Pattern pattern=Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
		Matcher matcher=pattern.matcher(source);
		//System.out.println(regex);
		if (matcher.find()) {
			ArrayList<String> groups=new ArrayList<>();
			//注意groupCount的值
			for (int i=0;i<=matcher.groupCount();++i) {
				groups.add(matcher.group(i));
			}
			return groups;
		}
		return null;
	}
	
	public static ArrayList<ArrayList<String>> allMatch(String source,String regex) {
		Pattern pattern=Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
		Matcher matcher=pattern.matcher(source);
		ArrayList<ArrayList<String>> all_matcher=new ArrayList<>();
		//System.out.println(regex);
		while (matcher.find()) {
			ArrayList<String> groups=new ArrayList<>();
			//注意groupCount的值
			for (int i=0;i<=matcher.groupCount();++i) {
				groups.add(matcher.group(i));
			}
			//System.out.println(matcher.group());
			all_matcher.add(groups);
		}
		return all_matcher;
	}
	
	public static String match(String regex,String text) {
		//System.out.println(regex);
		Pattern pattern=Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
		Matcher matcher=pattern.matcher(text);
		
		//System.out.println(regex);
		if (matcher.find()) {
			//System.out.println("ok");
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

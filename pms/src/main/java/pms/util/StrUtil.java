package pms.util;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StrUtil {
	public static String firstLetterToUpperCase(String str) {
		if (str == null || str.isEmpty()) {
			return str;
		}

		char[] ch = str.toCharArray();
		if (ch[0] >= 'a' && ch[0] <= 'z') {
			ch[0] = (char) (ch[0] - 32);
		}
		return new String(ch);
	}

	/**
	 * ²ð·Ö×Ö·û´®
	 */
	public static String[] splitString(String string, int len) {
		int x = string.length() / len;
		int y = string.length() % len;
		int z = 0;
		if (y != 0) {
			z = 1;
		}
		String[] strings = new String[x + z];
		String str = "";
		for (int i = 0; i < x + z; i++) {
			if (i == x + z - 1 && y != 0) {
				str = string.substring(i * len, i * len + y);
			} else {
				str = string.substring(i * len, i * len + len);
			}
			strings[i] = str;
		}
		return strings;
	}
	
	public static String[] split(String text, String regex, int left, int right) {
		Matcher match=Pattern.compile(regex).matcher(text);
		ArrayList<String> list=new ArrayList<>();
		int start=0;
		int end=0;
		
		while(match.find()) {
			end=match.start()+left;
			list.add(text.substring(start, end).trim());
			start=match.end()-right;
		}
		list.add(text.substring(start).trim());
		return list.toArray(new String[0]);
	}

	public static String replaceAllCaseIgnored(String text,String regex,String replace) {
		return Pattern.compile(regex,Pattern.CASE_INSENSITIVE).matcher(text).replaceAll(replace);
	}
	
	public static boolean isEmpty(char c) {
		return c==' '||c=='\r'||c=='\n'||c=='\t';
	}
}

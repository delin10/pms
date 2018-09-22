package pms.util;

import java.security.SecureRandom;

public class RandomUtil {
	private static SecureRandom random=new SecureRandom();
	public static int randomRangeOf(int start , int end) {
		return (int) (start + (end-start)*Math.random());
	}
	
	public static int generateSecureRandomInt(int start,int end) {
		if (end<start) {
			return  0;
		}
		return (int) (start + random.nextDouble()*(end-start));
	}
	
}

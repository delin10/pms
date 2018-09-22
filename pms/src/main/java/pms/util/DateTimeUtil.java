package pms.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {
	public static String timeFormatter="HH:mm:ss";
	public static String dateFormatter="yyyy-MM-dd";

	public static String getDateTime() {
		return LocalDate.now().format(DateTimeFormatter.ofPattern(dateFormatter))
				+" "+LocalTime.now().format(DateTimeFormatter.ofPattern(timeFormatter));
	}
	
	public static boolean isExpire_Second(long origin, long target, long gap) {
		return Instant.ofEpochSecond(origin).plusSeconds(gap).isBefore(Instant.ofEpochSecond(target));
	}
	
	public static void main(String[]args) throws InterruptedException {
		long now=Instant.now().getEpochSecond();
		
		Thread.sleep(4000);
		
		long target=Instant.now().getEpochSecond();
		System.out.println(isExpire_Second(now,target,5));
	}
}

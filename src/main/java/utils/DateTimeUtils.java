package utils;

import java.time.Instant;

public class DateTimeUtils {
	
	public static void assertValidISO8601(String timestamp) {
		
		try {
			Instant.parse(timestamp);
			
		}catch(Exception e) {
			
			throw new AssertionError("Invalid timestamp format : " + timestamp);
		}
	}
	
	

}

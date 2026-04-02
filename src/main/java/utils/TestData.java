package utils;


import java.util.UUID;

import com.github.javafaker.Faker;

public class TestData {

	private static final Faker faker = new Faker();
	
	public static String getName() {
		
		return faker.name().fullName();
	}
	
	public static String getFirstName() {
		
		return faker.name().firstName();
	}
	
	public static String getLastName() {
		
		return faker.name().lastName();
	}
	
	public static String getEmail() {
		
		// BEST Practice (always)
		return "auto_" + System.currentTimeMillis() + "_" + faker.internet().emailAddress();
	}
	
	public static String getPhoneNumber() {
		
		return faker.phoneNumber().cellPhone();
	}
	
	public static String getGender() {

		return faker.options().option("male", "female");
	}
	
	public static String getStatus() {
		
		return faker.options().option("active", "inactive");
	}
	
	public static String getUsername() {
		
		return faker.name().username();
	}
	
	public static long getRandomNumber() {
		
		return faker.number().randomNumber();
	}
	
	public static int getRandomNumberInRange(int min, int max) {
		
		return faker.number().numberBetween(min, max);
	}
	
	// UUID (INTERVIEW FAVORITE)
	public static String getUUID() {
		
		return UUID.randomUUID().toString();
	}
	
	// Company / Org (ENTERPRISE APIs)
	public static String getCompanyName() {
		
		return faker.company().name();
	}
	
	public static String getCity() {
		
		return faker.address().cityName();
	}
	
	public static String getCountry() {
		
		return faker.address().country();
	}
	
	public static String getZipCode() {
		
		return faker.address().zipCode();
	}
	
	public static String getState() {
		
		return faker.address().state();
	}
	
	// SIMILARLY you can write building name, room no., area name, location, zipcode, etc.
	
	public static String getRandomSentence() {
		
		return faker.lorem().sentence();
	}
	
	public static String getRandomParagraph() {
		
		return faker.lorem().paragraph();
	}
	

}

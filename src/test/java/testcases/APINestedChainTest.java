package testcases;

import org.testng.annotations.Test;

import pojo.User;
import utils.APIUtils;
import base.TestBase;
import utils.TestData;

// NOTE : DO NOT RUN THIS FILE AS https://gorest.co.in/public/v2 DOESN'T SUPPORT NESTED API STRUCTURE...

public class APINestedChainTest extends TestBase {
	
	@Test(enabled = false) // Disabled: GoRest doesn't support nested API structure
	void nestedPOJOTest() {
		
		// NOTE: This test uses a nested POJO structure (User with Address)
		// GoRest API (https://gorest.co.in/public/v2) does NOT support nested objects.
		// Keeping this code commented for reference / future use with E-Invoice API.
		
		/*
		Address address = new Address(
				TestData.getCity(),
				TestData.getCountry(),
				TestData.getZipCode().toString()
		);
		
		User user = new User(
				TestData.getName(),
				TestData.getEmail(),
				"active",
				address
		);
		
		int id = APIUtils.createUser(user);
		*/
				
		
	}
	
	

}

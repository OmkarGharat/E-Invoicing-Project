package testcases;

import static io.restassured.RestAssured.given;

import org.testng.annotations.Test;

import base.BaseResponse;
import pojo.User;
import rest.RequestBuilder;

public class UserCRUDTest {

	int userId;

	@Test(priority = 1)
	void createUser() {

		// @formatter:off
		 User user = new User(
	                "Omkar Builder Test",
	                "male",
	                "omkar" + System.currentTimeMillis() + "@gmail.com",
	                "active"
	        );
		 
		 userId = given()
				 	.spec(RequestBuilder.createRequest("gorest", user))
				 	
				 .when()
				 	.post("/users")
				 	
				 .then()
				 	.spec(BaseResponse.get201Spec())		// reusable VALIDATION
				 	.extract().path("id");
		 
	    System.out.println("✨ CREATED USER ID = " + userId);
	}
	
	// GET USER
	@Test(priority = 2, dependsOnMethods = {"createUser"})
	void getUser() {
		
		given()
			.spec(RequestBuilder.getRequest("gorest"))
			
		.when()
			.get("/users/" + userId)
			
		.then()
			.spec(BaseResponse.get200Spec());		// reusable VALIDATION	
	}
	
	// UPDATE USER (PUT)
	@Test(priority = 3, dependsOnMethods = {"createUser"})
	void updateUser() {
		
		User updated = new User(
                "Omkar Updated",
                "male",
                "omkar" + System.currentTimeMillis() + "@gmail.com",
                "active"
        );
		
		given()
			.spec(RequestBuilder.updateRequest("gorest", updated))
			
		.when()
			.put("/users/" + userId)
			
		.then()
			.spec(BaseResponse.get200Spec());
	}
	
	// PATCH USER (Partial Update)
	@Test(priority = 4, dependsOnMethods = {"createUser"})
	void patchUser() {
		
		User partial = new User();
		partial.setStatus("inactive");
		
		given()
			.spec(RequestBuilder.patchRequest("gorest", partial))
			
		.when()
			.patch("/users/" + userId)
			
		.then()
			.spec(BaseResponse.get200Spec());
	}
	
	// DELETE USER
	@Test(priority = 5, dependsOnMethods = {"createUser"})
	void deleteUser() {
		
		given()
			.spec(RequestBuilder.deleteRequest("gorest"))
			
		.when()
			.delete("/users/" + userId)
			
		.then()
			.statusCode(204);
		
        System.out.println("🗑 USER DELETED: " + userId);
	}
}

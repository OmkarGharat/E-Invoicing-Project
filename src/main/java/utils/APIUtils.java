package utils;

import static io.restassured.RestAssured.given;

import base.BaseResponse;
import io.restassured.response.Response;
import rest.RequestBuilder;

//NOTE GorestService is exactly same as APIUtils.java

public class APIUtils {

	public static int createUser(Object body) {

//		List<String> list = new ArrayList<>();

		// @formatter:off
		int id = given()
					.spec(RequestBuilder.createRequest("gorest", body))
					
				.when()
					.post("/users/")
					
				.then()
	            	.log().ifValidationFails() // This will show the server's error message
					.spec(BaseResponse.get201Spec())
					.extract().path("id");					
	
		return id;
	}
	
	public static <T> T getUser(int id, Class<T> responseClass) {
		
		Response response = given()
								.spec(RequestBuilder.getRequest("gorest"))
								
							.when()
								.get("/users/" + id)
								
							.then()
								.spec(BaseResponse.get200Spec())
								.extract().response();
					
		return response.as(responseClass);		
	}
	
	// UPDATE USER (PUT)
	
	public static void updateUser(int id, Object body) {
		
		given()
			.spec(RequestBuilder.updateRequest("gorest", body))
			
		.when()
			.put("/users/" + id)
			
		.then()
			.spec(BaseResponse.get200Spec());
	}
	
	// DELETE
	
	public static void deleteUser(int id) {
		
		given()
			.spec(RequestBuilder.deleteRequest("gorest"))
			
		.when()
			.delete("/users/" + id)
			
		.then()
			.statusCode(204);
		
	}
	
	
}

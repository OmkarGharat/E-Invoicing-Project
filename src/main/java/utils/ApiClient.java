package utils;

import java.util.Map;

import static io.restassured.RestAssured.given;

import base.BaseResponse;
import io.restassured.response.Response;
import rest.RequestBuilder;

public class ApiClient {

	public static Response get(String path) {

		//@formatter:off
        return given()
        				.spec(RequestBuilder.getRequest())
                	
                .when()
                		.get(path)
                	
                .then()
                		.spec(BaseResponse.get200Spec())
                		.extract().response();
    }
	
	// NOTE POST request
    public static Response postRequest(String path, Map<String, Object> validBody) {
    	
    			return given()
	      				.spec(RequestBuilder.createRequest(validBody))
	    					
	    				.when()
	    					.post(path)
		
    					.then()
    						.extract().response();
    	
    }
    
	// NOTE Negative POST request
    public static void negativePostRequest(String path, Map<String, Object> validBody, int expectedStatus) {
    	
    	Response response = given()
	      					 .spec(RequestBuilder.createRequest(validBody))
	    					
	    					.when()
	    					    .post(path);
							  
		  int actual = response.getStatusCode();
		  
		  if (actual == 405) {
			  
			  throw new IllegalArgumentException(
			            "❌ Wrong path: " + path + " does not allow POST (it only supports GET)."
					  );
		}
		  
	      response.then()
	        	  .statusCode(expectedStatus);
    	
    }
	
	 public static Response get(String path, Map<String, Object> queryParams) {
	       
		//@formatter:off
	        return given()
	                		.spec(RequestBuilder.getRequest())
	 					.queryParams(queryParams)

	               .when()
	                		.get(path)
	                	
	               .then()
	                		.spec(BaseResponse.get200Spec())
	                		.extract().response();
	    }
	
//	// Raw version (no validation - useful for negative tests)
//    public static Response getRaw(String path, Map<String, Object> queryParams) {
//        
//    	return given()
//                	.spec(RequestBuilder.getRequest())
//                	.queryParams(queryParams)
//                	
//                .when()
//                	.get(path);
//    }
}

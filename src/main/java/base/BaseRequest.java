package base;

import config.ConfigReader;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;

public class BaseRequest {

	// PART 1: The 'static' variable that holds the single instance in memory
//	public static RequestSpecification requestSpec;

	// PART 2: The static access method (the only way to get the object)
	public static RequestSpecification getRequestSpecification(String serviceName) {

		String baseURI = ConfigReader.get(serviceName + ".baseURI");
		String authType = ConfigReader.get(serviceName + ".authType").toUpperCase();

		// PART 3: The "Lazy Initialization" guard (the core of the singleton logic)
		// RequestSpecification spec = new RequestSpecBuilder().build();

		// @formatter:off
			// This complex object creation code runs ONLY IF requestSpec is null
	    RequestSpecBuilder builder = new RequestSpecBuilder()
							.setBaseUri(baseURI)
							.setContentType("application/json")
							.addHeader("Accept", "application/json")
							.log(LogDetail.ALL);
	    
	    switch(authType) {
	    
	    case "BEARER" : 
	    		builder.addHeader("Authorization", "Bearer" + " " + ConfigReader.get(serviceName + ".token"));
	    		break;
	    		
	    case "BASIC":
            	builder.setAuth(RestAssured.basic("user", "pass"));
            	break;
            
	    case "NONE":
	    		
	    default:
	    		// No auth header added
	    		break;
	    }
	    return builder.build();
	    
		
        // PART 4: Always return the single, existing object
//		return requestSpec;
	}
}

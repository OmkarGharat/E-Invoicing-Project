package base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import config.ConfigReader;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import utils.ApiLogCapture;

public class BaseRequest {

	private static final Logger logger = LogManager.getLogger(BaseRequest.class);

	public static RequestSpecification getRequestSpecification() {

		String baseURI = ConfigReader.get("baseURI");
		String authType = ConfigReader.get("authType").toUpperCase();

		logger.info("Building request spec | baseURI: {}", baseURI);

		// @formatter:off
	    RequestSpecBuilder builder = new RequestSpecBuilder()
					.setBaseUri(baseURI)
					.setContentType("application/json")
					.addHeader("Accept", "application/json")
					// Silently capture every request+response — printed only on test failure
					.addFilter(new ApiLogCapture());
	    
	    switch(authType) {
	    
	    case "APIKEY":
	        builder.addHeader("x-api-key", ConfigReader.get("apikey"));
	        break;
	    
	    case "BEARER" : 
	    		builder.addHeader("Authorization", "Bearer" + " " + ConfigReader.get("token"));
	    		break;
	    		
	    case "BASIC":
            	builder.setAuth(RestAssured.basic("user", "pass"));
            	break;
             
	    case "NONE":
	    		
	    default:
	    		// No auth header added
	    		break;
	    }
	    
	    logger.debug("Request spec built successfully with authType: {}", authType);
	    return builder.build();
	}
	
	public static RequestSpecification getUnauthenticatedSpec() {
		
		String baseURI = ConfigReader.get("baseURI");
		
		return new RequestSpecBuilder()
				.setBaseUri(baseURI)
				.setContentType("application/json")
				.addHeader("Accept", "application/json")
				.addFilter(new ApiLogCapture())
				.build();
	}
	
	
}

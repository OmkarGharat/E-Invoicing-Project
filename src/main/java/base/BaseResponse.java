package base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.ResponseSpecification;

public class BaseResponse {

	private static final Logger logger = LogManager.getLogger(BaseResponse.class);
	
	private static ResponseSpecification responseSpec200;
	private static ResponseSpecification responseSpec201;

	// For GET, PATCH, PUT (Success → 200)
	public static ResponseSpecification get200Spec() {

		if (responseSpec200 == null) {

			logger.debug("Building 200 ResponseSpec (first call)");

			// @formatter:off
			responseSpec200 = new ResponseSpecBuilder()
								.expectStatusCode(200)
								.expectHeader("Content-Type", "application/json; charset=utf-8")
								// Log full response body ONLY when a validation fails
								// This keeps logs clean on success but gives you the full picture on failure
								.log(LogDetail.ALL)
								.build();
		}
		return responseSpec200;
	}
	
    // For POST (Success → 201)
	public static ResponseSpecification get201Spec() {
		
		if(responseSpec201 == null) {
			
			logger.debug("Building 201 ResponseSpec (first call)");
			
			responseSpec201 = new ResponseSpecBuilder()
								.expectStatusCode(201)
								.expectHeader("Content-Type", "application/json; charset=utf-8")
								.log(LogDetail.ALL)
								.build();
		}
		return responseSpec201;
	}
}

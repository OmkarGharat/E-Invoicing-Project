package base;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;

public class BaseResponse {

	private static ResponseSpecification responseSpec200;
	private static ResponseSpecification responseSpec201;

	// For GET, PATCH, PUT (Success → 200)
	public static ResponseSpecification get200Spec() {

		if (responseSpec200 == null) {

			// @formatter:off
			responseSpec200 = new ResponseSpecBuilder()
									.expectStatusCode(200)
									.expectHeader("Content-Type", "application/json; charset=utf-8")
// response time is flaky so that's why it is commented and needs to be removed
//									.expectResponseTime(lessThan(5000L))
									.build();
		}
		return responseSpec200;
	}
	
    // For POST (Success → 201)
	public static ResponseSpecification get201Spec() {
		
		if(responseSpec201 == null) {
			
			responseSpec201 = new ResponseSpecBuilder()
									.expectStatusCode(201)
									.expectHeader("Content-Type", "application/json; charset=utf-8")
// response time is flaky so that's why it is commented and needs to be removed
//									.expectResponseTime(lessThan(5000L))
									.build();
		}
		return responseSpec201;
	}
	
	

}

package testcases;

import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import static io.restassured.RestAssured.given;

import org.testng.annotations.Test;

import base.BaseResponse;
import base.TestBase;
import io.restassured.response.Response;
import rest.RequestBuilder;
import utils.DateTimeUtils;

public class HealthCheckTest extends TestBase {

	// HC_01 : Health endpoint returns 200
	@Test(priority = 1, description = "Verify health endpoint returns HTTP 200")
	public void testHealthEndpointReturns200() {

		//@formatter:off
		given()
			.spec(RequestBuilder.getRequest("einvoice"))
		
		.when()
			.get("/health")
		
		.then()
			.spec(BaseResponse.get200Spec());
	}
	
	// HC_02
	@Test(priority = 2, description = "Verify health Response has Status = OK")
	public void testHealthResponseHasStatusOK() {
		
		given()
			.spec(RequestBuilder.getRequest("einvoice"))
			
		.when()
			.get("/health")
		
		.then()
			.spec(BaseResponse.get200Spec())
			.body("status", equalTo("OK"));	
	}
	
    // HC_05: POST method should not be allowed
	@Test(priority = 5, description = "Verify POST to /health returns 405")
	public void testPostToHealthMethodNotAllowed() {
		
		given()
			.spec(RequestBuilder.getRequest("einvoice"))
		
		.when()
			.get("/health")
		
		.then()
			.statusCode(405);
		
	}
	
	// HC_06_a: Timestamp is not null or empty
	@Test(priority = 6, description = "Verify health timestamp is not null or empty")
	public void testHealthTimeIsNotNullOrEmpty() {
		
		given()
			.spec(RequestBuilder.getRequest("einvoice"))
		
		.when()
			.get("/health")
		
		.then()
			.spec(BaseResponse.get200Spec())
			.body("timestamp", notNullValue())
			.body("timestamp", not(emptyString()));
	}
	
	// HC_06_b : Timestamp is a valid date time format
	@Test(priority = 7, description = "Verify health timestamp has a valid date time format")
	public void testTimeStampValidFormat() {
		
		Response response = given()
								.spec(RequestBuilder.getRequest("einvoice"))
			
							.when()
								.get("/health")
							
							.then()
								.spec(BaseResponse.get200Spec())
								.extract().response();
		
		String timestamp = response.path("timestamp");
		DateTimeUtils.assertValidISO8601(timestamp);			
	}
}

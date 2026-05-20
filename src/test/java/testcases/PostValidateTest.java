package testcases;

import static io.restassured.RestAssured.*;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.*;
import java.lang.reflect.Method;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static io.restassured.module.jsv.JsonSchemaValidator.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import base.BaseResponse;
import base.TestBase;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.val;
import pojo.ApiSingleSampleResponse;
import pojo.EInvoicePayload;
import pojo.SamplesResponse;
import rest.RequestBuilder;
import utils.ApiClient;

public class PostValidateTest extends TestBase {

	private static final String VALIDATE_PATH = "/api/e-invoice/validate";
	private static final String SAMPLE_PATH = "/api/e-invoice/sample/1";
	
	// holds the original sample -- fetched ONCE
	private Map<String, Object> originalSampleBody;
	
	@BeforeClass
	public void fetchSampleData() {
	
		originalSampleBody = fetchSampleBody(SAMPLE_PATH); // inherited from TestBase
	}
	
	private Map<String, Object> freshBody() {
				
        return deepCopyMap(originalSampleBody); // inherited from TestBase
	}

	@Test(	
			priority = 1, 
			description = "Happy path: valid payload → 200, isValid: true"
		 )
	public void testHappyPaths() {

		Response sampleResponse = ApiClient.get(SAMPLE_PATH);

		EInvoicePayload validPayload = sampleResponse
				.as(ApiSingleSampleResponse.class)
				.getData();

		//@formatter:off
		given()
			.spec(RequestBuilder.createRequest(validPayload))
		
		.when()
			.post(VALIDATE_PATH)
		
		.then()
			.spec(BaseResponse.get200Spec());

		//@formatter:on				
	}

	@Test(
			priority = 2, 
			description = "Test No Auth Returns 401 UnAuthorized."
		 )
	public void testNoAuthReturns401() {

		// Sending Empty Body
		// EInvoicePayload emptyPayload = new EInvoicePayload();

		//@formatter:off
		given()
			.spec(RequestBuilder.createUnauthenticatedRequest("{}"))
//			.baseUri("https://e-invoice-api.vercel.app")
//			.contentType(ContentType.JSON)
//			.accept(ContentType.JSON)
//			.body("{}") 

		.when()
			.post(VALIDATE_PATH)

		.then()
			.statusCode(401);
	}

	@DataProvider(name = "unsupportedMethods")
	public Object[][] unsupportedMethods() {

		return new Object[][] {

			{ "GET" }, { "PUT" }, { "DELETE" }, { "PATCH" }
		};
	}

	@Test(
			priority = 3, 
			description = "USING ANY OTHER HTTP METHOD other than POST should result in Method Not allowed 405", 
			dataProvider = "unsupportedMethods"
		 )
	public void testWrongMethodReturns405(String method) {

		// Here, {} means empty body

		given()
			.spec(RequestBuilder.createRequest("{}"))

		.when()
			.request(method, VALIDATE_PATH)

		.then()
			.statusCode(405);
	}

	@Test(
			priority = 4, 
			description = "sending empty body returns 400."
		 )
	public void testEmptyBodyReturns400() {

		// Empty body : Type 2
		Map<String, Object> emptyBody = new HashMap<>();
		
		given()
			.spec(RequestBuilder.createRequest(emptyBody))

		.when()
			.post(VALIDATE_PATH)

		.then()
			.statusCode(400);
	}

	@Test(
			priority = 5, 
			description = "testing json schema validation"
		 )
	public void testJsonSchemaValidation() {

		// TODO : The most important test case

		Response sampleResponse = ApiClient.get(SAMPLE_PATH);

		EInvoicePayload validPayload = sampleResponse
									 .as(ApiSingleSampleResponse.class)
									 .getData();

		given()
			.spec(RequestBuilder.createRequest(validPayload))

		.when()
			.post(VALIDATE_PATH)
			
		.then()
			.spec(BaseResponse.get200Spec())
	        .body(matchesJsonSchemaInClasspath("schemas/post-validate-schema.json"));

	}
	
	public Object[][] baseValues(){

		return new Object[][] {
			
			{"INVALID", 400}, // checkpoint 1: wrong format
			{null, 400} // checkpoint 2: missing/null value
		};
		
	}
	
	@DataProvider(name = "badValues")
	public Object[][] badValues(Method method){
		
		List<Object[]> finalData = new ArrayList<>();
		
		Collections.addAll(finalData, baseValues());
		
		ExtraBadValues extra = method.getAnnotation(ExtraBadValues.class);
		
		if(extra != null) {
			for (String raw : extra.value()) {
				
				Object parsedValue;
				
				  if (raw.matches("^-?\\d+$")) {
			            parsedValue = Integer.parseInt(raw);
			        } else if (raw.matches("^-?\\d+\\.\\d+$")) {
			            parsedValue = Double.parseDouble(raw);
			        } else if (raw.equalsIgnoreCase("true") || raw.equalsIgnoreCase("false")) {
			            parsedValue = Boolean.parseBoolean(raw);
			        } else {
			            parsedValue = raw; // fallback → keep as string
			        }
				
				finalData.add(new Object[] {parsedValue, 400});
			}
		}
		
		return finalData.toArray(new Object[0][]);
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface ExtraBadValues {
		String[] value();
	}

	@Test(
			priority = 6, 
			description = "testing invalid GSTIN", 
			dataProvider = "badValues"
		 )
	public void testBadGSTIN(Object badValue, int expectedStatus) {

		// NOTE : This single test will cover Buyer GSTIN and Seller GSTIN as their
		// regex are exactly same
//		
//		Response sampleResponse = ApiClient.get(SAMPLE_PATH);
//		Map<String, Object> validBody = sampleResponse.jsonPath().getMap("data");
		
		Map<String, Object> validBody = freshBody();
		
		// 2. Breaking GSTIN field
		Map<String, Object> sellerDtls = (Map<String, Object>) validBody.get("SellerDtls");
		
		sellerDtls.put("Gstin", badValue);
		
		// 3. Send and check
//		
//		  given()
//	        .spec(RequestBuilder.createRequest(validBody))
//	      .when()
//	        .post(VALIDATE_PATH)
//	      .then()
//	        .statusCode(expectedStatus);

		ApiClient.negativePostRequest(VALIDATE_PATH, validBody, expectedStatus);
	}

	@Test(
			priority = 7, 
			description = "Bad SupTyp → 400", 
			dataProvider = "badValues"
		 )
	public void testEnumBreak(Object badValue, int expectedStatus) {

		// NOTE Test enum break of SupTyp

		Map<String, Object> validBody = freshBody();
		
		Map<String, Object> tranDtls = (Map<String, Object>) validBody.get("TranDtls");
		tranDtls.put("SupTyp", badValue);
		
		ApiClient.negativePostRequest(VALIDATE_PATH, validBody, expectedStatus);
	}
	
	@Test(	
			priority = 8, 
			description = "TotItemVal wrong → 400", 
			dataProvider = "badValues"
		  )
	@ExtraBadValues({"12.45"})
	public void testTotalItemValWrong(Object badValue, int expectedStatus) {

		// Same pattern, break a number	
		// Breaking math

		Map<String, Object> validBody = freshBody();

//		Response sampleResponse = ApiClient.get(SAMPLE_PATH);
//		Map<String, Object> validBody = sampleResponse.jsonPath().getMap("data");
		
		Map<String, Object> valDtls = (Map<String, Object>) validBody.get("ValDtls");
		valDtls.put("TotInvVal", badValue);
		
		ApiClient.negativePostRequest(VALIDATE_PATH, validBody, expectedStatus);		
	}
	
	@Test(
			priority = 9,
			description = "Intra-state + IGST > 0 → 400"
		 )
	public void testIntraIGSTGreaterThanZero() {

		// intra means same
		// intra-state means same state
		
		Map<String, Object> validBody = freshBody();
		
		// Seller Details
		int MAHARASHTRA = 27; 							// StateCode
		String GSTIN = "27AWGPV7107B1Z1";
		
		Map<String, Object> sellerDtls = (Map<String, Object>) validBody.get("SellerDtls");
		sellerDtls.put("Gstin", GSTIN);
		sellerDtls.put("Stcd", MAHARASHTRA);
		
		Map<String, Object> buyerDtls = (Map<String, Object>) validBody.get("BuyerDtls");
		buyerDtls.put("Gstin", GSTIN);
		buyerDtls.put("Stcd", MAHARASHTRA);
		
		Map<String, Object> valDtls = (Map<String, Object>) validBody.get("ValDtls");
		valDtls.put("CgstVal", 0);
		valDtls.put("SgstVal", 0);
		valDtls.put("IgstVal", 67500);
		
		ApiClient.negativePostRequest(VALIDATE_PATH, validBody, 400);
	}
	
	
}

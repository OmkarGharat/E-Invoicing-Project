package testcases;

import static io.restassured.RestAssured.*;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import pojo.Item;
import pojo.SamplesResponse;
import rest.RequestBuilder;
import utils.ApiClient;
import utils.StateCodeUtils;

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
	
	@Test(priority = 10, description = "Tax consistency: IGST > 0 but same state → fail")
	public void testTaxConsistencyWithIGST() {
		
		Map<String, Object> validBody = freshBody();
		
		 int MAHARASHTRA = 27;
		 String buyerGSTIN = "27AWGPV7107B1Z1";
		 String sellerGSTIN = "27AAACB2902M1ZT";
		 
		 Map<String, Object> sellerDetails = (Map<String, Object>) validBody.get("SellerDtls");
		 sellerDetails.put("Gstin", buyerGSTIN);
		 sellerDetails.put("Stcd", MAHARASHTRA);
		 
		 Map<String, Object> buyerDetails = (Map<String, Object>) validBody.get("BuyerDtls");
		 buyerDetails.put("Gstin", sellerGSTIN);
		 buyerDetails.put("Stcd", MAHARASHTRA);
		 
		 Map<String, Object> valueDetails = (Map<String, Object>) validBody.get("ValDtls");
		 valueDetails.put("CgstVal", 0);
		 valueDetails.put("CgstVal", 0);
		 valueDetails.put("IgstVal", 67500);
		 
		 ApiClient.negativePostRequest(VALIDATE_PATH, validBody, 400);
		
	}
	
	@Test(
			priority = 11, 
			description = "Tax consistency: CGST/SGST but different states → fail"
		 )
	public void testTaxConsistencyWithCGSTAndSGST() {
		
		Map<String, Object> validBody = freshBody();
		
		 int MAHARASHTRA = 27;
		 int KARNATAKA = 29;
		 String buyerGSTIN = "27AWGPV7107B1Z1";
		 String sellerGSTIN = "29AAAGM0289C1ZF";
		 
		 Map<String, Object> sellerDetails = (Map<String, Object>) validBody.get("SellerDtls");
		 sellerDetails.put("Gstin", buyerGSTIN);
		 sellerDetails.put("Stcd", MAHARASHTRA);
		 
		 Map<String, Object> buyerDetails = (Map<String, Object>) validBody.get("BuyerDtls");
		 buyerDetails.put("Gstin", sellerGSTIN);
		 buyerDetails.put("Stcd", KARNATAKA);
		 
		 Map<String, Object> valueDetails = (Map<String, Object>) validBody.get("ValDtls");
		 valueDetails.put("CgstVal", 33750);
		 valueDetails.put("CgstVal", 33750);
		 valueDetails.put("IgstVal", 0);
		 
		 ApiClient.negativePostRequest(VALIDATE_PATH, validBody, 400);
	}
	
	@Test(
			priority = 12, 
			description = "Missing required section (no ItemList) → 400"
		 )
	public void testMissingRequiredFieldItemList() {
		
		Map<String, Object> invalidBody = freshBody();
		invalidBody.remove("ItemList");
		
		ApiClient.negativePostRequest(VALIDATE_PATH, invalidBody, 400);
	}
	
	@Test(	
			priority = 13, 
			description = "Pin-State mismatch → fail"
		 )
	public void testPinStateMismatch() {
		
		Map<String, Object> validBody = freshBody();
		
		 int KARNATAKA = 29;
		 String sellerGSTIN = "29AAAGM0289C1ZF";
		
		Map<String, Object> sellerDetails = (Map<String, Object>) validBody.get("SellerDtls");
		sellerDetails.put("Gstin", sellerGSTIN);
		sellerDetails.put("Stcd", KARNATAKA);
		
		Assert.assertEquals(
							 sellerGSTIN.substring(0, 2), 
							 String.valueOf(KARNATAKA), 
							 "Pincode and StateCode doesn't match with each other."
						   );
	}
	
	@Test(
			priority = 14, 
			description = "State code invalid (100) → fail"
		 )
	public void testInvalidStateCode() {
		
		Map<String, Object> invalidBody = freshBody();
		
		// State code invalid (100) → fail
		final String invalidStateCode = "100";
		
		Map<String, Object> sellerDetails = (Map<String, Object>) invalidBody.get("SellerDtls");
		sellerDetails.put("Stcd", invalidStateCode);
		
		ApiClient.negativePostRequest(VALIDATE_PATH, invalidBody, 400);	
	}
	
	@Test(
			priority = 15, 
			description = "Future date → fail"
		 )
	public void testFutureDate() {
		
		Map<String, Object> validBody = freshBody();
		final String futureDate = "25-05-2026";
		
		
		Map<String, Object> documentDetails = (Map<String, Object>) validBody.get("DocDtls");
		documentDetails.put("Dt", futureDate);
		
		ApiClient.negativePostRequest(VALIDATE_PATH, validBody, 400);
	}
	
	@Test(
			priority = 16, 
			description = "Negative UnitPrice → fail"
		 )
	public void testNegativeUnitPrice() throws JsonProcessingException {
		
		// Negative UnitPrice → fail
		
		Map<String, Object> validBody = freshBody();
		double unitPrice = -5000;
		
		List<Map<String, Object>> itemsList = (List<Map<String, Object>>) validBody.get("ItemList");
		
		Map<String, Object> firstItem = itemsList.get(0);
		firstItem.put("UnitPrice", unitPrice);
		
		ApiClient.negativePostRequest(VALIDATE_PATH, validBody, 400);
	}
	
//	@Test(
//			priority = 17, 
//			description = "POST/validate → POST/generate chain"
//		 )
//	public void testChain() {
//		
//		// I haven't understood this test what to do here...
//		
//	}
	
}

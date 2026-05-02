package testcases;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.File;

import static io.restassured.module.jsv.JsonSchemaValidator.*;
import static org.hamcrest.Matchers.*;
import static io.restassured.RestAssured.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.*;
import com.fasterxml.jackson.core.JsonProcessingException;

import base.BaseResponse;
import io.restassured.response.Response;
import pojo.Sample;
import pojo.SamplesResponse;
import rest.RequestBuilder;
import utils.ApiClient;
import utils.StateCodeUtils;

public class GetSamplesTests {

	private static final Logger log = LoggerFactory.getLogger(GetSamplesTests.class);
	private static final String INVOICES_PATH = "/api/e-invoice/samples";

	// INV_01 : Default GET returns HTTP 200 and Response Content-Type is JSON
	// @Test(priority = 1, description = "Default GET returns HTTP 200 and Response
	// Content-Type is JSON")
	public void testInvoiceStatus200() {

		//@formatter:off
		given()
			.spec(RequestBuilder.getRequest())
		
		.when()
			.get(INVOICES_PATH)
		
		.then()
			.spec(BaseResponse.get200Spec());
	}
	
	@Test(priority = 2, description = "JSON Schema Validation of Samples Class")
	public void testJsonSamplesSchemaValidation() {
		
		given()
			.spec(RequestBuilder.getRequest())
	
		.when()
			.get(INVOICES_PATH)
	
		.then()
			.assertThat()
			.spec(BaseResponse.get200Spec())
			.body("Checking if Samples JSON matches the samples-schema", 
				  matchesJsonSchemaInClasspath("schemas/samples-schema.json"));
	}
	
	@Test(priority = 4, description = "id cannot be duplicated")
	public void testIdCannotBeDuplicated() {
		
		
		
		
		
	}
	
	@Test(priority = 5, description = "invoiceNo cannot be empty string.")
	public void testInvoiceNumberNullOrEmpty() {
		
		//@formatter:off
		given()
			.spec(RequestBuilder.getRequest())

		.when()
			.get(INVOICES_PATH)

		.then()
			.spec(BaseResponse.get200Spec())
			.body("data.invoiceNo", everyItem(notNullValue()));
		

		// TODO : This can be handled using JSON Schema as well, it will save a lot of time 
		// as it is one time efforts only.

		
		//@formatter:off
//		Response response = given()
//								.spec(RequestBuilder.getRequest())
//				
//							.when()
//								.get(INVOICES_PATH)
//				
//							.then()
//								.spec(BaseResponse.get200Spec())
//								.extract().response();
//		
//		SamplesResponse sampleResponse = response.as(SamplesResponse.class);
//		
//		List<Sample> invoiceNumberList = sampleResponse.getData();
//		
//		boolean isInvoiceNoInvalid = invoiceNumberList.stream()
//				.anyMatch(sample -> sample.getInvoiceNo() == null || 
//									sample.getInvoiceNo().trim().isEmpty());
//		
//		Assert.assertFalse(isInvoiceNoInvalid, "Invoice Number is Invalid");	
	}
	
	@Test(priority = 6, description = "totalValue cannot be null.")
	public void testTotalValueNull() {
		
		//@formatter:off
			given()
				.spec(RequestBuilder.getRequest())

			.when()
				.get(INVOICES_PATH)

			.then()
				.spec(BaseResponse.get200Spec())
				.body("data.totalValue", everyItem(notNullValue()));
			
		// TODO : This can be handled using JSON Schema as well, it will save a lot of time 
		// as it is one time efforts only.
	}
	
	@Test(priority = 6, description = "Invoice Type must be a valid Invoice Type")
	public void invoiceTypeMustbeValid() {
		
		// Refer ApiClient utils class
		Response response = ApiClient.get(INVOICES_PATH);

		SamplesResponse sampleResponse = response.as(SamplesResponse.class);
		
		List<Sample> invoiceType = sampleResponse.getData();
		
		// Mutable List
		//		List<String> validInvoiceTypes = new ArrayList<>();

		// Immutable List
		List<String> validInvoiceTypes = List.of("B2B", "SEZWP", "SEZWOP", "EXPWP", "EXPWOP", "DEXP");
		
		boolean isValidInvoiceType = invoiceType.stream()
				.allMatch(inv -> validInvoiceTypes.contains(inv.getSupplyType()));
		
		Assert.assertTrue(isValidInvoiceType, "Invoice Type is not valid.");	
	}
	
	@Test(priority = 8, description = "Validating the Document Type.")
	public void testDocumentTypeMustBeValid() {
		
		// Refer ApiClient utils class
		Response response = ApiClient.get(INVOICES_PATH);
		
		SamplesResponse sampleResponse = response.as(SamplesResponse.class);
		List<Sample> documentType = sampleResponse.getData();
		
		List<String> validDocumentTypes = List.of("INV", "CRN", "DBN");
		
		boolean isValidDocumentType = documentType.stream()
				.allMatch(docType -> validDocumentTypes.contains(docType.getDocumentType()));
		
		Assert.assertTrue(isValidDocumentType, "Document Type is invalid.");
	}
	
	@Test(priority = 10, description = "Validating Buyer and Seller State Code")
	public void testValidBuyerSellerStateCode() {
		
		// Refer ApiClient utils class
		Response response = ApiClient.get(INVOICES_PATH);
		
		SamplesResponse sampleResponse = response.as(SamplesResponse.class);
		List<Sample> stateCodes = sampleResponse.getData();
		
		boolean isValidSellerStateCode = stateCodes.stream()
					.allMatch(code -> StateCodeUtils.isValidGSTINStateCode(code.getSellerState()));
		
		boolean isValidBuyerStateCode = stateCodes.stream()
				.allMatch(code -> StateCodeUtils.isValidGSTINStateCode(code.getBuyerState()));
		
		Assert.assertTrue(isValidBuyerStateCode, "Buyer State Code is Invalid");	
		Assert.assertTrue(isValidSellerStateCode, "Seller State Code is Invalid");	
	}
	
	@Test(priority = 11, description = "If StateCodes are different then isInterstate must be true.")
	public void testCaseNo11() throws JsonProcessingException {
		
		// TODO : need to check with Claude Opus 4.6
		
//		IsInterstate = SellerDtls.Stcd ≠ BuyerDtls.Pos
		
		Response response = ApiClient.get(INVOICES_PATH);
		
		SamplesResponse sampleresponse = response.as(SamplesResponse.class);
		List<Sample> sampleData = sampleresponse.getData();

		// Code to print the API Response in the console
//		ObjectMapper mapper = new ObjectMapper();
//		String json = mapper.writerWithDefaultPrettyPrinter()
//		                    .writeValueAsString(sampleData);
//		System.out.println(json);
		
		sampleData.forEach(sample -> {

			// Interstate means pos != sellerState different --> isInterstate must be true
			
			boolean expected = sample.isInterstate(); // true
			boolean actual = StateCodeUtils.isInterState(sample.getPlaceOfSupply(), sample.getSellerState());
			
			if (expected != actual) {
				log.error("Mismatch found for invoice {}: expected={}, actual={}", sample.getInvoiceNo(), expected, actual);
			}
			
			if (actual == true) {
//				System.out.print("ids : " + sample.getId() + ", ");				
				Assert.assertTrue(expected, "The isInterstate must be true.");
			}
		});
	}
	
	@Test(priority = 12, description = "If StateCodes are same then isInterstate must be false.")
	public void testCaseNo12() {
		
		// TODO : need to check with Claude Opus 4.6
		
		Response response = ApiClient.get(INVOICES_PATH);
		
		SamplesResponse samplesResponse = response.as(SamplesResponse.class);
		List<Sample> sampleData = samplesResponse.getData();
		
		sampleData.forEach(sample -> {
			
			boolean expected = sample.isInterstate(); // expecting false
			boolean actual = StateCodeUtils.isInterState(sample.getPlaceOfSupply(), sample.getSellerState());
			
			// Intrastate means pos == sellerState same --> isInterstate must be false
			
			if(expected != actual) {
				log.error("Mismatch found for invoice {}: expected={}, actual={}", sample.getInvoiceNo(), expected, actual);
			}
			
			if (actual == false) {
//				System.out.print("ids : " + sample.getId() + ", ");				
				Assert.assertFalse(expected, "The isInterstate must be false.");
			}
		}
		);
	}
	
	@Test(priority = 13, description = "itemCount cannot be less than 1")
	public void testItemCountNotLessThanOne() {
		
		Response response = ApiClient.get(INVOICES_PATH);
		
		SamplesResponse sampleResponse = response.as(SamplesResponse.class);
		List<Sample> sampleData = sampleResponse.getData();
		
		boolean isValidItemCount = sampleData.stream()
				.allMatch(inv -> 1 <= inv.getItemCount());
		
		Assert.assertTrue(isValidItemCount, "Item Count is Invalid.");
	}
	
	@Test(priority = 14, description = "To test whether endpoint actually works or not by sending the GET request")
	public void testWhetherEndpointActuallyWorks() {
		
		// TODO : Need to ask Claude Opus 4.6 about this test that whether this is correct or not.
		
		Response response = ApiClient.get(INVOICES_PATH);
		
		SamplesResponse sampleResponse = response.as(SamplesResponse.class);
		List<Sample> sampleData = sampleResponse.getData();
		
		sampleData.stream()
			.forEach(sample -> {
				
				String endPoint = sample.getEndpoint();
				
				String basePath = "https://e-invoice-api.vercel.app";
				
				Response res = get(basePath + endPoint);
				
				// Status Code Validation
				res.then().body(matchesJsonSchemaInClasspath("schema/samples-schema.json"));
				
				SamplesResponse detail = res.as(SamplesResponse.class);
				
				// Basic Validation
				Assert.assertTrue(detail.isSuccess());
				
				List<Sample> data = detail.getData();
				
				Assert.assertNotNull(detail.getData());				
			}
		);		
	}
	
	@Test(priority = 15, description = "endpoint cannot be empty string or null.")
	public void testEndPointCannotNull() {
		
		given()
			.spec(RequestBuilder.getRequest())
	
		.when()
			.get(INVOICES_PATH)
	
		.then()
			.spec(BaseResponse.get200Spec())
			.body("data.endpoint", not(emptyOrNullString()));		
	}
	
	@Test(priority = 16, description = "count is always greater than or equal to zero.")
	public void testCountIsGreaterThanOrEqualToZero() {
		
		given()
			.spec(RequestBuilder.getRequest())
	
		.when()
			.get(INVOICES_PATH)
	
		.then()
			.spec(BaseResponse.get200Spec())
			.body("count", greaterThanOrEqualTo(0));		
	}
	
	@Test(priority = 17, description = "pagination.page greater than or equal to 1")
	public void testPagination_Page_greaterThanOrEqualToOne() {
		
		given()
			.spec(RequestBuilder.getRequest())
	
		.when()
			.get(INVOICES_PATH)
	
		.then()
			.spec(BaseResponse.get200Spec())
			.body("pagination.page", greaterThanOrEqualTo(1));		
	}
	
	@Test(priority = 18, description = "pagination.limit greater than or equal to 1")
	public void testPagination_limit_greaterThanOrEqualToOne() {
		
		given()
			.spec(RequestBuilder.getRequest())
	
		.when()
			.get(INVOICES_PATH)
	
		.then()
			.spec(BaseResponse.get200Spec())
			.body("pagination.limit", greaterThanOrEqualTo(1));		
	}
	
	@Test(priority = 19, description = "pagination.total greater than or equal to 0")
	public void testPagination_total_greaterThanOrEqualToZero() {
		
		given()
			.spec(RequestBuilder.getRequest())
	
		.when()
			.get(INVOICES_PATH)
	
		.then()
			.spec(BaseResponse.get200Spec())
			.body("pagination.total", greaterThanOrEqualTo(0));		
	}
	
	@Test(priority = 20, description = "pagination.pages greater than or equal to 0")
	public void testPagination_pages_greaterThanOrEqualToZero() {
		
		given()
			.spec(RequestBuilder.getRequest())
	
		.when()
			.get(INVOICES_PATH)
	
		.then()
			.spec(BaseResponse.get200Spec())
			.body("pagination.pages", greaterThanOrEqualTo(0));		
	}
	
		@DataProvider(name = "unsupportedMethods")
		public Object[][] unsupportedMethods(){
			
			return new Object[][] {
				
				{"POST"}, {"PUT"}, {"DELETE"}, {"PATCH"}
				
			};
		}
	
	@Test(
			priority = 21, 
			description = "unsupported methods should return 405.",
			dataProvider = "unsupportedMethods",
			testName = "Unsupported Method : ${method}"
		 )
	public void unSupportedMethodShouldReturn405(String method) {
		
//		Reporter.log("Running unsupported method: " + method, true);
		
		given()
			.spec(RequestBuilder.getRequest())
			
		.when()
			.request(method, INVOICES_PATH)

		.then()
			.statusCode(405);
	}
	
	@Test(priority = 22, description = "To test whether data is sorted in ascending order only.")
	public void testSortByAscWorks() {
		
		Response response = 
				given()
				.spec(RequestBuilder.getRequest())
			
			.when()
				.get(INVOICES_PATH)
			
			.then()
				.spec(BaseResponse.get200Spec())
				.extract().response();
				
		List<Integer> actualList = response.jsonPath().getList("data.id");
		
		List<Integer> expectedList = new ArrayList<>(actualList);
		Collections.sort(expectedList);
		
		Assert.assertEquals(actualList, expectedList, "The data is not sorted in the ascending order.");
	}

}

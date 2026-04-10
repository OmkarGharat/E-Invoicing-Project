package testcases;

import static org.testng.Assert.assertTrue;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.instanceOf;

import static io.restassured.RestAssured.given;

import org.testng.Assert;
import org.testng.annotations.Test;

import base.BaseResponse;
import base.TestBase;
import io.restassured.response.Response;
import rest.RequestBuilder;

public class GetInvoiceTests1 extends TestBase {

	private static final String INVOICES_PATH = "/api/e-invoice/invoices";

	// INV_01 : Default GET returns HTTP 200
	@Test(priority = 1, description = "Default GET returns HTTP 200")
	public void testInvoiceStatus200() {

		//@formatter:off
		given()
			.spec(RequestBuilder.getRequest("einvoice"))
		
		.when()
			.get(INVOICES_PATH)
		
		.then()
			.spec(BaseResponse.get200Spec());
	}
	
	// INV_02 : Response Content-Type is JSON
	@Test(priority = 2, description = "Response Content-Type is JSON")
	public void testContentTypeIsJson() {
		
		given()
			.spec(RequestBuilder.getRequest("einvoice"))
		
		.when()
			.get(INVOICES_PATH)
		
		.then()
			.spec(BaseResponse.get200Spec());
	}
	
	// INV_03 : Top-level keys all present	
		@Test(priority = 3, description = "Top-level keys all present")
		public void testAllKeysArePresent() {
			
			given()
				.spec(RequestBuilder.getRequest("einvoice"))
		
			.when()
				.get(INVOICES_PATH)
		
			.then()
				.spec(BaseResponse.get200Spec())
				.body("keySet()", hasItems("success", "data", "pagination", 
										   "filters", "sort", "availableFields"));		
		}
	
	// INV_04 : success is boolean true
	@Test(priority = 4, description = "success is boolean true")
	public void testSuccesIsTrue() {
		
		given()
			.spec(RequestBuilder.getRequest("einvoice"))
		
		.when()
			.get(INVOICES_PATH)
		
		.then()
			.spec(BaseResponse.get200Spec())
			.body("success", equalTo(true));
	}

	// INV_05 : data is an array (List) (not null, not object)	
	@Test(priority = 5, description = "data is an array (List) (not null, not object)")
	public void testDataIsAList() {
		
		given()
			.spec(RequestBuilder.getRequest("einvoice"))
		
		.when()
			.get(INVOICES_PATH)
		
		.then()
			.spec(BaseResponse.get200Spec())
		
			// TODO find how it works internally ?
			.body("data", instanceOf(List.class));
	}
	
	// INV_06 : Each invoice object has all 19 required fields
	@Test(priority = 6, description = "Each invoice object has all 19 required fields")
	public void testAllDataFieldsArePresent() {
		
		given()
			.spec(RequestBuilder.getRequest("einvoice"))
			
		.when()
			.get(INVOICES_PATH)
		
		.then()
			.spec(BaseResponse.get200Spec())
			.body("data[0].keySet()", hasItems("id", "irn", "invoiceNo", "invoiceDate",
			        "sellerGstin", "sellerName",
			        "buyerGstin", "buyerName",
			        "supplyType", "documentType",
			        "totalValue", "status", "generatedAt",
			        "sellerState", "buyerState", "pos",
			        "isInterstate", "reverseCharge", "itemCount"));
	}
	
	// INV_07 : pagination object has all 6 fields
	@Test(priority = 7, description = "pagination object has all 6 fields")
	public void testAllPaginationFieldsArePresent() {
		
		given()
			.spec(RequestBuilder.getRequest("einvoice"))
		
		.when()
			.get(INVOICES_PATH)
		
		.then()
			.spec(BaseResponse.get200Spec())
			.body("pagination.keySet()", hasItems("page", "limit", "total", 
										 "pages", "hasNext", "hasPrev"));
	}
	
	// INV_08 : sort object has required fields
	@Test(priority = 8, description = "sort object has required fields")
	public void testAllSortHasAllFields() {
		
		given()
			.spec(RequestBuilder.getRequest("einvoice"))
			
		.when()
			.get(INVOICES_PATH)
			
		.then()
			.spec(BaseResponse.get200Spec())
			.body("sort.keySet()", hasItems("by", "order"));
	}
	
	// INV_09 : Default sort is by generatedAt descending
	@Test(priority = 9, description = "Default sort is by generatedAt descending")
	public void testSortIsGeneratedByDesc() {
		
		given()
			.spec(RequestBuilder.getRequest("einvoice"))
		
		.when()
			.get(INVOICES_PATH)
		
		.then()
			.spec(BaseResponse.get200Spec())
			.body("sort.by", equalTo("generatedAt"))
			.body("sort.order", equalTo("desc"));
		
		// Use https://jsonpathfinder.com to find the exact path to avoid mistakes
	}
	
	// INV_10 : Data is actually ordered by generatedAt descending
	@Test(priority = 10, description = "Data is actually ordered by generatedAt descending")
	public void testDataIsActuallySortedByGeneratedByDesc() {
		
		Response response = 
				given()
				.spec(RequestBuilder.getRequest("einvoice"))
			
			.when()
				.get(INVOICES_PATH)
			
			.then()
				.spec(BaseResponse.get200Spec())
				.extract().response();
		
		List<String> dates = response.jsonPath().getList("data.generatedAt");
		
		// TODO Haven't understood anything from this line. Need to understood and learn it
		// as i don't know how AI has written this
		boolean isSorted = IntStream.range(0, dates.size() - 1)
				.allMatch(i -> Instant.parse(dates.get(i))
						.isAfter(Instant.parse(dates.get(i + 1)))
						|| dates.get(i).equals(dates.get(i + 1)));		
		
	    assertTrue(isSorted, "generatedAt not sorted DESC");
	}
	
	//	INV_14 : limit=3 returns at most 3 results
	@Test(priority = 14, description = "limit=3 returns at most 3 results")
	public void testLimit3returnsMax3Results() {
	
		Response response = given()
								.spec(RequestBuilder.getRequest("einvoice"))
								.queryParam("limit", 3)
							
							.when()
								.get(INVOICES_PATH)
							
							.then()
								.spec(BaseResponse.get200Spec())
								.extract().response();
		
		var items = response.jsonPath().getList("data.id");
		
		System.out.println("Total Items inside data : " + items.size());
		
		var paginationLimit = response.jsonPath().getInt("pagination.limit");
		
		System.out.println("Pagination Limit = " + paginationLimit);
		
		Assert.assertEquals(items.size(), 3, "The API did not limit the results to 3");
		Assert.assertEquals(paginationLimit, 3, "The pagination Limit is not equal to 3");
	}
	
	// INV_15 : Page 2 with limit 3 returns correct page state
	@Test(priority = 15, description = "Page 2 with limit 3 returns correct page state.")
	public void testPage2Limit3returnsCorrectPageState() {
		
		// TODO : What do you mean by Correct Page State ??
		
		Response response = given()
								.spec(RequestBuilder.getRequest("einvoice"))
								.queryParam("page", 2)
								.queryParam("limit", 3)
							
							.when()
								.get(INVOICES_PATH)
							
							.then()
								.spec(BaseResponse.get200Spec())
								.extract().response();
		
		var paginationPage = response.jsonPath().getInt("pagination.page");
		var paginationLimit = response.jsonPath().getInt("pagination.limit");
		
		Assert.assertEquals(paginationPage, 2, "Page does not equal to 2");
		Assert.assertEquals(paginationLimit, 3, "Limit does not equal to 3");
	}
	
	// INV_16 : hasNext = false when all records fit on one page
	@Test(priority = 16, description = "hasNext = false when all records fit on one page")
	public void testHasNext() {
		
		Response response = given()
								.spec(RequestBuilder.getRequest("einvoice"))
			
							.when()
								.get(INVOICES_PATH)
							
							.then()
								.spec(BaseResponse.get200Spec())
								.extract().response();

		var paginationHasNext = response.jsonPath().getBoolean("pagination.hasNext");
		
		Assert.assertFalse(paginationHasNext, "hasNext is true");
	}
	
	// INV_17 : Combined filter (status + supplyType) filters correctly
	@Test(priority = 17, description = "Combined filter (status + supplyType) filters correctly")
	public void testCombinedFilter() {
		
		Map<String, Object> parameters = new HashMap<>();
		
		parameters.put("status", "Generated");
		parameters.put("supplyType", "B2B");
		
		Response response = given()
								.spec(RequestBuilder.getRequest("einvoice"))
								.queryParams(parameters)
								
							.when()
								.get(INVOICES_PATH)
								
							.then()
								.spec(BaseResponse.get200Spec())
								.extract().response();
		
		List<Map<String, Object>> allInvoices = response.jsonPath().getList("data");
		
//		long totalInvoicesReturned = allInvoices.size();
//			
//		var validInvoices = allInvoices.stream()
//						.filter(inv -> "Generated".equals(inv.get("status")))
//						.filter(inv -> "B2B".equals(inv.get("supplyType")))
//						.count();
//		
//		Assert.assertEquals(totalInvoicesReturned, validInvoices, "Expected 5 invoices with status=Generated and supplyType=B2B");

		// TODO Need to understand this and practice it and above code as well.
		boolean allMatch = allInvoices.stream()
					.allMatch(inv -> "Generated".equals(inv.get("status"))
							&&
					"B2B".equals(inv.get("supplyType")));
		
		Assert.assertTrue(allMatch, "Response contains records that do not match filter");
		
	}
	
	// INV_18 : Invalid status value
	@Test(priority = 18, description = "Invalid status value")
	public void invalidStatusValue() {
		
		given()
			.spec(RequestBuilder.getRequest("einvoice"))
			.queryParam("status", "BLAH")
			
		.when()
			.get(INVOICES_PATH)
		
		.then()
			.statusCode(400);
	}
	
	// INV_19 : page=0 (zero page)
	@Test(priority = 19, description = "page=0 (zero page)")
	public void pageValueIsZero() {
		
		given()
			.spec(RequestBuilder.getRequest("einvoice"))
			.queryParam("page", "0")
			
		.when()
			.get(INVOICES_PATH)
		
		.then()
			.statusCode(400);
	}
	
	// INV_20 : limit=0
	@Test(priority = 20, description = "limit=0")
	public void limitValueIsZero() {
		
		given()
			.spec(RequestBuilder.getRequest("einvoice"))
			.queryParam("limit", "0")
			
		.when()
			.get(INVOICES_PATH)
		
		.then()
			.statusCode(400);
	}
	
	// INV_21 : page=999 (beyond last page)
	@Test(priority = 21, description = "page=999 (beyond last page)")
	public void testLastPageValue() {
		
		given()
			.spec(RequestBuilder.getRequest("einvoice"))
			.queryParam("page", "999")
			
		.when()
			.get(INVOICES_PATH)
		
		.then()
			.statusCode(400);
	}
}

package testcases;

import static org.testng.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

import static io.restassured.RestAssured.given;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import base.BaseResponse;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import pojo.ApiSingleSampleResponse;
import pojo.EInvoicePayload;
import pojo.Item;
import pojo.SamplesResponse;
import rest.RequestBuilder;
import utils.ApiClient;
import utils.PinValidator;
import utils.StateCodeUtils;

public class GetOneSampleTest {

	private static final String SAMPLE_PATH = "/api/e-invoice/sample/1";

//	@Test(priority = 1, description = "GET api/e-invoice/sample/1 returns 200 and Content-Type is application/json")
	public void testSampleStatusAndContentType() {

		//@formatter:off
				given()
					.spec(RequestBuilder.getRequest())
				
				.when()
					.get(SAMPLE_PATH)
				
				.then()
					.spec(BaseResponse.get200Spec());
				
		//@formatter:on		
	}

//	@Test(priority = 5, description = "Verify that the Pin provided in SellerDtls or BuyerDtls matches the corresponding Stcd (State Code) as per India Post master data.")
	public void testPinMatchesWithStateCode() {
		
		// This test case is failing ... help me 
		// java.lang.AssertionError: Invalid PIN-State combinational in Seller details. expected [true] but found [false]


		Response response = ApiClient.get(SAMPLE_PATH);
		ApiSingleSampleResponse sampleResponse = response.as(ApiSingleSampleResponse.class);

		// Industry Standard: Use Optional to handle single objects in a "Stream-like"
		// way

		// TODO How can I be sure here that seller means only seller's pincode and state
		// and not buyer's ?
		// as seller here is just a word / predicator

		EInvoicePayload payload = sampleResponse.getData();

		String pin = (payload.getSellerDtls().getPinCode() != null) ? String.valueOf(payload.getSellerDtls().getPinCode()) : null;
		String state = payload.getSellerDtls().getStateCode();
										
		boolean isPincodeAndStateComboValid = PinValidator.isValidPinForState(pin, state);
					
		Assert.assertTrue(isPincodeAndStateComboValid, "Invalid PIN-State combinational in Seller details.");
	}

//	@Test(priority = 6, description = "buyerState and sellerState and Pos (Place of Supply) has value in between 1 to 38 and 96, 97, 99")
	public void testValidBuyerSellerAndPosCode() {

		Response response = ApiClient.get(SAMPLE_PATH);

		ApiSingleSampleResponse sampleResponse = response.as(ApiSingleSampleResponse.class);
		EInvoicePayload payload = sampleResponse.getData();
		Assert.assertNotNull(payload, "Data is null!");

		boolean isBuyerStateCodeValid = StateCodeUtils.isValidGSTINStateCode(payload.getBuyerDtls().getStateCode());
		boolean isSellerStateCodeValid = StateCodeUtils.isValidGSTINStateCode(payload.getSellerDtls().getStateCode());
		boolean isPosCodeValid = StateCodeUtils.isValidGSTINStateCode(payload.getBuyerDtls().getPlaceOfSupply());

		Assert.assertTrue(isBuyerStateCodeValid, "Buyer State Code is Invalid!");
		Assert.assertTrue(isSellerStateCodeValid, "Seller State Code is Invalid!");
		Assert.assertTrue(isPosCodeValid, "Place Of Supply State Code is Invalid!");
	}

//	@Test(priority = 7, description = "TotItemVal = AssAmt + CgstAmt + SgstAmt + IgstAmt")
	public void testBuyerSellerDiff() {

		Response response = ApiClient.get(SAMPLE_PATH);
		List<Item> payload = response.as(ApiSingleSampleResponse.class).getData().getItemList();

		payload.forEach(
				amount -> {
					Double totalExpectedAmount = amount.getAssessibleAmount() 
											   + amount.getCgstAmount()
											   + amount.getSgstAmount() 
											   + amount.getIgstAmount();
					
					Double totalActualItemValue = amount.getTotalItemValue();

					Assert.assertEquals(totalActualItemValue, 
										totalExpectedAmount,
										"Total Item Value Calculation Mismatch");
				}
		);
	}

//	@Test(priority = 8, description = "Amount = Qty * Rate (per item)")
	public void testAmountEqulsQtyXUnitPrice() {
		
		Response response = ApiClient.get(SAMPLE_PATH);
		List<Item> payload = response.as(ApiSingleSampleResponse.class).getData().getItemList();

		payload.forEach(
				
				amount -> {
					
					double ActualTotalAmount = amount.getQty() * amount.getUnitPrice();
					double expectedTotalAmount = amount.getUnitPrice();
					
					Assert.assertEquals(ActualTotalAmount, 
										expectedTotalAmount, 
										"Expected and Actual Total amount are not matching.");		
				}
				);
	}
	
	// Test Case 9 and 10 will be done using JSON Schema Validation
	
	@Test(priority = 11, description = "Missing Auth Header Returns 401 Unauthorized.")
	public void testMissingAuthHeaderReturns401() {
		
		RestAssured.baseURI = "https://e-invoice-api.vercel.app";
		
		given()
			.accept(ContentType.JSON)
	
		.when()
			.get(SAMPLE_PATH)
	
		.then()
			.statusCode(401);
	}
	
	@DataProvider(name = "unsupportedMethods")
	public Object[][] unsupportedMethods(){
		
		return new Object[][] {
			
			{"POST"}, {"PUT"}, {"DELETE"}, {"PATCH"}
		};
	}
	
	@Test(	
			priority = 12, 
			description = "USING ANY OTHER HTTP METHOD other than GET should result in Method Not allowed 405",
			dataProvider = "unsupportedMethods"	
		)
	public void testUnsupportedMethodsReturns405(String method) {
		
		given()
		.spec(RequestBuilder.getRequest())
		
		.when()
			.request(method, SAMPLE_PATH)

		.then()
			.statusCode(405);
	}
	
	@Test(priority = 13, description = "")
	public void testGSTINhasValidFormat() {
		
	}
	
	@Test(priority = 14, description = "If SupTyp is B2B, both parties must have regular GSTINs")
	public void testSupplyTypeB2BThenRegularGSTINforBothParties() {
		
		
		
	}
	
	@Test(priority = 17, description = "GET /sample/999 → 404 (invalid ID)")
	public void testInvalidIdIntegerReturns404() {
		
		
	}
	
	@Test(priority = 18, description = "")
	public void testInvalidIdStringReturns404() {
		
		
		
	}

}

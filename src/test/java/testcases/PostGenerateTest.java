package testcases;

import org.assertj.core.api.SoftAssertions;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

import base.BaseResponse;
import base.TestBase;
import io.restassured.response.Response;
import pojo.ApiSingleSampleResponse;
import pojo.EInvoicePayload;
import rest.RequestBuilder;
import utils.ApiClient;
import utils.QrCodeDecoderUtils;

public class PostGenerateTest extends TestBase {

	private static final String VALIDATE_PATH = "/api/e-invoice/generate";
	private static final String SAMPLE_PATH = "/api/e-invoice/sample/1";

	private Map<String, Object> originalSampleBody;

	@BeforeClass
	public void fetchSampleData() {
		originalSampleBody = fetchSampleBody(SAMPLE_PATH); // inherited from TestBase
	}

	private Map<String, Object> freshBody() {
		return deepCopyMap(originalSampleBody);
	}

	// ─── Layer 1: Contract ─────────────────────────────────

	@Test(priority = 2, description="JSON Schema Validation")
	public void testResponseMatchesSchema() {

		Response sampleResponse = ApiClient.get(SAMPLE_PATH);

		//@formatter:off

		EInvoicePayload validPayload = sampleResponse.as(ApiSingleSampleResponse.class)
												    .getData();
		
    		given()
    			.spec(RequestBuilder.createRequest(validPayload))
    		
    		.when()
    			.post(VALIDATE_PATH)
    		
    		.then()
    			.spec(BaseResponse.get200Spec())
    			.body(matchesJsonSchemaInClasspath("schemas/post-generate-schema.json"));
    	
		// IRN should be a 64-char hex string
		// AckNo should be a number, AckDt should be valid datetime
    		// SignedInvoice should be a non-empty string (JWT/JWS format)
		// important_notice field — UNIQUE to /generate

    		//@formatter:on
	}

	// ─── Layer 2: What's UNIQUE to /generate ───────────────

	@Test(priority = 4, description = "")
	public void testQRCodeIsValidBase64() {

		Map<String, Object> validBody = freshBody();

		// 1. Call your API
		Response response = ApiClient.postRequest(VALIDATE_PATH, validBody);

		// 2. Extract the field (use "data.QRCode" if using your current API response)
		String qrCode = response.jsonPath().getString("data.QRCode");
		
		System.out.println("####################################################################");
		
		System.out.println("qrCode : " + qrCode);
		
		System.out.println("####################################################################");

		// 3. Use your utility tool to open the hidden contents
		Map<String, String> decodedFields = QrCodeDecoderUtils.decodeStandardGstQr(qrCode);

		// 4. Extract the nested blocks from your input request body
		Map<String, Object> sellerDtls = (Map<String, Object>) validBody.get("SellerDtls");
		Map<String, Object> buyerDtls = (Map<String, Object>) validBody.get("BuyerDtls");
		Map<String, Object> docDtls = (Map<String, Object>) validBody.get("DocDtls");
		Map<String, Object> valDtls = (Map<String, Object>) validBody.get("ValDtls");

		SoftAssertions softly = new SoftAssertions();

		// 5. Compare what came out of the QR code with what you sent to the API
		softly.assertThat(decodedFields.get("sellerGstin"))
				.as("QR Field Check: Seller GSTIN")
				.isEqualTo(String.valueOf(sellerDtls.get("Gstin"))); // Links directly to "Gstin" in input

		softly.assertThat(decodedFields.get("buyerGstin"))
				.as("QR Field Check: Buyer GSTIN")
				.isEqualTo(String.valueOf(buyerDtls.get("Gstin")));

		softly.assertThat(decodedFields.get("docNo"))
				.as("QR Field Check: Document Number")
				.isEqualTo(String.valueOf(docDtls.get("No")));

		softly.assertThat(decodedFields.get("docDt"))
				.as("QR Field Check: Document Date")
				.isEqualTo(String.valueOf(docDtls.get("Dt")));

		softly.assertThat(decodedFields.get("totVal"))
				.as("QR Field Check: Total Invoice Value")
				.isEqualTo(String.valueOf(valDtls.get("TotInvVal")));

		// 6. Run all checks at once
		softly.assertAll();
	}

	// ─── Layer 2: Endpoint-specific error behavior ─────────

	@Test(priority = 3)
	public void testEmptyBodyReturns400() {
		// TODO YES, re-test this — it's testing the ENDPOINT's behavior, not the field
		// format
		// TODO Why we are retesting ?

		//@formatter:off
    		given()
    			.spec(RequestBuilder.createRequest("{}"))
    		
    		.when()
    			.post(VALIDATE_PATH)
    		
    		.then()
    			.statusCode(400);

	}

	@Test(priority = 1, description="Unauthorized acess, returns 401.")
	public void testNoAuthReturns401() {
		
		given()
			.spec(RequestBuilder.createUnauthenticatedRequest("{}"))
	
		.when()
			.post(VALIDATE_PATH)
	
		.then()
			.statusCode(401);
		
		// YES — each endpoint must independently reject unauthenticated requests LEARN WHY ? 
	}

	// ─── Layer 2: State change verification (THE BIG ONE) ──

	@Test(priority = 5, description = "The Chain Test")
	public void testGeneratedInvoiceIsRetrievable() {
		// POST /generate → get IRN → GET /invoices?irn=XXX → verify it exists
		// This is the "chain test" you were confused about!
		
		Map<String, Object> validBody = freshBody();
		
		Response response = ApiClient.postRequest(SAMPLE_PATH, validBody);
		String irn = response.jsonPath().getString("data.Irn");
		
		// TODO Find the difference between .get(), .getString(), .getMap(), .getList()
		
		Map<String, Object> newIRN = new HashMap<>();
		newIRN.put("data.Irn", irn);
		
		ApiClient.get("/api/e-invoice/invoices", newIRN);	
	}

	@Test(priority = 6, description = "Sending the same body after successful IRN generation must fail with status code 409.")
	public void testDuplicateGenerateReturnsSameIRN() {
		// Same payload twice → should return same IRN (idempotency) 
		// Or should it return 409 Conflict? Test the actual behavior.
		
		Map<String, Object> validBody = freshBody();
		
		// Sending the POST Request twice.
	    ApiClient.postRequest(VALIDATE_PATH, validBody);
	    
	    Response response2 = ApiClient.postRequest(VALIDATE_PATH, validBody);
		
		Assert.assertEquals(response2.getStatusCode(), 409, "Duplicate IRN. IRN already generated for this document.");
		
	}

	// ─── Layer 3: NOT duplicated ───────────────────────────

	// ❌ DON'T test bad GSTIN here
	// ❌ DON'T test intra-state IGST rules here
	// ❌ DON'T test pin-state mismatch here
	// ❌ DON'T test missing ItemList here
	// These are /validate's responsibility.
}

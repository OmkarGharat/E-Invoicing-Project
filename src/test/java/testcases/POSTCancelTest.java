package testcases;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import base.BaseResponse;
import base.TestBase;
import io.restassured.response.Response;
import rest.RequestBuilder;
import utils.ApiClient;
import utils.DateTimeUtils;

public class POSTCancelTest extends TestBase {

	private static final String GENERATE_PATH = "/api/e-invoice/generate";
	private static final String CANCEL_PATH   = "/api/e-invoice/cancel";
	private static final String SAMPLE_PATH   = "/api/e-invoice/sample/1";
	private static final String INVOICE_PATH  = "/api/e-invoice/invoices";

	private Map<String, Object> originalSampleBody;

	private String freshIrn;     // live invoice, not yet cancelled — used by happy path & chain test
	private String cancelledIrn; // already cancelled in @BeforeClass — used by double-cancel test

	@BeforeClass
	public void setup() {

		// ── Part 1: Generate a fresh invoice ──────────────────────────────────
		// Problem: sample always has the same DocNo → 409 Duplicate on second run.
		// Fix: replace DocNo with a unique value using timestamp.

		originalSampleBody = fetchSampleBody(SAMPLE_PATH);

		Map<String, Object> body1 = deepCopyMap(originalSampleBody);
		Map<String, Object> docDtls1 = (Map<String, Object>) body1.get("DocDtls");
		docDtls1.put("No", "A" + (System.currentTimeMillis() % 10000000));

		Response response1 = ApiClient.postRequest(GENERATE_PATH, body1);
		freshIrn = response1.jsonPath().getString("data.Irn");

		// ── Part 2: Generate another invoice and cancel it immediately ────────
		// This gives us cancelledIrn — needed for the double-cancel (409) test.
		// "B" prefix ensures DocNo is different from body1 even in the same millisecond.

		Map<String, Object> body2 = deepCopyMap(originalSampleBody);
		Map<String, Object> docDtls2 = (Map<String, Object>) body2.get("DocDtls");
		docDtls2.put("No", "B" + (System.currentTimeMillis() % 10000000));

		Response response2 = ApiClient.postRequest(GENERATE_PATH, body2);
		String irnToCancel = response2.jsonPath().getString("data.Irn");

		Map<String, Object> setupCancelBody = new HashMap<>();
		setupCancelBody.put("Irn",    irnToCancel);
		setupCancelBody.put("CnlRsn", "1");
		setupCancelBody.put("CnlRem", "Setup: pre-cancelled for double-cancel test");
		ApiClient.postRequest(CANCEL_PATH, setupCancelBody);

		cancelledIrn = irnToCancel;
	}

	// ─── HELPER ────────────────────────────────────────────────────────────────

	private Map<String, Object> cancelBody() {
		Map<String, Object> body = new HashMap<>();
		body.put("Irn",    freshIrn);
		body.put("CnlRsn", "1");
		body.put("CnlRem", "Duplicate invoice entry");
		return body;
	}

	// ─── Layer 1: Contract ─────────────────────────────────────────────────────

	@Test(
		priority    = 1,
		description = "Contract: response shape must match the JSON Schema for /cancel success response. "
		            + "Validates field types, required fields, CnlRsn enum (1-4), cancelledAt format."
	)
	public void testResponseMatchesSchema() {

		//@formatter:off
		given()
			.spec(RequestBuilder.createRequest(cancelBody()))

		.when()
			.post(CANCEL_PATH)

		.then()
			.spec(BaseResponse.get200Spec())
			.body(matchesJsonSchemaInClasspath("schemas/post-cancel-schema.json"));
		//@formatter:on
	}

	// ─── Layer 2: Happy path ────────────────────────────────────────────────────

	@Test(
		priority    = 2,
		description = "Happy path: POST /cancel returns 200, success=true, echoes back Irn/CnlRsn/CnlRem, "
		            + "status=Cancelled, and cancelledAt is a valid ISO 8601 timestamp."
	)
	public void testSuccessfulCancellationOfInvoice() {

		//@formatter:off
		Response response = given()
			.spec(RequestBuilder.createRequest(cancelBody()))
		.when()
			.post(CANCEL_PATH);

		response.then()
			.spec(BaseResponse.get200Spec())
			.body("success",     equalTo(Boolean.TRUE))
			.body("message",     equalTo("Invoice cancelled successfully"))
			.body("data.status", equalTo("Cancelled"))
			.body("data.Irn",    equalTo(freshIrn))
			.body("data.CnlRsn", equalTo("1"))
			.body("data.CnlRem", equalTo("Duplicate invoice entry"));
		//@formatter:on

		// cancelledAt requires DateTimeUtils — can't be done inside the Hamcrest chain
		String cancelledAt = response.jsonPath().getString("data.cancelledAt");
		DateTimeUtils.assertValidISO8601(cancelledAt);
	}

	@Test(
		priority         = 3,
		description      = "Chain test: after cancellation, GET /invoices?irn={freshIrn} must show status=Cancelled. "
		                 + "Verifies the state change is persisted and visible via the invoices endpoint.",
		dependsOnMethods = "testSuccessfulCancellationOfInvoice"
		// Why dependsOnMethods: if cancellation didn't happen, this fetch is meaningless.
		// Skipping is better than a misleading assertion failure.
	)
	public void testAfterCancellationOfInvoice() {

		Map<String, Object> queryParams = new HashMap<>();
		queryParams.put("irn", freshIrn);

		Response response = ApiClient.get(INVOICE_PATH, queryParams);

		Assert.assertEquals(
			response.jsonPath().getString("data[0].status"),
			"Cancelled",
			"Expected the cancelled invoice to show status=Cancelled in the invoices list"
		);
	}

	@Test(
		priority    = 4,
		description = "Double cancel: trying to cancel an already-cancelled invoice returns 409 Conflict. "
		            + "Uses cancelledIrn pre-cancelled in @BeforeClass."
	)
	public void testCancellingSameInvoiceTwice() {

		Map<String, Object> body = new HashMap<>();
		body.put("Irn",    cancelledIrn);
		body.put("CnlRsn", "1");
		body.put("CnlRem", "Duplicate invoice entry");

		//@formatter:off
		given()
			.spec(RequestBuilder.createRequest(body))

		.when()
			.post(CANCEL_PATH)

		.then()
			.statusCode(409)
			.body("success", equalTo(Boolean.FALSE))
			.body("message", equalTo("Invoice is already cancelled"))
			.body("error",   equalTo("Conflict: This invoice has already been cancelled"))
			.body("data.status",      equalTo("Cancelled"))
			.body("data.cancelledAt", notNullValue())
			.body("data.CnlRsn",      equalTo("1"))
			// The API echoes back the ORIGINAL cancellation data from @BeforeClass, not what we sent now
			.body("data.CnlRem",      equalTo("Setup: pre-cancelled for double-cancel test"));
		//@formatter:on
	}

	@Test(
		priority    = 5,
		description = "Fake IRN: a valid-looking 64-char hex IRN that doesn't exist in the system returns 404. "
		            + "Key difference from null IRN (400): body passes field validation but lookup finds nothing."
	)
	public void testSendingFakeIrnReturns404() {

		Map<String, Object> body = new HashMap<>();
		body.put("Irn",    "0000000000000000000000000000000000000000000000000000000000000000");
		body.put("CnlRsn", "1");
		body.put("CnlRem", "Duplicate invoice entry");

		//@formatter:off
		given()
			.spec(RequestBuilder.createRequest(body))

		.when()
			.post(CANCEL_PATH)

		.then()
			.statusCode(404)
			.body("success", equalTo(Boolean.FALSE))
			.body("message", equalTo("Invoice not found"));
		//@formatter:on
	}

	// ─── Layer 2: Security ─────────────────────────────────────────────────────

	@Test(
		priority    = 6,
		description = "Security: request without Authorization header returns 401 Unauthorized. "
		            + "Each endpoint must independently enforce authentication."
	)
	public void testNoAuthReturns401() {

		//@formatter:off
		given()
			.spec(RequestBuilder.createUnauthenticatedRequest(cancelBody()))

		.when()
			.post(CANCEL_PATH)

		.then()
			.statusCode(401);
		//@formatter:on
	}

	@Test(
		priority    = 7,
		description = "Wrong method: GET /cancel returns 405 Method Not Allowed. "
		            + "POST is the only allowed method on this endpoint."
	)
	public void testWrongHttpMethodReturns405() {

		//@formatter:off
		given()
			.spec(RequestBuilder.getRequest())

		.when()
			.get(CANCEL_PATH)

		.then()
			.statusCode(405);
		//@formatter:on
	}

	// ─── Layer 2: Input validation ─────────────────────────────────────────────

	@Test(
		priority    = 8,
		description = "Empty body {} returns 400: all three required fields (Irn, CnlRsn, CnlRem) are missing."
	)
	public void testEmptyBodyReturns400() {

		//@formatter:off
		given()
			.spec(RequestBuilder.createRequest("{}"))

		.when()
			.post(CANCEL_PATH)

		.then()
			.statusCode(400)
			.body("success", equalTo(Boolean.FALSE));
		//@formatter:on
	}

	@Test(
		priority    = 9,
		description = "Null IRN returns 400: Irn is null so field validation fails immediately — the lookup is never reached."
	)
	public void testNullIrnReturns400() {

		Map<String, Object> body = new HashMap<>();
		body.put("Irn",    null);
		body.put("CnlRsn", "1");
		body.put("CnlRem", "Test remark");

		//@formatter:off
		given()
			.spec(RequestBuilder.createRequest(body))

		.when()
			.post(CANCEL_PATH)

		.then()
			.statusCode(400)
			.body("success", equalTo(Boolean.FALSE));
		//@formatter:on
	}

	@Test(
		priority    = 10,
		description = "CnlRsn = '0' (below valid range 1-4) returns 400."
	)
	public void testInvalidCnlRsnBelowRangeReturns400() {

		Map<String, Object> body = new HashMap<>();
		body.put("Irn",    freshIrn);
		body.put("CnlRsn", "0");
		body.put("CnlRem", "Test remark");

		//@formatter:off
		given()
			.spec(RequestBuilder.createRequest(body))

		.when()
			.post(CANCEL_PATH)

		.then()
			.statusCode(400)
			.body("success", equalTo(Boolean.FALSE));
		//@formatter:on
	}

	@Test(
		priority    = 11,
		description = "CnlRsn = '5' (above valid range 1-4) returns 400."
	)
	public void testInvalidCnlRsnAboveRangeReturns400() {

		Map<String, Object> body = new HashMap<>();
		body.put("Irn",    freshIrn);
		body.put("CnlRsn", "5");
		body.put("CnlRem", "Test remark");

		//@formatter:off
		given()
			.spec(RequestBuilder.createRequest(body))

		.when()
			.post(CANCEL_PATH)

		.then()
			.statusCode(400)
			.body("success", equalTo(Boolean.FALSE));
		//@formatter:on
	}

	@Test(
		priority    = 12,
		description = "Null CnlRem returns 400: explicit null is treated the same as absent by the API."
	)
	public void testNullCnlRemReturns400() {

		Map<String, Object> body = new HashMap<>();
		body.put("Irn",    freshIrn);
		body.put("CnlRsn", "1");
		body.put("CnlRem", null);

		//@formatter:off
		given()
			.spec(RequestBuilder.createRequest(body))

		.when()
			.post(CANCEL_PATH)

		.then()
			.statusCode(400)
			.body("success", equalTo(Boolean.FALSE));
		//@formatter:on
	}

	@Test(
		priority    = 13,
		description = "CnlRem with 101 characters (one over the 100-char limit) returns 400."
	)
	public void testCnlRemExceedsMaxLengthReturns400() {

		String longRemark = "A".repeat(101);

		Map<String, Object> body = new HashMap<>();
		body.put("Irn",    freshIrn);
		body.put("CnlRsn", "1");
		body.put("CnlRem", longRemark);

		//@formatter:off
		given()
			.spec(RequestBuilder.createRequest(body))

		.when()
			.post(CANCEL_PATH)

		.then()
			.statusCode(400)
			.body("success", equalTo(Boolean.FALSE));
		//@formatter:on
	}

}

package stepdefinitions;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;

import base.BaseResponse;
import base.TestBase;
import config.ConfigReader;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import pojo.ApiSingleSampleResponse;
import pojo.EInvoicePayload;
import rest.RequestBuilder;
import utils.ApiClient;
import utils.DateTimeUtils;

public class EInvoiceSteps extends TestBase {

    private Response response;
    private EInvoicePayload validPayload;

    @Before
    public void setupScenario() {
        // Trigger Config setup if static block has not run
        ConfigReader.getEnvironment();
    }

    @Given("the E-Invoice API base setup is ready")
    public void the_e_invoice_api_base_setup_is_ready() {
        // Setup validated via ConfigReader
    }

    @When("I send a GET request to {string}")
    public void i_send_a_get_request_to(String endpoint) {
        response = given()
                    .spec(RequestBuilder.getRequest())
                   .when()
                    .get(endpoint);
    }

    @Then("the response status code should be {int}")
    public void the_response_status_code_should_be(Integer statusCode) {
        response.then().statusCode(statusCode);
    }

    @Then("the response status field should be {string}")
    public void the_response_status_field_should_be(String status) {
        response.then().body("status", equalTo(status));
    }

    @Then("the response timestamp should be valid ISO8601 format")
    public void the_response_timestamp_should_be_valid_iso8601_format() {
        String timestamp = response.path("timestamp");
        DateTimeUtils.assertValidISO8601(timestamp);
    }

    @Given("valid sample E-Invoice payload data is prepared")
    public void valid_sample_e_invoice_payload_data_is_prepared() {
        Response sampleResponse = ApiClient.get("/api/e-invoice/sample/1");
        validPayload = sampleResponse.as(ApiSingleSampleResponse.class).getData();
    }

    @When("I send a POST request to {string} with valid payload")
    public void i_send_a_post_request_to_with_valid_payload(String endpoint) {
        response = given()
                    .spec(RequestBuilder.createRequest(validPayload))
                   .when()
                    .post(endpoint);
    }

    @Then("the response body should match JSON schema {string}")
    public void the_response_body_should_match_json_schema(String schemaPath) {
        response.then()
                .spec(BaseResponse.get200Spec())
                .body(matchesJsonSchemaInClasspath(schemaPath));
    }
}

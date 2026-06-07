Feature: E-Invoicing API Automation Framework
  As an API Automation Engineer
  I want to test the E-Invoice system health and invoice generation APIs
  So that I can ensure API contracts, reliability, and security compliance

  @health
  Scenario: Verify E-Invoice API Health Check endpoint returns HTTP 200 OK
    Given the E-Invoice API base setup is ready
    When I send a GET request to "/health"
    Then the response status code should be 200
    And the response status field should be "OK"
    And the response timestamp should be valid ISO8601 format

  @generate
  Scenario: Verify successful E-Invoice generation contract and schema
    Given valid sample E-Invoice payload data is prepared
    When I send a POST request to "/api/e-invoice/generate" with valid payload
    Then the response status code should be 200
    And the response body should match JSON schema "schemas/post-generate-schema.json"

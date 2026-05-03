package rest;

import base.BaseRequest;
import io.restassured.specification.RequestSpecification;

public class RequestBuilder {

	// POST Request
	public static RequestSpecification createRequest(Object body) {

		// @formatter:off
		return BaseRequest
				.getRequestSpecification()
				.body(body);
	}
	
	// TODO DO THE SAME AS ABOVE FOR ALL FOLLOWING METHODS
	
    // PUT Request (Update)
	public static RequestSpecification updateRequest(Object body) {
		
		return BaseRequest
				.getRequestSpecification()
				.body(body);		
	}
	
    // PATCH Request (Partial Update)
	public static RequestSpecification patchRequest(Object body) {

	        return BaseRequest
	                .getRequestSpecification()
	                .body(body);
	}
	
	// DELETE Request
    public static RequestSpecification deleteRequest() {

        return BaseRequest
                .getRequestSpecification();
    }

    // GET Request
    public static RequestSpecification getRequest() {

        return BaseRequest
                .getRequestSpecification();
    }
}

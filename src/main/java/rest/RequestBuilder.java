package rest;

import base.BaseRequest;
import io.restassured.specification.RequestSpecification;

public class RequestBuilder {

	// CREATE USER
	public static RequestSpecification createRequest(Object body) {

		// @formatter:off
		return BaseRequest
				.getRequestSpecification()
				.body(body);
	}
	
	// TODO DO THE SAME AS ABOVE FOR ALL FOLLOWING METHODS
	
    // UPDATE USER (PUT)
	public static RequestSpecification updateRequest(Object body) {
		
		return BaseRequest
				.getRequestSpecification()
				.body(body);		
	}
	
    // PATCH USER (Partial Update)
	public static RequestSpecification patchRequest(Object body) {

	        return BaseRequest
	                .getRequestSpecification()
	                .body(body);
	}
	
	// DELETE USER (No body)
    public static RequestSpecification deleteRequest() {

        return BaseRequest
                .getRequestSpecification();
    }

    // GET USER (No body)
    public static RequestSpecification getRequest() {

        return BaseRequest
                .getRequestSpecification();
    }
}

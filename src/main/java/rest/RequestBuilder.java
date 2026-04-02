package rest;

import base.BaseRequest;
import io.restassured.specification.RequestSpecification;

public class RequestBuilder {

	// CREATE USER
	public static RequestSpecification createRequest(String serviceName, Object body) {

		// @formatter:off
		return BaseRequest
				.getRequestSpecification(serviceName)
				.body(body);
	}
	
	// TODO DO THE SAME AS ABOVE FOR ALL FOLLOWING METHODS
	
    // UPDATE USER (PUT)
	public static RequestSpecification updateRequest(String serviceName, Object body) {
		
		return BaseRequest
				.getRequestSpecification(serviceName)
				.body(body);		
	}
	
    // PATCH USER (Partial Update)
	public static RequestSpecification patchRequest(String serviceName, Object body) {

	        return BaseRequest
	                .getRequestSpecification(serviceName)
	                .body(body);
	}
	
	// DELETE USER (No body)
    public static RequestSpecification deleteRequest(String serviceName) {

        return BaseRequest
                .getRequestSpecification(serviceName);
    }

    // GET USER (No body)
    public static RequestSpecification getRequest(String serviceName) {

        return BaseRequest
                .getRequestSpecification(serviceName);
    }
}

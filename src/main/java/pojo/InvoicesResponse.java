package pojo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InvoicesResponse extends ApiResponse<Invoice> {
	
	private List<String> availableFields;
	

}

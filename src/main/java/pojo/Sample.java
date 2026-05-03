package pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Sample extends BaseInvoice {

	// NOTE This is Samples Response
	// Default constructor + getters + setters (only for the 3 fields above)
	// The 10 shared fields are INHERITED from BaseInvoice — no duplication!

	@JsonProperty("type")
	private String supplyType;

	@JsonProperty("description")
	private String description;

	@JsonProperty("endpoint")
	private String endpoint;

	public String getSupplyType() {
		return supplyType;
	}

	public void setSupplyType(String supplyType) {
		this.supplyType = supplyType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
}

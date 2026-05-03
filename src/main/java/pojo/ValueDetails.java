package pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ValueDetails {

	@JsonProperty("AssVal")
	public String assessibleValue;

	@JsonProperty("CgstVal")
	public String cgstValue;

	@JsonProperty("SgstVal")
	public String sgstValue;

	@JsonProperty("IgstVal")
	public String igstValue;

	@JsonProperty("TotInvVal")
	public String totalInvoiceValue;

	public String getAssessibleValue() {
		return assessibleValue;
	}

	public void setAssessibleValue(String assessibleValue) {
		this.assessibleValue = assessibleValue;
	}

	public String getCgstValue() {
		return cgstValue;
	}

	public void setCgstValue(String cgstValue) {
		this.cgstValue = cgstValue;
	}

	public String getSgstValue() {
		return sgstValue;
	}

	public void setSgstValue(String sgstValue) {
		this.sgstValue = sgstValue;
	}

	public String getIgstValue() {
		return igstValue;
	}

	public void setIgstValue(String igstValue) {
		this.igstValue = igstValue;
	}

	public String getTotalInvoiceValue() {
		return totalInvoiceValue;
	}

	public void setTotalInvoiceValue(String totalInvoiceValue) {
		this.totalInvoiceValue = totalInvoiceValue;
	}

}

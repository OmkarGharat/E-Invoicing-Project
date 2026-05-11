package pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ValueDetails {

	@JsonProperty("AssVal")
	public Double assessibleValue;

	@JsonProperty("CgstVal")
	public Double cgstValue;

	@JsonProperty("SgstVal")
	public Double sgstValue;

	@JsonProperty("IgstVal")
	public Double igstValue;

	@JsonProperty("TotInvVal")
	public Double totalInvoiceValue;

	public Double getAssessibleValue() {
		return assessibleValue;
	}

	public void setAssessibleValue(Double assessibleValue) {
		this.assessibleValue = assessibleValue;
	}

	public Double getCgstValue() {
		return cgstValue;
	}

	public void setCgstValue(Double cgstValue) {
		this.cgstValue = cgstValue;
	}

	public Double getSgstValue() {
		return sgstValue;
	}

	public void setSgstValue(Double sgstValue) {
		this.sgstValue = sgstValue;
	}

	public Double getIgstValue() {
		return igstValue;
	}

	public void setIgstValue(Double igstValue) {
		this.igstValue = igstValue;
	}

	public Double getTotalInvoiceValue() {
		return totalInvoiceValue;
	}

	public void setTotalInvoiceValue(Double totalInvoiceValue) {
		this.totalInvoiceValue = totalInvoiceValue;
	}

}

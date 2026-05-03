package pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Item {

	public String SlNo;

	@JsonProperty("IsServc")
	public String isService;

	@JsonProperty("PrdDesc")
	public String productDescription;

	@JsonProperty("HsnCd")
	public String hsnCode;

	// I haven't understood how to write this
//	BchDtls Nm

	@JsonProperty("Qty")
	public Double Qty;

	@JsonProperty("Unit")
	public String UnitOfMeasurement;

	@JsonProperty("UnitPrice")
	public Double unitPrice;

	@JsonProperty("TotAmt")
	public Double totalAmount;

	@JsonProperty("AssAmt")
	public Double assessibleAmount;

	@JsonProperty("GstRt")
	public String gstRate;

	@JsonProperty("IgstAmt")
	public Double igstAmount;

	@JsonProperty("CgstAmt")
	public Double cgstAmount;

	@JsonProperty("SgstAmt")
	public Double sgstAmount;

	@JsonProperty("TotItemVal")
	public Double totalItemValue;

	public String getSlNo() {
		return SlNo;
	}

	public void setSlNo(String slNo) {
		SlNo = slNo;
	}

	public String getIsService() {
		return isService;
	}

	public void setIsService(String isService) {
		this.isService = isService;
	}

	public String getProductDescription() {
		return productDescription;
	}

	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}

	public String getHsnCode() {
		return hsnCode;
	}

	public void setHsnCode(String hsnCode) {
		this.hsnCode = hsnCode;
	}

	public Double getQty() {
		return Qty;
	}

	public void setQty(Double qty) {
		Qty = qty;
	}

	public String getUnitOfMeasurement() {
		return UnitOfMeasurement;
	}

	public void setUnitOfMeasurement(String unitOfMeasurement) {
		UnitOfMeasurement = unitOfMeasurement;
	}

	public Double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(Double unitPrice) {
		this.unitPrice = unitPrice;
	}

	public Double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Double getAssessibleAmount() {
		return assessibleAmount;
	}

	public void setAssessibleAmount(Double assessibleAmount) {
		this.assessibleAmount = assessibleAmount;
	}

	public String getGstRate() {
		return gstRate;
	}

	public void setGstRate(String gstRate) {
		this.gstRate = gstRate;
	}

	public Double getIgstAmount() {
		return igstAmount;
	}

	public void setIgstAmount(Double igstAmount) {
		this.igstAmount = igstAmount;
	}

	public Double getCgstAmount() {
		return cgstAmount;
	}

	public void setCgstAmount(Double cgstAmount) {
		this.cgstAmount = cgstAmount;
	}

	public Double getSgstAmount() {
		return sgstAmount;
	}

	public void setSgstAmount(Double sgstAmount) {
		this.sgstAmount = sgstAmount;
	}

	public Double getTotalItemValue() {
		return totalItemValue;
	}

	public void setTotalItemValue(Double totalItemValue) {
		this.totalItemValue = totalItemValue;
	}

}

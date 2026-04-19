package pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Sample {
	
	// NOTE This is Invoice Response
	
    @JsonProperty("id")
	public int id;
	
    @JsonProperty("type")
	public String type;
	
    @JsonProperty("description")
	public String description;
	
    @JsonProperty("invoiceNo")
	public String invoiceNo;
	
    @JsonProperty("totalValue")
	public int totalValue;
	
    @JsonProperty("documentType")
	public String documentType;
	
    @JsonProperty("sellerState")
	public String sellerState;
	
    @JsonProperty("buyerState")
	public String buyerState;
	
    @JsonProperty("isInterstate")
	public boolean isInterstate;
	
    @JsonProperty("reverseCharge")
	public boolean reverseCharge;
	
    @JsonProperty("itemCount")
	public int itemCount;
	
    @JsonProperty("invoiceDate")
	public String invoiceDate;
	
    @JsonProperty("endpoint")
	public String endpoint;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	public int getTotalValue() {
		return totalValue;
	}

	public void setTotalValue(int totalValue) {
		this.totalValue = totalValue;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public String getSellerState() {
		return sellerState;
	}

	public void setSellerState(String sellerState) {
		this.sellerState = sellerState;
	}

	public String getBuyerState() {
		return buyerState;
	}

	public void setBuyerState(String buyerState) {
		this.buyerState = buyerState;
	}

	public boolean isInterstate() {
		return isInterstate;
	}

	public void setInterstate(boolean isInterstate) {
		this.isInterstate = isInterstate;
	}

	public boolean isReverseCharge() {
		return reverseCharge;
	}

	public void setReverseCharge(boolean reverseCharge) {
		this.reverseCharge = reverseCharge;
	}

	public int getItemCount() {
		return itemCount;
	}

	public void setItemCount(int itemCount) {
		this.itemCount = itemCount;
	}

	public String getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(String invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
}

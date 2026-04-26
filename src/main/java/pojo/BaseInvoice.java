package pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseInvoice {
	
	private int id;
	
    private String invoiceNo;
    
    private String invoiceDate;
    
    private String documentType;
    
    private Integer totalValue;
    
    @JsonProperty("pos")
    private String pos;
    
    private String sellerState;
    
    private String buyerState;
    
    @JsonProperty("isInterstate")
    private Boolean isInterstate;
    
    private Boolean reverseCharge;
    
    private Integer itemCount;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	public String getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(String invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public Integer getTotalValue() {
		return totalValue;
	}

	public void setTotalValue(Integer totalValue) {
		this.totalValue = totalValue;
	}

	public String getPlaceOfSupply() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
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

	public Boolean isInterstate() {
		return isInterstate;
	}

	public void setInterstate(Boolean isInterstate) {
		this.isInterstate = isInterstate;
	}
	
	public Boolean getReverseCharge() {
		return reverseCharge;
	}

	public void setReverseCharge(Boolean reverseCharge) {
		this.reverseCharge = reverseCharge;
	}

	public Integer getItemCount() {
		return itemCount;
	}

	public void setItemCount(Integer itemCount) {
		this.itemCount = itemCount;
	} 

}

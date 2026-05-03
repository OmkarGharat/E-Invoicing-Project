package pojo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EInvoicePayload {

	@JsonProperty("Version")
	private String version;

	@JsonProperty("TranDtls")
	private TransactionDetails tranDtls;

	@JsonProperty("DocDtls")
	private DocDetails docDtls;

	@JsonProperty("SellerDtls")
	private PartyDetails sellerDtls;

	@JsonProperty("BuyerDtls")
	private PartyDetails buyerDtls;

	@JsonProperty("ItemList")
	private List<Item> itemList;

	@JsonProperty("ValDtls")
	private ValueDetails valDtls;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public TransactionDetails getTranDtls() {
		return tranDtls;
	}

	public void setTranDtls(TransactionDetails tranDtls) {
		this.tranDtls = tranDtls;
	}

	public DocDetails getDocDtls() {
		return docDtls;
	}

	public void setDocDtls(DocDetails docDtls) {
		this.docDtls = docDtls;
	}

	public PartyDetails getSellerDtls() {
		return sellerDtls;
	}

	public void setSellerDtls(PartyDetails sellerDtls) {
		this.sellerDtls = sellerDtls;
	}

	public PartyDetails getBuyerDtls() {
		return buyerDtls;
	}

	public void setBuyerDtls(PartyDetails buyerDtls) {
		this.buyerDtls = buyerDtls;
	}

	public List<Item> getItemList() {
		return itemList;
	}

	public void setItemList(List<Item> itemList) {
		this.itemList = itemList;
	}

	public ValueDetails getValDtls() {
		return valDtls;
	}

	public void setValDtls(ValueDetails valDtls) {
		this.valDtls = valDtls;
	}

}

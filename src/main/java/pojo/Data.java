package pojo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Data {

	@JsonProperty("Version")
	public String version;

	public TransactionDetails TranDtls;

	public DocDetails DocDtls;

	public PartyDetails SellerDtls;

	public PartyDetails BuyerDtls;

	public List<Item> ItemList;

	public ValueDetails ValDtls;

}

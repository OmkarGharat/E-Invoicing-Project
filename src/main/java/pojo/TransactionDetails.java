package pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionDetails {

	@JsonProperty("TaxSch")
	public String taxScheme;

	@JsonProperty("SupTyp")
	public String supplyType;

	@JsonProperty("RegRev")
	public String regRev;

	@JsonProperty("IgstOnIntra")
	private String igstOnIntra;

	public String getTaxScheme() {
		return taxScheme;
	}

	public void setTaxScheme(String taxScheme) {
		this.taxScheme = taxScheme;
	}

	public String getSupplyType() {
		return supplyType;
	}

	public void setSupplyType(String supplyType) {
		this.supplyType = supplyType;
	}

	public String getRegRev() {
		return regRev;
	}

	public void setRegRev(String regRev) {
		this.regRev = regRev;
	}

	public String getIgstOnIntra() {
		return igstOnIntra;
	}

	public void setIgstOnIntra(String igstOnIntra) {
		this.igstOnIntra = igstOnIntra;
	}

}

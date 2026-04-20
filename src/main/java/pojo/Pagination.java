package pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Pagination {
	
    @JsonProperty("page")
	private Integer currentPageNo;
	
    @JsonProperty("limit")
	private Integer limit; 
	
    @JsonProperty("total")
	private Integer total;
	
    @JsonProperty("pages")
	private Integer pages;
    
    @JsonProperty("hasNext")
    private Boolean hasNextPage; // only in /invoices → wrapper (nullable)
    
    @JsonProperty("hasPrev")
    private Boolean hasPrevPage; // only in /invoices → wrapper (nullable)
    
    public Pagination() {}

	public Integer getCurrentPageNo() {
		return currentPageNo;
	}

	public void setCurrentPageNo(Integer currentPageNo) {
		this.currentPageNo = currentPageNo;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public Integer getPages() {
		return pages;
	}

	public void setPages(Integer pages) {
		this.pages = pages;
	}

	public Boolean getHasNextPage() {
		return hasNextPage;
	}

	public void setHasNextPage(Boolean hasNextPage) {
		this.hasNextPage = hasNextPage;
	}

	public Boolean getHasPrevPage() {
		return hasPrevPage;
	}

	public void setHasPrevPage(Boolean hasPrevPage) {
		this.hasPrevPage = hasPrevPage;
	}
	
}

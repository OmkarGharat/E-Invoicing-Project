package pojo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SamplesResponse {
	
	// NOTE This is Invoice Response
	
    @JsonProperty("success")
	private boolean success;
	
    @JsonProperty("data")
	private List<Sample> data;
	
    @JsonProperty("count")
	private int count;
	
    @JsonProperty("total")
	private int total;
	
    @JsonProperty("filters")
	private Filters filters;
	
    @JsonProperty("pagination")
	private Pagination pagination;
	
    @JsonProperty("sort")
	private Sort sort;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public List<Sample> getData() {
		return data;
	}

	public void setData(List<Sample> data) {
		this.data = data;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public Filters getFilters() {
		return filters;
	}

	public void setFilters(Filters filters) {
		this.filters = filters;
	}

	public Pagination getPagination() {
		return pagination;
	}

	public void setPagination(Pagination pagination) {
		this.pagination = pagination;
	}

	public Sort getSort() {
		return sort;
	}

	public void setSort(Sort sort) {
		this.sort = sort;
	}
}

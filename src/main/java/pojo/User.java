package pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL) // or NON_DEFAULT
public class User {

	private Integer id;
	private String name;
	private String gender;
	private String email;
	private String status;
	
	// ⭐ DEFAULT CONSTRUCTOR → REQUIRED FOR DESERIALIZATION
    public User() {
    }

	public User(String name, String gender, String email, String status) {
		this.name = name;
		this.gender = gender;
		this.email = email;
		this.status = status;
	}

	// All getters and setters

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}

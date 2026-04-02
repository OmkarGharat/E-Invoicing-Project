package testcases;

import org.testng.annotations.Test;

import pojo.User;
import utils.APIUtils;
import base.TestBase;
import utils.TestData;

public class APIChainTestUsingFaker extends TestBase {

	// POST method
	@Test
	void _APIChainTest() {

		User user = new User(
				TestData.getName(),
				TestData.getGender(),
				TestData.getEmail(),
				TestData.getStatus()
		);
		
		int id = APIUtils.createUser(user);

		User fetched = APIUtils.getUser(id, User.class);

		user.setName("Updated Omkar");
		APIUtils.updateUser(id, user);

		APIUtils.deleteUser(id);
	}

}

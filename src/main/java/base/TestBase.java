package base;

import org.testng.annotations.BeforeSuite;

import config.ConfigReader;

public class TestBase {
	
	// NOTE DON'T REMOVE TESTBASE CLASS AT ALL : THIS IS VERY IMPORTANT
	
	@BeforeSuite
	public void setupConfig() {
		
		System.out.println("SETUP CONFIG LOADING...");
		
		// Purpose : The TestBase class typically serves as the foundation for all your test classes. 
		// Its purpose is to handle global setup and teardown procedures that apply to your entire test run.
		
		// The next lines trigger the ConfigReader's static block if they haven't run yet
        String env = ConfigReader.getEnvironment();
        String uri = ConfigReader.get("baseURI");
		
		System.out.println("🌐 Active Environment:" + env);		
	    System.out.println("🔗 BaseURI → " + uri);

	}
}

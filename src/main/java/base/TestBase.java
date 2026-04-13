package base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeSuite;

import config.ConfigReader;

public class TestBase {
	
	// NOTE DON'T REMOVE TESTBASE CLASS AT ALL : THIS IS VERY IMPORTANT
	
	private static final Logger logger = LogManager.getLogger(TestBase.class);
	
	@BeforeSuite
	public void setupConfig() {
		
		logger.info("════════════════════════════════════════════");
		logger.info("  E-INVOICE API TEST SUITE — STARTING");
		logger.info("════════════════════════════════════════════");
		
		// Purpose : The TestBase class typically serves as the foundation for all your test classes. 
		// Its purpose is to handle global setup and teardown procedures that apply to your entire test run.
		
		// The next lines trigger the ConfigReader's static block if they haven't run yet
        String env = ConfigReader.getEnvironment();
        String uri = ConfigReader.get("einvoice.baseURI");
		
		logger.info("🌐 Active Environment: {}", env);
	    logger.info("🔗 E-Invoice BaseURI → {}", uri);
	    logger.info("════════════════════════════════════════════");
	}
}

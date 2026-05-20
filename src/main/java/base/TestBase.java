package base;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeSuite;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import config.ConfigReader;
import io.restassured.response.Response;
import utils.ApiClient;

public class TestBase {
	
	// NOTE DON'T REMOVE TESTBASE CLASS AT ALL : THIS IS VERY IMPORTANT
	
	private static final Logger logger = LogManager.getLogger(TestBase.class);
	private static final ObjectMapper objectMapper = new ObjectMapper();
	
	// ------------------------------------------------------------
	// SUITE LIFECYCLE
	// ------------------------------------------------------------
	
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
	
	// ------------------------------------------------------------
	// SHARED UTILITIES - available to all classes
	// ------------------------------------------------------------
		
	/**
	 * Creates a deep copy of a Map so that mutations in one test 
	 * don't bleed into another test.
	 * 
	 * Uses Jackson's ObjectMapper to serialize → deserialize,
	 * producing a completely independent copy of all nested Maps/Lists.
	 */
	
	// TODO - Need to learn this from Claude Opus 4.6
	protected Map<String, Object> deepCopyMap(Map<String, Object> original) {
		return objectMapper.convertValue(original, 
				new TypeReference<Map<String, Object>>() {});
	}
	
	/**
	 * Fetches sample data from a given API path and extracts 
	 * the "data" field as a Map.
	 * 
	 * Intended to be called in @BeforeClass of each test class:
	 * 
	 *   @BeforeClass
	 *   public void setup() {
	 *       originalBody = fetchSampleBody("/api/e-invoice/sample/1");
	 *   }
	 */
	protected Map<String, Object> fetchSampleBody(String samplePath) {
		
		logger.info("Fetching sample body from: {}", samplePath);
		Response response = ApiClient.get(samplePath);
		return response.jsonPath().getMap("data");
	}
	
	
	
}

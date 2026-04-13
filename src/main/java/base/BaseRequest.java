package base;

import java.io.OutputStream;
import java.io.PrintStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import config.ConfigReader;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.LogConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;

public class BaseRequest {

	private static final Logger logger = LogManager.getLogger(BaseRequest.class);

	/**
	 * Creates a PrintStream that redirects RestAssured's log output
	 * through Log4j2 instead of System.out.
	 * 
	 * WHY: RestAssured uses PrintStream (System.out) for logging.
	 * To get those logs into our log file, we wrap a Logger in a PrintStream.
	 */
	private static PrintStream getLoggerPrintStream() {

		Logger restLogger = LogManager.getLogger("restassured");

		return new PrintStream(new OutputStream() {

			private StringBuilder buffer = new StringBuilder();

			@Override
			public void write(int b) {
				if (b == '\n') {
					String line = buffer.toString();
					if (!line.trim().isEmpty()) {
						restLogger.debug(line);
					}
					buffer.setLength(0);
				} else {
					buffer.append((char) b);
				}
			}
		});
	}

	// PART 2: The static access method (the only way to get the object)
	public static RequestSpecification getRequestSpecification(String serviceName) {

		String baseURI = ConfigReader.get(serviceName + ".baseURI");
		String authType = ConfigReader.get(serviceName + ".authType").toUpperCase();

		logger.info("Building request for service: {} | baseURI: {}", serviceName, baseURI);

		// @formatter:off
	    RequestSpecBuilder builder = new RequestSpecBuilder()
					.setBaseUri(baseURI)
					.setContentType("application/json")
					.addHeader("Accept", "application/json")
					// Redirect RestAssured's log output through Log4j2
					.setConfig(RestAssured.config()
						.logConfig(LogConfig.logConfig()
							.defaultStream(getLoggerPrintStream())
							.enablePrettyPrinting(true)))
					.log(LogDetail.ALL);
	    
	    switch(authType) {
	    
	    case "APIKEY":
	        builder.addHeader("x-api-key", ConfigReader.get(serviceName + ".apikey"));
	        break;
	    
	    case "BEARER" : 
	    		builder.addHeader("Authorization", "Bearer" + " " + ConfigReader.get(serviceName + ".token"));
	    		break;
	    		
	    case "BASIC":
            	builder.setAuth(RestAssured.basic("user", "pass"));
            	break;
             
	    case "NONE":
	    		
	    default:
	    		// No auth header added
	    		break;
	    }
	    
	    logger.debug("Request spec built successfully with authType: {}", authType);
	    return builder.build();
	}
}

package config;

import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfigReader {

	private static final Logger logger = LogManager.getLogger(ConfigReader.class);
	private static Properties properties = new Properties();

	// Loads config.properties
	static {
		try {
			
		loadProperties("config/config.properties");

		// 2. Priority: Maven/System Property (-Denv) > Config.properties > default to 'qa'
        String env = System.getProperty("env");
        if (env == null || env.isEmpty()) {
            env = properties.getProperty("env", "qa");
        }
        
        // 3. Load the specific environment file (qa.properties, dev.properties, etc.)
        loadProperties("config/" + env.toLowerCase() + ".properties");
        
        // Store final env back for utility
        properties.setProperty("active.env", env);
        logger.info("🌐 Environment Loaded: {}", env.toUpperCase());
        
    } catch (Exception e) {
        
    	throw new RuntimeException("Failed to initialize Configuration.", e);
    }
}
	
	private static void loadProperties(String fileName) throws Exception {
		
        try (InputStream input = ConfigReader.class.getClassLoader().getResourceAsStream(fileName)) {
            
        	if (input != null) {
                properties.load(input);
                logger.debug("Loaded properties file: {}", fileName);
            
        	} else {
            
        		logger.warn("⚠️ Properties file not found: {}", fileName);
            }
        }
    }
	
	public static String get(String key) {
		
        return properties.getProperty(key);
    }
	
	public static String getEnvironment() {
		
		return properties.getProperty("active.env", "qa");
	}
}

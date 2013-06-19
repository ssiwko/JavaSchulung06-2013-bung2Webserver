package at.wkormann.webserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author duj
 * 
 * Configuration class used for the Web server. it should read property file and extract entries set as key=value.
 *
 */
public class WebServerConfiguration {
	private final static WebServerConfiguration INSTANCE = new WebServerConfiguration();

	public static WebServerConfiguration getInstance() {
		return INSTANCE;
	}

	private static Properties configuration = new Properties();

	private static Properties getConfiguration() {
		return configuration;
	}

	/**
	 * @param file Property file to load
	 */
	public void initialize(final String file) {
		InputStream in = null;
		try {
			in = new FileInputStream(new File(file));
			configuration.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getConfiguration(final String key) {
		return (String) getConfiguration().get(key);
	}

	public String getConfigurationWithDefaultValue(final String key, final String defaultValue) {
		return (String) getConfiguration().getProperty(key, defaultValue);
	}
}

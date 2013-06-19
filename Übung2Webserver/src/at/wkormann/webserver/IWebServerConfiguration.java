package at.wkormann.webserver;

import java.util.Map;

public interface IWebServerConfiguration {

	public int getPort(final String key);

	public int getPort(final String key, int defaultValue);

	public Map<String, String> getSites(final String key);

	public Map<String, String> getSites(final String key,
			Map<String, String> defaultValue);

	public String getConfiguration(final String key);

	public String getConfigurationWithDefaultValue(final String key,
			final String defaultValue);

}

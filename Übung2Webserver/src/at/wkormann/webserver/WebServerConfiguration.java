package at.wkormann.webserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

/**
 * @author duj
 * 
 *         Configuration class used for the Web server. it should read property
 *         file and extract entries set as key=value.
 * 
 */
public class WebServerConfiguration implements IWebServerConfiguration {
	// private final static WebServerConfiguration INSTANCE = new
	// WebServerConfiguration();
	//
	// public static WebServerConfiguration getInstance() {
	// return INSTANCE;
	// }
	//
	// private static Properties getConfiguration() {
	// return configuration;
	// }

	private static Properties configuration = new Properties();

	public WebServerConfiguration(String file) {
		initialize(file);
	}

	/**
	 * @param file
	 *            Property file to load
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

	public static void main(String[] args) {
		System.err.println("Do something...");

		IWebServerConfiguration cfg = new WebServerConfiguration(
				"server.properties");
		cfg.getSites("sum");
	}

	@Override
	public int getPort(String key) {
		final String tmp = configuration.getProperty(key);
		try {
			return Integer.valueOf(tmp).intValue();
		} catch (Exception e) {
			System.err.println("Failed to parse [" + tmp
					+ "] into int, returning 0.");
		}
		return 0;
	}

	public int getPort(String key, int defaultValue) {
		final int tmp = getPort(key);
		return (tmp <= 0 ? defaultValue : tmp);
	}

	public long getConfigAsLong(String key) {
		final String tmp = configuration.getProperty(key);
		try {
			return Long.valueOf(tmp).longValue();
		} catch (Exception e) {
			System.err.println("Failed to parse [" + tmp
					+ "] into long, returning 0.");
		}
		return 0L;
	}

	public long getConfigAsLong(String key, long defaultValue) {
		final long tmp = getConfigAsLong(key);
		return (tmp <= 0 ? defaultValue : tmp);
	}

	public Map<String, String> getSites(String key) {
		Map<String, String> retValue = new HashMap<String, String>();
		Set<Entry<Object, Object>> set = configuration.entrySet();
		Iterator<Entry<Object, Object>> it = set.iterator();
		while (it.hasNext()) {
			Entry<Object, Object> type = (Entry<Object, Object>) it.next();

			if (type.getKey().toString().startsWith(key)) {
				final String site = readHTMLDirectory(type.getValue()
						.toString());
				if (site != null && site.trim().length() > 0) {
					retValue.put(type.getKey().toString(), site);
				}
			}
		}
		return retValue;
	}

	private String readHTMLDirectory(String file) {
		Charset charset = Charset.forName(getConfiguration("port"));
		File f = new File(file);
		try {
			FileInputStream fis = new FileInputStream(f);
			byte[] content = new byte[(int) f.length()];
			fis.read(content);
			fis.close();
			CharBuffer charBuffer = charset.decode(ByteBuffer.wrap(content));
			String input = charBuffer.toString();
			String site = addDefaultHeaders(input);
			System.out.println("adding string: " + site.toString());
			return site;
			// retVal.put("/" + f.getName(), site);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String addDefaultHeaders(String content) {
		StringBuilder buiString = new StringBuilder();
		buiString.append("HTTP/1.1 200 OK \n");
		buiString.append("Date" + new Date().toString() + "\n");
		buiString.append("Server " + "Java Webserver by Wolfi and Edmund\n");
		buiString.append("Connection " + "close\n");
		buiString.append("Content-Type: text/html; charset=UTF-8 \n");
		buiString.append("Content-Length " + content.length() + "\n");
		buiString.append("\n" + content);
		return buiString.toString();
	}

	public Map<String, String> getSites(String key, Map<String, String> defaultValue) {
		final Map<String, String> sites = getSites(key);
		if (sites != null && sites.size() > 0) {
			return sites;
		}
		return defaultValue;
	}

	public String getConfiguration(final String key) {
		return configuration.getProperty(key);
	}

	public String getConfigurationWithDefaultValue(final String key,
			final String defaultValue) {
		return configuration.getProperty(key, defaultValue);
	}

}

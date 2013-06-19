
package at.wkormann.webserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Copyright 2013 SSI Schaefer PEEM GmbH. All Rights reserved. <br />
 * <br />
 * $Id$ <br />
 * <br />
 * This is the class header. The first sentence (ending with "."+SPACE) is important, because it is used summary in the package overview pages.<br />
 * <br />
 * 
 * @author ehe
 * @version $Revision$
 */

public class SimpleWebServer implements IClientToHandlerInterface {

  private final int port;
  private final String file;
  private final String charset;
  private boolean hold = false;
  ServerSocketChannel ssc = null;
  List<ClientHandler> handlerList = new ArrayList<>();
  private Map<String, String> webSites;
  
  private static IWebServerConfiguration config;

  public SimpleWebServer(String propertyFile) {
config = new WebServerConfiguration(propertyFile);
	  // if no value is found with the key, then default value will used
    this.port = config.getPort("port", 80);
    this.charset = config.getConfigurationWithDefaultValue("charset", "UTF-8");
    this.file = config.getConfigurationWithDefaultValue("default", "webroot/index.htm");
    
    this.webSites = config.getSites("site", new HashMap<String, String>());
  }

  private void init() {
    try {
      InetSocketAddress socketAddress = new InetSocketAddress(port);
      ssc = ServerSocketChannel.open();
      ssc.configureBlocking(false);
      ssc.bind(socketAddress);
      //webSites = readHTMLDirectory();
      ExecutorService executor = Executors.newFixedThreadPool(5);
      System.out.println("Socket opened waiting for connection");
      while (hold == false) {
        SocketChannel channel = ssc.accept();
        if (channel == null) {
          Thread.sleep(10);
        } else {
          System.out.println("connection acpeted start handler");
          ClientHandler handler = new ClientHandler(channel, webSites, this);
          executor.execute(handler);
          this.handlerList.add(handler);
        }
      }
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }

  /*private HashMap<String, String> readHTMLDirectory() {
	    Charset charset = Charset.forName(this.charset);
	    HashMap<String, String> retVal =  new HashMap<String, String>();
		File f = new File(this.file);
		try {
			FileInputStream fis = new FileInputStream(f);
			byte[] content = new byte[(int) f.length()];
			fis.read(content);
			fis.close();
			CharBuffer charBuffer = charset.decode(ByteBuffer.wrap(content));
			String input = charBuffer.toString();
			String site = addDefaultHeaders(input);
			System.out.println("adding string: "+site.toString());
			retVal.put("/index.htm", site);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    return retVal;
  }

  private String  addDefaultHeaders(String content) {
	  StringBuilder buiString = new StringBuilder();
	  buiString.append("HTTP/1.1 200 OK \n");
	  buiString.append("Date"+ new Date().toString()+"\n");
	  buiString.append("Server "+ "Java Webserver by Wolfi and Edmund\n");
	  buiString.append("Connection "+ "close\n");
	  buiString.append("Content-Type: text/html; charset=UTF-8 \n");
	  buiString.append("Content-Length "+ content.length()+"\n");
	  buiString.append("\n"+content);
	  return buiString.toString();
	}*/

  public void removeHandler(ClientHandler handler) {
    this.handlerList.remove(handler);
  }
  
  public static IWebServerConfiguration getConfig() {
	return config;
  }
  
  public static void setConfig(IWebServerConfiguration config) {
	SimpleWebServer.config = config;
  }

  public static void main(String[] args) {
	  // property file used by webserver
    String propertyFile = "server.properties";
    SimpleWebServer runner = new SimpleWebServer(propertyFile);
    runner.init();
  }
}

//---------------------------- Revision History ----------------------------
//$Log$
//
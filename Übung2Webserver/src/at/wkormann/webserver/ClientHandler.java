
package at.wkormann.webserver;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.HashMap;
import java.util.Map;

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

public class ClientHandler implements Runnable {

  private SocketChannel channel;
  private Map<String, String> webSites;
  private IClientToHandlerInterface parent;
  CharsetDecoder decoder;
  CharsetEncoder encoder;
  ByteBuffer buffer;
  CharBuffer charBuffer;
  Charset charset;

  public ClientHandler(SocketChannel channel, Map<String, String> webSites, IClientToHandlerInterface parent) {
    this.channel = channel;
    this.webSites = webSites;
    this.parent = parent;
    charset = Charset.forName("ISO-8859-1");
    decoder = charset.newDecoder();
    encoder = charset.newEncoder();

    buffer = ByteBuffer.allocateDirect(1024);
    charBuffer = CharBuffer.allocate(1024);
  }

  public void updateWebsites(HashMap<String, String> webSites) {
    synchronized (webSites) {
      this.webSites = webSites;
    }
  }

  @Override
  public void run() {
    try {
      channel.read(buffer);
      while (!isComplete(buffer)) {
        try {
          this.wait(10);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        channel.read(buffer);
      }
      buffer.flip();
      charBuffer = charset.decode(buffer);
      //Crete Helper class for creating an HTTPRequest object containing the Request Type and the content
      String input = charBuffer.toString();
      String[] splitted = input.split(" ");
      if (splitted[0].equalsIgnoreCase("GET")) {
        String tmp = splitted[1];
        System.out.println(tmp);
        String returnVal = this.webSites.get(tmp);
        if (returnVal == null) {
          returnVal =this.webSites.get("/index.htm");
        }
        this.channel.write(charset.encode(returnVal));
        System.out.println("returning: " + returnVal);
        Thread.sleep(100);
        exit();
      }
    } catch (IOException e) {
      e.printStackTrace();
      exit();
    } catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

  }

  private void exit() {
    try {
      this.channel.close();
    } catch (IOException e1) {
      e1.printStackTrace();
    }
    this.webSites = null;
    this.decoder = null;
    this.encoder = null;
    this.buffer = null;
    this.charBuffer = null;
    this.parent.removeHandler(this);
  }

  static boolean isComplete(ByteBuffer bb) {
    int p = bb.position() - 4;
    if (p < 0)
      return false;
    return (((bb.get(p + 0) == '\r') && (bb.get(p + 1) == '\n') && (bb.get(p + 2) == '\r') && (bb.get(p + 3) == '\n')));
  }
}

//---------------------------- Revision History ----------------------------
//$Log$
//
package org.apache.cordova.plugin.websocket;

import java.lang.reflect.Constructor;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_10;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * WebSocket Cordova Plugin
 */
public class WebSocket extends CordovaPlugin {

  // actions
  private static final String ACTION_CONNECT = "connect";
  private static final String ACTION_SEND = "send";
  private static final String ACTION_CLOSE = "close";
  private CordovaClient socketClient;
  private URI uri;
  private Draft draft;
  
  private static final Map<String, String> draftMap = new HashMap<String, String>();
  static {
    draftMap.put("draft10", "org.java_websocket.drafts.Draft_10");
    draftMap.put("draft17", "org.java_websocket.drafts.Draft_17");
    draftMap.put("draft75", "org.java_websocket.drafts.Draft_75");
    draftMap.put("draft76", "org.java_websocket.drafts.Draft_76");
  }

  @Override
  public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
    final WebSocket plugin = this;

    if (ACTION_CONNECT.equals(action)) {
      final String url = args.getString(0);
      final String protocol = args.getString(1);
      
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          plugin.connect(url, callbackContext, protocol);
        }
      });
      return true;
    }
    else if (ACTION_SEND.equals(action)) {
      final String data = args.getString(0);
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          plugin.send(data);
        }
      });
      return true;
    }
    else if (ACTION_CLOSE.equals(action)) {
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          plugin.socketClient.close();
        }
      });
      return true;
    }

    return false;
  }

  private void connect(String url, CallbackContext callbackContext, String protocol) {

  	
    if (url != null && url.length() > 0) {
      try {
        this.uri = new URI(url);
        
        this.draft = getDraft(protocol, callbackContext);
        
        
        this.socketClient = new CordovaClient(uri, draft, callbackContext);
        PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
        pluginResult.setKeepCallback(true);
        callbackContext.sendPluginResult(pluginResult);
        this.socketClient.connect();
      } catch (URISyntaxException e) {
        callbackContext.error("Not a valid URL");
      }
    } else {
      callbackContext.error("Not a valid URL");
    }
  }
  
  private Draft getDraft(String protocol, CallbackContext callbackContext) {
  	Draft draft = new Draft_10();
  	
  	if (protocol != null) {
    	String draftName = draftMap.get(protocol);
    	
    	if (draftName != null) {
        try {
        	Class<?> clazz = Class.forName(draftName);
        	Constructor<?> ctor = clazz.getConstructor(String.class);
        	draft = (Draft) ctor.newInstance();
        } 
        catch (Exception e) {
        	callbackContext.error("Draft not found.");
        } 
    	}
    }  
  	
  	return draft;
  }

  private void send(String data) {
    if (data != null && data.length() > 0 &&
      this.socketClient.getConnection().isOpen()) {
      this.socketClient.send(data);
    }
  }
}
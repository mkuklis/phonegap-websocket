package com.ququplay.websocket;

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
import org.json.JSONObject;

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
  private Map<String, String> headers;
  
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
      final JSONObject options = args.getJSONObject(1);
      
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          plugin.connect(url, callbackContext, options);
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

  private void connect(String url, CallbackContext callbackContext, JSONObject options) {

    if (url != null && url.length() > 0) {
      try {
        this.uri = new URI(url);
        this.draft = this.getDraft(options, callbackContext);
        this.headers = this.getHeaders(options);
        
        this.socketClient = new CordovaClient(this.uri, this.draft, this.headers, callbackContext);
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
  
  private Draft getDraft(JSONObject options, CallbackContext callbackContext) {

    String draftName;
    Draft draft = new Draft_10();
    
    try {
      draftName = options.getString("draft");
    } 
    catch (JSONException e1) {
      return draft;
    }
   
    if (draftName != null) {
      String draftClassName = draftMap.get(draftName);
      
      if (draftClassName != null) {
        try {
          Class<?> clazz = Class.forName(draftClassName);
          Constructor<?> ctor = clazz.getConstructor();
          draft = (Draft) ctor.newInstance();
        }
        catch (Exception e) {
          callbackContext.error("Draft not found.");
        }
      }
    }
    
    return draft;
  }
  
  private Map<String, String> getHeaders(JSONObject options) {    
    try {
      return Utils.jsonToMap(options.getJSONObject("headers"));
    } 
    catch (JSONException e) {
      return null;
    }
  }

  private void send(String data) {
    if (data != null && data.length() > 0 &&
      this.socketClient.getConnection() != null &&
      this.socketClient.getConnection().isOpen()) {
      this.socketClient.send(data);
    }
  }
}
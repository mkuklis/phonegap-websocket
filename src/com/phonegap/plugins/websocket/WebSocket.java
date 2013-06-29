package com.phonegap.plugins.websocket;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.apache.cordova.api.PluginResult;
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

  @Override
  public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
    final WebSocket plugin = this;

    if (ACTION_CONNECT.equals(action)) {
      final String url = args.getString(0);
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          plugin.connect(url, callbackContext);
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

  private void connect(String url, CallbackContext callbackContext) {

    if (url != null && url.length() > 0) {
      try {
        this.uri = new URI(url);
        this.socketClient = new CordovaClient(uri, callbackContext);
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

  private void send(String data) {
    if (data != null && data.length() > 0 && 
    		this.socketClient.getConnection().isOpen()) {
      this.socketClient.send(data);
    }
  }
}

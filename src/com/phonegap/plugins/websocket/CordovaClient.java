package com.phonegap.plugins.websocket;

import java.net.URI;
import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.PluginResult;
import org.apache.cordova.api.PluginResult.Status;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

public class CordovaClient extends WebSocketClient {

  private CallbackContext callbackContext;

  public CordovaClient(URI serverUri , Draft draft) {
    super(serverUri, draft);
  }

  public CordovaClient(URI serverURI, CallbackContext callbackContext) {
    super(serverURI);
    this.callbackContext = callbackContext;
  }

  @Override
  public void onOpen(ServerHandshake handshakedata) {
    sendResult("", "open", PluginResult.Status.OK);
  }

  @Override
  public void onMessage(String message) {
    sendResult(message, "message", PluginResult.Status.OK);
  }

  @Override
  public void onClose(int code, String reason, boolean remote) {
    sendResult("", "close", PluginResult.Status.OK);
  }

  @Override
  public void onError(Exception ex) {
    sendResult(ex.getMessage(), "error", PluginResult.Status.ERROR);
  }

  private void sendResult(String message, String type, Status status) {
    JSONObject event = createEvent(message, type);
    PluginResult pluginResult = new PluginResult(status, event);
    pluginResult.setKeepCallback(true);
    this.callbackContext.sendPluginResult(pluginResult);
  }

  private JSONObject createEvent(String data, String type) {
    JSONObject event;

    try {
      event = new JSONObject();
      event.put("type", type);
      event.put("data", data);
      event.put("readyState", this.getReadyState());
      return event;
    }
    catch (JSONException e) {
      e.printStackTrace();
    }

    return null;
  }
}

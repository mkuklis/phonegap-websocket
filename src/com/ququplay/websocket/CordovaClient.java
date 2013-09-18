package com.ququplay.websocket;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;
import org.java_websocket.WebSocket.READYSTATE;

public class CordovaClient extends WebSocketClient {

  private CallbackContext callbackContext;

  private static final Map<READYSTATE, Integer> stateMap = new HashMap<READYSTATE, Integer>();
  static {
    stateMap.put(READYSTATE.CONNECTING, 0);
    stateMap.put(READYSTATE.OPEN, 1);
    stateMap.put(READYSTATE.CLOSING, 2);
    stateMap.put(READYSTATE.CLOSED, 3);
    stateMap.put(READYSTATE.NOT_YET_CONNECTED, 3);
  }

  public CordovaClient(URI serverURI, Draft draft, Map<String, String> headers, CallbackContext callbackContext) {
    super(serverURI, draft, headers, 0);
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
      event.put("readyState", stateMap.get(this.getReadyState()));
      return event;
    }
    catch (JSONException e) {
      e.printStackTrace();
    }

    return null;
  }
}
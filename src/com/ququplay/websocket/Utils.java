package com.ququplay.websocket;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class Utils {
  public static Map<String, String> jsonToMap(JSONObject data) throws JSONException {

    @SuppressWarnings("unchecked")
    Iterator<String> keys = data.keys();
    Map<String, String> result = new HashMap<String, String>();

    while (keys.hasNext()) {
      String key = keys.next();
      result.put(key, data.getString(key));
    }

    return result;
  }
}
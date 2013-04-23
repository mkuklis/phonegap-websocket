## Websocket PhoneGap plugin for Android

Phonegap plugin which adds support for websockets under Android.
The websocket client is based on [Java-WebSocket](https://github.com/TooTallNate/Java-WebSocket).

The plugin works well with [socket.io](http://socket.io/).

## Installation

1. Copy src folder under your project's src
2. Copy libs/java_websocket.jar under your project's libs
3. Copy `assets/www/phonegap-websocket.js` unders your project's `assets/www/` and make sure it's included in your `index.html`
4. add plugin definition to res/xml/config.xml

```xml
  <plugin name="WebSocket" value="com.phonegap.plugins.websocket.WebSocket" />
```

##License:
<pre>
The MIT License
</pre>

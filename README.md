## Websocket PhoneGap plugin for Android

Phonegap plugin which adds support for websockets under Android.
The websocket client is based on [Java-WebSocket](https://github.com/TooTallNate/Java-WebSocket).

The plugin works well with [socket.io](http://socket.io/).

## Installation

1. Copy `src/*` folder under your project's `src`
2. Copy `libs/java_websocket.jar` under your project's `libs`
3. Copy `www/phonegap-websocket.js` unders your project's `assets/www/` and make sure it's included in your `index.html`
4. add plugin definition to res/xml/config.xml

```xml
  <plugin name="WebSocket" value="com.ququplay.websocket.WebSocket" />
```

## Demo

[https://bitbucket.org/mkuklis/phonegap-websocket-demo](https://bitbucket.org/mkuklis/phonegap-websocket-demo/)

## Note

If you plan to test it locally from your emulator please make sure to use `10.0.2.2` ip address when connecting to your 
local server.

On Android 2.x a Typed Array polyfill is required to send or receive ArrayBuffer data.  Sending and receiving Blob data 
is not supported on Android 2.x

##License:
<pre>
The MIT License
</pre>

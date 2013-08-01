## Websocket PhoneGap plugin for Android

Phonegap plugin which adds support for websockets under Android.
The websocket client is based on [Java-WebSocket](https://github.com/TooTallNate/Java-WebSocket).

The plugin works well with [socket.io](http://socket.io/).

Compatible with PhoneGap 3.0.x and installable via the PhoneGap (or Cordova) CLI.

## Installation

Inside your phonegap project:

```bash
cordova plugin add https://github.com/mkuklis/phonegap-websocket
```

Or using the phonegap CLI:

```bash
phonegap local plugin add https://github.com/mkuklis/phonegap-websocket
```

## Note

If you plan to test it locally from your emulator please make sure to use `10.0.2.2` ip address when connecting to your local server.

##License:

<pre>
The MIT License
</pre>

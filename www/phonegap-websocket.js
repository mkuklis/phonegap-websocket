var _websocket_id = 0;

// Websocket constructor
var WebSocket = function(url, opt1, opt2) {
  var socket = this;
  
	var protocols = null;
	var options = opt2 ? opt2 : {};
	if (typeof opt1 === "string") {

		protocols = opt1.trim();

	} else if (Array.isArray(opt1)) {

		for ( var i = 0; i < opt1.length; i++) {

			if (i > 0) {

				protocols += ", ";
			}
			protocols += opt1[i].trim();
		}

	} else if (opt1 !== null && typeof opt1 === "object") {

		options = opt1;
	}
	if (protocols !== null) {

		options.headers || (options.headers = {});
		options.headers["Sec-WebSocket-Protocol"] = protocols;
	}

  this.events = [];
  this.options = options;
  this.url = url;
  this.readyState = WebSocket.CONNECTING;
  this.socketId = "_cordova_websocket_" + _websocket_id;
  _websocket_id += 1;
  
  cordova.exec(
    function (event) {
      socket._handleEvent(event);
    },
    function (event) {
      socket._handleEvent(event);
    }, "WebSocket", "connect", [ this.socketId, url, options ]);
};

WebSocket.prototype = {
  send: function (data) {
    if (this.readyState == WebSocket.CLOSED ||
        this.readyState == WebSocket.CLOSING) {
      return;
    }
	if (data instanceof ArrayBuffer) {
		
		data = this._arrayBufferToArray(data);

	} else if (data instanceof Blob) {

		var reader = new FileReader();
		reader.onloadend = function() {
			this.send(reader.result);
		}.bind(this);
		reader.readAsArrayBuffer(data);
		return;
	}
    cordova.exec(function () {}, function () {}, "WebSocket", "send", [ this.socketId, data ]);
  },

  close: function () {
    if (this.readyState == WebSocket.CLOSED ||
        this.readyState == WebSocket.CLOSING) {
      return;
    }

    this.readyState = WebSocket.CLOSING;
    cordova.exec(function () {}, function () {}, "WebSocket", "close", [ this.socketId ]);
  },

  addEventListener: function (type, listener, useCapture) {
    this.events[type] || (this.events[type] = []);
    this.events[type].push(listener);
  },

  removeEventListener: function (type, listener, useCapture) {
    var events;

    if (!this.events[type]) return;

    events = this.events[type];

    for (var i = events.length - 1; i >= 0; --i) {
      if (events[i] === listener) {
        events.splice(i, 1);
        return;
      }
    }
  },

  dispatchEvent: function (event) {
    var handler;
    var events = this.events[event.type] || [];

    for (var i = 0, l = events.length; i < l; i++) {
      events[i](event);
    }

    handler = this["on" + event.type];
    if (handler) handler(event);
  },

  _handleEvent: function (event) {
    this.readyState = event.readyState;
	if (event.type == "message") {

		event = this._createMessageEvent("message", event.data);

	} else if (event.type == "messageBinary") {

		var result = this._arrayToBinaryType(event.data, this.binaryType);
		event = this._createBinaryMessageEvent("message", result);

	} else {

		event = this._createSimpleEvent(event.type);
	}
    this.dispatchEvent(event);
    if (event.readyState == WebSocket.CLOSING || event.readyState == WebSocket.CLOSED) {
      // cleanup socket from internal map
      cordova.exec(function () {}, function () {}, "WebSocket", "close", [ this.socketId ]);
    }
  },

  _createSimpleEvent: function (type) {
    var event = document.createEvent("Event");

    event.initEvent(type, false, false);

    return event;
  },

  _createMessageEvent: function (type, data) {
    var event = document.createEvent("MessageEvent");

    event.initMessageEvent("message", false, false, data, null, null, window, null);

    return event;
  },
  
	_createBinaryMessageEvent : function(type, data) {

		// This does not match the WebSocket spec. The Event is suppose to be a
		// MessageEvent. But in Android WebView, MessageEvent.initMessageEvent() 
		// makes a mess of ArrayBuffers.  This should work with most clients, as
		// long as they don't do something odd with the event.  The type is 
		// correctly set to "message", so client event routing logic should work.
		var event = document.createEvent("Event");

		event.initEvent("message", false, false);
		event.data = data;
		return event;
	},
  
	_arrayBufferToArray : function(arrayBuffer) {

		var output = [];
		var utf8arr = new Uint8Array(arrayBuffer);
		for ( var i = 0; i < utf8arr.length; i++) {

			output.push(utf8arr[i]);
		}
		return output;
	},
	
	_arrayToBinaryType : function(array, binaryType) {
		
		var result = null;
		var typedArr = new Uint8Array(array.length);
		typedArr.set(array);
		
		if (binaryType === "arraybuffer") {

			result = typedArr.buffer;

		} else if (binaryType === "blob") {
			
			var builder = new WebKitBlobBuilder();
			builder.append(typedArr.buffer);
			result = builder.getBlob("application/octet-stream");
		}
		return result;
	}
};

WebSocket.prototype.CONNECTING = WebSocket.CONNECTING = 0;
WebSocket.prototype.OPEN = WebSocket.OPEN = 1;
WebSocket.prototype.CLOSING = WebSocket.CLOSING = 2;
WebSocket.prototype.CLOSED = WebSocket.CLOSED = 3;

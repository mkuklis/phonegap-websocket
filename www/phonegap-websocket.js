(function () {

  // Websocket constructor
  var WebSocket = window.WebSocket = function (url) {
    var socket = this;

    this.events = [];
    this.url = url;
    this.readyState = WebSocket.CONNECTING;

    cordova.exec(
      function (event) {
        socket._handleEvent(event);
      },
      function (event) {
        socket._handleEvent(event);
      }, "WebSocket", "connect", [url]);
  }

  WebSocket.CONNECTING = 0;
  WebSocket.OPEN = 1;
  WebSocket.CLOSING = 2;
  WebSocket.CLOSED = 3;

  WebSocket.prototype = {
    send: function (data) {
      cordova.exec(function () {}, function () {}, "WebSocket", "send", [data]);
    },

    close: function () {
      if (this.readyState == WebSocket.CLOSED ||
          this.readyState == WebSocket.CLOSING) {
        return;
      }

      this.readyState = WebSocket.CLOSING;
      cordova.exec(function () {}, function () {}, "WebSocket", "close", []);
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
      event = (event.type == "message") ?
        event = this._createMessageEvent("message", event.data) :
        event = this._createSimpleEvent(event.type);
      this.dispatchEvent(event);
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
    }
  };
})();

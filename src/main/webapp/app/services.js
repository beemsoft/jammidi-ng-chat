(function(angular, SockJS, Stomp, _) {
  angular.module("app.services").service("ChatService", function($q, $timeout) {
    
    var service = {}, listener = $q.defer(), socket = {
      client: null,
      stomp: null
    }, messageIds = [];
    
    service.RECONNECT_TIMEOUT = 30000;
    service.SOCKET_URL = "/jammidi/chat";
    service.CHAT_TOPIC = "/topic/message";
    service.CHAT_BROKER = "/app/chat";
    
    service.receive = function() {
      return listener.promise;
    };
    
    service.send = function(message) {
      var id = Math.floor(Math.random() * 1000000);
      socket.stomp.send(service.CHAT_BROKER, {
        priority: 9
      }, JSON.stringify({
        message: message,
        id: id
      }));
      messageIds.push(id);
    };
    
    var reconnect = function() {
      $timeout(function() {
        initialize();
      }, this.RECONNECT_TIMEOUT);
    };
    
    var getMessage = function(data) {
      var message = JSON.parse(data), out = {};
      out.message = message.message;
      out.time = new Date(message.time);
      if (_.contains(messageIds, message.id)) {
        out.self = true;
        messageIds = _.remove(messageIds, message.id);
      }
      return out;
    };
    
    var startListener = function() {
      socket.stomp.subscribe(service.CHAT_TOPIC, function(data) {
        listener.notify(getMessage(data.body));
      });
    };
    
    var initialize = function() {
      socket.client = new SockJS(service.SOCKET_URL);
      socket.stomp = Stomp.over(socket.client);
      socket.stomp.connect({}, startListener);
      socket.stomp.onclose = reconnect;
    };
    
    initialize();
    return service;
  });

  angular.module("app.services").service("MidiService", function($q, $timeout) {

    var service = {}, listener = $q.defer(), socket = {
      client: null,
      stomp: null
    }, midiIds = [], prevMillis = 0;
    var isReplay = false, isFirstReplayNote = false;

    service.RECONNECT_TIMEOUT = 30000;
    service.SOCKET_URL = "/jammidi/midi";
    service.MIDI_TOPIC = "/topic/midi";
    service.MIDI_BROKER = "/app/midi";
    service.MIDI_REPLAY = "/app/replay";
    service.MIDI_REPLAY_ALL = "/app/replayAll";
    service.MIDI_CLEAR = "/app/clear";

    service.receive = function() {
      return listener.promise;
    };

    service.send = function(a, key, b, user, songTitle, desc) {
      function getInterval() {
        var date = new Date();
        var millis = date.getTime();
        var intervalMillis = 0;
        if (prevMillis !== 0) {
          intervalMillis = millis - prevMillis;
        }
        prevMillis = millis;
        return intervalMillis;
      }

      var version = 1;
      if (isReplay) {
        version = 2;
      }

      var id = Math.floor(Math.random() * 1000000);
      socket.stomp.send(service.MIDI_BROKER, {
        priority: 9
      }, JSON.stringify({
        interval: getInterval(),
        a: a,
        key: key,
        b: b,
        id: id,
        user: user,
        songTitle: songTitle,
        version: version,
        desc: desc
      }));
      midiIds.push(id);
    };

    service.replay = function(songTitle, version) {
      prevMillis = 0;
      isReplay = true;
      isFirstReplayNote = false;
      socket.stomp.send(service.MIDI_REPLAY, {
        priority: 9
      }, JSON.stringify({
            title: songTitle,
            version: version
          }
      ));
    };

    service.replayAll = function(songTitle) {
      socket.stomp.send(service.MIDI_REPLAY_ALL, {
            priority: 9
          }, songTitle
      );
    };

    service.clear = function(songTitle, version) {
      socket.stomp.send(service.MIDI_CLEAR, {
            priority: 9
          }, JSON.stringify({
                title: songTitle,
                version: version
              }
          )
      );
    };

    service.reset = function () {
      prevMillis = 0;
      isReplay = false;
    };

    var reconnect = function() {
      $timeout(function() {
        initialize();
      }, this.RECONNECT_TIMEOUT);
    };

    var getMidi = function(data) {
      var midi = JSON.parse(data), out = {};
      out.midi = midi;
      if (_.contains(midiIds, midi.id)) {
        out.self = true;
        midiIds = _.remove(midiIds, midi.id);
      }

      if (isReplay && !isFirstReplayNote) {
        isFirstReplayNote = true;
        var date = new Date();
        prevMillis = date.getTime();
      }
      return out;
    };

    var startListener = function() {
      socket.stomp.subscribe(service.MIDI_TOPIC, function(data) {
        listener.notify(getMidi(data.body));
      });
    };

    var initialize = function() {
      socket.client = new SockJS(service.SOCKET_URL);
      socket.stomp = Stomp.over(socket.client);
      socket.stomp.connect({}, startListener);
      socket.stomp.onclose = reconnect;
    };

    initialize();
    return service;
  });
})(angular, SockJS, Stomp, _);
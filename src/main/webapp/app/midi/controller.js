(function() {

  angular
      .module("jammidi.controllers")
      .factory('jazz', function () {
    var Jazz = document.getElementById("Jazz1"); if(!Jazz || !Jazz.isJazz) Jazz = document.getElementById("Jazz2");
    return {
      MidiInOpen: function(channel, callback) {
        Jazz.MidiInOpen(channel, callback);
      },
      MidiOutList: function() {
        return Jazz.MidiOutList();
      },
      MidiInList: function() {
        return Jazz.MidiInList();
      },
      MidiOut: function(a, key, b) {
        Jazz.MidiOut(a, key, b);
      }
    };
  });

  angular
      .module("jammidi.controllers")
      .controller("MainCtrl", function($scope, jazz, MidiService) {

    $scope.note = [];
    $scope.sounds = sounds;
    $scope._sound='Drawbar Organ';

    $scope.list = jazz.MidiOutList();
    $scope._out = $scope.list[0];
    $scope.listIn = jazz.MidiInList();
    $scope._in = $scope.listIn[0];

    $scope.isMuted = { value: false };

    MidiService.receive()
        .then(null, null, function(midiEvent) {
      if ($scope.note.indexOf(midiEvent.midi.key) == -1) {
        jazz.MidiOut(midiEvent.midi.a, midiEvent.midi.key, midiEvent.midi.b);
      }
      if (midiEvent.midi.b == 127 || midiEvent.midi.b == 0) {
        if ($scope.note.indexOf(midiEvent.midi.key) > -1) {
          $scope.note.splice($scope.note.indexOf(midiEvent.midi.key), 1)
        }
      } else {
        if ($scope.note.indexOf(midiEvent.midi.key) == -1) {
          $scope.note.push(midiEvent.midi.key)
        }
      }
    });

    function play (a, key, c) {
      if (c == 127 || c == 0) {
        $scope.note.splice($scope.note.indexOf(key),1)
      } else {
        $scope.note.push(key)
      }
      jazz.MidiOut(a, key, c);
      if (!$scope.isMuted.value) {
        function playRemote() {
          MidiService.send(a, key, c, $scope.user, $scope.songTitle, $scope.desc);
        }
        playRemote();
      }
    }

    jazz.MidiInOpen(0, function (t,a,key,c) {
      play(a, key, c);
    });

    $scope.keys = {
      b: [1,3,'|',6,8,10,'|',13,15,'|',18,20,22,'|',25,27,'|',30,32,34,'|',37,39,'|',42,44,46,'|',49,51,'|',54,56,58,'|',61,63,'|',66,68,70,'|',73,75,'|',78,80,82,'|',85,87,'|',90,92,94,'|',97,99,'|',102,104,106,'|',109,111,'|',114,116,118,'|',121,123,'|',126],
      w: [0,2,4,5,7,9,11,12,14,16,17,19,21,23,24,26,28,29,31,33,35,36,38,40,41,43,45,47,48,50,52,53,55,57,59,60,62,64,65,67,69,71,72,74,76,77,79,81,83,84,86,88,89,91,93,95,96,98,100,101,103,105,107,108,110,112,113,115,117,119,120,122,124,125,127]
    };

    $scope.down = function(id){
      play(0x90, id, 126);
    };
    $scope.up = function(id){
      play(0x80, id, 0);
    };

    $scope.changemidi = function() {
      jazz.MidiOutOpen($scope._out);
      jazz.MidiOut(0xc0, $.inArray( $scope._sound, $scope.sounds ), 0);
      event.target.blur();
    };

    $scope.changeSnd = function() {
      jazz.MidiOut(0xc0, $.inArray($scope._sound, $scope.sounds), 0);
      selSnd.blur();
    };

    $scope.isSelected = function (id) {
      return $scope.note.indexOf(id) > -1;
    };

    $scope.replay = function () {
      MidiService.replay($scope.songTitle, $scope.version);
    };

    $scope.replayAll = function () {
      MidiService.replayAll($scope.songTitle);
    };

    $scope.clear = function () {
      MidiService.clear($scope.songTitle, $scope.version);
    };

  });

})();
(function () {
  'use strict';

  angular
    .module('app')
    .controller('HomeController', HomeController);

  HomeController.$inject = ['$location', 'UserService', '$rootScope', 'ChatService', 'AuthenticationService'];
  function HomeController($location, UserService, $rootScope, ChatService, AuthenticationService) {
    var vm = this;

    vm.user = null;
    vm.allUsers = [];
    vm.update = update;
    vm.deleteUser = deleteUser;
    vm.logOff = logOff;

    initController();

    function initController() {
      loadCurrentUser();
      loadAllUsers();
    }

    function loadCurrentUser() {
      UserService.GetByUsername($rootScope.globals.currentUser.username)
        .then(function (response) {
          vm.user = response.data;
        });
    }

    function loadAllUsers() {
      UserService.GetAll()
        .then(function (response) {
          vm.allUsers = response.data;
        });
    }

    function deleteUser(user) {
      UserService.Delete(user)
        .then(function () {
          if (user.username === vm.user.username) {
            logOff();
          } else {
            loadAllUsers();
          }
        });
    }

    function update() {
      $location.path('/update');
    }

    function logOff() {
      ChatService.send(vm.user.username + ' logged off');
      AuthenticationService.ClearCredentials()
        .then(function () {
          $location.path('/login');
        })
    }
  }

})();
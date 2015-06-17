(function () {
  'use strict';

  angular
    .module('app')
    .controller('UpdateUserController', UpdateUserController);

  UpdateUserController.$inject = ['UserService', '$location', '$rootScope', 'FlashService'];
  function UpdateUserController(UserService, $location, $rootScope, FlashService) {
    var vm = this;

    vm.update = update;
    vm.user = null;

    initController();

    function initController() {
      loadCurrentUser();
    }

    function loadCurrentUser() {
      UserService.GetByUsername($rootScope.globals.currentUser.username)
        .then(function (response) {
          vm.user = response.data;
        });
    }

    function update() {
      vm.dataLoading = true;
      UserService.Update(vm.user)
        .then(function (response) {
          if (response.status === 200) {
            FlashService.Success('Update successful', true);
            $location.path('/login');
          } else {
            FlashService.Error(response.message);
            vm.dataLoading = false;
          }
        });
    }
  }

})();

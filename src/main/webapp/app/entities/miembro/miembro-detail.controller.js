(function() {
    'use strict';

    angular
        .module('reunionsApp')
        .controller('MiembroDetailController', MiembroDetailController);

    MiembroDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Miembro', 'Organo', 'Cargo', 'User'];

    function MiembroDetailController($scope, $rootScope, $stateParams, previousState, entity, Miembro, Organo, Cargo, User) {
        var vm = this;

        vm.miembro = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('reunionsApp:miembroUpdate', function(event, result) {
            vm.miembro = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();

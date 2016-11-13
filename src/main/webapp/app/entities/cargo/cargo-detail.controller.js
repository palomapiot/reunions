(function() {
    'use strict';

    angular
        .module('reunionsApp')
        .controller('CargoDetailController', CargoDetailController);

    CargoDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Cargo'];

    function CargoDetailController($scope, $rootScope, $stateParams, previousState, entity, Cargo) {
        var vm = this;

        vm.cargo = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('reunionsApp:cargoUpdate', function(event, result) {
            vm.cargo = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();

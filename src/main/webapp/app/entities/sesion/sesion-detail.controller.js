(function() {
    'use strict';

    angular
        .module('reunionsApp')
        .controller('SesionDetailController', SesionDetailController);

    SesionDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Sesion', 'Organo', 'Participante'];

    function SesionDetailController($scope, $rootScope, $stateParams, previousState, entity, Sesion, Organo, Participante) {
        var vm = this;

        vm.sesion = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('reunionsApp:sesionUpdate', function(event, result) {
            vm.sesion = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();

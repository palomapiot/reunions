(function() {
    'use strict';

    angular
        .module('reunionsApp')
        .controller('GrupoDetailController', GrupoDetailController);

    GrupoDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Grupo', 'Organo'];

    function GrupoDetailController($scope, $rootScope, $stateParams, previousState, entity, Grupo, Organo) {
        var vm = this;

        vm.grupo = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('reunionsApp:grupoUpdate', function(event, result) {
            vm.grupo = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();

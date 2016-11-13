(function() {
    'use strict';

    angular
        .module('reunionsApp')
        .controller('OrganoDetailController', OrganoDetailController);

    OrganoDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Organo', 'Grupo', 'Miembro', 'Sesion'];

    function OrganoDetailController($scope, $rootScope, $stateParams, previousState, entity, Organo, Grupo, Miembro, Sesion) {
        var vm = this;

        vm.organo = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('reunionsApp:organoUpdate', function(event, result) {
            vm.organo = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();

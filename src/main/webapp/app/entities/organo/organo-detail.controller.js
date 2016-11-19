(function() {
    'use strict';

    angular
        .module('reunionsApp')
        .controller('OrganoDetailController', OrganoDetailController);

    OrganoDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Organo', 'Grupo', 'Miembro', 'Sesion', 'ParseLinks', 'pagingParams', 'paginationConstants'];

    function OrganoDetailController($scope, $rootScope, $stateParams, previousState, entity, Organo, Grupo, Miembro, Sesion, ParseLinks, pagingParams, paginationConstants) {
        var vm = this;

        vm.organo = entity;

        vm.previousState = previousState.name;

        vm.miembrosAnteriores = Organo.miembrosAnteriores({ id : $stateParams.id });
        vm.miembros = Organo.miembros({ id : $stateParams.id })

        var unsubscribe = $rootScope.$on('reunionsApp:organoUpdate', function(event, result) {
            vm.organo = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();

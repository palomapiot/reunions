(function() {
    'use strict';

    angular
        .module('reunionsApp')
        .controller('OrganoDetailController', OrganoDetailController);

    OrganoDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Organo', 'Grupo', 'Miembro', 'Sesion', 'ParseLinks'];

    function OrganoDetailController($scope, $rootScope, $stateParams, previousState, entity, Organo, Grupo, Miembro, Sesion, ParseLinks) {
        var vm = this;

        vm.organo = entity;

        vm.previousState = previousState.name;

        vm.miembrosAnteriores = Organo.miembrosAnteriores({ id : $stateParams.id });
        vm.miembros = Organo.miembros({ id : $stateParams.id });
        vm.sesiones = Organo.sesiones({ id: $stateParams.id });

        vm.qFn = function(actual, expected) {
            if (angular.isObject(actual)) return false;
            function removeAccents(value) {
              return value.toString().replace(/á/g, 'a').replace(/é/g, 'e').replace(/í/g, 'i').replace(/ó/g, 'o').replace(/ú/g, 'u').replace(/ñ/g, 'n');
            }
            actual = removeAccents(angular.lowercase('' + actual));
            expected = removeAccents(angular.lowercase('' + expected));

            return actual.indexOf(expected) !== -1;
        }

        var unsubscribe = $rootScope.$on('reunionsApp:organoUpdate', function(event, result) {
            vm.organo = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();

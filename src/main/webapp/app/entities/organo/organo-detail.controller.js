(function() {
    'use strict';

    angular
        .module('reunionsApp')
        .controller('OrganoDetailController', OrganoDetailController);

    OrganoDetailController.$inject = ['$location', '$anchorScroll', '$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Principal', 'Organo', 'Grupo', 'Miembro', 'Sesion', 'ParseLinks'];

    function OrganoDetailController($location, $anchorScroll, $scope, $rootScope, $stateParams, previousState, entity, Principal, Organo, Grupo, Miembro, Sesion, ParseLinks) {
        var vm = this;

        vm.account = null;
        vm.isAuthenticated = null;
        vm.organo = entity;
        vm.admin = false;
        vm.predicate = 'user.lastName|noAccents';
        vm.reverse = true;
        vm.predicateAnteriores = 'user.lastName|noAccents';
        vm.reverseAnteriores = true;
        vm.goUp = function(id) {
                          var old = $location.hash();
                          $location.hash(id);
                          $anchorScroll();
                          //reset to old to keep any additional routing logic from kicking in
                          $location.hash(old);
                          };

        vm.previousState = previousState.name;

        getAccount();

        function getAccount() {
            Principal.identity().then(function(account) {
                vm.account = account;
                vm.isAuthenticated = Principal.isAuthenticated;
            });
        }

        vm.miembrosAnteriores = Organo.miembrosAnteriores({ id : $stateParams.id });
        Organo.miembros({ id : $stateParams.id }).$promise.then( function(data) {
             vm.miembros = data;
             if (vm.isAuthenticated) {
                if (vm.account.authorities.includes("ROLE_ADMIN")) {
                    vm.admin = true;
                } else {
                    data.forEach(function(element) {
                         if (element.user.login == vm.account.login && element.cargo.id < 3) vm.admin = true;
                    });
                }
             }
        });

        //vm.miembros = Organo.miembros({ id : $stateParams.id });
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

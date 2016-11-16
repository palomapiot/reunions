(function() {
    'use strict';

    angular
        .module('reunionsApp')
        .controller('OrganoDetailController', OrganoDetailController);

    OrganoDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Organo', 'Grupo', 'Miembro', 'Sesion', 'ParseLinks', 'pagingParams', 'paginationConstants'];

    function OrganoDetailController($scope, $rootScope, $stateParams, previousState, entity, Organo, Grupo, Miembro, Sesion, ParseLinks, pagingParams, paginationConstants) {
        var vm = this;

        vm.organo = entity;

        vm.loadPage = loadPage;
        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.transition = transition;

        vm.previousState = previousState.name;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        vm.loadMiembros = loadMiembros;
        vm.miembrosAnteriores = Organo.miembrosAnteriores({
            id : $stateParams.id});

        loadMiembros();

        function loadMiembros () {
            Organo.miembros({
                id : $stateParams.id,
                page: pagingParams.page - 1,
                size: vm.itemsPerPage,
                sort: sort()
            }, onSuccess, onError);
            function sort() {
                var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
                if (vm.predicate !== 'id') {
                    result.push('id');
                }
                return result;
            }
            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                vm.queryCount = vm.totalItems;
                vm.miembros = data;
                vm.page = pagingParams.page;
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
        }

        function loadPage (page) {
            vm.page = page;
            vm.transition();
        }

        function transition () {
            $state.transitionTo($state.$current, {
                page: vm.page,
                sort: vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc'),
                search: vm.currentSearch
            });
        }

        function clear () {
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'id';
            vm.reverse = true;
            vm.currentSearch = null;
            vm.transition();
        }

        var unsubscribe = $rootScope.$on('reunionsApp:organoUpdate', function(event, result) {
            vm.organo = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();

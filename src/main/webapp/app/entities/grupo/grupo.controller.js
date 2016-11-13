(function() {
    'use strict';

    angular
        .module('reunionsApp')
        .controller('GrupoController', GrupoController);

    GrupoController.$inject = ['$scope', '$state', 'Grupo', 'GrupoSearch'];

    function GrupoController ($scope, $state, Grupo, GrupoSearch) {
        var vm = this;
        
        vm.grupos = [];
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            Grupo.query(function(result) {
                vm.grupos = result;
            });
        }

        function search () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            GrupoSearch.query({query: vm.searchQuery}, function(result) {
                vm.grupos = result;
            });
        }    }
})();

(function() {
    'use strict';

    angular
        .module('reunionsApp')
        .controller('CargoController', CargoController);

    CargoController.$inject = ['$scope', '$state', 'Cargo', 'CargoSearch'];

    function CargoController ($scope, $state, Cargo, CargoSearch) {
        var vm = this;
        
        vm.cargos = [];
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            Cargo.query(function(result) {
                vm.cargos = result;
            });
        }

        function search () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            CargoSearch.query({query: vm.searchQuery}, function(result) {
                vm.cargos = result;
            });
        }    }
})();

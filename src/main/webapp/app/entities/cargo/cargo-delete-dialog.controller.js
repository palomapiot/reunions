(function() {
    'use strict';

    angular
        .module('reunionsApp')
        .controller('CargoDeleteController',CargoDeleteController);

    CargoDeleteController.$inject = ['$uibModalInstance', 'entity', 'Cargo'];

    function CargoDeleteController($uibModalInstance, entity, Cargo) {
        var vm = this;

        vm.cargo = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Cargo.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();

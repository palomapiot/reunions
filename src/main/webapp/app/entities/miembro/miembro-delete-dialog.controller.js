(function() {
    'use strict';

    angular
        .module('reunionsApp')
        .controller('MiembroDeleteController',MiembroDeleteController);

    MiembroDeleteController.$inject = ['$uibModalInstance', 'entity', 'Miembro'];

    function MiembroDeleteController($uibModalInstance, entity, Miembro) {
        var vm = this;

        vm.miembro = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete () {
            vm.miembro.fechaBaja = new Date();
            Miembro.update(vm.miembro,
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();

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

        function confirmDelete (id) {
            Miembro.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();

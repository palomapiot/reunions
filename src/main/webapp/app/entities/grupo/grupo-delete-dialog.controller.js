(function() {
    'use strict';

    angular
        .module('reunionsApp')
        .controller('GrupoDeleteController',GrupoDeleteController);

    GrupoDeleteController.$inject = ['$uibModalInstance', 'entity', 'Grupo'];

    function GrupoDeleteController($uibModalInstance, entity, Grupo) {
        var vm = this;

        vm.grupo = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Grupo.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();

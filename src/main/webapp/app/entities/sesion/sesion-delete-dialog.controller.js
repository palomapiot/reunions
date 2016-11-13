(function() {
    'use strict';

    angular
        .module('reunionsApp')
        .controller('SesionDeleteController',SesionDeleteController);

    SesionDeleteController.$inject = ['$uibModalInstance', 'entity', 'Sesion'];

    function SesionDeleteController($uibModalInstance, entity, Sesion) {
        var vm = this;

        vm.sesion = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Sesion.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();

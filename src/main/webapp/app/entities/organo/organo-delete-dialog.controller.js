(function() {
    'use strict';

    angular
        .module('reunionsApp')
        .controller('OrganoDeleteController',OrganoDeleteController);

    OrganoDeleteController.$inject = ['$uibModalInstance', 'entity', 'Organo'];

    function OrganoDeleteController($uibModalInstance, entity, Organo) {
        var vm = this;

        vm.organo = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Organo.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();

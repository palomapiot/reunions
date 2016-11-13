(function() {
    'use strict';

    angular
        .module('reunionsApp')
        .controller('CargoDialogController', CargoDialogController);

    CargoDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Cargo'];

    function CargoDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Cargo) {
        var vm = this;

        vm.cargo = entity;
        vm.clear = clear;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.cargo.id !== null) {
                Cargo.update(vm.cargo, onSaveSuccess, onSaveError);
            } else {
                Cargo.save(vm.cargo, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('reunionsApp:cargoUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();

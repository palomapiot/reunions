(function() {
    'use strict';

    angular
        .module('reunionsApp')
        .controller('GrupoDialogController', GrupoDialogController);

    GrupoDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Grupo', 'Organo'];

    function GrupoDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Grupo, Organo) {
        var vm = this;

        vm.grupo = entity;
        vm.clear = clear;
        vm.save = save;
        vm.organos = Organo.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.grupo.id !== null) {
                Grupo.update(vm.grupo, onSaveSuccess, onSaveError);
            } else {
                Grupo.save(vm.grupo, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('reunionsApp:grupoUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();

(function() {
    'use strict';

    angular
        .module('reunionsApp')
        .controller('OrganoDialogController', OrganoDialogController);

    OrganoDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Organo', 'Grupo', 'Miembro', 'Sesion'];

    function OrganoDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Organo, Grupo, Miembro, Sesion) {
        var vm = this;

        vm.organo = entity;
        vm.clear = clear;
        vm.save = save;
        vm.grupos = Grupo.query();
        vm.miembros = Miembro.query();
        vm.sesions = Sesion.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.organo.id !== null) {
                Organo.update(vm.organo, onSaveSuccess, onSaveError);
            } else {
                Organo.save(vm.organo, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('reunionsApp:organoUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();

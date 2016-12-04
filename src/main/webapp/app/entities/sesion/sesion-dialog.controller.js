(function() {
    'use strict';

    angular
        .module('reunionsApp')
        .controller('SesionDialogController', SesionDialogController);

    SesionDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Sesion', 'Organo', 'Participante'];

    function SesionDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Sesion, Organo, Participante) {
        var vm = this;

        vm.sesion = entity;

        if (vm.sesion.numero === null) {
            vm.last = false;
            vm.lastSesion = Organo.getLastSesion({id : $stateParams.id}, onLastSesionSuccess);
        }

        function onLastSesionSuccess (result) {
            if (result.id) {
                vm.sesion.numero = result.numero + 1;
            } else {
                vm.sesion.numero = 1;
            }

        }

        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.organos = Organo.query();
        vm.participantes = Participante.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.sesion.id !== null) {
                Sesion.update(vm.sesion, onSaveSuccess, onSaveError);
            } else {
                Sesion.save(vm.sesion, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('reunionsApp:sesionUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.primeraConvocatoria = false;
        vm.datePickerOpenStatus.segundaConvocatoria = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();

(function() {
    'use strict';

    angular
        .module('reunionsApp')
        .controller('ParticipanteDialogController', ParticipanteDialogController);

    ParticipanteDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Participante', 'Sesion', 'Cargo', 'User'];

    function ParticipanteDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Participante, Sesion, Cargo, User) {
        var vm = this;

        vm.participante = entity;
        vm.clear = clear;
        vm.save = save;
        vm.sesions = Sesion.query();
        vm.cargos = Cargo.query();
        vm.users = User.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.participante.id !== null) {
                Participante.update(vm.participante, onSaveSuccess, onSaveError);
            } else {
                Participante.save(vm.participante, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('reunionsApp:participanteUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();

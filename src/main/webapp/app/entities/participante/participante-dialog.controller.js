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

        vm.qFn = function(actual, expected) {
            if (angular.isObject(actual)) return false;
            function removeAccents(value) {
              return value.toString().replace(/á/g, 'a').replace(/é/g, 'e').replace(/í/g, 'i').replace(/ó/g, 'o').replace(/ú/g, 'u').replace(/ñ/g, 'n');
            }
            actual = removeAccents(angular.lowercase('' + actual));
            expected = removeAccents(angular.lowercase('' + expected));

            return actual.indexOf(expected) !== -1;
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

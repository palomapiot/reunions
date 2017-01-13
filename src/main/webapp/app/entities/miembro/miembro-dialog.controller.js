(function() {
    'use strict';

    angular
        .module('reunionsApp')
        .controller('MiembroDialogController', MiembroDialogController);

    MiembroDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Miembro', 'Organo', 'Cargo', 'User'];

    function MiembroDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Miembro, Organo, Cargo, User) {
        var vm = this;

        vm.miembro = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
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
            if (vm.miembro.id !== null) {
                Miembro.update(vm.miembro, onSaveSuccess, onSaveError);
            } else {
                Miembro.save(vm.miembro, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('reunionsApp:miembroUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
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

        vm.datePickerOpenStatus.fechaAlta = false;
        vm.datePickerOpenStatus.fechaBaja = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();

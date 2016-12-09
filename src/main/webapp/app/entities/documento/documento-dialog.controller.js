(function() {
    'use strict';

    angular
        .module('reunionsApp')
        .controller('DocumentoDialogController', DocumentoDialogController);

    DocumentoDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils', 'entity', 'Documento', 'Sesion'];

    function DocumentoDialogController ($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, entity, Documento, Sesion) {
        var vm = this;

        vm.documento = entity;
        vm.clear = clear;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
        vm.save = save;
        vm.sesions = Sesion.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.documento.id !== null) {
                Documento.update(vm.documento, onSaveSuccess, onSaveError);
            } else {
                Documento.save(vm.documento, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('reunionsApp:documentoUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


        vm.setArchivo = function ($file, documento) {
            if ($file) {
                DataUtils.toBase64($file, function(base64Data) {
                    $scope.$apply(function() {
                        documento.archivo = base64Data;
                        documento.archivoContentType = $file.type;
                    });
                });
            }
        };

    }
})();

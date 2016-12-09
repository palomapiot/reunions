(function() {
    'use strict';

    angular
        .module('reunionsApp')
        .controller('DocumentoDetailController', DocumentoDetailController);

    DocumentoDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'DataUtils', 'entity', 'Documento', 'Sesion'];

    function DocumentoDetailController($scope, $rootScope, $stateParams, previousState, DataUtils, entity, Documento, Sesion) {
        var vm = this;

        vm.documento = entity;
        vm.previousState = previousState.name;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;

        var unsubscribe = $rootScope.$on('reunionsApp:documentoUpdate', function(event, result) {
            vm.documento = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();

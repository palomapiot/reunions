(function() {
    'use strict';

    angular
        .module('reunionsApp')
        .controller('DocumentoController', DocumentoController);

    DocumentoController.$inject = ['$scope', '$state', 'DataUtils', 'Documento', 'DocumentoSearch'];

    function DocumentoController ($scope, $state, DataUtils, Documento, DocumentoSearch) {
        var vm = this;
        
        vm.documentos = [];
        vm.openFile = DataUtils.openFile;
        vm.byteSize = DataUtils.byteSize;
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            Documento.query(function(result) {
                vm.documentos = result;
            });
        }

        function search () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            DocumentoSearch.query({query: vm.searchQuery}, function(result) {
                vm.documentos = result;
            });
        }    }
})();

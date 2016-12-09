(function() {
    'use strict';

    angular
        .module('reunionsApp')
        .controller('UserManagementDetailController', UserManagementDetailController);

    UserManagementDetailController.$inject = ['$stateParams', 'previousState', 'DataUtils', 'User'];

    function UserManagementDetailController ($stateParams, previousState, DataUtils, User) {
        var vm = this;

        vm.previousState = previousState.name;
        vm.load = load;
        vm.user = {};
        vm.exportar = exportar;

        vm.load($stateParams.login);
        vm.resumen = User.resumen({ login: $stateParams.login})

        function load (login) {
            User.get({login: login}, function(result) {
                vm.user = result;
            });
        }

        function b64toBlob(b64Data, contentType, sliceSize) {
          contentType = contentType || '';
          sliceSize = sliceSize || 512;

          var byteCharacters = atob(b64Data);
          var byteArrays = [];

          for (var offset = 0; offset < byteCharacters.length; offset += sliceSize) {
            var slice = byteCharacters.slice(offset, offset + sliceSize);

            var byteNumbers = new Array(slice.length);
            for (var i = 0; i < slice.length; i++) {
              byteNumbers[i] = slice.charCodeAt(i);
            }

            var byteArray = new Uint8Array(byteNumbers);

            byteArrays.push(byteArray);
          }

          var blob = new Blob(byteArrays, {type: contentType});
          return blob;
        }

        function exportar () {
            var filename = "test.xlsx"
            var a = document.createElement("a");
            document.body.appendChild(a);
            a.style = "display: none";
            var excel = User.excel({login: vm.user.login});
            excel.$promise.then( function(result) {
                var file = b64toBlob(result.archivo, result.archivoContentType);
                //var blobUrl = URL.createObjectURL(blob);
                var fileURL = window.URL.createObjectURL(file);
                a.href = fileURL;
                a.download = filename;
                a.click();
            });
        }
    }
})();

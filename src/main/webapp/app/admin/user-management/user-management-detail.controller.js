(function() {
    'use strict';

    angular
        .module('reunionsApp')
        .controller('UserManagementDetailController', UserManagementDetailController);

    UserManagementDetailController.$inject = ['$location', '$anchorScroll', '$stateParams', '$previousState', 'DataUtils', 'User'];

    function UserManagementDetailController ($location, $anchorScroll, $stateParams, $previousState, DataUtils, User) {
        var vm = this;

        vm.load = load;
        vm.user = {};
        vm.exportar = exportar;
        vm.back = back;
        vm.goUp = function(id) {
                                  var old = $location.hash();
                                  $location.hash(id);
                                  $anchorScroll();
                                  //reset to old to keep any additional routing logic from kicking in
                                  $location.hash(old);
                                  };
        vm.qFn = function(actual, expected) {
            if (angular.isObject(actual)) return false;
            function removeAccents(value) {
              return value.toString().replace(/á/g, 'a').replace(/é/g, 'e').replace(/í/g, 'i').replace(/ó/g, 'o').replace(/ú/g, 'u').replace(/ñ/g, 'n');
            }
            actual = removeAccents(angular.lowercase('' + actual));
            expected = removeAccents(angular.lowercase('' + expected));

            return actual.indexOf(expected) !== -1;
        }

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

        function back () {
            $previousState.go();
        }
    }
})();

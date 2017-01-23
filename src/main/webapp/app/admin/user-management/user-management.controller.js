(function() {
    'use strict';

    angular
        .module('reunionsApp')
        .controller('UserManagementController', UserManagementController);

    UserManagementController.$inject = ['$location', '$anchorScroll', 'Principal', 'User', 'ParseLinks', 'AlertService', '$state', 'JhiLanguageService'];

    function UserManagementController($location, $anchorScroll, Principal, User, ParseLinks, AlertService, $state, JhiLanguageService) {
        var vm = this;

        vm.authorities = ['ROLE_USER', 'ROLE_ADMIN'];
        vm.currentAccount = null;
        vm.loadAll = loadAll;
        vm.users = [];
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

        vm.loadAll();

//        JhiLanguageService.getAll().then(function (languages) {
//            vm.languages = languages;
//        });
        Principal.identity().then(function(account) {
            vm.currentAccount = account;
        });

//        function setActive (user, isActivated) {
//            user.activated = isActivated;
//            User.update(user, function () {
//                vm.loadAll();
//                vm.clear();
//            });
//        }

        function loadAll () {
            User.getAll({ }, onSuccess, onError);
        }

        function onSuccess(data, headers) {
            //hide anonymous user from user management: it's a required user for Spring Security
            for (var i in data) {
                if (data[i]['login'] === 'anonymoususer') {
                    data.splice(i, 1);
                }
            }
            vm.users = data;
        }

        function onError(error) {
            AlertService.error(error.data.message);
        }

//        function clear () {
//            vm.user = {
//                id: null, login: null, firstName: null, lastName: null, email: null,
//                activated: null, langKey: null, createdBy: null, createdDate: null,
//                lastModifiedBy: null, lastModifiedDate: null, resetDate: null,
//                resetKey: null, authorities: null
//            };
//        }
//
//        function loadPage (page) {
//            vm.page = page;
//            vm.transition();
//        }
    }
})();

(function () {
    'use strict';

    angular
        .module('reunionsApp')
        .factory('User', User);

    User.$inject = ['$resource'];

    function User ($resource) {
        var service = $resource('api/users/:login', {}, {
            'query': {method: 'GET', isArray: true},
            'getAll': {method: 'GET', isArray: true, url:'api/users'},
            'getEvents': {method: 'GET', isArray: true, url:'api/users/events'},
            'getOthersEvents': {method: 'GET', isArray: true, url:'api/users/othersEvents'},
            'resumen': {method: 'GET', isArray: true, url: 'api/users/:login/resumen'},
            'excel': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }, url: 'api/users/:login/excel'
            },
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'save': { method:'POST' },
            'update': { method:'PUT' },
            'delete':{ method:'DELETE'}
        });

        return service;
    }
})();

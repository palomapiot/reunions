(function() {
    'use strict';
    angular
        .module('reunionsApp')
        .factory('Grupo', Grupo);

    Grupo.$inject = ['$resource'];

    function Grupo ($resource) {
        var resourceUrl =  'api/grupos/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();

(function() {
    'use strict';
    angular
        .module('reunionsApp')
        .factory('Sesion', Sesion);

    Sesion.$inject = ['$resource', 'DateUtils'];

    function Sesion ($resource, DateUtils) {
        var resourceUrl =  'api/sesions/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.primeraConvocatoria = DateUtils.convertDateTimeFromServer(data.primeraConvocatoria);
                        data.segundaConvocatoria = DateUtils.convertDateTimeFromServer(data.segundaConvocatoria);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();

(function() {
    'use strict';
    angular
        .module('reunionsApp')
        .factory('Organo', Organo);

    Organo.$inject = ['$resource', 'DateUtils'];

    function Organo ($resource, DateUtils) {
        var resourceUrl =  'api/organos/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'miembros': { method: 'GET', isArray: true, url: 'api/organos/:id/miembros'},
            'miembrosAnteriores': { method: 'GET', isArray: true, url:'api/organos/:id/miembrosAnteriores'},
            'sesiones': { method: 'GET', isArray: true, url: 'api/organos/:id/sesiones'},
            'getLastSesion': {
                method: 'GET',
                url: 'api/organos/:id/lastSesion',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.primeraConvocatoria = DateUtils.convertDateTimeFromServer(data.primeraConvocatoria);
                        data.segundaConvocatoria = DateUtils.convertDateTimeFromServer(data.segundaConvocatoria);
                    }
                    return data;
                }
            },
            'getAll': { method: 'GET', isArray: true, url:'api/organos'},
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

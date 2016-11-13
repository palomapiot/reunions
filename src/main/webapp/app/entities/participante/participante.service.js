(function() {
    'use strict';
    angular
        .module('reunionsApp')
        .factory('Participante', Participante);

    Participante.$inject = ['$resource'];

    function Participante ($resource) {
        var resourceUrl =  'api/participantes/:id';

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

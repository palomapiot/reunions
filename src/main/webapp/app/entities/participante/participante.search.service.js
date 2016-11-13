(function() {
    'use strict';

    angular
        .module('reunionsApp')
        .factory('ParticipanteSearch', ParticipanteSearch);

    ParticipanteSearch.$inject = ['$resource'];

    function ParticipanteSearch($resource) {
        var resourceUrl =  'api/_search/participantes/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();

(function() {
    'use strict';

    angular
        .module('reunionsApp')
        .factory('GrupoSearch', GrupoSearch);

    GrupoSearch.$inject = ['$resource'];

    function GrupoSearch($resource) {
        var resourceUrl =  'api/_search/grupos/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();

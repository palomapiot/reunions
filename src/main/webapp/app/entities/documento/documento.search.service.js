(function() {
    'use strict';

    angular
        .module('reunionsApp')
        .factory('DocumentoSearch', DocumentoSearch);

    DocumentoSearch.$inject = ['$resource'];

    function DocumentoSearch($resource) {
        var resourceUrl =  'api/_search/documentos/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();

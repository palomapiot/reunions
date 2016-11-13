(function() {
    'use strict';

    angular
        .module('reunionsApp')
        .factory('SesionSearch', SesionSearch);

    SesionSearch.$inject = ['$resource'];

    function SesionSearch($resource) {
        var resourceUrl =  'api/_search/sesions/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();

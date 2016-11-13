(function() {
    'use strict';

    angular
        .module('reunionsApp')
        .factory('MiembroSearch', MiembroSearch);

    MiembroSearch.$inject = ['$resource'];

    function MiembroSearch($resource) {
        var resourceUrl =  'api/_search/miembros/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();

(function() {
    'use strict';

    angular
        .module('reunionsApp')
        .factory('OrganoSearch', OrganoSearch);

    OrganoSearch.$inject = ['$resource'];

    function OrganoSearch($resource) {
        var resourceUrl =  'api/_search/organos/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();

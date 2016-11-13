(function() {
    'use strict';

    angular
        .module('reunionsApp')
        .factory('CargoSearch', CargoSearch);

    CargoSearch.$inject = ['$resource'];

    function CargoSearch($resource) {
        var resourceUrl =  'api/_search/cargos/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();

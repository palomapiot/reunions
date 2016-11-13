(function() {
    'use strict';
    angular
        .module('reunionsApp')
        .factory('Miembro', Miembro);

    Miembro.$inject = ['$resource', 'DateUtils'];

    function Miembro ($resource, DateUtils) {
        var resourceUrl =  'api/miembros/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.fechaAlta = DateUtils.convertLocalDateFromServer(data.fechaAlta);
                        data.fechaBaja = DateUtils.convertLocalDateFromServer(data.fechaBaja);
                    }
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.fechaAlta = DateUtils.convertLocalDateToServer(copy.fechaAlta);
                    copy.fechaBaja = DateUtils.convertLocalDateToServer(copy.fechaBaja);
                    return angular.toJson(copy);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.fechaAlta = DateUtils.convertLocalDateToServer(copy.fechaAlta);
                    copy.fechaBaja = DateUtils.convertLocalDateToServer(copy.fechaBaja);
                    return angular.toJson(copy);
                }
            }
        });
    }
})();

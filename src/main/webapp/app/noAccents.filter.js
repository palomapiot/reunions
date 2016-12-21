(function() {
    'use strict';

    angular
        .module('reunionsApp')
        .filter('noAccents', noAccents);

    function noAccents() {
        return function(data) {
            return data.toLowerCase()
                     .replace(/á/g, 'a')
                     .replace(/â/g, 'a')
                     .replace(/é/g, 'e')
                     .replace(/è/g, 'e')
                     .replace(/ê/g, 'e')
                     .replace(/í/g, 'i')
                     .replace(/ï/g, 'i')
                     .replace(/ì/g, 'i')
                     .replace(/ó/g, 'o')
                     .replace(/ô/g, 'o')
                     .replace(/ú/g, 'u')
                     .replace(/ü/g, 'u')
                     .replace(/ç/g, 'c')
                     .replace(/ß/g, 's');
        };
    }
})();

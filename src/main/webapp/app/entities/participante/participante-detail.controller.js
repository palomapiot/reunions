(function() {
    'use strict';

    angular
        .module('reunionsApp')
        .controller('ParticipanteDetailController', ParticipanteDetailController);

    ParticipanteDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Participante', 'Sesion', 'Cargo', 'User'];

    function ParticipanteDetailController($scope, $rootScope, $stateParams, previousState, entity, Participante, Sesion, Cargo, User) {
        var vm = this;

        vm.participante = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('reunionsApp:participanteUpdate', function(event, result) {
            vm.participante = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();

(function() {
    'use strict';

    angular
        .module('reunionsApp')
        .controller('SesionDetailController', SesionDetailController);

    SesionDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Sesion', 'Organo', 'Participante'];

    function SesionDetailController($scope, $rootScope, $stateParams, previousState, entity, Sesion, Organo, Participante) {
        var vm = this;

        vm.sesion = entity;
        vm.notificar = notificar;
        vm.exportar = exportar;
        vm.previousState = previousState.name;

        vm.participantes = Sesion.participantes({ id: $stateParams.id });

        function exportar () {
            var i;
            var cabecera = "Relación de asistentes a la sesión número " + vm.sesion.numero + " del órgano " + vm.sesion.organo.nombre;
            var asistentes = "\n\nAsiste:";
            var disculpas = "\n\nDisculpa:";
            var faltas = "\n\nFalta:";
            for (i in vm.participantes) {
                var p = vm.participantes[i];
                if (p.asistencia == "asiste") {
                    asistentes += "\n\t" + p.user.lastName + ", " + p.user.firstName;
                } else if (p.asistencia == "disculpa") {
                    disculpas += "\n\t" + p.user.lastName + ", " + p.user.firstName;
                } else if (p.asistencia == "falta") {
                    faltas += "\n\t" + p.user.lastName + ", " + p.user.firstName;
                }
            }
            var a = document.body.appendChild(
                    document.createElement("a")
                );
            a.download = "participacion.txt";
            a.href = "data:text/plain;base64," + btoa(cabecera + asistentes + disculpas + faltas);
            a.click()
        }

        function notificar () {
            Sesion.notificar(vm.sesion);
        }

        var unsubscribe = $rootScope.$on('reunionsApp:sesionUpdate', function(event, result) {
            vm.sesion = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();

(function() {
    'use strict';

    angular
        .module('reunionsApp')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['$scope', '$compile', 'Principal', 'LoginService', '$state', 'User'];

    function HomeController ($scope, $compile, Principal, LoginService, $state, User) {
        var vm = this;

        vm.account = null;
        vm.isAuthenticated = null;
        vm.login = LoginService.open;
        vm.register = register;
        vm.eventClick = eventClick;
        vm.events = [];
        vm.eventRender = function( event, element, view ) {
                element.attr({'uib-tooltip': event.title,
                             'uib-tooltip-append-to-body': true});
                $compile(element)($scope);
            };
        vm.calendarConfig = {
            calendar: {
                allDaySlot: false,
                //timezone: 'local',
                editable: false,
                //locale: 'es',
                firstDay: 1,
                header: {
                    left:   'title',
                    center: '',
                    right:  'today prev,next'
                },
                buttonText: {
                    today: moment().format("DD/MM/YY")
                },
                timeFormat: 'HH:mm',
                titleFormat: '[Sesiones de ] MMMM YYYY',
                eventClick: vm.eventClick,
                eventResizeStop: $scope.alertResize,
                eventDragStop: $scope.alertDrag,
                eventRender: vm.eventRender,
                dayClick: $scope.dayClick
            }
        };



        function eventClick (event, jsEvent, view) {
            $state.go("sesion-detail", {id: event.id});
        }

         vm.myevents = function(start, end, timezone, callback) {


            User.getEvents($scope, function(response) {

                  //  $rootScope.myobject = data;


                    vm.events = [];


                    angular.forEach(response,function(event){
                        console.log(event.primeraConvocatoria);
                        console.log(new Date(Date.parse(event.primeraConvocatoria)));
                        vm.events.push({
                            id: event.id,
                            title: event.organo.nombre,
                            start: new Date(Date.parse(event.primeraConvocatoria)),
                            color: event.numero == 1 ? '#c7007d' : 'grey'
                        });
                    });
                    callback(vm.events);
            });

//            User.getOthersEvents($scope, function(response2) {
//
//                angular.forEach(response2,function(event2){
//                    console.log(event2.primeraConvocatoria);
//                    console.log(new Date(Date.parse(event2.primeraConvocatoria)));
//                    vm.events.push({
//                        id: 2,//event2.id,
//                        title: "bla",//event2.organo.nombre,
//                        start: new Date(Date.parse(event2.segundaConvocatoria))
//                    });
//                });
//                callback(vm.events);
//            });

        }


        vm.eventSources = [vm.events,vm.myevents];

        $scope.$on('authenticationSuccess', function() {
            getAccount();
        });

        getAccount();

        function getAccount() {
            Principal.identity().then(function(account) {
                vm.account = account;
                vm.isAuthenticated = Principal.isAuthenticated;
            });
        }
        function register () {
            $state.go('register');
        }
    }
})();

'use strict';

describe('Controller Tests', function() {

    describe('Participante Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockParticipante, MockSesion, MockCargo, MockUser;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockParticipante = jasmine.createSpy('MockParticipante');
            MockSesion = jasmine.createSpy('MockSesion');
            MockCargo = jasmine.createSpy('MockCargo');
            MockUser = jasmine.createSpy('MockUser');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'Participante': MockParticipante,
                'Sesion': MockSesion,
                'Cargo': MockCargo,
                'User': MockUser
            };
            createController = function() {
                $injector.get('$controller')("ParticipanteDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'reunionsApp:participanteUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});

'use strict';

describe('Controller Tests', function() {

    describe('Sesion Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockSesion, MockOrgano, MockParticipante;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockSesion = jasmine.createSpy('MockSesion');
            MockOrgano = jasmine.createSpy('MockOrgano');
            MockParticipante = jasmine.createSpy('MockParticipante');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'Sesion': MockSesion,
                'Organo': MockOrgano,
                'Participante': MockParticipante
            };
            createController = function() {
                $injector.get('$controller')("SesionDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'reunionsApp:sesionUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});

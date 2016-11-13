'use strict';

describe('Controller Tests', function() {

    describe('Organo Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockOrgano, MockGrupo, MockMiembro, MockSesion;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockOrgano = jasmine.createSpy('MockOrgano');
            MockGrupo = jasmine.createSpy('MockGrupo');
            MockMiembro = jasmine.createSpy('MockMiembro');
            MockSesion = jasmine.createSpy('MockSesion');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'Organo': MockOrgano,
                'Grupo': MockGrupo,
                'Miembro': MockMiembro,
                'Sesion': MockSesion
            };
            createController = function() {
                $injector.get('$controller')("OrganoDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'reunionsApp:organoUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});

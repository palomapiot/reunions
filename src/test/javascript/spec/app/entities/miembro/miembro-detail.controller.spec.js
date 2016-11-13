'use strict';

describe('Controller Tests', function() {

    describe('Miembro Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockMiembro, MockOrgano, MockCargo, MockUser;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockMiembro = jasmine.createSpy('MockMiembro');
            MockOrgano = jasmine.createSpy('MockOrgano');
            MockCargo = jasmine.createSpy('MockCargo');
            MockUser = jasmine.createSpy('MockUser');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'Miembro': MockMiembro,
                'Organo': MockOrgano,
                'Cargo': MockCargo,
                'User': MockUser
            };
            createController = function() {
                $injector.get('$controller')("MiembroDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'reunionsApp:miembroUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});

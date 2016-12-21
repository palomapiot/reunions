(function() {
    'use strict';

    angular
        .module('reunionsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('cargo', {
            parent: 'entity',
            url: '/cargo',
            data: {
                authorities: ['ROLE_ADMIN'],
                pageTitle: 'reunionsApp.cargo.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/cargo/cargos.html',
                    controller: 'CargoController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('cargo');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('cargo-detail', {
            parent: 'entity',
            url: '/cargo/{id}',
            data: {
                authorities: ['ROLE_ADMIN'],
                pageTitle: 'reunionsApp.cargo.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/cargo/cargo-detail.html',
                    controller: 'CargoDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('cargo');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Cargo', function($stateParams, Cargo) {
                    return Cargo.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'cargo',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('cargo-detail.edit', {
            parent: 'cargo-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/cargo/cargo-dialog.html',
                    controller: 'CargoDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Cargo', function(Cargo) {
                            return Cargo.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('cargo.new', {
            parent: 'cargo',
            url: '/new',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/cargo/cargo-dialog.html',
                    controller: 'CargoDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                nombre: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('cargo', null, { reload: 'cargo' });
                }, function() {
                    $state.go('cargo');
                });
            }]
        })
        .state('cargo.edit', {
            parent: 'cargo',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/cargo/cargo-dialog.html',
                    controller: 'CargoDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Cargo', function(Cargo) {
                            return Cargo.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('cargo', null, { reload: 'cargo' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('cargo.delete', {
            parent: 'cargo',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/cargo/cargo-delete-dialog.html',
                    controller: 'CargoDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Cargo', function(Cargo) {
                            return Cargo.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('cargo', null, { reload: 'cargo' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();

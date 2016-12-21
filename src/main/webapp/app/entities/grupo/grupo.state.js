(function() {
    'use strict';

    angular
        .module('reunionsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('grupo', {
            parent: 'entity',
            url: '/grupo',
            data: {
                authorities: ['ROLE_ADMIN'],
                pageTitle: 'reunionsApp.grupo.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/grupo/grupos.html',
                    controller: 'GrupoController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('grupo');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('grupo-detail', {
            parent: 'entity',
            url: '/grupo/{id}',
            data: {
                authorities: ['ROLE_ADMIN'],
                pageTitle: 'reunionsApp.grupo.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/grupo/grupo-detail.html',
                    controller: 'GrupoDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('grupo');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Grupo', function($stateParams, Grupo) {
                    return Grupo.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'grupo',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('grupo-detail.edit', {
            parent: 'grupo-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/grupo/grupo-dialog.html',
                    controller: 'GrupoDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Grupo', function(Grupo) {
                            return Grupo.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('grupo.new', {
            parent: 'grupo',
            url: '/new',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/grupo/grupo-dialog.html',
                    controller: 'GrupoDialogController',
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
                    $state.go('grupo', null, { reload: 'grupo' });
                }, function() {
                    $state.go('grupo');
                });
            }]
        })
        .state('grupo.edit', {
            parent: 'grupo',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/grupo/grupo-dialog.html',
                    controller: 'GrupoDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Grupo', function(Grupo) {
                            return Grupo.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('grupo', null, { reload: 'grupo' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('grupo.delete', {
            parent: 'grupo',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/grupo/grupo-delete-dialog.html',
                    controller: 'GrupoDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Grupo', function(Grupo) {
                            return Grupo.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('grupo', null, { reload: 'grupo' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();

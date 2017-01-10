(function() {
    'use strict';

    angular
        .module('reunionsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('sesion', {
            parent: 'entity',
            url: '/sesion?page&sort&search',
            data: {
                authorities: ['ROLE_ADMIN'],
                pageTitle: 'reunionsApp.sesion.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/sesion/sesions.html',
                    controller: 'SesionController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('sesion');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('sesion-detail', {
            parent: 'organo',
            url: '/sesion/{id}',
            data: {
                authorities: [],
                pageTitle: 'reunionsApp.sesion.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/sesion/sesion-detail.html',
                    controller: 'SesionDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('sesion');
                    $translatePartialLoader.addPart('participante');
                    $translatePartialLoader.addPart('asistencia');
                    $translatePartialLoader.addPart('documento');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Sesion', function($stateParams, Sesion) {
                    return Sesion.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'sesion',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('sesion-detail.edit', {
            parent: 'sesion-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/sesion/sesion-dialog.html',
                    controller: 'SesionDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Sesion', function(Sesion) {
                            return Sesion.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('sesion.new', {
            parent: 'organo-detail',
            url: '/nuevaSesion',
            data: {
                authorities: ['ROLE_ADMIN', 'ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/sesion/sesion-dialog.html',
                    controller: 'SesionDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Organo', function (Organo) {
                            return {
                                numero: null,
                                primeraConvocatoria: null,
                                segundaConvocatoria: null,
                                lugar: null,
                                descripcion: null,
                                id: null,
                                organo: Organo.get({id : $stateParams.id})
                            };
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: 'organo-detail' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('sesion.edit', {
            parent: 'organo-detail',
            url: '/{ids}/editarSesion',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/sesion/sesion-dialog.html',
                    controller: 'SesionDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Sesion', function(Sesion) {
                            return Sesion.get({id : $stateParams.ids}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: 'organo-detail' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('sesion.delete', {
            parent: 'organo-detail',
            url: '/{ids}/eliminarSesion',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/sesion/sesion-delete-dialog.html',
                    controller: 'SesionDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Sesion', function(Sesion) {
                            return Sesion.get({id : $stateParams.ids}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: 'organo-detail' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();

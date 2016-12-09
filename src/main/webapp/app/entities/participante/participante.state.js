(function() {
    'use strict';

    angular
        .module('reunionsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('participante', {
            parent: 'entity',
            url: '/participante?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'reunionsApp.participante.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/participante/participantes.html',
                    controller: 'ParticipanteController',
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
                    $translatePartialLoader.addPart('participante');
                    $translatePartialLoader.addPart('asistencia');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('participante-detail', {
            parent: 'entity',
            url: '/participante/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'reunionsApp.participante.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/participante/participante-detail.html',
                    controller: 'ParticipanteDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('participante');
                    $translatePartialLoader.addPart('asistencia');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Participante', function($stateParams, Participante) {
                    return Participante.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'participante',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('participante-detail.edit', {
            parent: 'participante-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/participante/participante-dialog.html',
                    controller: 'ParticipanteDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Participante', function(Participante) {
                            return Participante.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('participante.new', {
            parent: 'sesion-detail',
            url: '/nuevoParticipante',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/participante/participante-dialog.html',
                    controller: 'ParticipanteDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Sesion', function (Sesion) {
                            return {
                                asistencia: null,
                                observaciones: null,
                                id: null,
                                sesion: Sesion.get({id : $stateParams.id})
                            };
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: 'sesion-detail' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('participante.edit', {
            parent: 'sesion-detail',
            url: '/{idp}/editarParticipante',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/participante/participante-dialog.html',
                    controller: 'ParticipanteDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Participante', function(Participante) {
                            return Participante.get({id : $stateParams.idp}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: 'sesion-detail' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('participante.delete', {
            parent: 'sesion-detail',
            url: '/{idp}/eliminarParticipante',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/participante/participante-delete-dialog.html',
                    controller: 'ParticipanteDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Participante', function(Participante) {
                            return Participante.get({id : $stateParams.idp}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: 'sesion-detail' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();

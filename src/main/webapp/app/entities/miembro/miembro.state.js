(function() {
    'use strict';

    angular
        .module('reunionsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('miembro', {
            parent: 'entity',
            url: '/miembro?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'reunionsApp.miembro.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/miembro/miembros.html',
                    controller: 'MiembroController',
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
                    $translatePartialLoader.addPart('miembro');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('miembro-detail', {
            parent: 'entity',
            url: '/miembro/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'reunionsApp.miembro.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/miembro/miembro-detail.html',
                    controller: 'MiembroDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('miembro');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Miembro', function($stateParams, Miembro) {
                    return Miembro.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'miembro',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('miembro-detail.edit', {
            parent: 'miembro-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/miembro/miembro-dialog.html',
                    controller: 'MiembroDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Miembro', function(Miembro) {
                            return Miembro.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('miembro.new', {
            parent: 'miembro',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/miembro/miembro-dialog.html',
                    controller: 'MiembroDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                fechaAlta: null,
                                fechaBaja: null,
                                observaciones: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('miembro', null, { reload: 'miembro' });
                }, function() {
                    $state.go('miembro');
                });
            }]
        })
        .state('miembro.edit', {
            parent: 'miembro',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/miembro/miembro-dialog.html',
                    controller: 'MiembroDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Miembro', function(Miembro) {
                            return Miembro.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('miembro', null, { reload: 'miembro' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('miembro.delete', {
            parent: 'miembro',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/miembro/miembro-delete-dialog.html',
                    controller: 'MiembroDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Miembro', function(Miembro) {
                            return Miembro.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('miembro', null, { reload: 'miembro' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();

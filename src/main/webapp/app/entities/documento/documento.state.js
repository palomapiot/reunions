(function() {
    'use strict';

    angular
        .module('reunionsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('documento', {
            parent: 'entity',
            url: '/documento',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'reunionsApp.documento.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/documento/documentos.html',
                    controller: 'DocumentoController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('documento');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('documento-detail', {
            parent: 'entity',
            url: '/documento/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'reunionsApp.documento.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/documento/documento-detail.html',
                    controller: 'DocumentoDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('documento');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Documento', function($stateParams, Documento) {
                    return Documento.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'documento',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('documento-detail.edit', {
            parent: 'documento-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/documento/documento-dialog.html',
                    controller: 'DocumentoDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Documento', function(Documento) {
                            return Documento.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('documento.new', {
            parent: 'sesion-detail',
            url: '/nuevoDocumento',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/documento/documento-dialog.html',
                    controller: 'DocumentoDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Sesion', function (Sesion) {
                            return {
                                nombre: null,
                                archivo: null,
                                archivoContentType: null,
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
        .state('documento.edit', {
            parent: 'documento',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/documento/documento-dialog.html',
                    controller: 'DocumentoDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Documento', function(Documento) {
                            return Documento.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('documento', null, { reload: 'documento' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('documento.delete', {
            parent: 'sesion-detail',
            url: '/{idd}/eliminarDocumento',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/documento/documento-delete-dialog.html',
                    controller: 'DocumentoDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Documento', function(Documento) {
                            return Documento.get({id : $stateParams.idd}).$promise;
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

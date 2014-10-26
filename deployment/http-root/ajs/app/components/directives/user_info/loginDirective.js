(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function loginCtrl($scope, LoginService ,$window) {
        $scope.data = {};
        $scope.errors = [];

        $scope.removeAlert = function(alert,index) {
            $scope.errors.splice(index, 1);
        };

        $scope.signin = function() {
            LoginService.login($scope.data.username, $scope.data.password).success(function() {
                $scope.$close();
                $window.location.reload();
            }).error(function(data,status) {
                if (status === 404) {
                    $scope.errors.push({ msg : 'Could not reach server', id : new Date(), close :  $scope.removeAlert });
                } else {
                    $scope.errors.push({ msg: 'Wrong username or password, please try again', id: new Date() ,close: $scope.removeAlert });
                }
            });
        };

        $scope.register = function() {
            LoginService.register($scope.data.username, $scope.data.password).success(function() {
                $scope.signin();
            }).error(function(data,status) {
                if (status === 409) {
                    $scope.errors.push({ msg : 'User already exists, please select a different username', id : new Date(), close: $scope.removeAlert  });
                } else {
                    $scope.errors.push({ msg: 'Error registering user', id: new Date() , close: $scope.removeAlert  });
                }
            });
        };

        $scope.cancel = function() {
            $scope.$dismiss();
        };

    }

    function codeineLogin($modal) {
        return {
            restrict: 'A',
            scope: true,
            link: function ($scope, element) {

                $scope.click = function() {
                    var modalInstance = $modal.open({
                        templateUrl: '/components/directives/user_info/login.html',
                        scope: $scope,
                        controller: loginCtrl
                    });

                    modalInstance.result.then(function () {
                    }, function () {
                    });
                };

                element.bind('click', $scope.click);
            }
        };
    }


    //// Angular Code ////
    angular.module('codeine').directive('codeineLogin', codeineLogin);

})(angular);
angular.module('codeine').controller('tagsFilterCtrl',['$scope','$rootScope', '$log','Constants','$location',
    function($scope,$rootScope,$log,Constants,$location) {
        $log.debug('tagsFilterCtrl: created');
        $scope.maxTags = 10;

        $scope.updateTags = function() {
            $log.debug('projectStatusCtrl: tags were changed');
            var on = [], off = [];
            for (var i=0; i < $scope.projectStatus.tag_info.length ; i++) {
                if ($scope.projectStatus.tag_info[i].state === 1) {
                    on.push($scope.projectStatus.tag_info[i].name);
                } else if ($scope.projectStatus.tag_info[i].state === 2) {
                    off.push($scope.projectStatus.tag_info[i].name);
                }
            }
            $scope.$apply(function() {
                $location.search('tagsOn',on.join(','));
                $location.search('tagsOff',off.join(','));
            });
            $rootScope.$emit(Constants.EVENTS.TAGS_CHANGED);
        };

    }]);
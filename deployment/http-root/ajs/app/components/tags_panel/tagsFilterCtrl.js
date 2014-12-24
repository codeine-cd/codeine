(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function tagsFilterCtrl($scope,$rootScope,Constants,$location) {
        /*jshint validthis:true */
        var vm = this;
        vm.maxTags = 10;

        function setTagState(name,stateVal) {
            for (var f=0; f < $scope.projectStatus.tag_info.length ; f++) {
                if ($scope.projectStatus.tag_info[f].name === name) {
                    $scope.projectStatus.tag_info[f].state = stateVal;
                }
            }
        }

        vm.initTagsFromQueryString = function() {
            var queryStringObject = $location.search();
            var shouldRefresh = false;
            for (var j=0; j < $scope.projectStatus.tag_info.length ; j++) {
                $scope.projectStatus.tag_info[j].state = 0;
            }
            if (angular.isDefined(queryStringObject.tagsOn)) {
                shouldRefresh = true;
                var array = queryStringObject.tagsOn.split(',');
                for (var i=0; i < array.length; i++) {
                    setTagState(array[i],1);
                }
            }
            if (angular.isDefined(queryStringObject.tagsOff)) {
                shouldRefresh = true;
                var array2 = queryStringObject.tagsOff.split(',');
                for (var i1=0; i1 < array2.length; i1++) {
                    setTagState(array2[i1],2);
                }
            }
            return shouldRefresh;
        };

        vm.initTagsFromQueryString();

        vm.updateTags = function() {
            var on = [], off = [];
            for (var i=0; i < $scope.projectStatus.tag_info.length ; i++) {
                if ($scope.projectStatus.tag_info[i].state === 1) {
                    on.push($scope.projectStatus.tag_info[i].name);
                } else if ($scope.projectStatus.tag_info[i].state === 2) {
                    off.push($scope.projectStatus.tag_info[i].name);
                }
            }
            $location.search('tagsOn',on.join(','));
            $location.search('tagsOff',off.join(','));
            $rootScope.$emit(Constants.EVENTS.TAGS_CHANGED);
        };
    }


    //// Angular Code ////
    angular.module('codeine').controller('tagsFilterCtrl', tagsFilterCtrl);

})(angular);
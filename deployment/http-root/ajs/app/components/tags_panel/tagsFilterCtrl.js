(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function tagsFilterCtrl($scope, $rootScope, Constants, $location, $log) {
        /*jshint validthis:true */
        var vm = this;
        vm.maxTags = 10;
        vm.filterMode = $location.search().filterMode || '||';

        var unbind = $scope.$watch('vm.filterMode', function(newVal, oldVal) {
            if (newVal !== oldVal) {
                $log.debug('vm.filterMode: ' + vm.filterMode);
                $location.search('filterMode', newVal);
                $rootScope.$emit(Constants.EVENTS.TAGS_CHANGED);
            }
        });

        function setTagState(name,stateVal) {
            for (var f=0; f < $scope.projectStatus.tag_info.length ; f++) {
                if ($scope.projectStatus.tag_info[f].immutable.name === name) {
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
            return shouldRefresh;
        };

        vm.initTagsFromQueryString();

        vm.updateTags = function() {
            var on = [];
            for (var i=0; i < $scope.projectStatus.tag_info.length ; i++) {
                if ($scope.projectStatus.tag_info[i].state === 1) {
                    on.push($scope.projectStatus.tag_info[i].immutable.name);
                }
            }
            if (on.length > 0) {
                $location.search('tagsOn', on.join(','));
            } else {
                $location.search('tagsOn', undefined);
            }
            $rootScope.$emit(Constants.EVENTS.TAGS_CHANGED);
        };

        vm.hasSelectedTags = function() {
            for (var i=0; i < $scope.projectStatus.tag_info.length ; i++) {
                if ($scope.projectStatus.tag_info[i].state === 1) {
                    return true;
                }
            }
           return false;
        };
        vm.clearSelectedTags = function() {
            for (var i=0; i < $scope.projectStatus.tag_info.length ; i++) {
                $scope.projectStatus.tag_info[i].state = 0;
            }
            vm.updateTags();
        };

        $scope.$on('$destroy', unbind);
    }


    //// Angular Code ////
    angular.module('codeine').controller('tagsFilterCtrl', tagsFilterCtrl);

})(angular);
(function (angular) {
    'use strict';
    //// JavaScript Code ////
    function ProjectsTabFactory() {
        function ProjectsTab(tabData) {
            if (tabData) {
                this.setData(tabData);
            }
        }

        ProjectsTab.prototype = {
            setData : function(data) {
                angular.extend(this,data);
            }
        };

        return ProjectsTab;
    }

    //// Angular Code ////
    angular.module('codeine').factory('ProjectsTab', ProjectsTabFactory);
})(angular);

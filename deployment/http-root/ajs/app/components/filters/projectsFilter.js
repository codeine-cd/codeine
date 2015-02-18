(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function projectsFilter() {
        return function(projects, selectedTab) {
            var res = [];
            for(var i=0; i < projects.length; i++) {
                for(var j=0; j < selectedTab.tab.exp.length; j++) {
                    var patt = new RegExp("^" + selectedTab.tab.exp[j] + "$");
                    if (patt.test(projects[i].name)) {
                        res.push(projects[i]);
                        break;
                    }
                }
            }
            return res;
        };
    }

    //// Angular Code ////
    angular.module('codeine').filter('projectsFilter', projectsFilter);

})(angular);
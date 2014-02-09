angular.module('codeine').filter('projectsFilter', [ '$log', function($log) {
    return function(projects, selectedTab) {
        var res = [];
        for(var i=0; i < projects.length; i++) {
            for(var j=0; j < selectedTab.exp.length; j++) {
                var patt=new RegExp(selectedTab.exp[j],"g");
                if (patt.test(projects[i].name)) {
                    res.push(projects[i]);
                }
            }
        }
        return res;
    }
}]);
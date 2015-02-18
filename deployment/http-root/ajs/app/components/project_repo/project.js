(function (angular) {
    'use strict';
    //// JavaScript Code ////
    function CodeineProjectFactory() {
        function CodeineProject(name,nodes_count) {
            this.name = name;
            this.nodes_count = nodes_count;
        }

        CodeineProject.prototype = {
            setData : function(data) {

            }
        }

        return CodeineProject;
    }

    //// Angular Code ////
    angular.module('codeine').factory('CodeineProject', CodeineProjectFactory);
})(angular);

(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function SelectedNodesServiceFactory ($log,$localStorage) {

        $localStorage.$default({
            nodes : [],
            url : '',
            params : []
        });

        var Api = {
            setSelectedNodes : function(nodes,url, params) {
                $log.debug('selectedNodesService: Storing nodes for url \'' + url + '\' : ' + nodes.length);
                $localStorage.nodes = nodes;
                $localStorage.url = url;
                if (params === undefined)
                {
                    params = [];
                }
                $localStorage.params = params;


            },

            getSelectedNodes : function(url) {
                $log.debug('selectedNodesService: retrieve nodes for url \'' + url + '\'');
                if ($localStorage.url === url) {
                    return $localStorage.nodes;
                }
                $log.error('selectedNodesService: given url \'' + url + '\' is not like the stored one \'' + $localStorage.url + '\'');
            },
            getSelectedParams : function(url) {
                $log.debug('selectedNodesService: retrieve nodes for url \'' + url + '\'');
                if ($localStorage.url === url) {
                    return $localStorage.params;
                }
                $log.error('selectedNodesService: given url \'' + url + '\' is not like the stored one \'' + $localStorage.url + '\'');
            }
        };

        return Api;
    }

    //// Angular Code ////
    angular.module('codeine').factory('SelectedNodesService', SelectedNodesServiceFactory);

})(angular);
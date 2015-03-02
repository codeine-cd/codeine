(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function SelectedNodesServiceFactory ($log) {

        var data = {
            nodes : [],
            url : '',
            params : []
        };

        var Api = {
            setSelectedNodes : function(nodes,url, params) {
                $log.debug('selectedNodesService: Storing nodes for url \'' + url + '\' : ' + nodes.length);
                data.nodes = nodes;
                data.url = url;
                if (params === undefined)
                {
                    params = [];
                }
                data.params = params;


            },

            getSelectedNodes : function(url) {
                $log.debug('selectedNodesService: retrieve nodes for url \'' + url + '\'');
                if (data.url === url) {
                    return data.nodes;
                }
                $log.error('selectedNodesService: given url \'' + url + '\' is not like the stored one \'' + data.url + '\'');
            },
            getSelectedParams : function(url) {
                $log.debug('selectedNodesService: retrieve nodes for url \'' + url + '\'');
                if (data.url === url) {
                    return data.params;
                }
                $log.error('selectedNodesService: given url \'' + url + '\' is not like the stored one \'' + data.url + '\'');
            }
        };

        return Api;
    }

    //// Angular Code ////
    angular.module('codeine').factory('SelectedNodesService', SelectedNodesServiceFactory);

})(angular);
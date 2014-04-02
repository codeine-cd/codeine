'use strict';
angular.module('codeine').constant('Constants', {
    CODEINE_NODES_PROJECT_NAME : 'Codeine_Internal_Nodes_Project',
    //CODEINE_WEB_SERVER : location.hostname.indexOf('127.0.0.1') === -1  ? '' : 'http://codeine.intel.com:12377',
    CODEINE_WEB_SERVER : location.hostname.indexOf('127.0.0.1') === -1  ? '' : '',
    EVENTS : {
        TAGS_CHANGED : 'tagsChanged'
    }
});
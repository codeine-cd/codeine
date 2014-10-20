(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function CodeineConfigurationServiceFactory($log,$interval,$q,CodeineService,ApplicationFocusService) {
        var globalConfiguration,experimentalConfiguration, initialized = false;

        function loadConfiguration() {
            if ((!ApplicationFocusService.isInFocus()) && (angular.isDefined(globalConfiguration))) {
                $log.debug('CodeineConfigurationService: will skip config refresh as app not in focus');
                return;
            }
            var promiseArrays = [];
            promiseArrays.push(CodeineService.getGlobalConfiguration().success(function(data) {
                $log.debug('CodeineConfigurationService: got global configuration ' + angular.toJson(data));
                globalConfiguration = data;
            }));

            promiseArrays.push(CodeineService.getExperimentalConfiguration().success(function(data) {
                $log.debug('CodeineConfigurationService: got experimental configuration ' + angular.toJson(data));
                experimentalConfiguration = data;
            }));

            return $q.all(promiseArrays).then(function() {
                initialized = true;
            });
        }

        function init() {
            var promise = loadConfiguration();
            $interval(loadConfiguration,300000,0,false);
            return promise;
        }

        function getGlobalConfiguration() {
            return globalConfiguration;
        }

        function getExperimentalConfiguration() {
            return experimentalConfiguration;
        }

        function isInitialized() {
            return initialized;
        }

        return {
            init : init,
            getGlobalConfiguration : getGlobalConfiguration,
            getExperimentalConfiguration : getExperimentalConfiguration,
            isInitialized : isInitialized
        };
    }

    //// Angular Code ////
    angular.module('codeine').factory('CodeineConfigurationService',CodeineConfigurationServiceFactory);

})(angular);
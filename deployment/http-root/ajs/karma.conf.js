module.exports = function(config) {
    config.set({

        basePath: './',

        files : [
            // bower:
            'app/bower_components/jquery/dist/jquery.js',
            'app/bower_components/json3/lib/json3.js',
            'app/bower_components/es5-shim/es5-shim.js',
            'app/bower_components/angular/angular.js',
            'app/bower_components/angular-animate/angular-animate.js',
            'app/bower_components/angular-route/angular-route.js',
            'app/bower_components/angular-ui-utils/ui-utils.js',
            'app/bower_components/select2/select2.js',
            'app/bower_components/angular-ui-select2/src/select2.js',
            'app/bower_components/bootstrap/dist/js/bootstrap.js',
            'app/bower_components/ngstorage/ngStorage.js',
            'app/bower_components/ng-textcomplete/ng-textcomplete.js',
            'app/bower_components/underscore/underscore.js',
            'app/bower_components/ngDistinctValues/ng-distinct-values.js',
            'app/bower_components/d3/d3.js',
            'app/bower_components/moment/moment.js',
            'app/bower_components/angular-bootstrap/ui-bootstrap-tpls.js',
            'app/bower_components/waypoints/waypoints.js',
            'app/bower_components/SHA-1/sha1.js',
            'app/bower_components/angulartics/src/angulartics.js',
            'app/bower_components/angulartics/src/angulartics-adobe.js',
            'app/bower_components/angulartics/src/angulartics-chartbeat.js',
            'app/bower_components/angulartics/src/angulartics-cnzz.js',
            'app/bower_components/angulartics/src/angulartics-flurry.js',
            'app/bower_components/angulartics/src/angulartics-ga-cordova.js',
            'app/bower_components/angulartics/src/angulartics-ga.js',
            'app/bower_components/angulartics/src/angulartics-gtm.js',
            'app/bower_components/angulartics/src/angulartics-kissmetrics.js',
            'app/bower_components/angulartics/src/angulartics-mixpanel.js',
            'app/bower_components/angulartics/src/angulartics-piwik.js',
            'app/bower_components/angulartics/src/angulartics-scroll.js',
            'app/bower_components/angulartics/src/angulartics-segmentio.js',
            'app/bower_components/angulartics/src/angulartics-splunk.js',
            'app/bower_components/angulartics/src/angulartics-woopra.js',
            'app/bower_components/angulartics/src/angulartics-marketo.js',
            'app/bower_components/angulartics/src/angulartics-intercom.js',
            'app/bower_components/angular-duration-filter-formatter/src/duration.js',
            'app/bower_components/angular-busy/dist/angular-busy.js',
            'app/bower_components/bootstrap-switch/dist/js/bootstrap-switch.js',
            'app/bower_components/angular-bootstrap-switch/dist/angular-bootstrap-switch.js',
            'app/bower_components/angular-messages/angular-messages.js',
            'app/bower_components/angular-mocks/angular-mocks.js',
            'app/bower_components/angular-scenario/angular-scenario.js',
            // endbower
            'app/components/**/*.module.js',
            'app/components/**/*.js',
            'test/**/*.spec.*js'
        ],

        colors: true,
        port : 9877,
        autoWatch : true,

        frameworks: ['jasmine'],

        browsers : ['PhantomJS'],

        plugins : [
            'karma-jasmine',
            'karma-coverage',
            'karma-phantomjs-launcher'
        ],

        preprocessors : {
            'app/components/**/*.js' : ['coverage']
        },

        reporters : ['progress', 'coverage'],

        coverageReporter: {
            type : 'html',
            dir : 'coverage/'
        }
    });
};
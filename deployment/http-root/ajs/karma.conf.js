module.exports = function(config){
    config.set({
        basePath : 'app/',

        files : [
            'bower_components/jquery/dist/jquery.js',
            'bower_components/es5-shim/es5-shim.js',
            'bower_components/angular/angular.js',
            'bower_components/angular-animate/angular-animate.js',
            'bower_components/angular-mocks/angular-mocks.js',
            'bower_components/json3/lib/json3.min.js',
            'bower_components/bootstrap/dist/js/bootstrap.js',
            'bower_components/angular-route/angular-route.js',
            'bower_components/angular-ui-bootstrap-bower/ui-bootstrap-tpls.js',
            'bower_components/select2/select2.js',
            'bower_components/angular-ui-select2/src/select2.js',
            'bower_components/angular-animate/angular-animate.js',
            'bower_components/ngstorage/ngStorage.js',
            'bower_components/angular-ui-utils/ui-utils.js',

            'components/**/*.js',
            'components/*.js',
            '../test/**/*.js',
            '../test/*.js'
        ],

        autoWatch : true,

        frameworks: ['jasmine'],

        browsers : ['PhantomJS'],

        plugins : [
            'karma-junit-reporter',
            'karma-chrome-launcher',
            'karma-firefox-launcher',
            'karma-jasmine',
            'karma-coverage',
            'karma-phantomjs-launcher'
        ],

        junitReporter : {
            outputFile: 'test_out/unit.xml',
            suite: 'unit'
        },

        preprocessors : {
            'app/components/**/*.js' : ['coverage']
        },

        reporters : ['coverage']

        //logLevel: config.LOG_DEBUG

    })}

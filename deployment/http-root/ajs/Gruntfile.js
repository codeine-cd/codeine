// Generated on 2014-02-19 using generator-angular 0.7.1
'use strict';

// # Globbing
// for performance reasons we're only matching one level down:
// 'test/spec/{,*/}*.js'
// use this if you want to recursively match all subfolders:
// 'test/spec/**/*.js'

module.exports = function (grunt) {

    // Load grunt tasks automatically
    require('load-grunt-tasks')(grunt);

    // Time how long tasks take. Can help when optimizing build times
    require('time-grunt')(grunt);

    var modRewrite = require('connect-modrewrite');

    // Define the configuration for all the tasks
    grunt.initConfig({

        // Project settings
        yeoman: {
            // configurable paths
            app: require('./bower.json').appPath || 'app',
            dist: 'dist',
            fullPathDist : '/ajs/dist'
        },

        // Watches files for changes and runs tasks based on the changed files
        watch: {
            js: {
                files: ['<%= yeoman.app %>/components/**/*.js'],
                tasks: ['newer:jshint:all'],
                options: {
                    livereload: true
                }
            },
            jsTest: {
                files: ['test/*.js'],
                tasks: ['newer:jshint:test', 'karma']
            },
            styles: {
                files: ['<%= yeoman.app %>/styles/{,*/}*.css'],
                tasks: ['newer:copy:styles', 'autoprefixer']
            },
            gruntfile: {
                files: ['Gruntfile.js']
            },
            livereload: {
                options: {
                    livereload: '<%= connect.options.livereload %>'
                },
                files: [
                    '<%= yeoman.app %>/{,*/}*.html',
                    '.tmp/styles/{,*/}*.css',
                    '<%= yeoman.app %>/images/{,*/}*.{png,jpg,jpeg,gif,webp,svg}'
                ]
            }
        },

        // The actual grunt server settings
        connect: {
            options: {
                port: 9000,
                // Change this to '0.0.0.0' to access the server from outside.
                hostname: 'localhost',
                livereload: 35729
            },
            livereload: {
                options: {
                    open: true,
                    base: [
                        '.tmp',
                        '<%= yeoman.app %>'
                    ],
                    middleware: function(connect, options) {
                        var middlewares = [];

                        middlewares.push(modRewrite(['^[^\\.]*$ /index.html [L]'])); //Matches everything that does not contain a '.' (period)
                        middlewares.push(modRewrite(['^.*[.].*[/].*$ /index.html [L]'])); //Matches everything that does not contain a '.' (period)
                        options.base.forEach(function(base) {
                            middlewares.push(connect.static(base));
                        });
                        return middlewares;
                    }
                }
            },
            test: {
                options: {
                    port: 9001,
                    base: [
                        '.tmp',
                        'test',
                        '<%= yeoman.app %>',
                        'bower_components'
                    ]
                }
            },
            dist: {
                options: {
                    base: '<%= yeoman.dist %>'
                }
            }
        },

        // Make sure code styles are up to par and there are no obvious mistakes
        jshint: {
            options: {
                jshintrc: '.jshintrc',
                reporter: require('jshint-stylish')
            },
            all: [
                '<%= yeoman.app %>/components/**/*.js'
            ],
            test: {
                options: {
                    jshintrc: 'test/.jshintrc'
                },
                src: ['test/*.js']
            }
        },

        // Empties folders to start fresh
        clean: {
            dist: {
                files: [{
                    dot: true,
                    src: [
                        '.tmp',
                        '<%= yeoman.dist %>/*',
                        '!<%= yeoman.dist %>/.git*'
                    ]
                }]
            },
            server: '.tmp'
        },

        // Add vendor prefixed styles
        autoprefixer: {
            options: {
                browsers: ['last 1 version']
            },
            dist: {
                files: [{
                    expand: true,
                    cwd: '.tmp/styles/',
                    src: '{,*/}*.css',
                    dest: '.tmp/styles/'
                }]
            }
        },

        // Automatically inject Bower components into the app
        'wiredep': {
            app: {
                src: '<%= yeoman.app %>/index.html',
                ignorePath: '<%= yeoman.app %>/',
                fileTypes: {
                    html: {
                        replace: {
                            js: '<script src="/{{filePath}}"></script>',
                            css: '<link rel="stylesheet" href="/{{filePath}}" />'
                        }
                    }
                }
            }
        },
        // Renames files for browser caching purposes
        rev: {
            dist: {
                files: {
                    src: [
                        '<%= yeoman.dist %>/views/*.html',
                        '<%= yeoman.dist %>/components/**/*.html',
                        '<%= yeoman.dist %>/scripts/{,*/}*.js',
                        '<%= yeoman.dist %>/styles/{,*/}*.css',
                        '<%= yeoman.dist %>/images/{,*/}*.{png,jpg,jpeg,gif,webp,svg}'
                    ]
                }
            }
        },

        // Reads HTML for usemin blocks to enable smart builds that automatically
        // concat, minify and revision files. Creates configurations in memory so
        // additional tasks can operate on them
        useminPrepare: {
            html: '<%= yeoman.app %>/index.html',
            options: {
                dest: '<%= yeoman.dist %>'
            }
        },

        // Performs rewrites based on rev and the useminPrepare configuration
        usemin: {
            html: ['<%= yeoman.dist %>/{,*/}*.html'],
            css: ['<%= yeoman.dist %>/styles/{,*/}*.css'],
            js: ['<%= yeoman.dist %>/scripts/*.scripts.js','<%= yeoman.dist %>/components/{,*/}*.html'],
            options: {
                assetsDirs: ['<%= yeoman.dist %>', '<%= yeoman.dist %>/images'],
                patterns: {
                    js: [
                        [/["']([^:"']+\.(?:html))["']/img, 'Update the JS to reference our revved htmls']
                    ]
                }
            }
        },
        svgmin: {
            dist: {
                files: [{
                    expand: true,
                    cwd: '<%= yeoman.app %>/images',
                    src: '{,*/}*.svg',
                    dest: '<%= yeoman.dist %>/images'
                }]
            }
        },
        htmlmin: {
            dist: {
                options: {
                    collapseWhitespace: true,
                    collapseBooleanAttributes: true,
                    removeCommentsFromCDATA: true,
                    removeOptionalTags: true
                },
                files: [{
                    expand: true,
                    cwd: '<%= yeoman.dist %>',
                    src: ['*.html', 'components/**/*.html','views/{,*/}*.html' ],
                    dest: '<%= yeoman.dist %>'
                }]
            }
        },


        // Copies remaining files to places other tasks can use
        copy: {
            dist: {
                files: [{
                    expand: true,
                    dot: true,
                    cwd: '<%= yeoman.app %>',
                    dest: '<%= yeoman.dist %>',
                    src: [
                        '*.{ico,png,txt}',
                        '.htaccess',
                        '*.html',
                        'views/{,*/}*.html',
                        'components/**/*.html',
                        'components/*.html',
                        //'bower_components/**/*',
                        'images/*',
                        'fonts/*'
                    ]
                }, {
                    expand: true,
                    cwd: '.tmp/images',
                    dest: '<%= yeoman.dist %>/images',
                    src: ['generated/*']
                }]
            },
            select2imagesDEV: {
                expand: true,
                cwd : '<%= yeoman.app %>/bower_components/select2',
                dest: '<%= yeoman.app %>/images',
                src: '*.{png,gif}'
            },
            select2imagesDIST: {
                expand: true,
                cwd : '<%= yeoman.app %>/bower_components/select2',
                dest: '<%= yeoman.dist %>/images',
                src: '*.{png,gif}'
            },
            styles: {
                expand: true,
                cwd: '<%= yeoman.app %>/styles',
                dest: '.tmp/styles/',
                src: '{,*/}*.css'
            }
        },

        // Run some tasks in parallel to speed up the build process
        concurrent: {
            server: [
                'copy:styles'
            ],
            test: [
                'copy:styles'
            ],
            dist: [
                'copy:styles',
                'svgmin'
            ]
        },

        ngAnnotate: {
            options: {
                singleQuotes: true
            },
            app : {
                files : {
                    '.tmp/concat/scripts/vendor.js' : ['.tmp/concat/scripts/vendor.js'],
                    '.tmp/concat/scripts/scripts.js' : ['.tmp/concat/scripts/scripts.js']
                }
            }
        },

        uglify: {
            options: {
                preserveComments: false,
                compress        : false,
                mangle          : false,
                beautify        : {
                    beautify: false,
                    ascii_only: true,
                    quote_keys: true
                }
            }
        },

        // Test settings
        karma: {
            unit: {
                configFile: 'karma.conf.js',
                singleRun: true
            }
        },
        /**
         * Replaces image paths in CSS to match ../images. Select2 is the only css file this is needed for so far.
         * Others can be added.
         */
        replace: {
            build: {
                src: ['<%= yeoman.app %>/bower_components/select2/select2.css'],
                overwrite: true,
                replacements: [
                    {
                        from: /(url)\('s/g,
                        to: '$1(\'/images/s'
                    }
                ]
            }
        }
    });


    grunt.registerTask('serve', function (target) {
        if (target === 'dist') {
            return grunt.task.run(['build', 'connect:dist:keepalive']);
        }

        grunt.task.run([
            'clean:server',
            'wiredep',
            'copy:select2imagesDEV',
            'concurrent:server',
            'autoprefixer',
            'connect:livereload',
            'watch'
        ]);
    });

    grunt.registerTask('server', function () {
        grunt.log.warn('The `server` task has been deprecated. Use `grunt serve` to start a server.');
        grunt.task.run(['serve']);
    });

    grunt.registerTask('test', [
        'clean:server',
        'concurrent:test',
        'autoprefixer',
        'connect:test',
        'karma'
    ]);

    grunt.registerTask('build', [
        'clean:dist',
        'wiredep',
        'useminPrepare',
        'replace:build',
        'concurrent:dist',
        'autoprefixer',
        'concat',
        'copy:dist',
        'copy:select2imagesDIST',
        'cssmin',
        'ngAnnotate',
        'uglify',
        'rev',
        'usemin',
        'htmlmin'
    ]);

    grunt.registerTask('default', [
        'newer:jshint',
        //'test',
        'build'
    ]);
};

'use strict';

module.exports = function (grunt) {

    // Load grunt tasks automatically
    require('load-grunt-tasks')(grunt);

    // Time how long tasks take. Can help when optimizing build times
    require('time-grunt')(grunt);

    var modRewrite = require('connect-modrewrite');

    var appConfig = {
        // configurable paths
        app : 'app',
        dist: 'dist'
    };

    // Define the configuration for all the tasks
    grunt.initConfig({

        // Project settings
        yeoman: appConfig,

        // Watches files for changes and runs tasks based on the changed files
        watch: {
            bower: {
                files: ['bower.json'],
                tasks: ['wiredep']
            },
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
                        return [
                            modRewrite(['^[^\\.]*$ /index.html [L]']),
                            connect.static('.tmp'),
                            connect().use(
                                '/bower_components',
                                connect.static('./bower_components')
                            ),
                            connect.static(appConfig.app)
                        ];
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
                src: ['test/**/*.js']
            }
        },

        jscs: {
            src : ['<%= yeoman.app %>/components/**/*.js'],
            options: {
                config: '.jscs.json'
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
            server: {
                options: {
                    map: true
                },
                files: [{
                    expand: true,
                    cwd: '.tmp/styles/',
                    src: '{,*/}*.css',
                    dest: '.tmp/styles/'
                }]
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
        wiredep: {
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
            },
            test: {
                src: 'karma.conf.js',
                fileTypes: {
                    js: {
                        block: /(([\s\t]*)\/\/\s*bower:*(\S*))(\n|\r|.)*?(\/\/\s*endbower)/gi,
                        detect: {
                            js: /'(.*\.js)'/gi
                        },
                        replace: {
                            js: '\'{{filePath}}\','
                        }
                    }
                },
                devDependencies: true
            }
        },
        // Renames files for browser caching purposes
        filerev: {
            dist: {
                files: [{
                    src: [
                        '<%= yeoman.dist %>/components/**/*.html',
                        '<%= yeoman.dist %>/scripts/{,*/}*.js',
                        '<%= yeoman.dist %>/styles/{,*/}*.css',
                        '<%= yeoman.dist %>/images/{,*/}*.{png,jpg,jpeg,gif,webp,svg}',
                        '<%= yeoman.dist %>/fonts/{,*/}*.{otf,eot,svg,ttf,woff,woff2}'
                    ]
                }]
            }
        },

        // Reads HTML for usemin blocks to enable smart builds that automatically
        // concat, minify and revision files. Creates configurations in memory so
        // additional tasks can operate on them
        useminPrepare: {
            html: ['<%= yeoman.app %>/index.html', '<%= yeoman.app %>/components/**/*.html'],
            options: {
                dest: '<%= yeoman.dist %>',
                flow: {
                    html: {
                        steps: {
                            js: ['concat', 'uglifyjs'],
                            css: ['cssmin']
                        },
                        post: {}
                    }
                }
            }
        },

        // Performs rewrites based on rev and the useminPrepare configuration
        usemin: {
            html: ['<%= yeoman.dist %>/**/*.html'],
            css: ['<%= yeoman.dist %>/styles/*.css'],
            js: ['<%= yeoman.dist %>/scripts/scripts.*.js',
                '<%= yeoman.dist %>/components/**/*.html',
                '<%= yeoman.dist %>/index.html'],
            options: {
                assetsDirs: ['<%= yeoman.dist %>', '<%= yeoman.dist %>/images', '<%= yeoman.dist %>/styles'],
                patterns: {
                    js: [
                        [/["']([^:"']+\.(?:html))["']/img, 'Update the JS to reference our revved htmls'],
                        [/[\\']([^:"']+\.(?:html))[\\']/img, 'Update the JS to reference our revved htmls'],
                        [/(images\/[^''""]*\.(png|jpg|jpeg|gif|webp|svg))/g, 'Replacing references to images'],
                        [/(\/images\/[^''""]*\.(png|jpg|jpeg|gif|webp|svg))/g, 'Replacing references to images']
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
                    conservativeCollapse: true,
                    collapseBooleanAttributes: true,
                    removeCommentsFromCDATA: true,
                    removeOptionalTags: true
                },
                files: [{
                    expand: true,
                    cwd: '<%= yeoman.dist %>',
                    src: ['*.html', 'components/**/*.html'],
                    dest: '<%= yeoman.dist %>'
                }]
            }
        },

        ngtemplates: {
            dist: {
                options: {
                    module: 'codeine',
                    prefix: '/',
                    htmlmin: '<%= htmlmin.dist.options %>',
                    usemin: '/scripts/scripts.js'
                },
                cwd: '<%= yeoman.app %>',
                src: 'components/**/{,*/}*.html',
                dest: '.tmp/templateCache.js'
            }
        },

        ngAnnotate: {
            options: {
                singleQuotes: true
            },
            dist: {
                files: [{
                    expand: true,
                    cwd: '.tmp/concat/scripts',
                    src: '*.js',
                    dest: '.tmp/concat/scripts'
                }]
            }
        },

        cdnify: {
            dist: {
                options: {
                    rewriter: function (url) {
                        if (url.indexOf('..') === 0) {
                            return url;
                        }
                        if (url.indexOf('data:') === 0) {
                            return url;
                        }
                        if (url.indexOf('/images') === 0) {
                            return url;
                        }
                        return '/images/' +  url;
                    },
                    css : true
                },
                files: [{
                    expand: true,
                    cwd: '<%= yeoman.dist %>/styles',
                    src: 'vendor.*.css',
                    dest: '<%= yeoman.dist %>/styles'
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
                        'version.json',
                        'components/*.swf',
                        'views/{,*/}*.html',
                        'components/**/*.html',
                        'components/*.html',
                        'images/*',
                        'fonts/*'
                    ]
                }, {
                    expand: true,
                    cwd: '.tmp/images',
                    dest: '<%= yeoman.dist %>/images',
                    src: ['generated/*']
                },
                    {
                        expand: true,
                        cwd: '<%= yeoman.app %>/bower_components/bootstrap/fonts',
                        dest: '<%= yeoman.dist %>/fonts',
                        src: '*.*'
                    },
                    {
                        expand: true,
                        cwd: '<%= yeoman.app %>/bower_components/font-awesome/fonts',
                        dest: '<%= yeoman.dist %>/fonts',
                        src: '*.*'
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
                //'imagemin',
                'svgmin'
            ]
        },

        // Test settings
        karma: {
            unit: {
                configFile: 'karma.conf.js',
                singleRun: true
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
            'concurrent:server',
            'autoprefixer',
            'connect:livereload',
            'watch'
        ]);
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
        'concurrent:dist',
        'autoprefixer',
        'ngtemplates',
        'concat',
        'ngAnnotate',
        'copy:dist',
        'copy:select2imagesDEV',
        'copy:select2imagesDIST',
        'cssmin',
        'uglify',
        'filerev',
        'cdnify',
        'usemin',
        'htmlmin'
    ]);

    grunt.registerTask('default', [
        'newer:jshint',
        //'test',
        'build'
    ]);
};
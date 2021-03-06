(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function textcomplete(Textcomplete) {
        return {
            restrict: 'EA',
            scope: {
                members: '=',
                message: '=',
                isRequired : '@'
            },
            template: '<textarea ng-model=\'message\' type=\'text\' name=\'script_content\' ng-required=\'{{isRequired}}\'></textarea>',
            link: function(scope, iElement, attrs) {
                var mentions = scope.members;
                var ta = iElement.find('textarea');
                ta.attr('id',attrs.elementId);
                ta.attr('rows',attrs.rows);
                ta.attr('class',attrs.elementClass);
                ta.attr('placeholder',attrs.elementPlaceholder);
                new Textcomplete(ta, [
                    {
                        match: /(^|\s)\$(\w*)$/,
                        search: function(term, callback) {
                            callback($.map(mentions, function(mention) {
                                return mention.toLowerCase().indexOf(term.toLowerCase()) === 0 ? mention : null;
                            }));
                        },
                        index: 2,
                        replace: function(mention) {
                            return '$1$' + mention + ' ';
                        }
                    }
                ]);
            }
        };
    }

    //// Angular Code ////
    angular.module('codeine').directive('textcomplete', textcomplete);

})(angular);
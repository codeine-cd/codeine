angular.module('codeine').directive('help', ['$log','HelpConstants', function ($log, HelpConstants) {
    return {
        restrict: 'E',
        transclude: false,
        template: '<i class="fa fa-question-circle codeine_help"></i>',
        link: function ($scope, element, attrs) {
            var text = HelpConstants[attrs.helpId] ? HelpConstants[attrs.helpId] : "No help yet for id '" + attrs.helpId +
            "' <a href='mailto:ohad.shai@intel.com?Subject=Please%20provide%20help%20for%20" + attrs.helpId
            + "' target='_top'>Suggest a message</a>";
            element.popover(
                {
                    content : '<span class="codeine_help_content">' + text + '</span>',
                    html : true,
                    placement : "bottom",
                    trigger : "hover",
                    delay : {
                        hide : 500
                    },
                    container :'body'
                }
            );
        }
    };
}]);
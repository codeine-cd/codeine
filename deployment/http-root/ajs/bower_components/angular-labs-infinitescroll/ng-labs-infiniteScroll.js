angular.module('labs.infiniteScroll', []);

angular.module('labs.infiniteScroll').directive('infiniteScroll', [
	'$rootScope', '$timeout', function($rootScope, $timeout) {
		return {
			link: function(scope, elem, attrs) {
				var checkWhenEnabled, handler, scrollDistance, scrollEnabled;
				scrollDistance = 0;
				if (attrs.infiniteScrollDistance != null) {
					scope.$watch(attrs.infiniteScrollDistance, function(value) {
						return scrollDistance = parseInt(value, 10);
					});
				}
				scrollEnabled = true;
				checkWhenEnabled = false;
				if (attrs.infiniteScrollDisabled != null) {
					scope.$watch(attrs.infiniteScrollDisabled, function(value) {
						scrollEnabled = !value;
						if (scrollEnabled && checkWhenEnabled) {
							checkWhenEnabled = false;
							return handler();
						}
					});
				}
				handler = function() {
					var viewBottom = elem.scrollTop() + elem.height();
					var remaining = elem[0].scrollHeight - viewBottom;
					shouldScroll = remaining <= scrollDistance;
					if (shouldScroll && scrollEnabled) {
						if ($rootScope.$$phase) {
							return scope.$eval(attrs.infiniteScroll);
						} else {
							return scope.$apply(attrs.infiniteScroll);
						}
					} else if (shouldScroll) {
						return checkWhenEnabled = true;
					}
				};
				elem.on('scroll', handler);
				scope.$on('$destroy', function() {
					return elem.off('scroll', handler);
				});
				return $timeout((function() {
					if (attrs.infiniteScrollImmediateCheck) {
						if (scope.$eval(attrs.infiniteScrollImmediateCheck)) {
							return handler();
						}
					} else {
						return handler();
					}
				}), 0);
			}
		};
	}
]);

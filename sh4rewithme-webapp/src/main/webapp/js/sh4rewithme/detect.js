var isEventSupported = (function() {
	var TAGNAMES = {
		'select' : 'input',
		'change' : 'input',
		'submit' : 'form',
		'reset' : 'form',
		'error' : 'img',
		'load' : 'img',
		'abort' : 'img'
	};
	function isEventSupported(eventName, element) {
		element = element
				|| document.createElement(TAGNAMES[eventName] || 'div');
		eventName = 'on' + eventName;
		// When using `setAttribute`, IE skips "unload", WebKit skips "unload"
		// and "resize", whereas `in` "catches" those
		var isSupported = eventName in element;
		if (!isSupported) {
			// If it has no `setAttribute` (i.e. doesn't implement Node
			// interface), try generic element
			if (!element.setAttribute) {
				element = document.createElement('div');
			}
			if (element.setAttribute && element.removeAttribute) {
				element.setAttribute(eventName, '');
				isSupported = typeof element[eventName] == 'function';
				// If property was created, "remove it" (by setting value to
				// `undefined`)
				if (typeof element[eventName] != 'undefined') {
					element[eventName] = undefined;
				}
				element.removeAttribute(eventName);
			}
		}
		element = null;
		return isSupported;
	}
	return isEventSupported;
})();
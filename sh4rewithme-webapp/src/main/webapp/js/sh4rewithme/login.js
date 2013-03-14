var getParameterByName = function(name) {
	name = name.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
	var regexS = "[\\?&]" + name + "=([^&#]*)";
	var regex = new RegExp(regexS);
	var results = regex.exec(window.location.search);
	if (results == null)
		return null;
	else
		return decodeURIComponent(results[1].replace(/\+/g, " "));
};
$(document).ready(function() {
	var userId = getParameterByName("userId");
	if (userId) {
		$("#j_username").val(userId);
	}
});
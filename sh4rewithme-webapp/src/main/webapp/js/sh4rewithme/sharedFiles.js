$(document).ready(function() {
	var buddies = [];
	var xhr = new XMLHttpRequest();
	xhr.onreadystatechange = function() {
		if (xhr.readyState == 4) {
			if (xhr.status == 200) {
				buddies = eval(xhr.responseText);
			} else {
			}
		}
	};
	xhr.open('GET', './buddies');
	xhr.setRequestHeader('Accept', 'application/json');
	xhr.send();
	$('.delete').click(function(e) {
		// trap regular click
		e.preventDefault();
		// get href and send a DELETE on it
		var href = $(this).attr('href');
		var xhr = new XMLHttpRequest();
		var liEl = $(this).parents('li');
		xhr.onreadystatechange = function() {
			if (xhr.readyState == 4) {
				if (xhr.status == 200) {
					liEl.hide(500);
				} else {
					$('#delete-error').show(500);
				}
			}
		};
		xhr.open('DELETE', href);
		xhr.send();
		return false;
	});
	$('.change-privacy').click(function(e) {
		// trap regular click
		e.preventDefault();
		// get href and send a PUT on it
		var href = $(this).attr('href');
		var xhr = new XMLHttpRequest();
		xhr.onreadystatechange = function() {
			if (xhr.readyState == 4) {
				if (xhr.status == 200) {
					window.location.reload();
				} else {
					$('#change-privacy-error').show(500);
				}
			}
		};
		xhr.open('PUT', href);
		xhr.send();
		return false;
	});
	$('.add-buddy').click(function(e) {
		// trap regular click
		e.preventDefault();
		var href = $(this).attr('href');
		$('#addbuddy-cancel').click(function() {
			$('#addbuddy-modal').modal('hide');
		});
		$('#addbuddy-validate').click(function() {
			var buddy = $('#buddy').val();
			$('#addbuddy-modal').modal('hide');
			if (buddies) {
				var xhr = new XMLHttpRequest();
				xhr.onreadystatechange = function() {
					if (xhr.readyState == 4) {
						if (xhr.status == 200) {
							window.location.reload();
						} else {
							$('#addbuddy-cancelled-error').show(500);
						}
					}
				};
				xhr.open('PUT', href + buddy);
				xhr.send();
			} else {
				$('#addbuddy-cancelled-error').show(500);
			}
		});
		$('#addbuddy-modal').modal();
		$('#buddy').typeahead({
			source : function(query) {
				return buddies;
			}
		});
		return false;
	});
});
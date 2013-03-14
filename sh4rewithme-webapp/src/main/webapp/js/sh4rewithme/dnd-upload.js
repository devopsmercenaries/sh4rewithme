var scoped = (function() {
	var tests = {
		filereader : typeof FileReader != 'undefined',
		dnd : (isEventSupported('dragstart') && isEventSupported('drop')),
		formdata : !!window.FormData,
		progress : "upload" in new XMLHttpRequest
	};
	var acceptedTypes = {
		'image/png' : true,
		'image/jpeg' : true,
		'image/gif' : true
	};
	var progress = document.getElementById('uploadprogress');
	if (tests.filereader && tests.dnd && tests.formdata && tests.progress) {
		var previewfile = function(file) {
			if (tests.filereader === true) {
				if (acceptedTypes[file.type] === true) {
					var reader = new FileReader();
					reader.onload = function(event) {
						var image = new Image();
						image.src = event.target.result;
						image.width = 250; // a fake resize
						$('#drop-area').appendChild(image);
					};
					reader.readAsDataURL(file);
				}
			} else {
				$('#drop-area').innerHTML += '<p>Uploaded ' + file.name + ' ' + (file.fileSize ? (file.fileSize / 1024 | 0) + 'K' : '');
				console.log(file);
			}
		};
		var readfiles = function(files) {
			$('#quickshare-cancel').click(function() {
				$('#quickshare-modal').modal('hide');
			});
			$('#quickshare-upload').click(function() {
				$('#quickshare-modal').modal('hide');
				var description = $('#quickshare-description').val();
				var expiration = $('#quickshare-expiration').val();
				if (description) {
					// debugger;
					var formData = tests.formdata ? new FormData() : null;
					for ( var i = 0; i < files.length; i++) {
						if (tests.formdata) {
							formData.append('file', files[i]);
							formData.append('description', description);
							formData.append('expiration', expiration);
						}
						previewfile(files[i]);
					}
					// now post a new XHR request
					if (tests.formdata) {
						var xhr = new XMLHttpRequest();
						xhr.open('POST', './upload');
						xhr.onload = function() {
							progress.value = progress.innerHTML = 100;
						};
						if (tests.progress) {
							xhr.upload.onprogress = function(event) {
								if (event.lengthComputable) {
									var complete = (event.loaded / event.total * 100 | 0);
									progress.value = progress.innerHTML = complete;
								}
							};
						}
						xhr.send(formData);
					}
				} else {
					$('#quickshare-cancelled-error').show(500);
				}
			});
			$('#quickshare-modal').modal();
		};
		// let's run stuff
		$('#quickshare').toggleClass('hidden');
		var droparea = document.getElementById('drop-area');
		droparea.ondragover = function() {
			droparea.className = 'hover';
			$('#quickshare-cancelled-error').hide(500);
			return false;
		};
		droparea.ondragend = function() {
			droparea.className = '';
			return false;
		};
		droparea.ondrop = function(e) {
			droparea.className = '';
			e.preventDefault();
			readfiles(e.dataTransfer.files);
		};
	} else {
		$('#noquickshare').toggleClass('hidden');
		if (!(tests.filereader)) {
			$('#filereader').toggleClass('hidden');
		}
		if (!(tests.dnd)) {
			$('#dnd').toggleClass('hidden');
		}
		if (!(tests.formdata)) {
			$('#formdata').toggleClass('hidden');
		}
		if (!(tests.progress)) {
			$('#progress').toggleClass('hidden');
		}
	}
})();
$(() => {
	let tabUrl = 'https://';
	// Checks if user is on an url which represents a repository on github
	chrome.tabs.query({ active: true, currentWindow: true }, (tabs) => {
		let url = tabs[0].url;
		let urlArray = url.split('/');
		if (urlArray[4] === undefined) {
			$('#mainContent').hide();
		} else {
			$('#fallback').hide();
			for (let i = 2; i < 5; ++i) tabUrl += urlArray[i] + '/';
		}
	});
	$('#displayUrl').text('Select a feature');

	// The features that an user can select
	let feature;
	$('#cloneRepo').click(() => {
		feature = 'clone-url';
		$('#displayUrl').text('Now please select an ide');
	});

	// The IDEs that an user can select
	let ide;
	$('#dropdownMenu a').click(function () {
		ide = $(this).attr('id');
		let url = `${ide}://${feature}?url=${tabUrl}`;
		$('#dropdownMenu').slideUp('fast');
		$('#displayUrl').text(url);
	});

	$('#addData').click(() => {
		// Storing all the data in localstorage for future references
		chrome.storage.sync.get('allUrl', (value) => {
			if (ide !== undefined && feature != undefined) {
				let val,
					obj = {},
					allUrl = value.allUrl ? value.allUrl : [];
				if (allUrl.length > 0) {
					val = allUrl.length + 1;
				} else {
					val = 1;
				}
				let newKey = `url${val}`;
				obj = {
					url: tabUrl,
					feature: feature,
				};
				allUrl.push(obj);
				chrome.storage.sync.set({ allUrl: allUrl });
			}
		});
		window.close();
	});
});

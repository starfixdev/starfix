$(() => {
	let tabUrl;
	// Checks if user is on an url which represents a repository on github
	chrome.tabs.query({ active: true, currentWindow: true }, (tabs) => {
		let url = tabs[0].url;
		urlArray = url.split('/');
		if (urlArray[4] === undefined) {
			$('#mainContent').hide();
			tabUrl = tabs[0].url;
		} else {
			$('#fallback').hide();
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
		$('#displayUrl').text(url);
	});

	// Storing all the data in localstorage for future references
	let allUrl = JSON.parse(localStorage.getItem('allUrl'));
	if(ide !== undefined && feature != undefined) {
		let val 
		if(allUrl) {
			val = Object.keys(allUrl).length + 1;
			let newKey = `url-${val}`;
			const obj = {
				[newKey]: {
					url: tabUrl,
					feature: feature
				}
			}
			Object.assign(allUrl, obj);
		} else {
			val = 1;
			let newKey = `url-${val}`;
			const obj = {
				[newKey]: {
					url: tabUrl,
					feature: feature
				}
			}
			Object.assign(allUrl, obj);
		}
		localStorage.setItem(JSON.stringify(allUrl));
	}
});

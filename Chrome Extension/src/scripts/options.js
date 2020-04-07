$(() => {
	chrome.storage.sync.get('allUrl', (val) => {
		let ol = document.createElement('ol');
		let allUrl = val.allUrl;
		allUrl.forEach((urlItem) => {
			let li = document.createElement('li');
			li.innerHTML = `<b>url:</b> ${urlItem.url}, <b>feature:</b> ${urlItem.feature}`;
			ol.append(li);
		});
		$('#history').append(ol);
	});
});

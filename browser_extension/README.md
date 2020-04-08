# Starfish Browser extension


This is the browser extension for Starfish, supporting Chrome and Firefox. It adds a **Open in IDE** button to the configured GitHub and GitLab installations (defaults to domains containing `github.com` or `gitlab.com`) which opens up the srafish Application.

Note: It will not open up starfish as currently the application does not exists.You'll get the `open xdg-open` option.

## @Github
![Github Demo](https://user-images.githubusercontent.com/31308705/78818628-965dd600-79f2-11ea-916e-5ff06ddc7b42.gif)

## @Gitlab
![Gitlab Demo](https://user-images.githubusercontent.com/31308705/78818989-23a12a80-79f3-11ea-87e8-3a2b6abf54f9.gif)

## Screenshot to Show generated Link
`ide://#https://github.com/maxandersen/jbang`
[See bottom left when cursor is pointed to `open in IDE`]

![Screenshot for Demo](https://user-images.githubusercontent.com/31308705/78819496-f6a14780-79f3-11ea-8c4b-52e8a3f28931.png)

## Supported Browsers
* Google Chrome 
* Mozilla Firefox.
## Build

```
yarn install && yarn build
```

## Test

[Build](#build) first and then load it as ["unpackaged extension" (Chrome)](https://developer.chrome.com/extensions/getstarted#unpacked) or ["temporary add-on" (Firefox)](https://blog.mozilla.org/addons/2015/12/23/loading-temporary-add-ons/). It should be active until the next restart of your browser.

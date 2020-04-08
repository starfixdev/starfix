# Starfish Browser extension


This is the browser extension for Starfish, supporting Chrome and Firefox. It adds a **Open in IDE** button to the configured GitHub and GitLab installations (defaults to domains containing `github.com` or `gitlab.com`) which directly creates a workspace for that context:


## Build

```
yarn install && yarn build
```

## Test

[Build](#build) first and then load it as ["unpackaged extension" (Chrome)](https://developer.chrome.com/extensions/getstarted#unpacked) or ["temporary add-on" (Firefox)](https://blog.mozilla.org/addons/2015/12/23/loading-temporary-add-ons/). It should be active until the next restart of your browser.

current_url = window.location.href;
var page_uri = "ide://#" + window.location.href;

if (current_url.indexOf("https://github.com/") != -1)

{ //Code for Github Begins

    if (!localStorage.getItem("starfish")) {
        navigator.registerProtocolHandler("web+ide",
            "https://github.com/?ide=%s",
            "ide");
        localStorage.setItem("starfish", "true");
    }
    var link = document.createElement('a');
    link.setAttribute("title", "Open In IDE");
    //var page_uri ="ide://#"+window.location.href;
    link.setAttribute("href", page_uri);
    link.setAttribute("class", "btn btn-sm btn-primary dev")
    link.innerHTML = "Open in IDE";

    var starfish = document.createElement('div');
    //starfish.setAttribute("class","empty-icon position-relative gitpod-nav-btn float-right");
    starfish.appendChild(link);
    var el = document.getElementsByClassName("file-navigation")[0];
    el.appendChild(starfish);

} //Code for Github Ends here
else
if (current_url.indexOf("https://gitlab.com") != -1) { //Code for Gitlab begins here
    if (!localStorage.getItem("starfish")) {
        navigator.registerProtocolHandler("web+ide",
            "https://gitlab.com/?ide=%s",
            "ide");
        localStorage.setItem("starfish", "true");
    }
    var link = document.createElement('a');
    link.setAttribute("title", "Open In IDE");

    link.setAttribute("href", page_uri);
    link.setAttribute("class", "btn btn-primary clone-dropdown-btn qa-clone-dropdown")
    link.innerHTML = "Open in IDE";

    var starfish = document.createElement('div');
    //starfish.setAttribute("class","empty-icon position-relative gitpod-nav-btn float-right");
    starfish.setAttribute("class", "git-clone-holder js-git-clone-holder margin-left-5")
    starfish.appendChild(link);
    var el = document.getElementsByClassName("tree-controls")[0];
    el.appendChild(starfish);


}
else
if (current_url.indexOf("https://gist.github.com/") != -1) { //Code for Gitlab begins here
    if (!localStorage.getItem("starfish")) {
        navigator.registerProtocolHandler("web+ide",
            "https://gist.github.com/?ide=%s",
            "ide");
        localStorage.setItem("starfish", "true");
    }
    var link = document.createElement('a');
    link.setAttribute("title", "Open In IDE");

    link.setAttribute("href", page_uri);
    link.setAttribute("class", "btn btn-sm")
    link.innerHTML = "Open in IDE";

    var starfish = document.createElement('div');
    //starfish.setAttribute("class","empty-icon position-relative gitpod-nav-btn float-right");
    starfish.setAttribute("class", "file-navigation-option margin-left-5")
    starfish.appendChild(link);
    var el = document.getElementsByClassName("file-navigation-options")[0];
    el.appendChild(starfish);


}



//document.getElementsByClassName("get-repo-select-menu")[0].insertAdjacentHTML("afterend",starfish);
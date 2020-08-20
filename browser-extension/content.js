current_url = window.location.href;
var page_uri = "ide://" + window.location.href;

if (current_url.indexOf("https://github.com/") != -1)

{ //Code for Github Begins

    if (!localStorage.getItem("starfix")) {
        navigator.registerProtocolHandler("web+ide",
            "https://github.com/?ide=%s",
            "ide");
        localStorage.setItem("starfix", "true");
    }
    var link = document.createElement('a');
    link.setAttribute("title", "Open In IDE");

    

    var clone_method="https"; //Initializing with default value

    chrome.storage.sync.get({
        method: 'https'
      }, function(items) {
        clone_method=items.method;  
        if(clone_method=="ssh"){
            var repo_link=current_url.substring(current_url.indexOf("github.com/")+"github.com/".length);
            page_uri="ide://git@github.com:"+repo_link;
            console.log(page_uri);
        }
        page_uri+=".git";

        link.setAttribute("href", page_uri);  
        console.log(clone_method); 
      });
   
  

    link.setAttribute("class", "btn btn-sm btn-primary dev")
    link.innerHTML = "Open in IDE";

    var starfix = document.createElement('div');
    //starfix.setAttribute("class","empty-icon position-relative gitpod-nav-btn float-right");
    starfix.appendChild(link);
    var el = document.getElementsByClassName("file-navigation")[0];
    el.appendChild(starfix);

} //Code for Github Ends here
else
if (current_url.indexOf("https://gitlab.com") != -1) { //Code for Gitlab begins here
    if (!localStorage.getItem("starfix")) {
        navigator.registerProtocolHandler("web+ide",
            "https://gitlab.com/?ide=%s",
            "ide");
        localStorage.setItem("starfix", "true");
    }
    var link = document.createElement('a');
    link.setAttribute("title", "Open In IDE");

    link.setAttribute("href", page_uri);
    link.setAttribute("class", "btn btn-primary clone-dropdown-btn qa-clone-dropdown")
    link.innerHTML = "Open in IDE";

    var starfix = document.createElement('div');
    //starfix.setAttribute("class","empty-icon position-relative gitpod-nav-btn float-right");
    starfix.setAttribute("class", "git-clone-holder js-git-clone-holder margin-left-5")
    starfix.appendChild(link);
    var el = document.getElementsByClassName("tree-controls")[0];
    el.appendChild(starfix);


}
else
if (current_url.indexOf("https://gist.github.com/") != -1) { //Code for gist.github.com Begins here
    if (!localStorage.getItem("starfix")) {
        navigator.registerProtocolHandler("web+ide",
            "https://gist.github.com/?ide=%s",
            "ide");
        localStorage.setItem("starfix", "true");
    }
    var link = document.createElement('a');
    link.setAttribute("title", "Open In IDE");

    link.setAttribute("href", page_uri);
    link.setAttribute("class", "btn btn-sm")
    link.innerHTML = "Open in IDE";

    var starfix = document.createElement('div');
    //starfix.setAttribute("class","empty-icon position-relative gitpod-nav-btn float-right");
    starfix.setAttribute("class", "file-navigation-option margin-left-5")
    starfix.appendChild(link);
    var el = document.getElementsByClassName("file-navigation-options")[0];
    el.appendChild(starfix);


}



//document.getElementsByClassName("get-repo-select-menu")[0].insertAdjacentHTML("afterend",starfix);
/*var images = document.getElementsByTagName('img');
for (var i = 0, l = images.length; i < l; i++) {
  images[i].src = 'http://placekitten.com/' + images[i].width + '/' + images[i].height;
}*/
/*var el=document.getElementsByClassName("file-navigation")[0];
var btn = document.createElement("div");   // Create a div element
btn.innerHTML = "<div id="gitpod-btn-container" class="empty-icon position-relative gitpod-nav-btn float-right">
<a id="gitpod-btn-nav" title="Open in IDE" href="ide://#https://github.com/lateral/chrome-extension-blogpost" class="btn btn-sm btn-primary dev">Open in IDE</a></div>";                   
el.appendChild(btn);  
*/

//console.log(document.getElementsByClassName("get-repo-select-menu")[0]);


  if(!localStorage.getItem("starfish")){
     navigator.registerProtocolHandler("web+ide",
                           "https://github.com/?ide=%s",
                           "ide");
     localStorage.setItem("starfish","true");
  }
var link = document.createElement('a');
link.setAttribute("title","Open In IDE");
var page_uri ="ide://#"+window.location.href;
link.setAttribute("href",page_uri);
link.setAttribute("class","btn btn-sm btn-primary dev")
link.innerHTML="Open in IDE";

var starfish=document.createElement('div');
//starfish.setAttribute("class","empty-icon position-relative gitpod-nav-btn float-right");
starfish.appendChild(link);
var el=document.getElementsByClassName("file-navigation")[0];
el.appendChild(starfish); 




//document.getElementsByClassName("get-repo-select-menu")[0].insertAdjacentHTML("afterend",starfish);

/*document.getElementsByClassName("get-repo-select-menu")[0].insertAdjacentHTML("afterend", 
                "<div id="gitpod-btn-container" class="empty-icon position-relative gitpod-nav-btn float-right">
                <a id="gitpod-btn-nav" title="Open in IDE" href="ide://#https://github.com/lateral/chrome-extension-blogpost" class="btn btn-sm btn-primary dev">Open in IDE</a></div>"); */
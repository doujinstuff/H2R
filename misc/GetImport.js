var elements = document.querySelectorAll("div.img-container div.img-overlay a"), i = 0;

var FAVORITE = 1;
var ON_HOLD = 2;
var PLAN_TO_READ = 3;
var COMPLETED = 4;
var jsonArray = [];

var bookmarkText = document.querySelector("main.bg-black h3.block-title small.text-danger").innerText;
bookmarkText = bookmarkText.substring(1, bookmarkText.indexOf(" "));

var pageText = document.querySelector("main.bg-black h3.block-title small.text-danger").innerText;
pageText = pageText.substring(pageText.indexOf("Page ") + 5, pageText.indexOf("]"));

var bookmarkId = 0;
if(bookmarkText == "Favorite") {
	bookmarkId = 1;
}
if(bookmarkText == "On") {
	bookmarkId = 2;
}
if(bookmarkText == "Plan") {
	bookmarkId = 3;
}
if(bookmarkText == "Completed") {
	bookmarkId = 4;
}

while(i < elements.length) {
	var e = elements[i].querySelector("h2.mangaPopover");

	var id = e.getAttribute("data-mid");

	var title = e.getAttribute("data-title").trim();
    if(title.indexOf(" [") > 0) {
	    title = title.substring(0, title.indexOf(" [")).trim();
    }

	var url = elements[i].getAttribute("href");
	url = new URL(url);
	url = "\\" + url.pathname.slice(0,-1) + "\\/";

	jsonObj = { "id":id, "title":title, "url":url, "bookmark":bookmarkId, "bookmark_date": new Date().getTime()};
	jsonArray.push(jsonObj);
	i = i + 1;
}
var dataStr = "data:text/json;charset=utf-8," + encodeURIComponent(JSON.stringify(jsonArray));
var downloadAnchorNode = document.createElement('a');
downloadAnchorNode.setAttribute("href", dataStr);
downloadAnchorNode.setAttribute("download", bookmarkText + "-" + pageText + ".json");
downloadAnchorNode.click();

try {
    document.querySelector("a#js-linkNext").click();
} catch(err) {
    alert("Done!");
}

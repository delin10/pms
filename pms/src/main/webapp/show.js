function show(str) {
    var xmlhttp = new XMLHttpRequest();
    if (str == "") {
        document.getElementById("txt").innerHTML = "";
        return;
    }
    xmlhttp.onreadystatechange = function() {
        if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
            document.getElementById("txt").innerHTML = xmlhttp.responseText;
        }
    }
    xmlhttp.open("GET", "db.php?q=" + str, true);
    xmlhttp.send();
}
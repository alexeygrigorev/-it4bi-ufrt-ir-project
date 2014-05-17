
// Data provide between client-side and server-side
function dataService() {

    var self = {};

    // Get correct Server URL like http://localhost:8080/it4bi-ufrt-ir-project/
     self.serverURL = window.location.href;
    // self.serverURL = "http://localhost:8080/it4bi-ufrt-ir-project/";

    // Get all registered USERS
    self.getUsers = function (callback) {
        var URL = self.serverURL + "/rest/info/users";

        $.get(URL, function (data) {

            // Map received fields to expected fields
            users = $.map(data, function (d) {
                return new userInfo({
                    id: d.ID,
                    name: d.name,
                    surname: d.surname,
                    country: d.country,
                    sex: d.sex,
                    birthday: d.birthday
                });
            });

            // Return users back to the caller
            callback(users);
        });
    };

    // Perform search on DOCUMENTS
    self.searchDOC = function (query, userID, callback) {
        // Stamp is added to avoid caching
        var url = self.serverURL + "/rest/search/doc?q=" + query + "&u=" + userID + "&stamp=" + new Date().getTime();
        $.get(url, function (data) {
            // Map received fields to expected fields
            docs = $.map(data, function (d) {
                return new docInfo({
                    docID: d.docId,
                    docTitle: d.docTitle,
                    tags: d.tags,
                    mime: d.mime,
                    isOwner: d.owned,
                    isLiked: d.liked
                });
            });

            // Return results back to the caller
            callback(docs);
        });
    };

    // LIKE the document
    self.likeDOC = function (docID, userID, callback) {
        // Stamp is added to avoid caching
        var url = self.serverURL + "/rest/upload/like?docID=" + docID + "&userID=" + userID + "&stamp=" + new Date().getTime();
        $.get(url, function (data) {
            if (callback != undefined) {
                callback(data);
            }
        });
    };

    // Get the URL for document downloading
    self.getDownloadDocBaseURL = function () {
        var url = self.serverURL + "/rest/upload/get/";
        return url;
    };

    // Perform search on WEB
    self.searchWEB = function (query, userID, callback, source) {
        var url = self.serverURL + "/rest/search/social?q=" + query + "&u=" + userID + "&source=" + source;

        $.get(url, function (data) {

            // Map received fields to expected fields
            docs = $.map(data, function (d) {
                return new webInfo({
                    id: d.id,
                    title: d.title,
                    description: d.description,
                    link: d.link,
                    timestamp: d.timestamp,
                    user: d.user,
                    userlink: d.userlink,
                    source: d.source,
                    type: d.type,
                    sentiment: d.sentiment
                });
            });

            // Return results back to the caller
            callback(docs);
        });
    };

    // Perform search on WEB FACEBOOK
    self.searchWEBFacebook = function (query, userID, callback) {
        self.searchWEB(query, userID, callback, 0);
    };

    // Perform search on WEB TWITTER
    self.searchWEBTwitter = function (query, userID, callback) {
        self.searchWEB(query, userID, callback, 1);
    };

    // Perform search on WEB Videos
    self.searchWEBVideos = function (query, userID, callback) {
        self.searchWEB(query, userID, callback, 2);
    };

    // Perform search on WEB News
    self.searchWEBNews = function (query, userID, callback) {
        self.searchWEB(query, userID, callback, 3);
    };

    // Get autocorrection for the query
    self.getAutocorrection = function (query, callback) {
        var URL = self.serverURL + "/rest/search/autocorrection?q=" + query + "&stamp=" + new Date().getTime();

        $.get(URL, function (data) {
            // Map received fields to expected fields
            correctionRes = new autocorrectionInfo({
                isCorrected: data.isCorrected,
                originalQuery: data.originalQuery,
                correctedQuery: data.correctedQuery,
                suggestions: data.suggestions
            });

            // Return results back to the caller
            callback(correctionRes);
        });
    };

    // Return random logo
    self.getRandomLogo = function () {
        logos = new Array(
            "IMG/Logo01.jpg",
            "IMG/Logo02.jpg",
            "IMG/Logo03.jpg",
            "IMG/Logo04.jpg",
            "IMG/Logo05.jpg",
            "IMG/Logo06.jpg",
            "IMG/Logo07.jpg",
            "IMG/Logo08.jpg",
            "IMG/Logo09.jpg",
            "IMG/Logo10.jpg",
            "IMG/Logo11.jpg",
            "IMG/Logo12.jpg",
            "IMG/Logo13.jpg",
            "IMG/Logo14.jpg",
            "IMG/Logo15.jpg",
            "IMG/LogoFootball01.png",
            "IMG/LogoFootball02.png");

        randIndex = Math.floor(Math.random() * logos.length);
        return logos[randIndex];
    };

    // Perform search on DATA WAREHOUSE
    self.searchDW = function (query, userID, callback) {
        var url = self.serverURL + "/rest/search/dwh?q=" + query + "&u=" + userID + "&stamp=" + new Date().getTime();

        $.get(url, function (data) {

            // Map received fields to expected fields
            entries = $.map(data.matched, function (d) {
                return new dwPreprocessInfo({
                    name: d.name,
                    originalResponse: d
                });
            });

            // Return results back to the caller
            callback(entries);
        });
    };

    // Execute DATA WAREHOUSE entry
    self.executeDWEntry = function (dwEntryInfo, callback, error) {
        var url = self.serverURL + "rest/dwh/execute";
        
        $.ajax({
            type: "POST",
            contentType: "application/json; charset=utf-8",
            url: url,
            data: JSON.stringify(dwEntryInfo.originalResponse),
            success: callback,
            error: error,
            dataType: "json"
        });
    };

    return self;
}
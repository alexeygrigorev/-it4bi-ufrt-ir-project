var dataServiceProvider = dataService();

/* TODO: IMPLEMENT CHANGE OF SERVER URL FROM DEBUG INFORMATION */
/* TODO: REMOVE SLEEPING */
/* TODO: RECOMMENDATIONS */
/* TODO: SHOW TAGS */
/* TODO: SEARCH BY DW */

//Here's a custom Knockout binding that makes elements shown/hidden via jQuery's methods
ko.bindingHandlers.fadeVisible = {
    init: function (element, valueAccessor) {
        // Initially set the element to be instantly visible/hidden depending on the value
        var value = valueAccessor();
        $(element).toggle(ko.unwrap(value));
    },
    update: function (element, valueAccessor) {
        var value = valueAccessor();
        ko.unwrap(value) ? $(element).fadeIn() : $(element).hide();
    }
};

function searchEngineViewModel() {

    var self = {};

    // User Interface
    self.logo = ko.observable('IMG/LogoFootball02.png');
    self.mode = ko.observable('Search');
    self.showDebug = ko.observable('No');
    self.backgroundColor = ko.observable('white');
    self.showSearchResults = ko.observable(false);
    self.searchDWinProgress = ko.observable(true);
    self.searchDWEntryinProgress = ko.observable(true);
    self.searchDOCinProgress = ko.observable(true);
    self.searchDOCRecomendinProgress = ko.observable(true);
    self.searchWEBFacebookinProgress = ko.observable(true);
    self.searchWEBTwitterinProgress = ko.observable(true);
    self.searchWEBNewsinProgress = ko.observable(true);
    self.searchWEBVideosinProgress = ko.observable(true);
    self.displayLimit = 5;
    // Features
    self.users = ko.observableArray([]);
    self.loggedUser = ko.observable();
    self.uploadDocumentTitle = ko.observable('');
    // Searching
    self.serverURL = ko.observable('');
    self.searchQuery = ko.observable('');
    self.searchQueryAutocorrectedSelect = ko.observable();
    self.searchQueryAutocorrected = ko.observable('');
    self.searchDocs = ko.observable(true);
    self.searchDW = ko.observable(true);
    self.searchWEB = ko.observable(true);
    self.searchWEBFacebook = ko.observable(true);
    self.searchWEBTwitter = ko.observable(false);
    self.searchWEBNews = ko.observable(false);
    self.searchWEBVideos = ko.observable(false);
    self.resultsDOC = ko.observableArray([]);
    self.resultsDOCRecommendations = ko.observableArray([]);
    self.resultsDW = ko.observableArray([]);
    self.resultsDWEntry = ko.observableArray();
    self.resultsWEBFacebook = ko.observableArray([]);
    self.resultsWEBTwitter = ko.observableArray([]);
    self.resultsWEBNews = ko.observableArray([]);
    self.resultsWEBVideos = ko.observableArray([]);

    // Return results depending on the chosen WEB search subsection
    self.getWEBresults = function () {
        if (self.searchWEBFacebook()) return self.resultsWEBFacebook;
        if (self.searchWEBTwitter()) return self.resultsWEBTwitter;
        if (self.searchWEBNews()) return self.resultsWEBNews;
        if (self.searchWEBVideos()) return self.resultsWEBVideos;
    };

    // Return progress of the search on the chosen WEB search subsection
    self.getWEBInProgress = function () {
        if (self.searchWEBFacebook()) return self.searchWEBFacebookinProgress;
        if (self.searchWEBTwitter()) return self.searchWEBTwitterinProgress;
        if (self.searchWEBNews()) return self.searchWEBNewsinProgress;
        if (self.searchWEBVideos()) return self.searchWEBVideosinProgress;
    };

    self.initialize = function () {

        // Setting up correct Server URL from data provider
        self.serverURL(dataServiceProvider.serverURL);

        // Get list of registered users
        dataServiceProvider.getUsers(function (users) {
            // Need to insert objects into 'ko.observableArray' and not to substitute the array
            $.each(users, function (i, u) {
                self.users.push(u);
            });
        });
    };

    // On user chane reset all data
    self.userChanged = function () {
        self.showSearchResults(false);
        self.resultsDOC([]);
        self.resultsDOCRecommendations([]);
        self.resultsDW([]);
        self.resultsWEBFacebook([]);
        self.resultsWEBTwitter([]);
        self.resultsWEBNews([]);
        self.resultsWEBVideos([]);
    };

    // Toggle displaying of debug information
    self.toggleDebug = function () {
        switch (self.showDebug()) {
            case 'No': self.showDebug('Yes'); break;
            default: self.showDebug('No'); break;
        }
    };

    // Toggle change of background color from Debug Information
    ko.computed(function () {
        $(document.body).css("background-color", self.backgroundColor());
    });

    // Initialize logo with random image
    self.initializeLogo = function () {
        logoSrc = dataServiceProvider.getRandomLogo();
        self.logo(logoSrc);
    };

    // Show profile of logged-in user
    self.showMyProfile = function () {
        self.mode('MyProfile');
    };

    // Show documents uploaded by logged-in user
    self.showMyFiles = function () {
        self.mode('MyFiles');
    };

    // Show page to upload document by logged-in user
    self.showUploadFile = function () {
        if (self.mode() != 'UploadFile') {
            self.mode('UploadFile');
            self.uploadDocumentTitle('');

            // Bind file uploader only once
            $("#fileUploader").uploadFile({
                url: self.serverURL() + "/rest/upload/doc",
                autoSubmit: true,
                multiple: false,
                showDone: true,
                showStatusAfterSuccess: true,
                fileCounterStyle: ") ",
                dragDropStr: "<span><b>Drag &amp; Drop a Single File Here</b></span>",
                dynamicFormData: function () {
                    return {
                        userID: self.loggedUser() ? self.loggedUser().id : -1,
                        docTitle: self.uploadDocumentTitle()
                    };
                },
                onSelect: function (files) {
                    if (self.uploadDocumentTitle() == '') {
                        self.uploadDocumentTitle(files[0].name);
                    }
                },
                onSuccess: function () {
                    self.uploadDocumentTitle('');
                }
            });
        }
    };

    // Show search page
    self.showSearchPage = function () {
        self.mode('Search');
    };

    // Show DW executed results
    self.showDWResultsPage = function () {
        self.mode('DW Result');
    };

    // Show only results from search by ALL SOURCES
    self.showALLResultsOnly = function () {
        // Yeah, this way :)
        self.searchDocs(false);
        self.searchWEB(false);
        self.searchDW(false);
        self.searchDocs(true);
        self.searchWEB(true);
        self.searchDW(true);
    };

    // Show only results from search by DOCUMENTS
    self.showDOCResultsOnly = function () {
        self.searchDocs(false);
        self.searchDW(false);
        self.searchWEB(false);
        self.searchDocs(true);
    };

    // Show only results from search by DW
    self.showDWResultsOnly = function () {
        self.searchDocs(false);
        self.searchDW(false);
        self.searchWEB(false);
        self.searchDW(true);
    };

    // Show only results from search by WEB
    self.showWEBResultsOnly = function () {
        self.searchDocs(false);
        self.searchDW(false);
        self.searchWEB(false);
        self.searchWEB(true);
    };

    // Show only results from search by WEB Facebook
    self.showWEBFacebookResults = function () {
        self.searchWEBFacebook(true);
        self.searchWEBTwitter(false);
        self.searchWEBNews(false);
        self.searchWEBVideos(false);
    };

    // Show only results from search by WEB Facebook
    self.showWEBTwitterResults = function () {
        self.searchWEBTwitter(true);
        self.searchWEBFacebook(false);
        self.searchWEBNews(false);
        self.searchWEBVideos(false);
    };

    // Show only results from search by WEB News
    self.showWEBNewsResults = function () {
        self.searchWEBNews(true);
        self.searchWEBTwitter(false);
        self.searchWEBFacebook(false);
        self.searchWEBVideos(false);
    };

    // Show only results from search by WEB Videos
    self.showWEBVideosResults = function () {
        self.searchWEBVideos(true);
        self.searchWEBNews(false);
        self.searchWEBTwitter(false);
        self.searchWEBFacebook(false);
    };

    // True if search by ALL group is selected
    self.isALLSearch = ko.computed(function () {
        if (self.searchDW() == true && self.searchWEB() == true && self.searchDocs() == true) {
            return true;
        }
        return false;
    });

    // True if search by DOC group is selected
    self.isDOCSearch = ko.computed(function () {
        if (self.searchDW() == false && self.searchWEB() == false && self.searchDocs() == true) {
            return true;
        }
        return false;
    });

    // True if search by DW group is selected
    self.isDWSearch = ko.computed(function () {
        if (self.searchDW() == true && self.searchWEB() == false && self.searchDocs() == false) {
            return true;
        }
        return false;
    });

    // True if search by WEB group is selected
    self.isWEBSearch = ko.computed(function () {
        if (self.searchDW() == false && self.searchWEB() == true && self.searchDocs() == false) {
            return true;
        }
        return false;
    });

    // Search by EVERYTHING
    self.performSearch = function () {
        userID = self.loggedUser().id;
        query = self.searchQuery();
        if (query == '') {
            return;
        }

        self.hideAUTOCORRECTION();
        self.showSearchResults(false);
        self.showSearchResults(true);
        self.performSearchAUTOCORRECTION(query);

        self.performSearchDOC(query, userID);
        self.performSearchDW(query, userID);
        self.performSearchWEBFacebook(query, userID);
        self.performSearchWEBTwitter(query, userID);
        self.performSearchWEBNews(query, userID);
        self.performSearchWEBVideos(query, userID);
    };

    // Perform query from autocorrection region
    self.performSearchFromAutocorrection = function () {
        self.searchQuery(self.searchQueryAutocorrectedSelect());
        self.performSearch();
    };

    // Search for AUTOCORRECTIONS
    self.performSearchAUTOCORRECTION = function (query) {
        dataServiceProvider.getAutocorrection(query, function (correctionRes) {
            self.searchQueryAutocorrectedSelect(correctionRes.correctedQuery);
            self.searchQueryAutocorrected(correctionRes);
        });
    };

    // Hide AUTOCORRECTION results
    self.hideAUTOCORRECTION = function (query) {
        self.searchQueryAutocorrectedSelect('');
        self.searchQueryAutocorrected('');
    };

    // Search by DOCUMENTS by given user and RECOMMENDATIONS
    self.performSearchDOC = function (query, userID) {
        self.resultsDOC.removeAll();
        self.resultsDOCRecommendations.removeAll();

        self.searchDOCinProgress(true);
        dataServiceProvider.searchDOC(query, userID, function (documents) {
            // Need to insert objects into 'ko.observableArray' and not to substitute the array
            $.each(documents, function (i, d) {
                self.resultsDOC.push(d);
            });
            self.searchDOCinProgress(false);
        });

        self.searchDOCRecomendinProgress(true);
        dataServiceProvider.getDOCRecommendations(userID, function (documents) {
            // Need to insert objects into 'ko.observableArray' and not to substitute the array

            $.each(documents, function (i, d) {
                // To avoid duplicates
                var match = ko.utils.arrayFirst(self.resultsDOCRecommendations(), function (item) {
                    return d.docID === item.docID;
                });

                if (!match) {
                    self.resultsDOCRecommendations.push(d);
                }
            });
            self.searchDOCRecomendinProgress(false);
        });
    };

    // Search by DATA WAREHOUSE by given user
    self.performSearchDW = function (query, userID) {
        self.resultsDW.removeAll();
        self.searchDWinProgress(true);

        dataServiceProvider.searchDW(query, userID, function (entries) {
            // Need to insert objects into 'ko.observableArray' and not to substitute the array
            $.each(entries, function (i, d) {
                self.resultsDW.push(d);
            });
            self.searchDWinProgress(false);
        });
    };

    // Search by WEB Facebook by given user
    self.performSearchWEBFacebook = function (query, userID) {
        self.resultsWEBFacebook.removeAll();
        self.searchWEBFacebookinProgress(true);

        dataServiceProvider.searchWEBFacebook(query, userID, function (documents) {
            // Need to insert objects into 'ko.observableArray' and not to substitute the array
            $.each(documents, function (i, d) {
                // Filter empty elements
                if (d.title != '' || d.description != '') {
                    self.resultsWEBFacebook.push(d);
                }
            });

            self.searchWEBFacebookinProgress(false);
        });
    };

    // Search by WEB Twitter by given user
    self.performSearchWEBTwitter = function (query, userID) {
        self.resultsWEBTwitter.removeAll();
        self.searchWEBTwitterinProgress(true);

        dataServiceProvider.searchWEBTwitter(query, userID, function (documents) {
            // Need to insert objects into 'ko.observableArray' and not to substitute the array
            $.each(documents, function (i, d) {
                // Filter empty elements
                if (d.title != '' || d.description != '') {
                    self.resultsWEBTwitter.push(d);
                }
            });

            self.searchWEBTwitterinProgress(false);
        });
    };

    // Search by WEB News by given user
    self.performSearchWEBNews = function (query, userID) {
        self.resultsWEBNews.removeAll();
        self.searchWEBNewsinProgress(true);

        dataServiceProvider.searchWEBNews(query, userID, function (documents) {
            // Need to insert objects into 'ko.observableArray' and not to substitute the array
            $.each(documents, function (i, d) {
                // Filter empty elements
                if (d.title != '' || d.description != '') {
                    self.resultsWEBNews.push(d);
                }
            });

            self.searchWEBNewsinProgress(false);
        });
    };

    // Search by WEB Videos by given user
    self.performSearchWEBVideos = function (query, userID) {
        self.resultsWEBVideos.removeAll();
        self.searchWEBVideosinProgress(true);

        dataServiceProvider.searchWEBVideos(query, userID, function (documents) {
            // Need to insert objects into 'ko.observableArray' and not to substitute the array
            $.each(documents, function (i, d) {
                // Filter empty elements
                if (d.title != '' || d.description != '') {
                    self.resultsWEBVideos.push(d);
                }
            });

            self.searchWEBVideosinProgress(false);
        });
    };

    self.WEBPositiveScore = ko.computed(function () {
        var results = self.getWEBresults()();
        var positive = 0;

        $.each(results, function (i, d) {
            positive = positive + d.isPositive();
        });

        return positive;
    });

    self.WEBNegativeScore = ko.computed(function () {
        var results = self.getWEBresults()();
        var negative = 0;

        $.each(results, function (i, d) {
            negative = negative + d.isNegative();
        });

        return negative;
    });

    self.WEBNeutralScore = ko.computed(function () {
        var results = self.getWEBresults()();
        var neutral = 0;

        $.each(results, function (i, d) {
            neutral = neutral + d.isNeutral();
        });

        return neutral;
    });

    // Like the document defined by 'this'. Knockout logic
    self.likeDoc = function () {
        dataServiceProvider.likeDOC(this.docID, self.loggedUser().id)
        this.isLiked(true);
    };

    // Download the document
    self.downloadDoc = function () {
        var baseURL = dataServiceProvider.getDownloadDocBaseURL();
        var URL = baseURL + this.docID + this.getMimeShort();
        var win = window.open(URL, '_blank');
        win.focus();
    };

    // Execute DW Entry
    self.executeDWEntry = function () {
        self.showDWResultsPage();

        self.searchDWEntryinProgress(true);
        dataServiceProvider.executeDWEntry(this,
            /* Success */
            function (res) {

                executedEntry = new dwExecutedEntryInfo({
                    queryName: res.queryName,
                    columnNames: res.columnNames,
                    rows: res.rows,
                    rowsDelimeted: res.rows2
                });

                self.resultsDWEntry(executedEntry);
                self.searchDWEntryinProgress(false);
            },
            /* Error */
            function () {
                self.searchDWEntryinProgress(false);
                alert('DW Search Error');
            });
    };

    return self;
}
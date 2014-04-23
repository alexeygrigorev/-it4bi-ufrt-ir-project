var dataServiceProvider = dataService();

/* TODO: IMPLEMENT CHANGE OF SERVER URL FROM DEBUG INFORMATION */
/* TODO: REMOVE SLEEPING */

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
    self.searchDOCinProgress = ko.observable(true);
    self.searchWEBinProgress = ko.observable(true);
    self.displayLimit = 5;
    // Features
    self.users = ko.observableArray([]);
    self.loggedUser = ko.observable();
    self.uploadDocumentTitle = ko.observable('');
    // Searching
    self.serverURL = ko.observable('');
    self.searchQuery = ko.observable('');
    self.searchDocs = ko.observable(true);
    self.searchWEB = ko.observable(true);
    self.searchDW = ko.observable(true);
    self.resultsDOC = ko.observableArray([]);
    self.resultsWEB = ko.observableArray([]);
    self.resultsDW = ko.observableArray([]);

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

        self.showSearchResults(true);

        self.performSearchDOC(query, userID);
        self.performSearchDW(query, userID);
        self.performSearchWEB(query, userID);
    };

    // Search by DOCUMENTS by given user
    self.performSearchDOC = function (query, userID) {
        self.resultsDOC.removeAll();
        self.searchDOCinProgress(true);
        dataServiceProvider.searchDOC(query, userID, function (documents) {
            // Need to insert objects into 'ko.observableArray' and not to substitute the array
            $.each(documents, function (i, d) {
                self.resultsDOC.push(d);
            });
            self.searchDOCinProgress(false);
        });
    };

    // Search by DATA WAREHOUSE by given user
    self.performSearchDW = function (query, userID) {
        self.resultsDW.removeAll();
        self.searchDWinProgress(true);

        setTimeout(function () {
            self.searchDWinProgress(false);
        }, 2000);
    };

    // Search by WEB by given user
    self.performSearchWEB = function (query, userID) {
        self.resultsWEB.removeAll();
        self.searchWEBinProgress(true);

        dataServiceProvider.searchWEB(query, userID, function (documents) {
            // Need to insert objects into 'ko.observableArray' and not to substitute the array
            $.each(documents, function (i, d) {
                self.resultsWEB.push(d);
            });
            self.searchWEBinProgress(false);
        });
    };

    return self;
}
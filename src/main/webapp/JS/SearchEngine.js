var dataServiceProvider = dataService();

/* TODO: IMPLEMENT CHANGE OF SERVER URL FROM DEBUG INFORMATION */
/* TODO: REMOVE SLEEPING */

function searchEngineViewModel() {

    var self = {};

    // User Interface
    self.logo = ko.observable('IMG/LogoFootball02.png');
    self.mode = ko.observable('Search');
    self.showDebug = ko.observable('No');
    self.showSearchResults = ko.observable('No');
    self.backgroundColor = ko.observable('white');
    // Features
    self.users = ko.observableArray([]);
    self.loggedUser = ko.observable();
    self.uploadDocumentTitle = ko.observable('');
    // Searching
    self.serverURL = ko.observable('');
    self.searchQuery = ko.observable('');
    self.searchDocs = ko.observable('Yes');
    self.searchWEB = ko.observable('Yes');
    self.searchDW = ko.observable('Yes');
    self.resultsDOC = ko.observableArray([]);

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
                    }
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

    // Search by EVERYTHING
    self.performSearch = function () {
        userID = self.loggedUser().id;
        query = self.searchQuery();
        self.showSearchResults('Yes');

        self.searchDOC(query, userID);
    };

    // Search by DOCUMENTS by given user
    self.searchDOC = function (query, userID) {
        dataServiceProvider.searchDOC(query, userID, function (documents) {
            self.resultsDOC.removeAll();
            // Need to insert objects into 'ko.observableArray' and not to substitute the array
            $.each(documents, function (i, d) {
                self.resultsDOC.push(d);
            });
        });
    };

    return self;
}
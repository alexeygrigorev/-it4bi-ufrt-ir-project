var dataServiceProvider = dataService();

function searchEngineViewModel() {

    var self = {};

    self.mode = ko.observable('Search');
    self.logo = ko.observable();
    self.loggedUser = ko.observable();
    self.searchQuery = ko.observable('');
    self.uploadDocumentTitle = ko.observable('');
    self.users = ko.observableArray([]);
    self.resultsDOC = ko.observableArray([]);

    self.initialize = function () {

        // Initialize logo with random image
        self.initializeLogo();

        // Get list of registered users
        dataServiceProvider.getUsers(function (users) {
            // Need to insert objects into 'ko.observableArray' and not to substitute the array
            $.each(users, function (i, u) {
                self.users.push(u);
            });
        });
    };

    // Initialize logo with random image
    self.initializeLogo = function () {
        logoSrc = dataServiceProvider.getLogo();
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
                url: "http://localhost:8080/it4bi-ufrt-ir-project/rest/upload/doc",
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
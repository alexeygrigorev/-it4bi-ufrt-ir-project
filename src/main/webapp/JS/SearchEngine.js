var dataServiceProvider = dataService();

function searchEngineViewModel() {

    var self = {};
    self.description = "Hello, I am Brain";

    self.users = ko.observableArray([]);
    self.loggedUser = ko.observable();
    self.resultsDOC = ko.observableArray([]);

    self.initialize = function () {
        // Get list of rigestered users
        dataServiceProvider.getUsers(function (users) {
            // Need to insert objects into 'ko.observableArray' and not to substitute the array
            $.each(users, function (i, u) {
                self.users.push(u);
            });
        });
    };

    // Search by EVERYTHING
    self.performSearch = function () {
        userID = self.loggedUser().id;

        self.searchDOC("SOME QUERY", userID);
    };

    // Search by DOCUMENTS for given user
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
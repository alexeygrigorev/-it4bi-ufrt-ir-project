
// Data provide between client-side and server-side
function dataService() {

    var self = {};

    // Get all registered USERS
    self.getUsers = function (callback) {
        var URL = "http://localhost:8080/it4bi-ufrt-ir-project/rest/info/users";

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
    }
    
    // Perform search on DOCUMENTS
    self.searchDOC = function (query, userID, callback) {
        var URL = "http://localhost:8080/it4bi-ufrt-ir-project/rest/search/doc?q=" + query + "&u=" + userID;
        
        $.get(URL, function (data) {
            // Return results back to the caller
            callback(data);
        });
    }

    return self;
}
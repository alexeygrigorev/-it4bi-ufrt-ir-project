
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

    // Return random logo
    self.getLogo = function () {
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
            "IMG/Logo15.jpg");

        randIndex = Math.floor(Math.random() * logos.length);
        return logos[randIndex];
    };

    return self;
}
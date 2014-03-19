
function dataService() {
    var self = {};
    
    self.searchDoc = function (query, callback) {
        var URL = "http://localhost:8080/it4bi-ufrt-ir-project/rest/search/doc?q=" + query;
        
        $.get(URL, function (data) {

            // Transform the data
            //$.each(documents, function (i, d) {
            //    alert(d.docName);
            //});
            callback(data);
        });
    }

    return self;
}
var dataServiceProvider = dataService();

function searchEngineViewModel() {

    var self = {};
    self.description = "Hello, I am Brain";

    self.resultsDoc = ko.observableArray([]);

    // Search by documents.
    self.searchDoc = function (query) {
        dataServiceProvider.searchDoc(query, function (documents) {            
            $.each(documents, function (i, d) {
                self.resultsDoc.push(d);
            });
        });        
    };        

    return self;
}
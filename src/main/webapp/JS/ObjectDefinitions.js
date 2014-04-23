
// USER
function userInfo(spec) {
    var self = {};

    self.id = spec.id;
    self.name = spec.name;
    self.surname = spec.surname;
    self.country = spec.country;
    self.sex = spec.sex;
    self.birthday = spec.birthday;

    self.userFullName = function () {
        return self.name + ' ' + self.surname;
    };

    self.sexFormatted = function () {
        switch (self.sex) {
            case 'MALE': return 'Male'; break;
            case 'FEMALE': return 'Female'; break;
        }
    };

    return self;
}

// DOCUMENT
function docInfo(spec) {
    var self = {};

    self.docID = spec.docID;
    self.docPath = spec.docPath;
    self.docTitle = spec.docTitle;
    self.docExtension = spec.docExtension;
    self.ownerID = spec.ownerID;
    self.score = spec.score;
    self.isLiked = spec.isLiked;

    return self;
}

// WEB SOCIAL MENTION
function webInfo(spec) {
    var self = {};

    self.id = spec.id;
    self.title = spec.title;
    self.description = spec.description;
    self.link = spec.link;
    self.timestamp = spec.timestamp;
    self.user = spec.user;
    self.userlink = spec.userlink;
    self.source = spec.source;
    self.type = spec.type;
    self.socialSource = spec.socialSource;
    self.isExpanded = ko.observable(false);

    self.getTitle = function () {
        var titleModified = self.title.replace("в", "");
        titleModified = titleModified.replace("вЂњ", "");
        titleModified = titleModified.replace("вЂ", "");
        titleModified = titleModified.replace("в", "");
        titleModified = titleModified.replace("Ђ", "");
        titleModified = titleModified.replace("Ђ", "");
        titleModified = titleModified.replace("в", "");
        titleModified = titleModified.replace("Ђ", "");
        titleModified = titleModified.replace("њ", "");
        titleModified = titleModified.replace("ќ", "");
        titleModified = titleModified.replace("�", "");        

        if (titleModified != '') {
            return titleModified + ' ' + self.description;
        }

        titleModified = self.description;
        if (titleModified == '') {
            titleModified = 'No title';
        }
        return titleModified;
    };

    self.getSource = function () {
        switch (self.source) {
            case 'facebook':
            case 'FACEBOOK': return 'Facebook'; break;
            default: return self.source; break;
        }
    };

    self.getType = function () {
        return self.type;
    };

    self.getIconURL = function () {
        switch (self.socialSource) {
            case 'facebook':
            case 'FACEBOOK': return 'IMG/facebook.jpg'; break;
            default: return 'IMG/question.jpg'; break;
        }
    };

    self.getTime = function () {
        var date = new Date(self.timestamp * 1000);

        formattedTime = '';
        // Calculate date parts and replace instances in format string accordingly
        formattedTime += (date.getDate() < 10 ? '0' : '') + date.getDate();        // Pad with '0' if needed
        formattedTime += '-';
        formattedTime += (date.getMonth() < 9 ? '0' : '') + (date.getMonth() + 1); // Months are zero-based
        formattedTime += '-';
        formattedTime += date.getFullYear();
        formattedTime += ' ';
        formattedTime += (date.getHours() < 10 ? '0' : '') + date.getHours();
        formattedTime += ':';
        formattedTime += (date.getMinutes() < 10 ? '0' : '') + date.getMinutes();
        formattedTime += ':';
        formattedTime += (date.getSeconds() < 10 ? '0' : '') + date.getSeconds();
        return formattedTime;
    };

    self.togleExpand = function () {
        self.isExpanded(self.isExpanded() ? false : true);
    };

    return self;
}
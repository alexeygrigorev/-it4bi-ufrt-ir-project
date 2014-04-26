function replaceAll(find, replace, str) {
    return str.replace(new RegExp(find, 'g'), replace);
};

function filterNoENGText(text) {
    var allowed = "0123456789 qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM+=_-)(*&^%$#@!{}:|<>?,./;'\[]";
    var newText = '';

    for (var i = 0; i < text.length; i++) {
        var char = text[i];
        if (allowed.indexOf(char) != -1) {
            newText = newText + char;
        }
    }
    return newText;
};

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
    self.docTitle = spec.docTitle;
    self.mime = spec.mime;
    self.tags = spec.tags;
    self.isOwner = spec.isOwner;
    self.isLiked = ko.observable(spec.isLiked);

    self.getMime = function () {
        switch (self.mime) {
            case 'application/pdf': return 'application/pdf'; break;
            case 'application/vnd.openxmlformats-officedocument.wordprocessingml.document': return 'application/docx'; break;
            case 'text/plain; charset=windows-1252': return 'text/plain'; break;
            default: return self.mime; break;
        }
    }

    self.getMimeShort = function () {
        switch (self.mime) {
            case 'application/pdf': return '.pdf'; break;
            case 'application/vnd.openxmlformats-officedocument.wordprocessingml.document': return '.docx'; break;
            case 'text/plain; charset=windows-1252': return '.txt'; break;
            default: return ''; break;
        }
    }

    return self;
}

// WEB SOCIAL MENTION
function webInfo(spec) {
    var self = {};

    self.id = spec.id;
    self.title = filterNoENGText(spec.title);
    self.description = filterNoENGText(spec.description);
    self.link = spec.link;
    self.timestamp = spec.timestamp;
    self.user = filterNoENGText(spec.user);
    self.userlink = spec.userlink;
    self.source = spec.source;
    self.type = spec.type;
    self.sentiment = spec.sentiment;
    self.isExpanded = ko.observable(false);    

    self.isPositive = function () {
        switch (self.sentiment) {
            case 'Positive': return 1; break;
            default: return 0; break;
        }
    };

    self.isNegative = function () {
        switch (self.sentiment) {
            case 'Negative': return 1; break;
            default: return 0; break;
        }
    };

    self.isNeutral = function () {
        if (self.isPositive() != 1 && self.isNegative() != 1) {
            return 1;
        }
        return 0;
    };

    self.getTitle = function () {
        if (self.title != '') {
            return self.title + ' ' + self.description;
        }

        if (self.description != '') {
            return self.description;            
        }
        return 'No title';
    };

    self.getSource = function () {
        switch (self.source) {
            case 'facebook':
            case 'FACEBOOK': return 'Facebook'; break;
            case 'twitter': return 'Twitter '; break;
            case 'dailymotion': return 'Dailymotion'; break;
            case 'break': return 'Break'; break;
            case 'google_news': return 'Google News'; break;
            case 'youtube': return 'Youtube'; break;
            case 'metacafe': return 'Metacafe'; break;
            case 'Yahoo News': return 'Yahoo News'; break;
            default: return self.source; break;
        }
    };

    self.getType = function () {
        return self.type;
    };

    self.getIconURL = function () {
        switch (self.source) {
            case 'facebook':
            case 'FACEBOOK': return 'IMG/Facebook.png'; break;
            case 'twitter': return 'IMG/Twitter.png'; break;
            case 'dailymotion': return 'IMG/Dailymotion.png'; break;
            case 'break': return 'IMG/Break.png'; break;
            case 'google_news': return 'IMG/GoogleNews.png'; break;
            case 'youtube': return 'IMG/Youtube.png'; break;
            case 'metacafe': return 'IMG/Metacafe.png'; break;
            case 'Yahoo News': return 'IMG/YahooNews.png'; break;                
            default: return 'IMG/question.png'; break;
        }
    };

    self.getSmileIconURL = function () {
        switch (self.sentiment) {
            case 'Positive': return 'IMG/Positive.png'; break;
            case 'Negative': return 'IMG/Negative.png'; break;
            default: return 'IMG/Neutral.png'; break;
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

// AUTOCORRECTION RESULT
function autocorrectionInfo(spec) {
    var self = {};

    self.isCorrected = spec.isCorrected;
    self.originalQuery = spec.originalQuery;
    self.correctedQuery = spec.correctedQuery;
    self.suggestions = spec.suggestions;

    return self;
}
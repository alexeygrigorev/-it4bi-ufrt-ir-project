
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
    }

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
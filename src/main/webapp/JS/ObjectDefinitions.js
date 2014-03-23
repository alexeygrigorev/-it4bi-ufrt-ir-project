
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
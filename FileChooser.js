function FileChooser() {}

FileChooser.prototype.open =
    function (success, failure) {
        cordova.exec(success, failure, "FileChooser", "open", []);
    };

var fileChooser = new FileChooser();
module.exports = fileChooser;

var exec = require('cordova/exec');

exports.coolMethod = function (arg0, success, error) {
    exec(success, error, 'videocallOutsystems', 'coolMethod', [arg0]);
};

exports.call = function (arg0, success, error) {
    exec(success, error, 'videocallOutsystems', 'videoCall', [arg0]);
};

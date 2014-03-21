var exec = require("cordova/exec");
var PushManager = function () {};

PushManager.prototype.initWork = function(callback,username,serverurl) {    
    //alert("push initwork");
    exec(null, callback, "PushManager", "initWork",  [username,serverurl]);
};

PushManager.prototype.stopWork = function() {    
    //alert("push stopWork");
    exec(null, null, "PushManager", "stopWork",  []);
};

PushManager.prototype.setTags=function(tags,succallback,failcallback) {
    //alert("push setTags");
    exec(succallback, failcallback, 'PushManager', 'setTags', [tags]);
};

PushManager.prototype.delTags = function(tags,callback) {
    //alert("push delTags");
    exec(null, callback, "PushManager", "delTags",  [tags]);
};
module.exports = new PushManager();

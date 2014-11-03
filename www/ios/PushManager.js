var exec = require("cordova/exec");
var PushManager = function () {};

PushManager.prototype.initWork = function(succcb,failcb,username,serverurl) {
    //alert("push initwork");
    exec(succcb, failcb, "PushManager", "initWork",  [username,serverurl]);
};

PushManager.prototype.stopWork = function() {
    //alert("push stopWork");
    exec(null, null, "PushManager", "stopWork",  []);
};

PushManager.prototype.setTags=function(succallback,failcallback,tags) {
    //alert("push setTags");
    exec(succallback, failcallback, 'PushManager', 'setTags', [tags]);
};

PushManager.prototype.delTags = function(delSucc,delFail,tags) {
    //alert("push delTags");
    exec(delSucc, delFail, "PushManager", "delTags",  [tags]);
};

PushManager.prototype.listTags = function(succallback) {

        exec(succallback, null, "PushManager", "listTags",  []);
};

module.exports = new PushManager();

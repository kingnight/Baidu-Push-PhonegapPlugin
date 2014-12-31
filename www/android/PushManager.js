        var exec = require("cordova/exec");
	var PushManager = function () {};
	
	PushManager.prototype.initWork = function(successCallback,errorCallback,username,severurl) {
		exec(successCallback, errorCallback, "PushManager", "initWork",  [username,severurl]);
	};
	PushManager.prototype.stopWork = function() {
		exec(null, null, "PushManager", "stopWork",  []);
	};
	PushManager.prototype.resumeWork = function() {
		exec(null, null, "PushManager", "resumeWork",  []);
	};
	PushManager.prototype.isPushEnabled = function(callback) {
		exec(callback, null, "PushManager", "isPushEnabled",  []);
	};
	PushManager.prototype.setTags=function(succallback,failcallback,tags) {
		exec(succallback, failcallback, 'PushManager', 'setTags', [tags]);
	};
	PushManager.prototype.delTags = function(SuccCallBack,Failcallback,tags) {
		exec(SuccCallBack, Failcallback, "PushManager", "delTags",  [tags]);
	};
	PushManager.prototype.listTags = function(callback) {
		exec(callback, null, "PushManager", "listTags",  []);
	};
	PushManager.prototype.getTagInfo = function(callback) {
		exec(callback, null, "PushManager", "getTagInfo",  []);
	};
	
	module.exports = new PushManager();

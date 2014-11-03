cordova.define("com.aisino.plugin.push.PushManager", function(require, exports, module) { var exec = require("cordova/exec");
	var PushManager = function () {};
	
	PushManager.prototype.initWork = function(successCallback,errorCallback,username,severurl) {
		cordova.exec(successCallback, errorCallback, "PushManager", "initWork",  [username,severurl]);
	};
	PushManager.prototype.stopWork = function() {
		cordova.exec(null, null, "PushManager", "stopWork",  []);
	};
	PushManager.prototype.resumeWork = function() {
		cordova.exec(null, null, "PushManager", "resumeWork",  []);
	};
	PushManager.prototype.isPushEnabled = function(callback) {
		cordova.exec(callback, null, "PushManager", "isPushEnabled",  []);
	};
	PushManager.prototype.setTags=function(succallback,failcallback,tags) {
		cordova.exec(succallback, failcallback, 'PushManager', 'setTags', [tags]);
	};
	PushManager.prototype.delTags = function(SuccCallBack,Failcallback,tags) {
		cordova.exec(SuccCallBack, Failcallback, "PushManager", "delTags",  [tags]);
	};
	PushManager.prototype.listTags = function(callback) {
		cordova.exec(callback, null, "PushManager", "listTags",  []);
	};
	PushManager.prototype.getTagInfo = function(callback) {
		cordova.exec(callback, null, "PushManager", "getTagInfo",  []);
	};
	
	module.exports = new PushManager();


});

var exec = require("cordova/exec");
	var PushManager = function () {};
	
	PushManager.prototype.initWork = function(successCallback,errorCallback,username,severurl) {
	
		
		//console.log("push initwork");
		cordova.exec(successCallback, errorCallback, "PushManager", "initWork",  [username,severurl]);
	};
	PushManager.prototype.stopWork = function() {
		
		//console.log("push stopWork");
		cordova.exec(null, null, "PushManager", "stopWork",  []);
	};
	PushManager.prototype.resumeWork = function() {
		
		//console.log("push resumeWork");
		cordova.exec(null, null, "PushManager", "resumeWork",  []);
	};
	PushManager.prototype.isPushEnabled = function(callback) {
		
		//console.log("push isPushEnabled");
		cordova.exec(callback, null, "PushManager", "isPushEnabled",  []);
	};
	
	PushManager.prototype.setTags=function(tags,succallback,failcallback) {
		//console.log("push setTags");
		cordova.exec(succallback, failcallback, 'PushManager', 'setTags', [tags]);
	};
	PushManager.prototype.delTags = function(tags,callback) {
		
		//console.log("push delTags");
		cordova.exec(null, callback, "PushManager", "delTags",  [tags]);
	};
	PushManager.prototype.listTags = function(callback) {
		
		//console.log("push listTags");
		cordova.exec(callback, null, "PushManager", "listTags",  []);
	};
	PushManager.prototype.getTagInfo = function(callback) {
		
		//console.log("push getTagInfo");
		cordova.exec(callback, null, "PushManager", "getTagInfo",  []);
	};
	
	module.exports = new PushManager();


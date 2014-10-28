/**
 * Created with JetBrains PhpStorm.
 * User: Naher
 * Date: 10/22/13
 * Time: 6:51 PM
 * To change this template use File | Settings | File Templates.
 */
 /*function Downloader() {}

 Downloader.prototype.downloadFile = function(fileUrl, params, win, fail) {
 //Make params hash optional.
 if (!fail) win = params;
 PhoneGap.exec(win, fail, "Downloader", "downloadFile", [fileUrl, params]);
 };

 PhoneGap.addConstructor(function() {
     PhoneGap.addPlugin("downloader", new Downloader());
     PluginManager.addService("Downloader", "com.phonegap.plugins.downloader.Downloader");
 });

 if(!window.plugins) {
    window.plugins = {};
 }
 if (!window.plugins.downloader) {
    window.plugins.downloader = new Downloader();
 }*/
cordova.define("cordova/plugin/downloader", function (require, exports, module) {
    var exec = require("cordova/exec");
    module.exports = {
        get: function (message, win, fail) {
            exec(win, fail, "Downloader", "get", [message]);
        }
    };
});

var errorpath =''

function download(queue) {
    var fileTransfer = new FileTransfer();
    var downloader = cordova.require("cordova/plugin/downloader");
    downloader.get({list:queue},
        function() {
            console.log("PhoneGap Plugin: Downloader: callback success");
        },
        function() {            
            console.log("PhoneGap Plugin: Downloader: callback error for ");            
        }
    );
}


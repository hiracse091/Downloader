/**
 * Created with JetBrains PhpStorm.
 * User: Naher
 * Date: 10/22/13
 * Time: 6:51 PM
 * To change this template use File | Settings | File Templates.
 */
 
 var downloader = {
    get: function(parmas,successCallback, errorCallback) {
        cordova.exec(
            successCallback, // success callback function
            errorCallback, // error callback function
            'Downloader', // mapped to our native Java class called "BMSHAHashing"
            'get', // with this action name
            [parmas]
        ); 
     }
}
module.exports = hashcreator;





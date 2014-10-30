package org.apache.cordova.downloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.dcastalia.hecontent.utils.BMCommon;
import com.dcastalia.main.R;
public class Downloader extends CordovaPlugin {
 
 @Override
 public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) {
  if (action.equals("get")) {
   cordova.getThreadPool().execute(new Runnable() {
    public void run() {
     try {
      JSONObject params = args.getJSONObject(0);
      //String fileUrl=params.getString("url");
     // Boolean overwrite=params.getBoolean("overwrite");      
      JSONArray listA = params.getJSONArray("list");
      //showToast("Downloader list : Length :"+listA.length(),"short");
      for (int i=0; i<listA.length(); i++) {
    	  String imageURL = listA.getString(i);
    	  if(!(imageURL.equals("") || imageURL.equals("null")))
    	   {
    	    String fileName = imageURL.substring(imageURL.lastIndexOf('/')+1, imageURL.length());
    	    File directory = new File(Environment.getExternalStorageDirectory(),BMCommon.DEFAULT_PATH);
    	    directory.mkdirs();
    	    File file = new File(directory,fileName);
    	    if(!file.exists())
    	    {
    	     if(!(BMCommon.heDownloadList.contains(imageURL)))
    	     {
    	    	 BMCommon.heDownloadList.add(imageURL);
    	     }
    	    }
    	      
    	   }
    	 // BMCommon.heDownloadList.add(imageURL);
    	}
      Intent DownloadIntent = new Intent("com.dcastalia.hefbusinessmanager.sync.download");
      Context context = cordova.getActivity().getApplicationContext();
      context.sendBroadcast(DownloadIntent);
      
      
     } catch (JSONException e) {
      e.printStackTrace();
     // Log.e("PhoneGapLog", "Downloader Plugin: Error: " + PluginResult.Status.JSON_EXCEPTION);
      callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.JSON_EXCEPTION));
     }/* catch (InterruptedException e) {
      e.printStackTrace();
      Log.e("PhoneGapLog", "Downloader Plugin: Error: " + PluginResult.Status.ERROR);
      callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR));
     }*/
    }
   });
   return true;
  } else {
  // Log.e("PhoneGapLog", "Downloader Plugin: Error: " + PluginResult.Status.INVALID_ACTION);
   callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.INVALID_ACTION));
   return false;
  }
 }
 
 private Boolean downloadUrl(String fileUrl, String dirName,
   String fileName, Boolean overwrite, CallbackContext callbackContext)
     throws InterruptedException, JSONException {
  try {
   File dir = new File(dirName);
   if (!dir.exists()) {
    dir.mkdirs();
   }
   File file = new File(dirName, fileName);
   if (overwrite == true || !file.exists()) {
    Intent intent = new Intent ();
    intent.addFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
    PendingIntent pend = PendingIntent.getActivity(cordova.getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
          NotificationManager mNotifyManager = (NotificationManager) cordova.getActivity().getSystemService(Activity.NOTIFICATION_SERVICE);
    NotificationCompat.Builder mBuilder =
         new NotificationCompat.Builder(cordova.getActivity())
         .setSmallIcon(R.drawable.ic_stat_notification)
         .setContentTitle(cordova.getActivity().getString(R.string.app_name))
         .setContentText("File: " + fileName + " - 0%");
    int mNotificationId = new Random().nextInt(10000);
    URL url = new URL(fileUrl);
    HttpURLConnection ucon = (HttpURLConnection) url.openConnection();
    ucon.setRequestMethod("GET");
    ucon.connect();
    
    InputStream is = ucon.getInputStream();
    
    //long lastModified = ucon.getLastModified();    //lastModified()    getHeaderField("Last-Modified");
   // Log.e("PhoneGapLog", "Downloader Plugin: File last modified: "+new Date(ucon.getLastModified()));
    
    byte[] buffer = new byte[1024];
    int readed = 0, progress = 0, totalReaded = 0, fileSize = ucon.getContentLength();
    FileOutputStream fos = new FileOutputStream(file);
    showToast("Download started.","short");
    int step = 0;
    while ((readed = is.read(buffer)) > 0) {
     fos.write(buffer, 0, readed);
     totalReaded += readed;
     int newProgress = (int) (totalReaded*100/fileSize);
     if (newProgress != progress & newProgress > step) {
      mBuilder.setProgress(100, newProgress, false);
      mBuilder.setContentText("File: " + fileName + " - " + step + "%");
      mBuilder.setContentIntent(pend);
      mNotifyManager.notify(mNotificationId, mBuilder.build());
      step = step + 1;
     }
    }
    fos.flush();
    fos.close();
    is.close();
    ucon.disconnect();
    mBuilder.setContentText("Download of \"" + fileName + "\" complete").setProgress(0,0,false);
             mNotifyManager.notify(mNotificationId, mBuilder.build());
             try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                // Log.d("PhoneGapLog", "Downloader Plugin: Thread sleep error: " + e);
                }
             mNotifyManager.cancel(mNotificationId);
    showToast("Download finished.","short");
   } else if (overwrite == false) {
    showToast("File is already downloaded.","short");
   }
   if(!file.exists()) {
    showToast("Download went wrong, please try again or contact the developer.","long");
   // Log.e("PhoneGapLog", "Downloader Plugin: Error: Download went wrong.");
   }
   callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK));
   return true;
  } catch (FileNotFoundException e) {
   showToast("File does not exists or cannot connect to webserver, please try again or contact the developer.","long");
  // Log.e("PhoneGapLog", "Downloader Plugin: Error: " + PluginResult.Status.ERROR);
   e.printStackTrace();
   callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR));
   return false;
  } catch (IOException e) {
   showToast("Error downloading file, please try again or contact the developer.","short");
   //Log.e("PhoneGapLog", "Downloader Plugin: Error: " + PluginResult.Status.ERROR);
   e.printStackTrace();
   callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR));
   return false;
  }
 }
 
 private void showToast(final String message, final String duration) {
  cordova.getActivity().runOnUiThread(new Runnable() {
   public void run() {
    Toast toast;
    if(duration.equals("long")) {
     toast = Toast.makeText(cordova.getActivity(), message, Toast.LENGTH_LONG);
    } else {
     toast = Toast.makeText(cordova.getActivity(), message, Toast.LENGTH_SHORT);
    }
    toast.show();
   }
  });
 }
}

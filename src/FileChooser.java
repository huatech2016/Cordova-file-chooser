package com.sentinel.filechooser;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import com.sentinel.filechooser.FileUtils;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class FileChooser extends CordovaPlugin {

	private static final String TAG = "FileChooser";
	private static final String ACTION_OPEN = "open";
	private static final int PICK_FILE_REQUEST = 1;
	CallbackContext callback;

	@Override
	public boolean execute(String action, CordovaArgs args,
			CallbackContext callbackContext) throws JSONException {

		if (action.equals(ACTION_OPEN)) {
			chooseFile(callbackContext);
			return true;
		}

		return false;
	}

	public void chooseFile(CallbackContext callbackContext) {
// type and title should be configurable
//("application/*")含义是包含但不仅限于常见的文档。包含office 文档，pdf 文档 ，不包含图片，基本够用。暂未找到更好的解决办法
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("application/*");

		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);

		Intent chooser = Intent.createChooser(intent, "Select File");
		cordova.startActivityForResult(this, chooser, PICK_FILE_REQUEST);

		PluginResult pluginResult = new PluginResult(
				PluginResult.Status.NO_RESULT);
		pluginResult.setKeepCallback(true);
		callback = callbackContext;
		callbackContext.sendPluginResult(pluginResult);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == PICK_FILE_REQUEST && callback != null) {

			if (resultCode == Activity.RESULT_OK) {

				Uri uri = data.getData();
				if (uri != null) {
					Log.i(TAG, ""+uri.toString());
					String path = FileUtils.getPath(cordova.getActivity(), uri);
					JSONArray jsonArray = new JSONArray();
					jsonArray.put(path);
					Log.i(TAG, ""+path);
					callback.success(jsonArray);
				} else {
					callback.error("File uri was null");
				}

			} else if (resultCode == Activity.RESULT_CANCELED) {

				// TODO NO_RESULT or error callback?
				PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
				callback.sendPluginResult(pluginResult);
			} else {
				callback.error(resultCode);
			}
		}
	}

}

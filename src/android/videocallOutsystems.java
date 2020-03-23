package cordovaPluginVideocallOutsystems;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.*;

import android.content.Context;
import android.content.Intent;
import android.view.Display;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaInterface;

/**
 * This class echoes a string called from JavaScript.
 */
public class videocallOutsystems extends CordovaPlugin {
	private static final String LOG_TAG = "videocallOutsystems";

	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("coolMethod")) {
            String message = args.getString(0);
            this.coolMethod(message, callbackContext);
            return true;
        } else if (action.equals("videoCall")) {
			String sip = args.getString(0);
			Context context = cordova.getActivity().getApplicationContext();
			cordova.getThreadPool().execute(new Runnable() {
				@Override
                public void run() {
                    openNewActivity(context, sip);
                }
            });
			return true;
		}
        return false;
    }

    private void coolMethod(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
	}
	
	private void openNewActivity(Context context, String sip) {
		LOG.d(LOG_TAG, "[ PASSING SIP TO NEW ACTIVITY ] : " + sip);
		Intent intent = new Intent(context, NewActivity.class);
		intent.putExtra("SIP", sip);
        this.cordova.getActivity().startActivity(intent);
    }
}

package cordovaPluginVideocallOutsystems;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.*;

import android.content.Context;
import android.content.Intent;
import android.view.Display;

import com.alicecallsbob.fcsdk.android.phone.AudioDeviceManager;
import com.alicecallsbob.fcsdk.android.phone.CallCreationWithErrorException;
import com.alicecallsbob.fcsdk.android.phone.CallListener;
import com.alicecallsbob.fcsdk.android.phone.CallStatus;
import com.alicecallsbob.fcsdk.android.phone.CallStatusInfo;
import com.alicecallsbob.fcsdk.android.phone.Phone;
import com.alicecallsbob.fcsdk.android.phone.PhoneListener;
import com.alicecallsbob.fcsdk.android.phone.PhoneVideoCaptureResolution;
import com.alicecallsbob.fcsdk.android.phone.PhoneVideoCaptureSetting;
import com.alicecallsbob.fcsdk.android.phone.VideoSurface;
import com.alicecallsbob.fcsdk.android.phone.VideoSurfaceListener;
import com.alicecallsbob.fcsdk.android.uc.UC;
import com.alicecallsbob.fcsdk.android.uc.UCFactory;
import com.alicecallsbob.fcsdk.android.uc.UCListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaInterface;

/**
 * This class echoes a string called from JavaScript.
 */
public class videocallOutsystems extends CordovaPlugin implements UCListener, PhoneListener, VideoSurfaceListener, CallListener {
	private static final String LOG_TAG = "videocallOutsystems";
	com.alicecallsbob.fcsdk.android.phone.Call call1;
	UC mUC;
    Phone mPhone;
	VideoSurface mPreviewView, videoSurface;

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
			String sessionId = args.getString(0);
			//String divId = args.getString(1);
			Context context = cordova.getActivity().getApplicationContext();
			this.openNewActivity(context);
			// this.call("nrr%3A%21%21sFJrGFo.IHJ.Hv.BG%3Acggh%21DJqFnJl%21nFIrvHzFqHJyy%3FJuuzFl%3D5N4N-HbFgijcE-IeEf-ggIa-IJcf-bdGjajJGgddg", callbackContext);
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
	
	private void openNewActivity(Context context) {
        Intent intent = new Intent(context, NewActivity.class);
        this.cordova.getActivity().startActivity(intent);
    }
	
/* 	private createUCCor (Context context, String session) {
		ServerMessageTransport transport = new ServerMessageTransport();
		ServerMessageManager serverMessageManager = new ServerMessageManager(transport, session);
		transport.setListener((ServerMessageTransportListener)serverMessageManager);
		return (UC) new UCImplCor(serverMessageManager, context, stunServers);
	}
	private UCImplCor (ServerMessageManager serverMessageManager, Context context, List<String> stunServers) {
		super(serverMessageManager);
		Log.d("UCImplCor", "Constructor");
		setupHandlersCor();
		this.mServerMessageManager = serverMessageManager;
		this.mApplicationContext = context;
	}
	private void setupHandlers() {
		InitialisationSuccessHandler successHandler = new InitialisationSuccessHandler(this);
		InitialisationFailureHandler failureHandler = new InitialisationFailureHandler(this);
		SystemFailureHandler systemFailureHandler = new SystemFailureHandler(this);
		GenericErrorHandler genericErrorHandler = new GenericErrorHandler(this);
		addHandler("INITIALISATION_SUCCESS", (ServerMessageHandler)successHandler);
		addHandler("INITIALISATION_ERROR", (ServerMessageHandler)failureHandler);
		addHandler("SYSTEM_FAILURE", (ServerMessageHandler)systemFailureHandler);
		addHandler("GENERIC_ERROR", (ServerMessageHandler)genericErrorHandler);
  } */
	
	private void call(String sessionID, CallbackContext callbackContext) {
		
		LOG.d(LOG_TAG, "[1] : INIT CALL");
		
 		Context context = this.cordova.getActivity().getApplicationContext(); 
		mUC = UCFactory.createUc(context, //context
				sessionID, //session token obtained from //Session Token REST API.
				videocallOutsystems.this); //listener 
		mUC.setNetworkReachable(true);
		mUC.startSession();

		LOG.d(LOG_TAG, "[1] : mUC DONE");

		mPhone = mUC.getPhone();
		mPhone.addListener(videocallOutsystems.this);
		//set size, resolution, framerate
		mPhone.setPreferredCaptureResolution(PhoneVideoCaptureResolution.RESOLUTION_640x480);
		mPhone.setPreferredCaptureFrameRate(30);

		LOG.d(LOG_TAG, "[2] : mPhone DONE");
	
		//remote
		// Display display = getWindowManager().getDefaultDisplay();
		// Point size = new Point();
		// display.getSize(size);
		// int width = size.x;
		// int height = size.y;
		// final Point remoteSize = new Point(width,height);
		// remoteSize.x *= 0.9f;
		// remoteSize.y = (int) (remoteSize.x * 0.7f);
		// videoSurface = mPhone.createVideoSurface(getApplicationContext(), remoteSize, this.cordova.getActivity());
		// RelativeLayout.LayoutParams remoteLp = new RelativeLayout.LayoutParams(
		// 		RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		// videoSurface.setLayoutParams(remoteLp);

		// LOG.d(LOG_TAG, "[3] : display DONE");
	
		//local
		/*final Point previewSize = new Point(remoteSize.x/3,remoteSize.y/3);
		previewSize.x *= 0.9f;
		previewSize.y = (int) (previewSize.x * 0.7f);
		mPreviewView = mPhone.createVideoSurface(getApplicationContext(), previewSize, this.cordova.getActivity());
		//The preview view should be on top
		mPreviewView.setZOrderOnTop(true);
		RelativeLayout.LayoutParams localLp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		mPreviewView.setLayoutParams(localLp);
	
	
		mLocalVideoContainer.addView(mPreviewView);
		mPreviewView.setFocusable(true);
		mLocalVideoContainer.setVisibility(View.VISIBLE);
		callLayout.setVisibility(View.VISIBLE);
		mVideoContainer.setVisibility(View.VISIBLE);
		mVideoContainer.addView(videoSurface);
	
	
		setSpeakerphoneOn(true);
		togglePreview.setTag("ON");
		togglePreview.setImageResource(android.R.drawable.button_onoff_indicator_on);
		toggleSpeaker.setTag("ON");
		toggleSpeaker.setImageResource(android.R.drawable.button_onoff_indicator_on);
		toggleCamera.setTag("OFF");
		toggleCamera.setImageResource(android.R.drawable.button_onoff_indicator_off);
		toggleMuteAudio.setTag("OFF");
		toggleMuteAudio.setImageResource(android.R.drawable.button_onoff_indicator_off);
		toggleMuteVideo.setTag("OFF");
		toggleMuteVideo.setImageResource(android.R.drawable.button_onoff_indicator_off);
		toggleHold.setTag("OFF");
		toggleHold.setImageResource(android.R.drawable.button_onoff_indicator_off);
		mIsSpeakerActive = true;
		mIsPreviewActive = true;
	
	
		String agent = "sip:7700@10.20.234.90"; //parameterized
		//make the call
		try {
			call1 = mPhone.createCall(agent,
					true, true, // audio and video enabled
					this.cordova.getActivity()); // CallListener
		} catch (CallCreationWithErrorException e) {
			e.printStackTrace();
		}*/
	}
    

    @Override
    public void onStatusChanged(final com.alicecallsbob.fcsdk.android.phone.Call call, final CallStatus status) {
        //
    }

    @Override
    public void onStatusChanged(com.alicecallsbob.fcsdk.android.phone.Call call, CallStatusInfo callStatusInfo) {
        onStatusChanged(call, callStatusInfo.getCallStatus());
    }

    @Override
    public void onIncomingCall(com.alicecallsbob.fcsdk.android.phone.Call call) {
        // 
    }

    @Override
    public void onDialFailed(com.alicecallsbob.fcsdk.android.phone.Call call, String s, CallStatus callStatus) {

    }

    @Override
    public void onCallFailed(com.alicecallsbob.fcsdk.android.phone.Call call, String s, CallStatus callStatus) {

    }

    @Override
    public void onMediaChangeRequested(com.alicecallsbob.fcsdk.android.phone.Call call, boolean b, boolean b1) {

    }


    @Override
    public void onRemoteDisplayNameChanged(com.alicecallsbob.fcsdk.android.phone.Call call, String s) {

    }

    @Override
    public void onRemoteMediaStream(com.alicecallsbob.fcsdk.android.phone.Call call) {

    }

    @Override
    public void onInboundQualityChanged(com.alicecallsbob.fcsdk.android.phone.Call call, final int quality) {
    }

    @Override
    public void onRemoteHeld(com.alicecallsbob.fcsdk.android.phone.Call call) {

    }

    @Override
    public void onRemoteUnheld(com.alicecallsbob.fcsdk.android.phone.Call call) {

    }

    @Override
    public void onCaptureSettingChange(PhoneVideoCaptureSetting phoneVideoCaptureSetting, int i) {

    }

    @Override
    public void onLocalMediaStream() {

    }

    @Override
    public void onFrameSizeChanged(int i, int i1, VideoSurface.Endpoint endpoint, VideoSurface videoSurface) {

    }

    @Override
    public void onSurfaceRenderingStarted(VideoSurface videoSurface) {
    }

    @Override
    public void onSessionStarted() {

    }

    @Override
    public void onSessionNotStarted() {

    }

    @Override
    public void onSystemFailure() {
    }

    @Override
    public void onConnectionLost() {
    }

    @Override
    public void onConnectionRetry(int i, long l) {

    }

    @Override
    public void onConnectionReestablished() {

    }

    @Override
    public void onGenericError(String s, String s1) {

    }
	
}

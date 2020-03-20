package cordovaPluginVideocallOutsystems;

import android.app.Activity;
import android.os.Bundle;
import org.apache.cordova.*;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.graphics.Point;
import android.widget.RelativeLayout;
import android.widget.ImageView;
import android.view.View;
import android.media.AudioManager;

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

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import com.codebutler.android_websockets.WebSocketClient;

public class NewActivity extends Activity implements UCListener, PhoneListener, VideoSurfaceListener, CallListener {

    private static final String LOG_TAG = "videocallOutsystems";
    public static final String SeasonID = "nrr%3A%21%21sFJrGFo.IHJ.Hv.BG%3Acggh%21DJqFnJl%21nFIrvHzFqHJyy%3FJuuzFl%3D5N4N-dEceeief-ggjd-gEba-ciad-efafgfjEffHj";
    public static final String AgentID = "sip:7700@10.20.234.90";

    com.alicecallsbob.fcsdk.android.phone.Call call1;
	UC mUC;
    Phone mPhone;
	VideoSurface mPreviewView, videoSurface;
    RelativeLayout mVideoContainer, callLayout, mLocalVideoContainer;
    AudioManager mAudioManager;
    ImageView togglePreview, toggleSpeaker, toggleCamera, toggleMuteAudio, toggleMuteVideo, toggleHold, signal;
    private boolean mIsSpeakerActive;
    private boolean mIsPreviewActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        String package_name = getApplication().getPackageName();
        setContentView(getApplication().getResources().getIdentifier("activity_new", "layout", package_name));

        mVideoContainer =  (RelativeLayout) this.findViewById(getApplication().getResources().getIdentifier("videoFrame", "id", package_name));

        callLayout =  (RelativeLayout) this.findViewById(getApplication().getResources().getIdentifier("callLayout", "id", package_name));

        mLocalVideoContainer =  (RelativeLayout) this.findViewById(getApplication().getResources().getIdentifier("localFrame", "id", package_name));

        togglePreview = (ImageView) this.findViewById(getApplication().getResources().getIdentifier("previewToggleImage", "id", package_name));

        toggleSpeaker = (ImageView) this.findViewById(getApplication().getResources().getIdentifier("speakerToggleImage", "id", package_name));

        toggleCamera= (ImageView) this.findViewById(getApplication().getResources().getIdentifier("cameraToggleImage", "id", package_name));

        toggleMuteAudio = (ImageView) this.findViewById(getApplication().getResources().getIdentifier("muteToggleImage", "id", package_name));

        toggleMuteVideo = (ImageView) this.findViewById(getApplication().getResources().getIdentifier("videoToggleImage", "id", package_name));

        toggleHold = (ImageView) this.findViewById(getApplication().getResources().getIdentifier("holdToggleImage", "id", package_name));

        this.call(SeasonID);
    }

    @Override
    public void onBackPressed() {
        //
    }

    @Override
    protected  void onResume() {
        super.onResume();
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

    private void call(String sessionID) {
		
		LOG.d(LOG_TAG, "[1] : INIT CALL");
		
		mUC = UCFactory.createUc(getApplicationContext(), //context
				sessionID, //session token obtained from //Session Token REST API.
				NewActivity.this); //listener 
		
      
        try {
            // Create a trust manager that does not validate certificate chains
            TrustManager trustAllCerts = new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            };

            // // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("TLS"); // Add in try catch block if you get error.
            sc.init(null, new TrustManager[] { trustAllCerts }, new java.security.SecureRandom()); // Add in try catch block if you get error.
            // mUC.setDefaultSSLSocketFactory(sc.getSocketFactory());

            mUC.setTrustManager(trustAllCerts);

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            // Install the all-trusting host verifier
            mUC.setHostnameVerifier(allHostsValid);
        }catch(Exception e){
            e.printStackTrace();
        }
      
        mUC.setNetworkReachable(true);
        mUC.startSession();
		
        LOG.d(LOG_TAG, "[1] : mUC DONE");

		mPhone = mUC.getPhone();
		mPhone.addListener(NewActivity.this);
		//set size, resolution, framerate
		mPhone.setPreferredCaptureResolution(PhoneVideoCaptureResolution.RESOLUTION_640x480);
		mPhone.setPreferredCaptureFrameRate(30);

		LOG.d(LOG_TAG, "[2] : mPhone DONE");
	
		// //remote
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int height = size.y;
		final Point remoteSize = new Point(width,height);
		remoteSize.x *= 0.9f;
		remoteSize.y = (int) (remoteSize.x * 0.7f);
		videoSurface = mPhone.createVideoSurface(getApplicationContext(), remoteSize, NewActivity.this);
		RelativeLayout.LayoutParams remoteLp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		videoSurface.setLayoutParams(remoteLp);

		LOG.d(LOG_TAG, "[3] : display DONE");
	
		// //local
		final Point previewSize = new Point(remoteSize.x/3,remoteSize.y/3);
		previewSize.x *= 0.9f;
		previewSize.y = (int) (previewSize.x * 0.7f);
		mPreviewView = mPhone.createVideoSurface(getApplicationContext(), previewSize, NewActivity.this);
		//The preview view should be on top
		mPreviewView.setZOrderOnTop(true);
		RelativeLayout.LayoutParams localLp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        mPreviewView.setLayoutParams(localLp);
        
        LOG.d(LOG_TAG, "[4] : previewSize DONE");
	
	
        mLocalVideoContainer.addView(mPreviewView);
        LOG.d(LOG_TAG, "[5] : mLocalVideoContainer DONE");
		mPreviewView.setFocusable(true);
		mLocalVideoContainer.setVisibility(0);
		callLayout.setVisibility(0);
		mVideoContainer.setVisibility(0);
        mVideoContainer.addView(videoSurface);
        
        LOG.d(LOG_TAG, "[6] : mVideoContainer DONE");
	
	
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
        
        LOG.d(LOG_TAG, "[6] : togglePreview DONE");
	
	
		String agent = AgentID; //parameterized
		//make the call
		try {
			call1 = mPhone.createCall(agent,
					true, true, // audio and video enabled
					NewActivity.this); // CallListener
		} catch (CallCreationWithErrorException e) {
			e.printStackTrace();
		}
    }
    
    protected void setSpeakerphoneOn(final boolean on) {
        if (mAudioManager != null && mAudioManager.isSpeakerphoneOn() != on) {
            mAudioManager.setSpeakerphoneOn(on);
        }
    }
}
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
import android.widget.Toast;
import android.hardware.Camera;

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

import java.net.Socket;

import java.io.EOFException;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLSocketFactory;
import java.net.InetAddress;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import com.codebutler.android_websockets.WebSocketClient;

import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONObject;

import android.Manifest;
import android.support.v4.app.ActivityCompat;
import android.content.pm.PackageManager;
import android.view.ScaleGestureDetector;
import android.widget.ImageButton;
import android.widget.FrameLayout;
import android.view.MotionEvent;


public class NewActivity extends Activity implements UCListener, PhoneListener, VideoSurfaceListener, CallListener, View.OnTouchListener, View.OnClickListener {

    private static final String LOG_TAG = "videocallOutsystems";

    private static final int REQUEST_CAMERA = 0;

    private static final int REQUEST_MICROPHONE = 200;

    private String package_name = "";

    private String agent = "sip:7704@10.20.234.90"; //parameterized

    com.alicecallsbob.fcsdk.android.phone.Call call1;
	UC mUC;
    Phone mPhone;
	VideoSurface mPreviewView, videoSurface;
    RelativeLayout mVideoContainer, callLayout, mLocalVideoContainer;
    AudioManager mAudioManager;
    ImageView togglePreview, toggleSpeaker, toggleCamera, toggleMuteAudio, toggleMuteVideo, toggleHold, signal;
    private boolean mIsSpeakerActive;
    private boolean mIsPreviewActive;
    private boolean mIsFrontCameraActive;
    private ScaleGestureDetector mScaleGestureDetector;
    private boolean mScale = false;
    FrameLayout frameLayout;
    private boolean mIsVideoMuted = false;
    private boolean mIsAudioMuted = false;
    private boolean mHold = false;

    private void requestMicrophonePermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.RECORD_AUDIO)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            LOG.i(LOG_TAG,
                    "Displaying microphone permission rationale to provide additional context.");
            ActivityCompat.requestPermissions(NewActivity.this,
            new String[]{Manifest.permission.RECORD_AUDIO},
            REQUEST_MICROPHONE);
        } else {

            // Camera permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
            REQUEST_MICROPHONE);
        }
    }

    private void requestCameraPermission() {
        LOG.i(LOG_TAG, "CAMERA permission has NOT been granted. Requesting permission.");

        // BEGIN_INCLUDE(camera_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            LOG.i(LOG_TAG,
                    "Displaying camera permission rationale to provide additional context.");
            ActivityCompat.requestPermissions(NewActivity.this,
            new String[]{Manifest.permission.CAMERA},
            REQUEST_CAMERA);
        } else {

            // Camera permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA);
        }
        // END_INCLUDE(camera_permission_request)
    }

    private void initiateCall(){
        OkHttpClient client = new OkHttpClient().newBuilder()
        .build();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "assist=assist-1111111-T-1&reasin=http://10.20.219.168:8080&reaspub=reasdev.bca.co.id&serverport=8443&sip=test");
        Request request = new Request.Builder()
        .url("http://202.6.215.244:8080/remsessionglobal.php")
        .method("POST", body)
        .addHeader("Content-Type", "application/x-www-form-urlencoded")
        .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();

                LOG.d(LOG_TAG, "Call failure " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            jsonObject.getString("sessionid");
                            LOG.d(LOG_TAG, " [SESSION ID FROM BCA ] : " + jsonObject.getString("sessionid"));
                            
                            callAgent(jsonObject.getString("sessionid"));
                        }catch(Exception e){
                            LOG.d(LOG_TAG, "Exception " + e.getMessage());
                        }
                    }
                });
            }
        });
    }

    private void initializeStartup(){
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        LOG.d(LOG_TAG, "[ RECEIVING INTENT EXTRAS ] : " + extras.getString("SIP"));
        if(extras.getString("SIP") != null && !"".equals(extras.getString("SIP"))){
            agent = extras.getString("SIP");
            LOG.d(LOG_TAG, "[ USING PARAMETERIZE AGENT SIP ] : " + agent);
        }else{
            LOG.d(LOG_TAG, "[ USING DEFAULT AGENT SIP ] : " + agent);
        }

        package_name = getApplication().getPackageName();

        setContentView(getApplication().getResources().getIdentifier("activity_new", "layout", package_name));

        mVideoContainer =  (RelativeLayout) this.findViewById(getApplication().getResources().getIdentifier("videoFrame", "id", package_name));

        callLayout =  (RelativeLayout) this.findViewById(getApplication().getResources().getIdentifier("callLayout", "id", package_name));

        mLocalVideoContainer =  (RelativeLayout) this.findViewById(getApplication().getResources().getIdentifier("localFrame", "id", package_name));

        signal = (ImageView)findViewById(getApplication().getResources().getIdentifier("signal", "id", package_name));

        togglePreview = (ImageView) this.findViewById(getApplication().getResources().getIdentifier("previewToggleImage", "id", package_name));

        toggleSpeaker = (ImageView) this.findViewById(getApplication().getResources().getIdentifier("speakerToggleImage", "id", package_name));

        toggleCamera= (ImageView) this.findViewById(getApplication().getResources().getIdentifier("cameraToggleImage", "id", package_name));

        toggleMuteAudio = (ImageView) this.findViewById(getApplication().getResources().getIdentifier("muteToggleImage", "id", package_name));

        toggleMuteVideo = (ImageView) this.findViewById(getApplication().getResources().getIdentifier("videoToggleImage", "id", package_name));

        toggleHold = (ImageView) this.findViewById(getApplication().getResources().getIdentifier("holdToggleImage", "id", package_name));

        frameLayout =  (FrameLayout) this.findViewById(getApplication().getResources().getIdentifier("frameLayout", "id", package_name));
        mScaleGestureDetector = new ScaleGestureDetector(this, new MyScaleGestureListener());
        frameLayout.setOnTouchListener(this);

        ImageButton btnEndCall = (ImageButton) this.findViewById(getApplication().getResources().getIdentifier("btnEndCall", "id", package_name));
        View btnToggleMuteAudio = findViewById(getApplication().getResources().getIdentifier("switchMute", "id", package_name));
        View btnToggleMuteVideo = findViewById(getApplication().getResources().getIdentifier("switchMuteVideo", "id", package_name));
        View mSpeakerButton = findViewById(getApplication().getResources().getIdentifier("switchSpeaker", "id", package_name));
        View mHoldButton = findViewById(getApplication().getResources().getIdentifier("switchHold", "id", package_name));
        View mSwitchCamera = findViewById(getApplication().getResources().getIdentifier("switchCamera", "id", package_name));
        View mSwitchPreview = findViewById(getApplication().getResources().getIdentifier("switchPreview", "id", package_name));

        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        //set listener
        frameLayout.setOnTouchListener(this);
        btnEndCall.setOnClickListener(this);
        btnToggleMuteAudio.setOnClickListener(this);
        btnToggleMuteVideo.setOnClickListener(this);
        mSwitchCamera.setOnClickListener(this);
        mSwitchPreview.setOnClickListener(this);
        mSpeakerButton.setOnClickListener(this);
        mHoldButton.setOnClickListener(this);

        this.initiateCall();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        LOG.i(LOG_TAG, "CAMERA. Checking permission.");
        // BEGIN_INCLUDE(camera_permission)
        // Check if the Camera permission is already available.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Camera permission has not been granted.
            requestCameraPermission();
        } else {
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED){
                requestMicrophonePermission();
            }else{
                LOG.i(LOG_TAG,
                    "CAMERA and MICROPHONE permission has already been granted. Displaying camera preview.");
                this.initializeStartup();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(call1 != null){
            call1.end();
        }
        finish();
    }

    @Override
    protected  void onResume() {
        super.onResume();
        mIsVideoMuted = false;
        mIsAudioMuted = false;
        mIsSpeakerActive = true;
        mIsFrontCameraActive = true;
        mIsPreviewActive = true;
        mHold = false;
        mScale = false;
    }

    @Override
    public void onStatusChanged(final com.alicecallsbob.fcsdk.android.phone.Call call, final CallStatus status) {
        LOG.d(LOG_TAG, "[ onStatusChanged ] : " + status);
        switch (status) {
            case ENDED:
                terminate(call);
                break;
            case ERROR:
                terminate(call);
                break;
            case BUSY:
                terminate(call);
                break;
            case MEDIA_PENDING:
                terminate(call);
                break;
            case NOT_FOUND:
                terminate(call);
                break;
            case TIMED_OUT:
                terminate(call);
                break;
            case UNINITIALIZED:
                terminate(call);
                break;
            case REQUEST_TERMINATED:
                terminate(call);
                break;
        }
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
        LOG.d(LOG_TAG, "[ onDialFailed ] : " + callStatus);
    }

    @Override
    public void onCallFailed(com.alicecallsbob.fcsdk.android.phone.Call call, String s, CallStatus callStatus) {
        LOG.d(LOG_TAG, "[ onCallFailed ] : " + callStatus);
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(quality<20) signal.setImageResource(getApplication().getResources().getIdentifier("signal1", "drawable", package_name));
                else if(quality>=20&&quality<40)signal.setImageResource(getApplication().getResources().getIdentifier("signal2", "drawable", package_name));
                else if(quality>=40&&quality<60)signal.setImageResource(getApplication().getResources().getIdentifier("signal3", "drawable", package_name));
                else if(quality>=60&&quality<80)signal.setImageResource(getApplication().getResources().getIdentifier("signal4", "drawable", package_name));
                else if(quality>=80)signal.setImageResource(getApplication().getResources().getIdentifier("signal5", "drawable", package_name));
            }
        });
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
        if(videoSurface==this.videoSurface) {
            call1.setVideoView(videoSurface);
        }
        mPhone.setPreviewView(mPreviewView);
    }

    @Override
    public void onSessionStarted() {
        LOG.d(LOG_TAG, "[ onSessionStarted ]...");
    }

    @Override
    public void onSessionNotStarted() {
        LOG.d(LOG_TAG, "[ onSessionNotStarted ]...");
    }

    @Override
    public void onSystemFailure() {
        LOG.d(LOG_TAG, "[ onSystemFailure ]...");
        terminate(call1);
    }

    @Override
    public void onConnectionLost() {
        LOG.d(LOG_TAG, "[ onConnectionLost ]...");
        if(call1!=null){
            call1.end();
        }else terminate(call1);
    }

    @Override
    public void onConnectionRetry(int i, long l) {
        LOG.d(LOG_TAG, "[ onConnectionRetry ]...");
    }

    @Override
    public void onConnectionReestablished() {
        LOG.d(LOG_TAG, "[ onConnectionRetry ]...");
    }

    @Override
    public void onGenericError(String s, String s1) {
        LOG.d(LOG_TAG, "[ onGenericError ] : " + s + " # " + s1);
    }

    @Override
    public void onDestroy(){
        LOG.d(LOG_TAG, "[ onDestroy ]...");
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {

        final View view = v;

        ImageView toggleImage = null;
        if (v.getId() == getApplication().getResources().getIdentifier("btnEndCall", "id", package_name)) {
            if(call1 != null){
                call1.end();
            }
            finish();
        }
        else
        if (v.getId() == getApplication().getResources().getIdentifier("switchMute", "id", package_name)) {
            muteAudio(!mIsAudioMuted);
            toggleImage = (ImageView) view.findViewById(getApplication().getResources().getIdentifier("muteToggleImage", "id", package_name));
            final boolean isChecked = "ON".equals(toggleImage.getTag());
            final boolean check = !isChecked;
            if (check) {
                toggleImage.setTag("ON");
                toggleImage
                        .setImageResource(android.R.drawable.button_onoff_indicator_on);
            } else {
                toggleImage.setTag("OFF");
                toggleImage
                        .setImageResource(android.R.drawable.button_onoff_indicator_off);
            }
        }
        else
        if (v.getId() == getApplication().getResources().getIdentifier("switchMuteVideo", "id", package_name)) {
            muteVideo(!mIsVideoMuted);
            toggleImage = (ImageView) view.findViewById(getApplication().getResources().getIdentifier("videoToggleImage", "id", package_name));
            final boolean isChecked = "ON".equals(toggleImage.getTag());
            final boolean check = !isChecked;
            if (check) {
                toggleImage.setTag("ON");
                toggleImage
                        .setImageResource(android.R.drawable.button_onoff_indicator_on);
            } else {
                toggleImage.setTag("OFF");
                toggleImage
                        .setImageResource(android.R.drawable.button_onoff_indicator_off);
            }
        }
        else
        if (v.getId() == getApplication().getResources().getIdentifier("switchSpeaker", "id", package_name)) {
            enableSpeaker(!mIsSpeakerActive);
            toggleImage = (ImageView) view.findViewById(getApplication().getResources().getIdentifier("speakerToggleImage", "id", package_name));
            final boolean isChecked = "ON".equals(toggleImage.getTag());
            final boolean check = !isChecked;
            if (check) {
                toggleImage.setTag("ON");
                toggleImage
                        .setImageResource(android.R.drawable.button_onoff_indicator_on);
            } else {
                toggleImage.setTag("OFF");
                toggleImage
                        .setImageResource(android.R.drawable.button_onoff_indicator_off);
            }
        }
        else
        if (v.getId() == getApplication().getResources().getIdentifier("switchHold", "id", package_name)) {
            putOnHold(!mHold);
            toggleImage = (ImageView) view.findViewById(getApplication().getResources().getIdentifier("holdToggleImage", "id", package_name));
            final boolean isChecked = "ON".equals(toggleImage.getTag());
            final boolean check = !isChecked;
            if (check) {
                toggleImage.setTag("ON");
                toggleImage
                        .setImageResource(android.R.drawable.button_onoff_indicator_on);
            } else {
                toggleImage.setTag("OFF");
                toggleImage
                        .setImageResource(android.R.drawable.button_onoff_indicator_off);
            }
        }
        else
        if (v.getId() == getApplication().getResources().getIdentifier("switchCamera", "id", package_name)) {
            toggleCamera(!mIsPreviewActive);
            toggleImage = (ImageView) view.findViewById(getApplication().getResources().getIdentifier("cameraToggleImage", "id", package_name));
            final boolean isChecked = "ON".equals(toggleImage.getTag());
            final boolean check = !isChecked;
            if (check) {
                toggleImage.setTag("ON");
                toggleImage
                        .setImageResource(android.R.drawable.button_onoff_indicator_on);
            } else {
                toggleImage.setTag("OFF");
                toggleImage
                        .setImageResource(android.R.drawable.button_onoff_indicator_off);
            }
        }
        else
        if (v.getId() == getApplication().getResources().getIdentifier("switchPreview", "id", package_name)) {
            enablePreview(!mIsPreviewActive);
            toggleImage = (ImageView) view.findViewById(getApplication().getResources().getIdentifier("previewToggleImage", "id", package_name));
            final boolean isChecked = "ON".equals(toggleImage.getTag());
            final boolean check = !isChecked;
            if (check) {
                toggleImage.setTag("ON");
                toggleImage
                        .setImageResource(android.R.drawable.button_onoff_indicator_on);
            } else {
                toggleImage.setTag("OFF");
                toggleImage
                        .setImageResource(android.R.drawable.button_onoff_indicator_off);
            }
        }
    }

    protected void enableSpeaker(final boolean enable) {
        if (enable != mIsSpeakerActive) {
            mIsSpeakerActive = enable;
            setSpeakerphoneOn(enable ? true : false);
        }
    }
    protected void setSpeakerphoneOn(final boolean on) {
        LOG.d(LOG_TAG, "[ setSpeakerphoneOn ] : " + on);
        if (mAudioManager != null && mAudioManager.isSpeakerphoneOn() != on) {
            mAudioManager.setSpeakerphoneOn(on);
        }
    }

    protected void muteAudio(final boolean mute) {
        LOG.d(LOG_TAG, "[ muteAudio ] : " + mute);
        if (mute != mIsAudioMuted) {
            mIsAudioMuted = mute ? true : false;
            // if mute is checked we disable audio and vice versa
            mPhone.enableLocalAudio(mute ? false : true);
        }
    }

    protected void enablePreview(final boolean enable) {
        if (enable != mIsPreviewActive) {
            mIsPreviewActive = enable;
            mPreviewView.setVisibility(enable ? View.VISIBLE : View.INVISIBLE);
        }
    }

    protected void muteVideo(final boolean mute) {
        if (mute != mIsVideoMuted) {
            mIsVideoMuted = mute ? true : false;
            // if mute is checked we disable video and vice versa
            mPhone.enableLocalVideo(mute ? false : true);
        }
    }

    protected void putOnHold(final boolean onHold) {
        if (onHold!= mHold) {
            mHold = onHold ? true : false;
            if(onHold) call1.hold();
            else call1.resume();
        }
    }

    private void callAgent(String sessionID) {

        if(call1!=null){
            call1.end();
        }
		
        LOG.d(LOG_TAG, "[1] : INIT CALL");
        // LOG.d(LOG_TAG, "adsasdsa " + cordovaPluginVideocallOutsystems.NewActivity.R.id.switchMute);
		
		mUC = UCFactory.createUc(getApplicationContext(), //context
				sessionID, //session token obtained from //Session Token REST API.
				NewActivity.this); //listener 
		mUC.setNetworkReachable(true);
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
            // SSLContext sc = SSLContext.getInstance("TLS"); // Add in try catch block if you get error.
            // sc.init(null, new TrustManager[] { trustAllCerts }, new java.security.SecureRandom()); // Add in try catch block if you get error.
            // mUC.setDefaultSSLSocketFactory(sc.getSocketFactory());

            mUC.setTrustManager(trustAllCerts);

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            // Install the all-trusting host verifier
            mUC.setHostnameVerifier(allHostsValid);
        }catch(Exception e){
            e.printStackTrace();
        }
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
		// remoteSize.x *= 1.0f;
		remoteSize.y = (int) (remoteSize.y * 0.9f);
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
		mLocalVideoContainer.setVisibility(View.VISIBLE);
		callLayout.setVisibility(View.VISIBLE);
		mVideoContainer.setVisibility(View.VISIBLE);
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
        
        LOG.d(LOG_TAG, "[7] : togglePreview DONE");
		//make the call
		try {
			call1 = mPhone.createCall(agent,
					true, true, // audio and video enabled
					NewActivity.this); // CallListener
		} catch (CallCreationWithErrorException e) {
			e.printStackTrace();
        } 
    }

    protected void toggleCamera(final boolean toggledForward) {

        LOG.d(LOG_TAG, "toggleCamera() - toggledForward: " + toggledForward +
                " mIsFrontCameraActive: " + mIsFrontCameraActive);


        final int cameraSelected;
        if (toggledForward)
        {
            if (mIsFrontCameraActive)
            {
                LOG.d(LOG_TAG, "Camera choice toggled forward - currently using front camera - switching to rear");
                cameraSelected = Camera.CameraInfo.CAMERA_FACING_BACK;
                mIsFrontCameraActive = false;
            }
            else
            {
                LOG.d(LOG_TAG, "Camera choice toggled forward - currently using rear camera - switching to front");
                cameraSelected = Camera.CameraInfo.CAMERA_FACING_FRONT;
                mIsFrontCameraActive = true;
            }
        }
        else
        {
            if (mIsFrontCameraActive)
            {
                LOG.d(LOG_TAG, "Camera choice toggled backward - currently using front camera - switching to rear");
                cameraSelected = Camera.CameraInfo.CAMERA_FACING_BACK;
                mIsFrontCameraActive = false;
            }
            else
            {
                LOG.d(LOG_TAG, "Camera choice toggled backward - currently using rear camera - switching to front");
                cameraSelected = Camera.CameraInfo.CAMERA_FACING_FRONT;
                mIsFrontCameraActive = true;
            }
        }

        // tell SDK which camera to use
        setCameraToUse(cameraSelected);
        // Update the camera orientation
    }

    protected void setCameraToUse(final int cameraDirectionSelected) {

        LOG.d(LOG_TAG, "setCameraToUse() - cameraDirectionSelected: " + cameraDirectionSelected);

        /*
         * As per the android docs. if there is only 1 camera on the device, the idx is always n-1 where n is the number
         * of cameras. If there is only 1 camera, the camera idx is always 0 whether it is a FRONT or BACK facing.
         *
         * If there is more than 1 camera, we default to the first selected facing camera we can find.
         * If we cannot find a selected facing camera, we default to idx 0.
         */
        int cameraIdxToUse = 0; // default to 0
        final int numberOfCameras = Camera.getNumberOfCameras();
        if (numberOfCameras == 1)
        {
            // be safe
            LOG.d(LOG_TAG, "setCameraToUse() - Only 1 camera on device, defaulting to idx 0");
        }
        else
        {
            LOG.d(LOG_TAG, "setCameraToUse() - More than 1 camera on device, using the first camera facing the selected direction...");

            boolean cameraFound = false;
            for (int i = 0; i < numberOfCameras; i++)
            {
                final Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                Camera.getCameraInfo(i, cameraInfo);

                if (cameraInfo.facing == cameraDirectionSelected)
                {
                    cameraIdxToUse = i;
                    LOG.d(LOG_TAG, "Using camera at idx: " + i);
                    cameraFound = true;
                    break;
                }
            }

            if (!cameraFound)
            {
                LOG.w(LOG_TAG, "setCameraToUse() - Failed to find camera facing the direction selected - defaulting to idx 0");
            }
        }
        if(cameraIdxToUse>0) mPhone.setVideoOrientation(270); else mPhone.setVideoOrientation(90);
        mPhone.setCamera(cameraIdxToUse);
    }

    private class MyScaleGestureListener implements ScaleGestureDetector.OnScaleGestureListener {
        private int mW, mH;

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            // scale our video view
            mW *= detector.getScaleFactor();
            mH *= detector.getScaleFactor();
            if (mW < 100) { // limits width
                mW = videoSurface.getWidth();
                mH = videoSurface.getHeight();
            }
            LOG.d(LOG_TAG, "scale=" + detector.getScaleFactor() + ", w=" + mW + ", h=" + mH);
            videoSurface.getHolder().setFixedSize(mW, mH); // important
            mPreviewView.getHolder().setFixedSize(mW/3,mH/3);
            return true;
        }


        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            mW = videoSurface.getWidth();
            mH = videoSurface.getHeight();
            LOG.d(LOG_TAG, "scale=" + detector.getScaleFactor() + ", w=" + mW + ", h=" + mH);
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            LOG.d(LOG_TAG, "scale=" + detector.getScaleFactor() + ", w=" + mW + ", h=" + mH);
            mScale = true;
        }

    }

    float dX, dY;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mScaleGestureDetector.onTouchEvent(event);
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:

                dX = frameLayout.getX() - event.getRawX();
                dY = frameLayout.getY() - event.getRawY();
                mScale = false;
                break;

            case MotionEvent.ACTION_MOVE:
                if(!mScale) {
                    frameLayout.animate()
                            .x(event.getRawX() + dX)
                            .y(event.getRawY() + dY)
                            .setDuration(0)
                            .start();
                    break;
                }
            default:
                return false;
        }
        return  true;
    }

    public void terminate(final com.alicecallsbob.fcsdk.android.phone.Call call1){
        LOG.d(LOG_TAG, "[ terminate ] ... " + call1.getCallStatus());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                call1.removeListener(NewActivity.this);
                call1.setVideoView(null);
                mVideoContainer.setVisibility(View.GONE);
                mVideoContainer.removeAllViews();
                mLocalVideoContainer.setVisibility(View.GONE);
                mLocalVideoContainer.removeAllViews();
                callLayout.setVisibility(View.GONE);
                Toast.makeText(NewActivity.this, "Terminating Call.. No Answer with status : " + call1.getCallStatus(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        LOG.d(LOG_TAG, "grantResults " + grantResults.length + " - With requestCode : " + requestCode);
        switch (requestCode) {
            case 0: {
                if(ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED){
                    this.requestMicrophonePermission();
                }
            }
            case 200: {
                LOG.d(LOG_TAG, "MASUK GRANT 200");
                if(ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED){
                    this.initializeStartup();
                }
            }break;
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
package sc.skycommunicate;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.sip.SipAudioCall;
import android.net.sip.SipProfile;


public class IncomingCallReceiver extends BroadcastReceiver {

        private static final String TAG = "IncomingCallReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {

            SipAudioCall incomingCall = null;
            try {
                SipAudioCall.Listener listener = new SipAudioCall.Listener() {
                    @Override
                    public void onRinging(SipAudioCall call, SipProfile caller) {
                        try {
                            call.answerCall(30);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                MainActivityCall MyActivity = (MainActivityCall) context;
                incomingCall = MyActivity.manager.takeAudioCall(intent, listener);
                incomingCall.answerCall(30);
                incomingCall.startAudio();
                incomingCall.setSpeakerMode(true);
                if(incomingCall.isMuted()) {
                    incomingCall.toggleMute();
                }
                MyActivity.call = incomingCall;
                //MyActivity.updateStatus(incomingCall);
            } catch (Exception e) {
                if (incomingCall != null) {
                    incomingCall.close();
                }
            }
        }
    }


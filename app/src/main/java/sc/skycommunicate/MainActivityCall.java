package sc.skycommunicate;


import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.net.sip.SipRegistrationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.text.ParseException;

        public class MainActivityCall extends Activity {

            private static final String TAG = "MainActivityCall";

            Button button;
            EditText user;
            EditText pass;
            EditText domain;
            EditText server;

            public SipManager manager = null;
            public SipProfile profile = null;
            public SipAudioCall call = null;
            public IncomingCallReceiver callReceiver;

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_main_video);

                user = (EditText) findViewById(R.id.edit_user);
                user.setText("antonio");
                pass = (EditText) findViewById(R.id.edit_pass);
                pass.setText("antonio");
                domain = (EditText) findViewById(R.id.edit_domain);
                domain.setText("irac17.ddns.net");
                server = (EditText) findViewById(R.id.edit_server);
                server.setText("hector@irac17.ddns.net");

                button = (Button) findViewById(R.id.button_call);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initiateCall();
                    }
                });

                IntentFilter filter = new IntentFilter();
                filter.addAction("android.SipDemo.INCOMING_CALL");
                callReceiver = new IncomingCallReceiver();
                this.registerReceiver(callReceiver, filter);

                initializeManager();
            }

            public void initializeManager() {
                if(manager == null) {
                    manager = SipManager.newInstance(this);
                }

                initializeLocalProfile();
            }

            public void initializeLocalProfile() {
                if (manager == null) {
                    return;
                }

                if (profile != null) {
                    closeLocalProfile();
                }

                String username = user.getText().toString();
                String password = pass.getText().toString();
                String domain_text = domain.getText().toString();

                try {
                    SipProfile.Builder builder = new SipProfile.Builder(username, domain_text);
                    builder.setPassword(password);
                    //builder.setProtocol("UDP");
                    profile = builder.build();

                    Intent i = new Intent();
                    i.setAction("android.SipDemo.INCOMING_CALL");
                    PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, Intent.FILL_IN_DATA);
                    manager.open(profile, pi, null);


                    // This listener must be added AFTER manager.open is called,
                    // Otherwise the methods aren't guaranteed to fire.

                    manager.setRegistrationListener(profile.getUriString(), new SipRegistrationListener() {
                        public void onRegistering(String localProfileUri) {
                            updateStatus("Registering with SIP Server...");
                            Log.d(TAG,"Registering");
                        }

                        public void onRegistrationDone(String localProfileUri, long expiryTime) {
                            //Toast.makeText(MainActivity.this, "Ready", Toast.LENGTH_LONG).show();
                            Log.d(TAG,"Registered");
                            updateStatus("Registered");
                        }

                        public void onRegistrationFailed(String localProfileUri, int errorCode,
                                                         String errorMessage) {
                            updateStatus("Registration failed.  Please check settings.");
                            Log.d(TAG,"Failed registering "+errorMessage);
                            updateStatus("Failed registering");
                        }
                    });
                } catch (ParseException pe) {
                    pe.printStackTrace();
                } catch (SipException se) {
                    se.printStackTrace();
                }
            }

            @Override
            protected void onStop() {
                super.onStop();
                Log.d(TAG, "onStop");
                closeLocalProfile();
            }

            @Override
            protected void onRestart() {
                super.onRestart();
                Log.d(TAG, "onRestart");
                initializeManager();
            }

            public void closeLocalProfile() {
                Log.d(TAG,"Closing local profile");
                if (manager == null) {
                    return;
                }
                try {
                    if (profile != null) {
                        manager.close(profile.getUriString());
                    }
                } catch (Exception ee) {
                    Log.d(TAG,"Failed to close local profile");
                }
            }

            public void initiateCall() {

                //updateStatus(sipAddress);

                try {
                    SipAudioCall.Listener listener = new SipAudioCall.Listener() {
                        // Much of the client's interaction with the SIP Stack will
                        // happen via listeners.  Even making an outgoing call, don't
                        // forget to set up a listener to set things up once the call is established.
                        @Override
                        public void onCallEstablished(SipAudioCall call) {
                            Log.d(TAG,"Call established");
                            call.startAudio();
                            call.setSpeakerMode(true);
                            if (call.isMuted()){
                                call.toggleMute();
                            }

                            updateStatus("Ready");
                        }

                        @Override
                        public void onCallEnded(SipAudioCall call) {
                            Toast.makeText(MainActivityCall.this, "Call ended",Toast.LENGTH_LONG).show();
                            updateStatus("Call ended");
                        }

                        @Override
                        public void onCallBusy(SipAudioCall call) {
                            super.onCallBusy(call);
                            Log.d(TAG,"onCallBusy");
                        }

                        @Override
                        public void onCallHeld(SipAudioCall call) {
                            super.onCallHeld(call);
                            Log.d(TAG,"onCallHeld");
                        }

                        @Override
                        public void onError(SipAudioCall call, int errorCode, String errorMessage){
                            Log.d(TAG,"Error calling: "+errorMessage.toString());
                            updateStatus("Error calling");
                        }

                        @Override
                        public void onChanged(SipAudioCall call) {
                            super.onChanged(call);
                            Log.d(TAG,"onChanged");
                        }

                        @Override
                        public void onReadyToCall(SipAudioCall call) {
                            super.onReadyToCall(call);
                            Log.d(TAG,"onReadyToCall");
                        }

                        @Override
                        public void onCalling(SipAudioCall call){
                            updateStatus("Calling");
                            Log.d(TAG,"Calling");
                        }

                        @Override
                        public void onRinging(SipAudioCall call, SipProfile caller) {
                            super.onRinging(call, caller);
                            Log.d(TAG,"onRinging");
                        }

                        @Override
                        public void onRingingBack(SipAudioCall call) {
                            super.onRingingBack(call);
                            Log.d(TAG,"onRingingBack");
                        }
                    };

                    call = manager.makeAudioCall(profile.getUriString(), server.getText().toString(), listener, 30);

                }
                catch (Exception e) {
                    Log.d(TAG, "Error when trying to close manager.", e);
                    if (profile != null) {
                        try {
                            manager.close(profile.getUriString());
                        } catch (Exception ee) {
                            Log.d(TAG, "Error when trying to close manager.", ee);
                            ee.printStackTrace();
                        }
                    }
                    if (call != null) {
                        call.close();
                    }
                }
            }

            public void updateStatus(final String status) {
                // Be a good citizen.  Make sure UI changes fire on the UI thread.
                this.runOnUiThread(new Runnable() {
                    public void run() {
                        TextView labelView = (TextView) findViewById(R.id.sipLabel);
                        labelView.setText(status);
                        if (call!=null){
                            //Log.d(TAG,"State: "+String.valueOf(call.getState()));
                        }

                    }
                });
            }
        }



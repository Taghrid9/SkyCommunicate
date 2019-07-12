package sc.skycommunicate;

import android.Manifest;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivityVideo extends AppCompatActivity implements Session.SessionListener, PublisherKit.PublisherListener{


    private static String API_KEY = "46157792"; //
    private static String SESSION_ID = "1_MX40NjE1Nzc5Mn5-MTUzMjI3NzU0NjQ4OH5wRzhvalRXMHV5QmpxUW5NMFJxTFpxQkp-fg";
    private static String TOKEN = "T1==cGFydG5lcl9pZD00NjE1Nzc5MiZzaWc9YmE1NzE0ZTVhOGQyMWEzZGExYmI1NzZmOWJiZjU4NzEyMzE4ZjdiNzpzZXNzaW9uX2lkPTFfTVg0ME5qRTFOemM1TW41LU1UVXpNakkzTnpVME5qUTRPSDV3UnpodmFsUlhNSFY1UW1weFVXNU5NRkp4VEZweFFrcC1mZyZjcmVhdGVfdGltZT0xNTMyMjc3NzE0Jm5vbmNlPTAuOTA1MjQ3Njc0MTI1NzY0NSZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xNTMyMjk5MzA5JmluaXRpYWxfbGF5b3V0X2NsYXNzX2xpc3Q9";
    private static String LOG_TAG = MainActivityVideo.class.getSimpleName();
    private static final int RC_SETTINGS = 123;


    private Session session;

    private Publisher publisher;
    private Subscriber subscriber;
    private FrameLayout PublisherContainer;
    private FrameLayout SubscriberContainer;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_video);



        requestPermissions();


        PublisherContainer = (FrameLayout)findViewById(R.id.publisher_container);
        SubscriberContainer = (FrameLayout)findViewById(R.id.subscriber_container);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @AfterPermissionGranted(RC_SETTINGS)
    private void requestPermissions() {
        String[] perm ={Manifest.permission.INTERNET, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
        if (EasyPermissions.hasPermissions(this,perm))
        {
            session = new Session.Builder(this,API_KEY,SESSION_ID).build();
            session.setSessionListener(this);
            session.connect(TOKEN);
        }
        else{

            EasyPermissions.requestPermissions(this,"This app needs to access your camera and mic", RC_SETTINGS,perm);
        }
    }

    @Override
    public void onConnected(Session session) {


        publisher =new Publisher.Builder(this).build();
        publisher.setPublisherListener(this);

        PublisherContainer.addView(publisher.getView()); // able to see me on screen
        session.publish(publisher);


    }

    @Override
    public void onDisconnected(Session session) {

    }

    @Override
    public void onStreamReceived(Session session, Stream stream) {


        if (subscriber == null)
        {
            subscriber = new Subscriber.Builder(this,stream).build();
            session.subscribe(subscriber);
            SubscriberContainer.addView(subscriber.getView()); // able to see jer on screen
        }

    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {

        if (subscriber != null)
        {
            subscriber = null;
            SubscriberContainer.removeAllViews();
        }

    }

    @Override
    public void onError(Session session, OpentokError opentokError) {

    }

    @Override
    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {

    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {

    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {

    }
}

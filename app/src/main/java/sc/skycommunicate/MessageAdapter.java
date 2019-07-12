package sc.skycommunicate;



import android.graphics.Color;

import android.os.Build;

import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class MessageAdapter  extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{


    private List<Messages> userMessagesList;

    private DatabaseReference mUserDatabase;

    private FirebaseAuth mAuth;


    public MessageAdapter(List<Messages> userMessagesList){

        this.userMessagesList = userMessagesList;
    }





    @Override
    public MessageAdapter.MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {



        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_layout_of_user_sender, parent, false);


        mAuth = FirebaseAuth.getInstance();

        return new MessageViewHolder(v);
    }



    public class MessageViewHolder extends RecyclerView.ViewHolder{

        public TextView messageText;
        public CircleImageView userProfileImage;
        public TextView  displayName;


        public MessageViewHolder(View view){

            super(view);

            messageText = (TextView)view.findViewById(R.id.message_text_layout);
            userProfileImage = (CircleImageView)view.findViewById(R.id.message_profile_layout);
            displayName = (TextView) view.findViewById(R.id.name_text_layout);
        }
    }


//*********************** chat position




    @Override
    public void onBindViewHolder(final MessageViewHolder holder, int position) {


        String message_sender_id = mAuth.getCurrentUser().getUid();

        Messages messages = userMessagesList.get(position);

        String fromUserId = messages.getFrom();

        if (fromUserId.equals(message_sender_id)) {

            holder.messageText.setBackgroundResource(R.drawable.message_text_background);


            //holder.messageText.setTextColor(Color.BLACK);


            //holder.messageText.setGravity(Gravity.RIGHT);



        } else {

            holder.messageText.setBackgroundResource(R.drawable.message_text_background_two);

            //holder.messageText.setTextColor(Color.WHITE);

            //holder.messageText.setGravity(Gravity.LEFT);
        }


        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserId);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("thumb_image").getValue().toString();

                holder.displayName.setText(name);


                Picasso.with(holder.userProfileImage.getContext()).load(image)
                        .placeholder(R.drawable.fata_profil).into(holder.userProfileImage);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        holder.messageText.setText(messages.getMessage());

    }


    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }






}

package jp.ac.ecc.sk3a17.sns;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private ImageView cover;
    private CircleImageView avatar;
    private TextView userName, fullName, status;
    private DatabaseReference userRef, friendRequestRef, friendRef, notificationRef;
    private FirebaseAuth mAuth;
    private String currentUserID, uid, state;
    private Button sendRequest, denyRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        cover = findViewById(R.id.profile_cover);
        avatar = findViewById(R.id.profile_avatar);
        userName = findViewById(R.id.profile_user_name);
        fullName = findViewById(R.id.profile_full_name);
        status = findViewById(R.id.profile_status);
        sendRequest = findViewById(R.id.profile_send_request);
        denyRequest = findViewById(R.id.profile_deny_request);
        uid = getIntent().getStringExtra("UID");
        state = "not_friend";

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        friendRequestRef = FirebaseDatabase.getInstance().getReference().child("FriendRequest");
        friendRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        notificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications");
        if (!uid.equals(currentUserID)) {
            sendRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendRequest.setEnabled(false);
                    if (state.equals("not_friend")) {
                        SendFriendRequest();
                    }
                    if (state.equals("request_sent")) {
                        CancelRequest();
                    }
                    if (state.equals("request_received")) {
                        AcceptRequest();
                    }
                    if (state.equals("friends")) {
                        Unfriend();
                    }
                }
            });
        } else {
            sendRequest.setVisibility(View.INVISIBLE);
            sendRequest.setEnabled(false);
            denyRequest.setVisibility(View.INVISIBLE);
            denyRequest.setEnabled(false);
        }


        //load current user info
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.hasChild("profileImage")) {
                    String image = dataSnapshot.child("profileImage").getValue().toString();
                    Picasso.get().load(image).into(avatar);
                }
                if (dataSnapshot.exists() && dataSnapshot.hasChild("cover")) {
                    String coverImage = dataSnapshot.child("cover").getValue().toString();
                    Picasso.get().load(coverImage).into(cover);
                }
                if (dataSnapshot.exists() && dataSnapshot.hasChild("status")) {
                    String userStatus = dataSnapshot.child("status").getValue().toString();
                    status.setText(userStatus);

                }
                String getUserName = dataSnapshot.child("userName").getValue().toString();
                String getFullName = dataSnapshot.child("fullName").getValue().toString();
                userName.setText(getUserName);
                fullName.setText(getFullName);
                MaintenanceButton();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void Unfriend() {
        friendRef.child(currentUserID).child(uid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    friendRef.child(uid).child(currentUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                sendRequest.setEnabled(true);
                                state = "not_friend";
                                sendRequest.setText("Send Friend Request");
                                denyRequest.setVisibility(View.INVISIBLE);
                                denyRequest.setEnabled(false);

                            }
                        }
                    });
                }
            }
        });
    }

    private void AcceptRequest() {

        friendRef.child(currentUserID).child(uid).child("friend").setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    friendRef.child(uid).child(currentUserID).child("friend").setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                friendRequestRef.child(currentUserID).child(uid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            friendRequestRef.child(uid).child(currentUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        sendRequest.setEnabled(true);
                                                        state = "friends";
                                                        sendRequest.setText("Unfriend");
                                                        denyRequest.setVisibility(View.INVISIBLE);
                                                        denyRequest.setEnabled(false);

                                                    }
                                                }
                                            });
                                        }
                                    }
                                });


                            }
                        }
                    });
                }
            }
        });
    }

    private void MaintenanceButton() {
        friendRequestRef.child(currentUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(uid)) {
                    String requestType = dataSnapshot.child(uid).child("requestType").getValue().toString();
                    if (requestType.equals("sent")) {
                        state = "request_sent";
                        sendRequest.setText("Cancel Request");
                        denyRequest.setVisibility(View.INVISIBLE);
                        denyRequest.setEnabled(false);
                    } else if (requestType.equals("received")) {
                        state = "request_received";
                        sendRequest.setText("Accept");
                        denyRequest.setVisibility(View.VISIBLE);
                        denyRequest.setEnabled(true);
                        denyRequest.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CancelRequest();
                            }
                        });
                    }
                } else {
                    friendRef.child(currentUserID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(uid)) {
                                state = "friends";
                                sendRequest.setText("Unfriend");
                                denyRequest.setVisibility(View.INVISIBLE);
                                denyRequest.setEnabled(false);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void CancelRequest() {
        friendRequestRef.child(currentUserID).child(uid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    friendRequestRef.child(uid).child(currentUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                sendRequest.setEnabled(true);
                                state = "not_friend";
                                sendRequest.setText("Send Friend Request");
                                denyRequest.setVisibility(View.INVISIBLE);
                                denyRequest.setEnabled(false);

                            }
                        }
                    });
                }
            }
        });

    }

    private void SendFriendRequest() {
        friendRequestRef.child(currentUserID).child(uid).child("requestType").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    friendRequestRef.child(uid).child(currentUserID).child("requestType").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                HashMap<String, String> chatNotificationMap = new HashMap<>();
                                chatNotificationMap.put("from", currentUserID);
                                chatNotificationMap.put("type", "request");
                                notificationRef.child(uid).push().setValue(chatNotificationMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            sendRequest.setEnabled(true);
                                            state = "request_sent";
                                            sendRequest.setText("Cancel Request");
                                            denyRequest.setVisibility(View.INVISIBLE);
                                            denyRequest.setEnabled(false);
                                        }
                                    }
                                });

                            }
                        }
                    });
                }
            }
        });
    }
}

package jp.ac.ecc.sk3a17.sns;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ClickedPostActivity extends AppCompatActivity {
    private Button deleteButton;
    private TextView postDescription;
    private ImageView postImage;
    private String postKey;
    private FirebaseAuth mAuth; //for authentication
    private DatabaseReference postRef;
    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clicked_post);

        deleteButton = findViewById(R.id.clicked_post_delete);
        postDescription = findViewById(R.id.clicked_post_description);
        postImage = findViewById(R.id.clicked_post_image);
        //set edit and delete button invisible as default
        deleteButton.setVisibility(View.INVISIBLE);

        postKey = getIntent().getStringExtra("PostKey");

        //Create reference to Users and Posts node
        postRef = FirebaseDatabase.getInstance().getReference().child("Posts");

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        postRef.child(postKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String description = dataSnapshot.child("description").getValue().toString();
                    String image = dataSnapshot.child("postImage").getValue().toString();
                    String uid = dataSnapshot.child("uid").getValue().toString();
                    //if this post belong to current user then allow to edit or delete post
                    if (currentUserID.equals(uid)) {
                        deleteButton.setVisibility(View.VISIBLE);
                    } else {
                        postDescription.setInputType(InputType.TYPE_NULL);
                    }
                    postDescription.setText(description);
                    Picasso.get().load(image).into(postImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Delete button click event
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //remove post from real time database but posted image's still in storage
                postRef.child(postKey).removeValue();
                SendToMain();
            }
        });

    }

    private void SendToMain() {
        Intent mainIntent = new Intent(ClickedPostActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}

package jp.ac.ecc.sk3a17.sns;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class PostActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView selectPostImage;
    private Button addPost;
    private EditText description;
    private static final int galleryPick = 1;
    private Uri imageUri;
    private String validationDescription, currentUserId, saveCurrentDate, saveCurrentTime, postRandomName, downloadUri;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef, postRef;
    private StorageReference postImageRef; //to save profile image to storage
    private ProgressDialog loadingBar;
    private long postCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        //add toolbar to the activity and back to the previous activity button(DisplayShowHomeEnabled(true))
        toolbar = findViewById(R.id.post_tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Post");

        //loading bar
        loadingBar = new ProgressDialog(this);


        addPost = findViewById(R.id.post_add_post);
        description = findViewById(R.id.post_description);
        selectPostImage = findViewById(R.id.post_select_image);

        mAuth = FirebaseAuth.getInstance();
        //get current useID through mAuth
        currentUserId = mAuth.getCurrentUser().getUid();
        postImageRef = FirebaseStorage.getInstance().getReference();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postRef = FirebaseDatabase.getInstance().getReference().child("Posts");

        //select picture from local gallery
        selectPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

        //Post button click event
        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidatePost();
            }
        });


    }

    private void ValidatePost() {
        validationDescription = description.getText().toString();
        if (imageUri == null || validationDescription == null) {
            Toast.makeText(this, "Please select a picture you'd like to post and write something", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Adding post");
            loadingBar.setMessage("Please wait a moment");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(false);
            //store image to firebase storage
            StoringImageToFirebase();
        }
    }

    private void StoringImageToFirebase() {

        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentDate = currentDate.format(calFordDate.getTime());
        saveCurrentTime = currentTime.format(calFordDate.getTime());
        //generate random name using posted date and time
        postRandomName = saveCurrentDate + saveCurrentTime;
        //create a folder to save posted images
        final StorageReference filePath = postImageRef.child("Posted Images").child(imageUri.getLastPathSegment() + postRandomName + ".jpg");
        //save image to database
        filePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            downloadUri = uri.toString();
                            SavePost();
                        }
                    });
                }
            }
        });


    }

    private void SavePost() {
        //retrieve user's full name and avatar for adding to Post node will be generated later
        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    postCounter = dataSnapshot.getChildrenCount();
                } else {
                    postCounter = 0;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        userRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String fullName = dataSnapshot.child("fullName").getValue().toString();
                    String avatar = dataSnapshot.child("profileImage").getValue().toString();

                    HashMap postMap = new HashMap();
                    postMap.put("uid", currentUserId);
                    postMap.put("date", saveCurrentDate);
                    postMap.put("time", saveCurrentTime);
                    postMap.put("description", validationDescription);
                    postMap.put("postImage", downloadUri);
                    postMap.put("profileImage", avatar);
                    postMap.put("fullName", fullName);
                    postMap.put("postCounter", postCounter);
                    postRef.child(currentUserId + postRandomName).updateChildren(postMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()){
                                SendToMain();
                                loadingBar.dismiss();
                            }else{
                                Toast.makeText(PostActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, galleryPick);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            SendToMain();
        }
        return super.onOptionsItemSelected(item);
    }

    private void SendToMain() {
        Intent mainIntent = new Intent(PostActivity.this, MainActivity.class);
        startActivity(mainIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == galleryPick && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            selectPostImage.setImageURI(imageUri);
        }
    }
}

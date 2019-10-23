package jp.ac.ecc.sk3a17.sns;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private ImageView cover;
    private CircleImageView avatar;
    private TextView userName;
    private EditText fullName, status;
    private Button saveButton;
    private DatabaseReference userRef;
    private FirebaseAuth mAuth;
    private String currentUserID;
    private static final int galleryPick = 1;
    private Uri imageUri = null, avatarUri = null;
    private int clickCode = 0;
    private ProgressDialog loadingBar;
    private StorageReference userProfileImageRef, userCoverRef; //to save profile image to storage

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        cover = findViewById(R.id.profile_cover);
        avatar = findViewById(R.id.profile_avatar);
        userName = findViewById(R.id.profile_user_name);
        fullName = findViewById(R.id.profile_full_name);
        saveButton = findViewById(R.id.profile_save);
        status = findViewById(R.id.profile_status);
        loadingBar = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);

        //create a folder to save file
        userProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        userCoverRef = FirebaseStorage.getInstance().getReference().child("Cover Images");


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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //update cover image
        cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCode = 0;
                OpenGallery();
            }
        });
        //update avatar
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCode = 1;
                OpenGallery();
            }
        });

        //save button click
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveInfo();
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == galleryPick && resultCode == RESULT_OK && data != null && clickCode == 0) {
            imageUri = data.getData();
            cover.setImageURI(imageUri);
        } else if (requestCode == galleryPick && resultCode == RESULT_OK && data != null && clickCode == 1) {
            //start CropImage Activity
            imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }
        //when crop button is clicked, this will be called
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                avatarUri = result.getUri();
                Picasso.get().load(avatarUri).into(avatar);
            }
        }
    }

    private void SaveInfo() {
        if (TextUtils.isEmpty(fullName.getText().toString())) {
            fullName.setError("フルネームを入力してください");
            fullName.requestFocus();
        } else {
            if (avatarUri != null) {
                //save image to storage, type of file is jpg
                final StorageReference filePath = userProfileImageRef.child(currentUserID + ".jpg");
                filePath.putFile(avatarUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            //save image link to realtime database
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String downloadUrl = uri.toString();
                                    userRef.child("profileImage").setValue(downloadUrl)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {

                                                    } else {
                                                        String message = task.getException().getMessage();
                                                        Toast.makeText(ProfileActivity.this, "Error Occurred..." + message, Toast.LENGTH_SHORT).show();
                                                        loadingBar.dismiss();
                                                    }
                                                }
                                            });
                                }
                            });
                        }
                    }
                });
            }
            if (imageUri != null) {
                //save image to storage, type of file is jpg
                final StorageReference filePath = userCoverRef.child(currentUserID + ".jpg");
                filePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            //save image link to realtime database
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String downloadUrl = uri.toString();
                                    userRef.child("cover").setValue(downloadUrl)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {

                                                    } else {
                                                        String message = task.getException().getMessage();
                                                        Toast.makeText(ProfileActivity.this, "Error Occurred..." + message, Toast.LENGTH_SHORT).show();
                                                        loadingBar.dismiss();
                                                    }
                                                }
                                            });
                                }
                            });
                        }
                    }
                });
            }
            userRef.child("status").setValue(status.getText().toString());
            userRef.child("fullName").setValue(fullName.getText().toString());
            SendToMain();
        }

    }

    private void SendToMain() {
        Intent mainIntent = new Intent(ProfileActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}

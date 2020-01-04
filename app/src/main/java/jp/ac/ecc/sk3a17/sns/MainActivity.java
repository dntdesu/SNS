package jp.ac.ecc.sk3a17.sns;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.ac.ecc.sk3a17.sns.Exercise.ExerciseFragment;
import jp.ac.ecc.sk3a17.sns.Fragments.CaloriesFragment;
import jp.ac.ecc.sk3a17.sns.Fragments.HomeFragment;
import jp.ac.ecc.sk3a17.sns.Fragments.WeightFragment;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle; //for opening navigation view when tap on icon on the appbar
    private FirebaseAuth mAuth; //for authentication
    private DatabaseReference userRef, postRef; //for connecting to real time database
    private GoogleSignInClient mGoogleSignInClient;
    private CircleImageView navProfileImage;
    private String currentUserID;
    private ImageButton addNewPost;
    private TextView high, weight, bmi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e(TAG, "Start Main");
        setContentView(R.layout.activity_main);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        navigationView = findViewById(R.id.navigation_view);
        drawerLayout = findViewById(R.id.drawable_layout);
        addNewPost = findViewById(R.id.main_add_new_post);

        //Create reference to Users and Posts node
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postRef = FirebaseDatabase.getInstance().getReference().child("Posts");

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        //Navigation view
        View navView = navigationView.inflateHeaderView(R.layout.navigation_header); //set header for navigation view
        navProfileImage = navView.findViewById(R.id.nav_header_image);
        high = navView.findViewById(R.id.nav_high);
        weight = navView.findViewById(R.id.nav_weight);
        bmi = navView.findViewById(R.id.nav_bmi);


        //Bottom navigation
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        getSupportFragmentManager().beginTransaction().replace(R.id.main_container,
                new HomeFragment()).commit();


        //custom toolbar and ActionBarDrawerToggle's things
        toolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(toolbar);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        //display profile image on navigation
        userRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.hasChild("profileImage")) {
                    String image = dataSnapshot.child("profileImage").getValue().toString();
                    Picasso.get().load(image).into(navProfileImage);
                }
                if (dataSnapshot.exists() && dataSnapshot.hasChild("high") && dataSnapshot.hasChild("weight")) {
                    String userHigh = dataSnapshot.child("high").getValue().toString();
                    String userWeight = dataSnapshot.child("weight").getValue().toString();
                    //BMI formula
                    Double BMI = Double.parseDouble(userWeight) / ((Double.parseDouble(userHigh) / 100) * (Double.parseDouble(userHigh) / 100));
                    String userBMI = String.format("%.1f", BMI);
                    high.setText(userHigh);
                    weight.setText(userWeight);
                    bmi.setText("BMI : " + userBMI);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        navProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendToProfile();
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                userMenuSelector(menuItem);
                return false;
            }
        });
        bottomNavigationView.setOnNavigationItemSelectedListener(botNavListener);
        addNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendToPost();
            }
        });
    }


    private void SendToPost() {
        Intent postIntent = new Intent(MainActivity.this, PostActivity.class);
        startActivity(postIntent);
    }

    //It will be run after OnCreate, check if user has login or not

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            SendToLogin();
        } else {
            //check user is on database or not
            CheckUserExistence();
        }
    }

    private void CheckUserExistence() {
        final String current_user_id = mAuth.getCurrentUser().getUid();
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(current_user_id)) {
                    SendToSetUp();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void SendToSetUp() {
        Intent setUpIntent = new Intent(MainActivity.this, SetUpActivity.class);
        setUpIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setUpIntent);
        finish();
    }

    private void SendToLogin() {
        Intent loginIntent = new Intent(MainActivity.this, LogInActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    private void SendToProfile() {
        Intent profileIntent = new Intent(MainActivity.this, ProfileUpdateActivity.class);
        startActivity(profileIntent);
    }

    private void SendToFindFriends() {
        Intent intent = new Intent(MainActivity.this, FindFriendActivity.class);
        startActivity(intent);
    }

    private void SendToFriendList() {
        Intent intent = new Intent(MainActivity.this, FriendListActivity.class);
        startActivity(intent);
    }

    private void SendToInbox() {
        Intent intent = new Intent(MainActivity.this, InboxActivity.class);
        startActivity(intent);
    }

    private void SendToFriendRequest() {
        Intent intent = new Intent(MainActivity.this, FriendRequestActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void userMenuSelector(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_profile:
                SendToProfile();
                break;
            case R.id.nav_friends:
                SendToFriendList();
                break;
            case R.id.nav_find_friend:
                SendToFindFriends();
                break;
            case R.id.nav_messages:
                SendToInbox();
                break;
            case R.id.nav_friend_request:
                SendToFriendRequest();
                break;
            case R.id.nav_logout:
                mAuth.signOut();
                mGoogleSignInClient.signOut();
                SendToLogin();
                break;

        }
    }


    private BottomNavigationView.OnNavigationItemSelectedListener botNavListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;
                    switch (menuItem.getItemId()) {
                        case R.id.bot_home:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.bot_weight:
                            selectedFragment = new WeightFragment();
                            break;
                        case R.id.bot_exercise:
                            selectedFragment = new ExerciseFragment();
                            break;
                        case R.id.bot_calories:
                            selectedFragment = new CaloriesFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_container,
                            selectedFragment).commit();
                    return true;
                }
            };
}

package jp.ac.ecc.sk3a17.sns;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import jp.ac.ecc.sk3a17.sns.Model.Users;

public class FriendRequestActivity extends AppCompatActivity {
    private RecyclerView friendRequestList;
    private DatabaseReference friendRequestRef, userRef;
    private FirebaseAuth mAuth;
    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request);

        friendRequestList = findViewById(R.id.friend_request_list);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        friendRequestRef = FirebaseDatabase.getInstance().getReference().child("FriendRequest").child(currentUserID);
        //List set up
        friendRequestList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        friendRequestList.setLayoutManager(linearLayoutManager);
        DisplayFriendRequestList();
    }

    private void DisplayFriendRequestList() {
        Query query = friendRequestRef.orderByChild("request_type");
        FirebaseRecyclerOptions<Users> options = new FirebaseRecyclerOptions.Builder<Users>()
                .setQuery(query, Users.class)
                .build();

        FirebaseRecyclerAdapter<Users, FriendRequestActivity.FriendViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, FriendRequestActivity.FriendViewHolder>(options) {

            @NonNull
            @Override
            public FriendRequestActivity.FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_layout, parent, false);
                FriendRequestActivity.FriendViewHolder usersViewHolder = new FriendRequestActivity.FriendViewHolder(view);
                return usersViewHolder;
            }

            @Override
            protected void onBindViewHolder(@NonNull final FriendRequestActivity.FriendViewHolder holder, int position, @NonNull final Users model) {

                final String uid = getRef(position).getKey(); //get key of item has been clicked
                DatabaseReference typeRef = getRef(position).child("request_type").getRef();
                typeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        userRef.child(uid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                final String userName1 = dataSnapshot.child("userName").getValue().toString();
                                final String fullName1 = dataSnapshot.child("fullName").getValue().toString();
                                final String avatar1 = dataSnapshot.child("profileImage").getValue().toString();
                                holder.userName.setText(userName1);
                                holder.fullName.setText(fullName1);
                                Picasso.get().load(avatar1).placeholder(R.drawable.profile).into(holder.avatar);

                                holder.mView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(FriendRequestActivity.this, ProfileActivity.class);
                                        intent.putExtra("UID", uid);
                                        intent.putExtra("name", fullName1);
                                        intent.putExtra("avatar", avatar1);
                                        startActivity(intent);
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        };

        friendRequestList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder {
        TextView userName, fullName;
        ImageView avatar;

        View mView;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            userName = mView.findViewById(R.id.users_user_name);
            fullName = mView.findViewById(R.id.users_full_name);
            avatar = mView.findViewById(R.id.users_avatar);
        }
    }
}


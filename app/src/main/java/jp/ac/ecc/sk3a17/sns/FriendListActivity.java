package jp.ac.ecc.sk3a17.sns;

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
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import jp.ac.ecc.sk3a17.sns.Model.Users;

public class FriendListActivity extends AppCompatActivity {
    private RecyclerView friendList;
    private DatabaseReference friendRef, userRef;
    private FirebaseAuth mAuth;
    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        friendList = findViewById(R.id.friend_list_list);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        friendRef = FirebaseDatabase.getInstance().getReference().child("Friends").child(currentUserID);
        //List set up
        friendList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        friendList.setLayoutManager(linearLayoutManager);
        DisplayFriendList();
    }

    private void DisplayFriendList() {
        FirebaseRecyclerOptions<Users> options = new FirebaseRecyclerOptions.Builder<Users>()
                .setQuery(friendRef, Users.class)
                .build();

        FirebaseRecyclerAdapter<Users, FriendViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, FriendViewHolder>(options) {

            @NonNull
            @Override
            public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_layout, parent, false);
                FriendViewHolder usersViewHolder = new FriendViewHolder(view);
                return usersViewHolder;
            }

            @Override
            protected void onBindViewHolder(@NonNull final FriendViewHolder holder, int position, @NonNull final Users model) {
                //String uid = model.getUid();
                final String uid = getRef(position).getKey(); //get key of item has been clicked
                userRef.child(uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final String userName1 = dataSnapshot.child("userName").getValue().toString();
                        final String fullName1 = dataSnapshot.child("fullName").getValue().toString();
                        final String avatar1 = dataSnapshot.child("profileImage").getValue().toString();
                        holder.userName.setText(userName1);
                        holder.fullName.setText(fullName1);
                        Picasso.get().load(avatar1).placeholder(R.drawable.profile).into(holder.avatar);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        };

        friendList.setAdapter(firebaseRecyclerAdapter);
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

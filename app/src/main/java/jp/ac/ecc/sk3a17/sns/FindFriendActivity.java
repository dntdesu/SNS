package jp.ac.ecc.sk3a17.sns;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import jp.ac.ecc.sk3a17.sns.Model.Users;

public class FindFriendActivity extends AppCompatActivity {

    private RecyclerView userList;
    private DatabaseReference userRef;
    private EditText input;
    private String currentUserID;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);

        input = findViewById(R.id.find_friend_input);
        userList = findViewById(R.id.find_friend_users_list);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        input.requestFocus();

        // users list setup
        userList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        userList.setLayoutManager(linearLayoutManager);
        userList.setVisibility(View.INVISIBLE);

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String inputText = input.getText().toString();
                if (!TextUtils.isEmpty(inputText)) {
                    userList.setVisibility(View.VISIBLE);
                    SearchFriends(inputText);
                } else {
                    userList.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void SearchFriends(String inputText) {
        Query query = userRef.orderByChild("fullName").startAt(inputText).endAt(inputText + "¥¥uf8ff");
        FirebaseRecyclerOptions<Users> options = new FirebaseRecyclerOptions.Builder<Users>()
                .setQuery(query, Users.class)
                .build();

        FirebaseRecyclerAdapter<Users, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(options) {

            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_layout, parent, false);
                UsersViewHolder usersViewHolder = new UsersViewHolder(view);
                return usersViewHolder;
            }

            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder holder, int position, @NonNull Users model) {
                //String uid = model.getUid();
                final String uid = getRef(position).getKey(); //get key of item has been clicked
                holder.userName.setText(model.getUserName());
                holder.fullName.setText(model.getFullName());
                Picasso.get().load(model.getProfileImage()).placeholder(R.drawable.profile).into(holder.avatar);
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //send to clickedActivity to see more details, edit or delete post
                        Intent clickedIntent = new Intent(FindFriendActivity.this, ProfileActivity.class);
                        //send key to clickedActivity by using putExtra
                        clickedIntent.putExtra("UID", uid);
                        startActivity(clickedIntent);

                    }
                });
            }
        };

        userList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {
        TextView userName, fullName;
        ImageView avatar;

        View mView;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            userName = mView.findViewById(R.id.users_user_name);
            fullName = mView.findViewById(R.id.users_full_name);
            avatar = mView.findViewById(R.id.users_avatar);
        }
    }
}

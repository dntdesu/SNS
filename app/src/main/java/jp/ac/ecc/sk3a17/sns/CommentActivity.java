package jp.ac.ecc.sk3a17.sns;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import jp.ac.ecc.sk3a17.sns.Model.Comments;

public class CommentActivity extends AppCompatActivity {
    private ImageButton postComment;
    private EditText commentInput;
    private RecyclerView commentView;
    private String postKey, currentUserID;
    private DatabaseReference userRef, postRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        postComment = findViewById(R.id.comment_post_comment);
        commentInput = findViewById(R.id.comment_input);
        commentView = findViewById(R.id.comment_view);
        postKey = getIntent().getStringExtra("PostKey");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(postKey).child("Comments");
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        commentView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        commentView.setLayoutManager(linearLayoutManager);

        postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String userName = dataSnapshot.child("userName").getValue().toString();
                            Validate(userName);
                            commentInput.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    private void Validate(String userName) {
        String input = commentInput.getText().toString();
        if (TextUtils.isEmpty(input)) {
            commentInput.setError("コメントを入力してください");
            commentInput.requestFocus();
        } else {
            Calendar calFordDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
            final String saveCurrentDate = currentDate.format(calFordDate.getTime());
            final String saveCurrentTime = currentTime.format(calFordDate.getTime());

            final String randomKey = currentUserID + saveCurrentDate + saveCurrentTime;
            HashMap hashMap = new HashMap();
            hashMap.put("uid", currentUserID);
            hashMap.put("comment", input);
            hashMap.put("date", saveCurrentDate);
            hashMap.put("time", saveCurrentTime);
            hashMap.put("userName", userName);

            postRef.child(randomKey).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {

                    }
                }
            });
        }
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView userName, date, comment, time;
        View view;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            userName = view.findViewById(R.id.commentLayout_user_name);
            date = view.findViewById(R.id.commentLayout_date);
            time = view.findViewById(R.id.commentLayout_time);
            comment = view.findViewById(R.id.commentLayout_text);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        Query sortPostDescending = postRef.orderByChild("date");
        FirebaseRecyclerOptions<Comments> options = new FirebaseRecyclerOptions.Builder<Comments>()
                .setQuery(sortPostDescending, Comments.class)
                .build();
        FirebaseRecyclerAdapter<Comments, CommentActivity.CommentViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Comments, CommentActivity.CommentViewHolder>(options) {

            @NonNull
            @Override
            public CommentActivity.CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_comments_layout, parent, false);
                CommentActivity.CommentViewHolder commentViewHolder = new CommentActivity.CommentViewHolder(view);
                return commentViewHolder;
            }

            @Override
            protected void onBindViewHolder(@NonNull CommentActivity.CommentViewHolder holder, int position, @NonNull Comments model) {
                holder.userName.setText(model.getUserName());
                holder.date.setText(model.getDate());
                holder.time.setText(model.getTime());
                holder.comment.setText(model.getComment());

            }
        };

        commentView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }
}

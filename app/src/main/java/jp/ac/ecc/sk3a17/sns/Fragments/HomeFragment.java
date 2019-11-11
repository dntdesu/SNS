package jp.ac.ecc.sk3a17.sns.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import jp.ac.ecc.sk3a17.sns.ClickedPostActivity;
import jp.ac.ecc.sk3a17.sns.Model.Posts;
import jp.ac.ecc.sk3a17.sns.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private View homeView;
    private RecyclerView postList;
    private DatabaseReference postRef;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        homeView = inflater.inflate(R.layout.fragment_home, container, false);

        postList = homeView.findViewById(R.id.all_users_post_list);
        postRef = FirebaseDatabase.getInstance().getReference().child("Posts");

        // Post list setup
        postList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);

        //To display posts, need to have a recycler view, and a Firebase Recycler Adapter
        DisplayAllUsersPost();

        return homeView;
    }

    private void DisplayAllUsersPost() {
        //using Firebase Recycler Adapter to retrieve all the posts
        //Firebase Recycler Adapter needs a module class and a static class
        //query data in descending order
        Query sortPostDescending = postRef.orderByChild("postCounter");
        FirebaseRecyclerOptions<Posts> options = new FirebaseRecyclerOptions.Builder<Posts>()
                .setQuery(sortPostDescending, Posts.class)
                .build();
        FirebaseRecyclerAdapter<Posts, HomeFragment.PostViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Posts, HomeFragment.PostViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull HomeFragment.PostViewHolder holder, int position, @NonNull Posts model) {
                //recyclerview onclick
                final String postKey = getRef(position).getKey(); //get key of item has been clicked

                //set value to view holder
                holder.userName.setText(model.getFullName());
                holder.postDate.setText(model.getDate());
                holder.postTime.setText(model.getTime() + "  ");
                holder.description.setText(model.getDescription());
                Picasso.get().load(model.getProfileImage()).into(holder.avatar);
                Picasso.get().load(model.getPostImage()).into(holder.postImage);

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //send to clickedActivity to see more details, edit or delete post
                        Intent clickedIntent = new Intent(getContext(), ClickedPostActivity.class);
                        //send key to clickedActivity by using putExtra
                        clickedIntent.putExtra("PostKey", postKey);
                        startActivity(clickedIntent);

                    }
                });
            }

            @NonNull
            @Override
            public HomeFragment.PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_posts_layout, parent, false);
                HomeFragment.PostViewHolder postViewHolder = new HomeFragment.PostViewHolder(view);
                return postViewHolder;
            }
        };
        postList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView userName, postDate, postTime, description;
        ImageView postImage, avatar;

        View mView;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            userName = mView.findViewById(R.id.postLayout_name);
            postDate = mView.findViewById(R.id.postLayout_date);
            postTime = mView.findViewById(R.id.postLayout_time);
            description = mView.findViewById(R.id.postLayout_post_description);
            avatar = mView.findViewById(R.id.postLayout_avatar);
            postImage = mView.findViewById(R.id.postLayout_image);
        }
    }

}

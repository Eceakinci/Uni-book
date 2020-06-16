package com.loory.unibook.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.loory.unibook.Fragment.PostDetailFragment;
import com.loory.unibook.Fragment.ProfileFragment;
import com.loory.unibook.Model.Post;
import com.loory.unibook.Model.User;
import com.loory.unibook.R;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{

    public Context mContext;
    public List<Post> mPost;

    private FirebaseUser firebaseUser;

    public PostAdapter(Context context, List<Post> posts) {
        this.mContext = context;
        this.mPost = posts;
    }

    @NonNull
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PostAdapter.ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Post post = mPost.get(position);

        Glide.with(mContext).load(post.getPostimage())
                .apply(new RequestOptions().placeholder(R.drawable.placeholder))
                .into(holder.post_image);

        holder.title.setVisibility(View.VISIBLE);
        holder.title.setText(post.getTitle());

        holder.author.setVisibility(View.VISIBLE);
        holder.author.setText(post.getAuthor());

        holder.price.setVisibility(View.VISIBLE);
        holder.price.setText(post.getPrice());

        holder.numOfPages.setVisibility(View.VISIBLE);
        holder.numOfPages.setText(post.getNumOfPages());

        holder.edition.setVisibility(View.VISIBLE);
        holder.edition.setText(post.getEdition());

        publisherInfo(holder.image_profile,holder.username,post.getPublisher());
        isSaved(post.getPostid(),holder.save);


        holder.save.setOnClickListener(new View.OnClickListener() {

            //burada "save" yerine bookmark da yabilirsin hata alirsan
            @Override
            public void onClick(View v) {
                //save'le
                if(holder.save.getTag().equals("save")){
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                            .child(post.getPostid()) .setValue(true);
                //ya da save'den cikar
                }else{
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                            .child(post.getPostid()).removeValue();
                }
            }
        });

        holder.image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("profileid",post.getPublisher());
                editor.apply();

                ( (FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                ,new ProfileFragment()).commit();
            }
        });

        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("profileid",post.getPublisher());
                editor.apply();

                ( (FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                        ,new ProfileFragment()).commit();
            }
        });



    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView image_profile, post_image, save, chat;
        public TextView username, title, author, price, numOfPages, edition;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            post_image = itemView.findViewById(R.id.post_image);
            save = itemView.findViewById(R.id.save);
            chat = itemView.findViewById(R.id.gotoChat);
            username = itemView.findViewById(R.id.username_p);
            title = itemView.findViewById(R.id.title_p);
            author = itemView.findViewById(R.id.author_p);
            price  = itemView.findViewById(R.id.price_p);
            numOfPages = itemView.findViewById(R.id.numOfPages_p);
            edition = itemView.findViewById(R.id.edition_p);

        }
    }

    private void publisherInfo(final ImageView image_profile, final TextView username, final String id){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.child("Users").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(mContext).load(user.getImageurl()).into(image_profile);
                username.setText(user.getUsername());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void isSaved(final String postid, final ImageView imageView){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Saves")
                .child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               //hata gelirse "postid" olabilir bu kisim
                if(dataSnapshot.child(postid).exists()){
                    imageView.setImageResource(R.drawable.ic_bookmark_black);
                    imageView.setTag("saved");
                }else{
                    imageView.setImageResource(R.drawable.ic_ibookmark);
                    imageView.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

package com.loory.unibook.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.loory.unibook.Fragment.ProfileFragment;
import com.loory.unibook.Model.Chat;
import com.loory.unibook.Model.User;
import com.loory.unibook.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {


    //Adapter is an interface whose implementations provide data and control the display of that data. ListViews own adapters that completely control the ListView’s display. So adapters control the content displayed in the list as well as how to display it.
    private Context mContext;
    private List<Chat> mChat;
    private String imageUrl;

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;


    private FirebaseUser firebaseUser;

    public MessageAdapter(Context mContext, List<Chat> mChat, String imageUrl) {
        this.mContext = mContext;
        this.mChat = mChat;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }else{
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        Chat chat = mChat.get(position);
        holder.show_message.setText(chat.getMessage());
        //if image url equals burada var
        //default'u firebase'e koyduktan sonra burayi yorumdan cikar

//        if(imageUrl.equals("default")){
//            holder.image_profile.setImageResource(R.mipmap.ic_launcher);
//        }else{
//            Glide.with(mContext).load(imageUrl).into(holder.image_profile);
//        }

    }

    //user search icin aslinda
    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView show_message;
        public CircleImageView image_profile;


        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            image_profile = itemView.findViewById(R.id.image_profile);
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(mChat.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        }else{
            return MSG_TYPE_LEFT;
        }
    }
}
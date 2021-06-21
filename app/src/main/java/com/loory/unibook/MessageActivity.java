package com.loory.unibook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.loory.unibook.Adapter.MessageAdapter;
import com.loory.unibook.Model.Chat;
import com.loory.unibook.Model.User;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView username;
    FirebaseUser fUser;
    DatabaseReference userReference;

    ImageButton btnSend;
    EditText textSend;
    MessageAdapter messageAdapter;
    List<Chat> mChat;

    RecyclerView recyclerView;

    Intent intent;

    private String strMessage = "";
    private byte encryptionKey [] = {-100, 40, 123, -21, 88, 32, 25, 62, -37, 93, -53, 12, 34, 64, 24, 72};
    private Cipher cipher, decipher;
    private SecretKeySpec secretKeySpec;
    //private SecretKey secKey;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar = findViewById(R.id.toolbar_message);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Change finish
                finish();
            }
        });

        recyclerView = findViewById(R.id.recycler_view_message);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        profile_image = findViewById(R.id.image_profile_message);
        username = findViewById(R.id.username_message);
        btnSend = findViewById(R.id.btn_send);
        textSend = findViewById(R.id.text_send);

        intent = getIntent();

        fUser  = FirebaseAuth.getInstance().getCurrentUser();

        final String userid = intent.getStringExtra("userid");



//            try {
//                cipher = Cipher.getInstance("AES");
//                decipher = Cipher.getInstance("AES");
//            } catch (NoSuchPaddingException e) {
//                e.printStackTrace();
//            } catch (NoSuchAlgorithmException e) {
//                e.printStackTrace();
//            }

//        try {
//           secKey = getSecretEncryptionKey();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        secretKeySpec = new SecretKeySpec(secKey.getEncoded(),"AES");

        secretKeySpec = new SecretKeySpec(encryptionKey,"AES");


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = textSend.getText().toString();
                if(!message.equals("")){
                    //sender receiver message
                    sendMessage(fUser.getUid(),userid,message);
                }else{
                    Toast.makeText(MessageActivity.this,"You can't send empty message",Toast.LENGTH_SHORT).show();
                }
                textSend.setText("");
            }
        });

        userReference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                Glide.with(MessageActivity.this).load(user.getImageurl()).into(profile_image);

//                if(user.getImageurl().equals("default")){
//                    profile_image.setImageResource(R.mipmap.ic_launcher);
//                }else{
//                    Glide.with(MessageActivity.this).load(user.getImageurl()).into(profile_image);
//
//                }
                readMessage(fUser.getUid(),userid,user.getImageurl());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static SecretKey getSecretEncryptionKey() throws Exception{
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(128); // The AES key size in number of bits
        SecretKey secKey = generator.generateKey();
        return secKey;
    }


    private void sendMessage(String sender, String receiver, String message){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        message = AESEncryption(message);

        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);

        reference.child("Chats").push().setValue(hashMap);
    }

    private void readMessage(final String myid, final String userid, final String imageurl){
        mChat = new ArrayList<>();
        userReference = FirebaseDatabase.getInstance().getReference("Chats");
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(myid) &&chat.getSender().equals(userid)
                        || chat.getReceiver().equals(userid) && chat.getSender().equals(myid)){

                        try {
                            strMessage = chat.getMessage();
                            chat.setMessage(AESDecryption(strMessage));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }


                        mChat.add(chat);
                    }
                    messageAdapter = new MessageAdapter(MessageActivity.this, mChat,imageurl);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public String AESEncryption(String string){
        byte [] strByte = string.getBytes();
        byte [] encryptByte = new byte[strByte.length];

        try{
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            encryptByte = cipher.doFinal(strByte);
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        String result = "";
        try {
            result = new String(encryptByte, "ISO-8859-1");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return  result;
    }

    public String AESDecryption(String string) throws UnsupportedEncodingException {
        byte [] encryptedByte = string.getBytes("ISO-8859-1");
        String decryptedByte = string;

        byte [] decryption;
        try{
            decipher = Cipher.getInstance("AES");
            decipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            decryption = decipher.doFinal(encryptedByte);
            decryptedByte = new String(decryption);
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return decryptedByte;
    }
}

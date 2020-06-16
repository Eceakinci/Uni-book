package com.loory.unibook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.UUID;

public class PostActivity extends AppCompatActivity {
    //Here is imageUrl from get it from firebase
    //imagevies, edit texts and text view for posting activity

    Uri imageUrl;
    String myUrl = "";
    StorageTask uploadTask;
    StorageReference storageReference;


    ImageView close, imageView;
    EditText title, price, numOfPages, edition, author;
    TextView post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);


        //decleration of components of xml file

        close = findViewById(R.id.close);
        imageView = findViewById(R.id.seleced_image);
        post = findViewById(R.id.post);

        author = findViewById(R.id.author);
        title = findViewById(R.id.title);
        price = findViewById(R.id.price);
        numOfPages = findViewById(R.id.numOfPages);
        edition = findViewById(R.id.edition);

        //get "books" information in Firebase Storage
        storageReference = FirebaseStorage.getInstance().getReference("books");

        //when user click X(close in the upper left side of page)
        //go to feed activity
        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //when user click POST(upper right side of the page)
        //return "uploadImage" function
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });
        //implemented library to app gradle
        //i find it in github
        CropImage.activity().setAspectRatio(1,1)
                .start(PostActivity.this);
    }

    private String getFileExtension(Uri uri){

        UUID uuid = UUID.randomUUID();
        final String imageName = "posts/" + uuid + ".jpg";
        return imageName;
    }
    private void uploadImage(){
        //if image url not null(if image chosen) go to storage reference
        if(imageUrl != null){
            final StorageReference fileReference = storageReference.child(getFileExtension(imageUrl));

            uploadTask = fileReference.putFile(imageUrl);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri  downloadUri = task.getResult();
                        myUrl = downloadUri.toString();

                        //then if task is succesfull(image is choose by user)
                        //Create a hashmap to put informations about book
                        //i choose hashmap for this process because of the key value relationship
                        //if all thing is successfull then create an instance in firebase database
                        //and put all informations in database
                        //firebase database is can also store image url
                        //so we can take this informations for display these in home fragment

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

                        String postid = reference.push().getKey();

                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("postid",postid);
                        hashMap.put("author",author.getText().toString());
                        hashMap.put("edition",edition.getText().toString());
                        hashMap.put("numOfPages",numOfPages.getText().toString());
                        hashMap.put("postimage",myUrl);
                        hashMap.put("price",price.getText().toString());
                        hashMap.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        hashMap.put("title",title.getText().toString());


                        reference.child(postid).setValue(hashMap);
                        //if process is successful than go to feed activity(home fragment)

                        startActivity(new Intent(PostActivity.this,MainActivity.class));
                        finish();
                    }else{
                        Toast.makeText(PostActivity.this,"Failed",Toast.LENGTH_SHORT).show();

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();

                }
            });
        }else{
            Toast.makeText(PostActivity.this,"No Image Selected",Toast.LENGTH_SHORT).show();

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //it is process for crop an image
        //as i said it is implemented library in app gradle
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE ){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUrl = result.getUri();

            imageView.setImageURI(imageUrl);
        }else{
            Toast.makeText(this,"Something gone wrong",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(PostActivity.this,MainActivity.class));
            finish();

        }
    }
}

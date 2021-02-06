package com.example.tmas;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

public class FeedActivity extends AppCompatActivity {

   private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    ArrayList<String> userEmailFB;
    ArrayList<String> userCommentFB;
    ArrayList<String> userImageFB;

    RecylerViewAdapter recylerViewAdapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.tmas_options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.add_post){
            Intent intentToUpload = new Intent(FeedActivity.this, UploadActivity.class);
            startActivity(intentToUpload);
        } else if (item.getItemId() == R.id.sign_out) {
            firebaseAuth.signOut();
            Intent intentToSignUp = new Intent(FeedActivity.this, SignActivity.class);
            startActivity(intentToSignUp);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        userEmailFB = new ArrayList<>();
        userCommentFB = new ArrayList<>();
        userImageFB = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        getDataFromFirestore();
        //Recylerview
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recylerViewAdapter = new RecylerViewAdapter(userEmailFB, userCommentFB, userImageFB);
        recyclerView.setAdapter(recylerViewAdapter);
    }

    public void getDataFromFirestore() {
        // Data Read

        CollectionReference collectionReference = firebaseFirestore.collection("Posts");
        collectionReference.orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                if(e != null) {
                    Toast.makeText(FeedActivity.this, e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
                }
                if (value != null){
                    for (DocumentSnapshot snapshot: value.getDocuments()){
                        Map<String, Object> data = snapshot.getData();
                        String userEmail = "From:  " + (String) data.get("useremail");
                        String downloadUrl = (String) data.get("downloadurl");
                        String comment = (String) data.get("comment");

                        userEmailFB.add(userEmail);
                        userImageFB.add(downloadUrl);
                        userCommentFB.add(comment);

                        recylerViewAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }
}
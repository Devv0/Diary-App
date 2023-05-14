package com.example.diaryapp.diary;

import android.os.Bundle;

import com.example.diaryapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class AddEntry extends AppCompatActivity {
    FirebaseFirestore fStore;
    EditText entryTitle, entryContent;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fStore = FirebaseFirestore.getInstance();
        entryContent = findViewById(R.id.addEntryContent);
        entryTitle = findViewById(R.id.addEntryTitle);

        user = FirebaseAuth.getInstance().getCurrentUser();
        //adds new user entries
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nTitle = entryTitle.getText().toString();
                String nContent = entryContent.getText().toString();

                if(nTitle.isEmpty() || nContent.isEmpty()){
                    Toast.makeText(AddEntry.this, "Cannot be saved", Toast.LENGTH_SHORT).show();
                    return;
                }

                DocumentReference docref = fStore.collection("entries").document(user.getUid()).collection("myNotes").document();
                Map<String,Object> entry = new HashMap<>();
                entry.put("title",nTitle);
                entry.put("content",nContent);

                docref.set(entry).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AddEntry.this, "Entry Added", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddEntry.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.close_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    //discards unwanted note
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.close){
            onBackPressed();
            Toast.makeText(this, "Entry Discarded", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
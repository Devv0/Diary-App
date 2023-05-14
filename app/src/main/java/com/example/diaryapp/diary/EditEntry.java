package com.example.diaryapp.diary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.diaryapp.MainActivity;
import com.example.diaryapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditEntry extends AppCompatActivity {
    Intent data;
    EditText editEntryTitle, editEntryContent;
    FirebaseFirestore fStore;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_entry);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fStore = fStore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        data = getIntent();

        editEntryContent = findViewById(R.id.editEntryContent);
        editEntryTitle = findViewById(R.id.editEntryTitle);

        String entryTitle = data.getStringExtra("title");
        String entryContent = data.getStringExtra("content");

        editEntryTitle.setText(entryTitle);
        editEntryContent.setText(entryContent);

        FloatingActionButton fab = findViewById(R.id.saveEdit);
        //lets user edit entries
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nTitle = editEntryTitle.getText().toString();
                String nContent = editEntryContent.getText().toString();

                if(nTitle.isEmpty() || nContent.isEmpty()){
                    Toast.makeText(EditEntry.this, "Cannot be saved", Toast.LENGTH_SHORT).show();
                    return;
                }

                DocumentReference docref = fStore.collection("entries").document(user.getUid()).collection("myNotes").document(data.getStringExtra("entryId"));

                Map<String,Object> entry = new HashMap<>();
                entry.put("title",nTitle);
                entry.put("content",nContent);

                docref.update(entry).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EditEntry.this, "Entry Saved", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditEntry.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
    }
}
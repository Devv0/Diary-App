package com.example.diaryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diaryapp.auth.Login;
import com.example.diaryapp.auth.Register;
import com.example.diaryapp.diary.AddEntry;
import com.example.diaryapp.diary.DiaryDetails;
import com.example.diaryapp.model.Adapter;
import com.example.diaryapp.model.Entry;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView nav_view;
    RecyclerView diaryLists;
    Adapter adapter;
    FirebaseFirestore fStore;
    FirestoreRecyclerAdapter<Entry,EntryViewHolder> entryAdapter;
    FirebaseUser user;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        //gets data from firebase
        Query query = fStore.collection("entries").document(user.getUid()).collection("myNotes").orderBy("title", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Entry> allEntries = new FirestoreRecyclerOptions.Builder<Entry>()
                .setQuery(query, Entry.class)
                .build();
        //displays diary entries
        entryAdapter = new FirestoreRecyclerAdapter<Entry, EntryViewHolder>(allEntries) {
            @Override
            protected void onBindViewHolder(@NonNull EntryViewHolder entryViewHolder, int i, @NonNull Entry entry) {
                entryViewHolder.diaryTitle.setText(entry.getTitle());
                entryViewHolder.diaryContent.setText(entry.getContent());
                String docId = entryAdapter.getSnapshots().getSnapshot(i).getId();

                entryViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(v.getContext(), DiaryDetails.class);
                        i.putExtra("title", entry.getTitle());
                        i.putExtra("content", entry.getContent());
                        i.putExtra("entryId",docId);
                        v.getContext().startActivity(i);
                    }
                });
            }

            @NonNull
            @Override
            public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.diary_view_layout,parent, false);
                return new EntryViewHolder(view);
            }
        };

        diaryLists = findViewById(R.id.diarylist);
        //handles nav menu
        drawerLayout = findViewById(R.id.drawer);
        nav_view = findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();

        diaryLists.setLayoutManager(new LinearLayoutManager(this));
        diaryLists.setAdapter(entryAdapter);

        View headerView = nav_view.getHeaderView(0);
        TextView username = headerView.findViewById(R.id.userDisplayName);
        TextView userEmail = headerView.findViewById(R.id.userDisplayEmail);
        //displays user details
        if(user.isAnonymous()){
            userEmail.setVisibility(View.GONE);
            username.setText("Temp User");
        }else{
            username.setText(user.getDisplayName());
            userEmail.setText(user.getEmail());
        }
        //sends user to add entry
        FloatingActionButton fab = findViewById(R.id.addEntryFloat);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(nav_view.getContext(), AddEntry.class));

            }
        });

    }
    //handles nav menu buttons
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        switch (item.getItemId()){
            case R.id.addEntry:
                startActivity(new Intent(this, AddEntry.class));
                break;
            case R.id.logout:
                checkUser();
                break;
            case R.id.sync:
                if(user.isAnonymous()){
                    startActivity(new Intent(this, Login.class));
                }else{
                    Toast.makeText(this, "Account Connected", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void checkUser() {
        if(user.isAnonymous()){
            displayAlert();
        }else{
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(),Splash.class));
        }
    }
    //handles logout
    private void displayAlert() {
        AlertDialog.Builder warning = new AlertDialog.Builder(this)
                .setTitle("Are you sure?")
                .setMessage("You are a Temporary user. Logging out will delete Entries")
                .setPositiveButton("Create Account", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), Register.class));
                        finish();
                    }
                }).setNegativeButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startActivity(new Intent(getApplicationContext(),Splash.class));
                                finish();
                            }
                        });
                    }
                });
        warning.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.settings){
            Toast.makeText(this, "Settings Menu is Clicked.", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    public class EntryViewHolder extends RecyclerView.ViewHolder{
        TextView diaryTitle, diaryContent;
        View view;
        public EntryViewHolder(@NonNull View itemView) {
            super(itemView);

            diaryTitle = itemView.findViewById(R.id.titles);
            diaryContent = itemView.findViewById(R.id.content);
            view = itemView;
        }

    }
    @Override
    protected void onStart(){
        super.onStart();
        entryAdapter.startListening();
    }

    @Override
    protected void onStop(){
        super.onStop();
        if(entryAdapter != null){
            entryAdapter.stopListening();;
        }
    }
}
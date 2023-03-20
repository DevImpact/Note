package com.devimpact.inote;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devimpact.inote.admanager.AppOpenManager;
import com.devimpact.inote.admanager.AdManager;
import com.devimpact.inote.addeditnote.AddEditNoteActivity;
import com.devimpact.inote.adapter.NoteListAdapter;
import com.devimpact.inote.model.Note;
import com.devimpact.inote.viewmodel.NoteViewModel;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.AdListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.ads.AdRequest;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    
    private RecyclerView recyclerView;
    private NoteListAdapter noteListAdapter;
    private NoteViewModel noteViewModel;
    private final String ADMOB_APP_OPEN_AD_UNIT_ID = "ca-app-pub-7977505325397665/7574997795";
    private final String NATIVE_AD_UNIT_ID = "ca-app-pub-7977505325397665/6487763944";
    private AdView mAdView;
    private AdManager adManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // Start App Open Ad
        AppOpenManager appOpenManager = new AppOpenManager(this, ADMOB_APP_OPEN_AD_UNIT_ID);
        appOpenManager.fetchAd();

        recyclerView = findViewById(R.id.note_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        mAdView = findViewById(R.id.adView);
        adManager = new AdManager(this, NATIVE_AD_UNIT_ID);
        adManager.loadNativeAd();

        noteListAdapter = new NoteListAdapter(mAdView, adManager);
        recyclerView.setAdapter(noteListAdapter);

        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                noteListAdapter.setNotes(notes);
            }
        });

        FloatingActionButton fab = findViewById(R.id.add_note_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
                startActivityForResult(intent, ADD_NOTE_REQUEST);
            }
        });

        // Set an ad listener to the ad view.
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mAdView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
                mAdView.setVisibility(View.GONE);
            }
        });

        // Add an action for the Share button.
        Button shareBtn = findViewById(R.id.social_link);
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.devimpact.inote"));
                startActivity(intent);
            }
        });

        // Add a link to the developer's profile.
        TextView devProfileLink = findViewById(R.id.google_link);
        devProfileLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(
                        "https://play.google.com/store/apps/dev?id=8467983811225231952"));
                startActivity(intent);
            }
        });

        // Add a link to the privacy policy.
        TextView privacyPolicyLink = findViewById(R.id.privacy_policy);
        privacyPolicyLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://sites.google.com/view/dev-impact/privacy-policy"));
                startActivity(intent);
            }
        });
    }

    private static final int ADD_NOTE_REQUEST = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_NOTE_REQUEST && resultCode == RESULT_OK) {
            String title = data.getStringExtra(AddEditNoteActivity.EXTRA_TITLE);
            String description = data.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION);
            String priority = data.getStringExtra(AddEditNoteActivity.EXTRA_PRIORITY);

            Note note = new Note(title, description, priority);
            noteViewModel.insert(note);

            Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Note not saved", Toast.LENGTH_SHORT).show();
        }
    }
}
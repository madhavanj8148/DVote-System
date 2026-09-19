package com.example.dvotesystem;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class LanguageActivity extends BaseActivity {

    private LanguageAdapter adapter;
    private List<Language> languageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        RecyclerView rvLanguages = findViewById(R.id.rvLanguages);
        SearchView searchView = findViewById(R.id.searchView);

        setupLanguageList();

        adapter = new LanguageAdapter(languageList, language -> changeLanguage(language.getCode()));
        rvLanguages.setLayoutManager(new LinearLayoutManager(this));
        rvLanguages.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return true;
            }
        });
    }

    private void setupLanguageList() {
        languageList = new ArrayList<>();
        languageList.add(new Language(getString(R.string.english), "en", "🇺🇸"));
        languageList.add(new Language(getString(R.string.telugu), "te", "🇮🇳"));
        languageList.add(new Language(getString(R.string.hindi), "hi", "🇮🇳"));
        languageList.add(new Language(getString(R.string.assamese), "as", "🇮🇳"));
        languageList.add(new Language(getString(R.string.bengali), "bn", "🇮🇳"));
        languageList.add(new Language(getString(R.string.bodo), "brx", "🇮🇳"));
        languageList.add(new Language(getString(R.string.dogri), "doi", "🇮🇳"));
        languageList.add(new Language(getString(R.string.gujarati), "gu", "🇮🇳"));
        languageList.add(new Language(getString(R.string.kannada), "kn", "🇮🇳"));
        languageList.add(new Language(getString(R.string.kashmiri), "ks", "🇮🇳"));
        languageList.add(new Language(getString(R.string.konkani), "kok", "🇮🇳"));
        languageList.add(new Language(getString(R.string.maithili), "mai", "🇮🇳"));
        languageList.add(new Language(getString(R.string.malayalam), "ml", "🇮🇳"));
        languageList.add(new Language(getString(R.string.manipuri), "mni", "🇮🇳"));
        languageList.add(new Language(getString(R.string.marathi), "mr", "🇮🇳"));
        languageList.add(new Language(getString(R.string.nepali), "ne", "🇮🇳"));
        languageList.add(new Language(getString(R.string.odia), "or", "🇮🇳"));
        languageList.add(new Language(getString(R.string.punjabi), "pa", "🇮🇳"));
        languageList.add(new Language(getString(R.string.sanskrit), "sa", "🇮🇳"));
        languageList.add(new Language(getString(R.string.santali), "sat", "🇮🇳"));
        languageList.add(new Language(getString(R.string.sindhi), "sd", "🇮🇳"));
        languageList.add(new Language(getString(R.string.tamil), "ta", "🇮🇳"));
        languageList.add(new Language(getString(R.string.urdu), "ur", "🇮🇳"));
    }

    private void changeLanguage(String lang) {
        LocaleHelper.setLocale(this, lang);
        Intent intent = new Intent(this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}

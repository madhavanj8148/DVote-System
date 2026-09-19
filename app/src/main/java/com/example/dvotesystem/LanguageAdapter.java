package com.example.dvotesystem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder> {

    private List<Language> languages;
    private List<Language> filteredLanguages;
    private OnLanguageClickListener listener;

    public interface OnLanguageClickListener {
        void onLanguageClick(Language language);
    }

    public LanguageAdapter(List<Language> languages, OnLanguageClickListener listener) {
        this.languages = languages;
        this.filteredLanguages = new ArrayList<>(languages);
        this.listener = listener;
    }

    @NonNull
    @Override
    public LanguageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_language, parent, false);
        return new LanguageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LanguageViewHolder holder, int position) {
        Language language = filteredLanguages.get(position);
        holder.tvLanguageName.setText(language.getName());
        holder.tvFlag.setText(language.getFlag());
        holder.itemView.setOnClickListener(v -> listener.onLanguageClick(language));
    }

    @Override
    public int getItemCount() {
        return filteredLanguages.size();
    }

    public void filter(String text) {
        filteredLanguages.clear();
        if (text.isEmpty()) {
            filteredLanguages.addAll(languages);
        } else {
            text = text.toLowerCase();
            for (Language item : languages) {
                if (item.getName().toLowerCase().contains(text)) {
                    filteredLanguages.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class LanguageViewHolder extends RecyclerView.ViewHolder {
        TextView tvLanguageName;
        TextView tvFlag;

        public LanguageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLanguageName = itemView.findViewById(R.id.tvLanguageName);
            tvFlag = itemView.findViewById(R.id.tvFlag);
        }
    }
}

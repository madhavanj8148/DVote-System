package com.example.dvotesystem;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class  CandidateAdapter extends RecyclerView.Adapter<CandidateAdapter.CandidateViewHolder> {

    private List<Candidate> candidateList;

    public CandidateAdapter(List<Candidate> candidateList) {
        this.candidateList = candidateList;
    }

    @NonNull
    @Override
    public CandidateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_candidate, parent, false);
        return new CandidateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CandidateViewHolder holder, int position) {
        Candidate candidate = candidateList.get(position);
        holder.tvCandidateName.setText(candidate.candidateName);
        holder.tvPartyName.setText(candidate.partyName);
        
        // Load Candidate Photo
        if (!TextUtils.isEmpty(candidate.photoUrl)) {
            Glide.with(holder.itemView.getContext())
                    .load(candidate.photoUrl)
                    .placeholder(android.R.drawable.ic_menu_report_image)
                    .error(android.R.drawable.ic_menu_report_image)
                    .into(holder.ivCandidatePhoto);
        } else {
            holder.ivCandidatePhoto.setImageResource(android.R.drawable.ic_menu_report_image);
        }

        // Load Party Symbol
        if (!TextUtils.isEmpty(candidate.partySymbol)) {
            Glide.with(holder.itemView.getContext())
                    .load(candidate.partySymbol)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_gallery)
                    .into(holder.ivPartySymbol);
        } else {
            holder.ivPartySymbol.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }

    @Override
    public int getItemCount() {
        return candidateList.size();
    }

    public static class CandidateViewHolder extends RecyclerView.ViewHolder {
        TextView tvCandidateName, tvPartyName;
        ImageView ivCandidatePhoto, ivPartySymbol;

        public CandidateViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCandidateName = itemView.findViewById(R.id.tvCandidateName);
            tvPartyName = itemView.findViewById(R.id.tvPartyName);
            ivCandidatePhoto = itemView.findViewById(R.id.ivCandidatePhoto);
            ivPartySymbol = itemView.findViewById(R.id.ivPartySymbol);
        }
    }
}

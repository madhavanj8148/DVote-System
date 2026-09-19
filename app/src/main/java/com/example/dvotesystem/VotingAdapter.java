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
import com.google.android.material.button.MaterialButton;
import java.util.List;

public class VotingAdapter extends RecyclerView.Adapter<VotingAdapter.VotingViewHolder> {

    private List<Candidate> candidateList;
    private OnVoteClickListener listener;

    public interface OnVoteClickListener {
        void onVoteClick(Candidate candidate);
    }

    public VotingAdapter(List<Candidate> candidateList, OnVoteClickListener listener) {
        this.candidateList = candidateList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VotingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_voting_candidate, parent, false);
        return new VotingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VotingViewHolder holder, int position) {
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

        holder.btnVoteNow.setOnClickListener(v -> listener.onVoteClick(candidate));
    }

    @Override
    public int getItemCount() {
        return candidateList.size();
    }

    public static class VotingViewHolder extends RecyclerView.ViewHolder {
        TextView tvCandidateName, tvPartyName;
        ImageView ivCandidatePhoto;
        MaterialButton btnVoteNow;

        public VotingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCandidateName = itemView.findViewById(R.id.tvCandidateName);
            tvPartyName = itemView.findViewById(R.id.tvPartyName);
            ivCandidatePhoto = itemView.findViewById(R.id.ivCandidatePhoto);
            btnVoteNow = itemView.findViewById(R.id.btnVoteNow);
        }
    }
}

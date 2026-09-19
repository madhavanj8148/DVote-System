package com.example.dvotesystem;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import java.util.List;
import java.util.Locale;

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ResultsViewHolder> {

    private List<Candidate> candidateList;
    private int totalVotes = 0;

    public ResultsAdapter(List<Candidate> candidateList) {
        this.candidateList = candidateList;
        calculateTotalVotes();
    }

    private void calculateTotalVotes() {
        totalVotes = 0;
        for (Candidate c : candidateList) {
            totalVotes += c.voteCount;
        }
    }

    public void updateList(List<Candidate> newList) {
        this.candidateList = newList;
        calculateTotalVotes();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ResultsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_result_candidate, parent, false);
        return new ResultsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultsViewHolder holder, int position) {
        Candidate candidate = candidateList.get(position);
        holder.tvResultName.setText(candidate.candidateName);
        holder.tvResultParty.setText(candidate.partyName);
        
        // Count-up animation for votes
        ValueAnimator animator = ValueAnimator.ofInt(0, candidate.voteCount);
        animator.setDuration(1000);
        animator.addUpdateListener(animation -> holder.tvResultVotes.setText(animation.getAnimatedValue().toString()));
        animator.start();

        // Progress and Percentage animation
        double percentage = totalVotes > 0 ? (candidate.voteCount * 100.0) / totalVotes : 0.0;
        holder.progressResult.setProgress(0);
        holder.progressResult.setProgressCompat((int) Math.round(percentage), true);
        
        ValueAnimator percentAnimator = ValueAnimator.ofFloat(0f, (float) percentage);
        percentAnimator.setDuration(1200);
        percentAnimator.addUpdateListener(animation -> {
            float val = (float) animation.getAnimatedValue();
            holder.tvResultPercentage.setText(String.format(Locale.getDefault(), "%.1f%%", val));
        });
        percentAnimator.start();

        // Load Photo
        if (!TextUtils.isEmpty(candidate.photoUrl)) {
            Glide.with(holder.itemView.getContext())
                    .load(candidate.photoUrl)
                    .placeholder(R.drawable.ic_person)
                    .into(holder.ivPhoto);
        } else {
            holder.ivPhoto.setImageResource(R.drawable.ic_person);
        }

        if (position == 0 && candidate.voteCount > 0) {
            holder.tvWinnerTag.setVisibility(View.VISIBLE);
            holder.cardResult.setStrokeColor(Color.parseColor("#00C853"));
            holder.cardResult.setStrokeWidth(4);
        } else {
            holder.tvWinnerTag.setVisibility(View.GONE);
            holder.cardResult.setStrokeWidth(0);
        }
    }

    @Override
    public int getItemCount() {
        return candidateList.size();
    }

    public static class ResultsViewHolder extends RecyclerView.ViewHolder {
        TextView tvResultName, tvResultParty, tvResultVotes, tvWinnerTag, tvResultPercentage;
        LinearProgressIndicator progressResult;
        MaterialCardView cardResult;
        ImageView ivPhoto;

        public ResultsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvResultName = itemView.findViewById(R.id.tvResultName);
            tvResultParty = itemView.findViewById(R.id.tvResultParty);
            tvResultVotes = itemView.findViewById(R.id.tvResultVotes);
            tvWinnerTag = itemView.findViewById(R.id.tvWinnerTag);
            tvResultPercentage = itemView.findViewById(R.id.tvResultPercentage);
            progressResult = itemView.findViewById(R.id.progressResult);
            cardResult = itemView.findViewById(R.id.cardResult);
            ivPhoto = itemView.findViewById(R.id.ivResultCandidatePhoto);
        }
    }
}

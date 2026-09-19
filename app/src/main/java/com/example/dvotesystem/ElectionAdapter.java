package com.example.dvotesystem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import java.util.List;

public class ElectionAdapter extends RecyclerView.Adapter<ElectionAdapter.ElectionViewHolder> {

    private List<Election> electionList;
    private OnElectionClickListener listener;
    private boolean isAdmin = false;

    public interface OnElectionClickListener {
        void onElectionClick(Election election);
        void onDeleteClick(Election election);
    }

    public ElectionAdapter(List<Election> electionList, OnElectionClickListener listener) {
        this.electionList = electionList;
        this.listener = listener;
    }

    public void setAdmin(boolean admin) {
        this.isAdmin = admin;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ElectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_election, parent, false);
        return new ElectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ElectionViewHolder holder, int position) {
        Election election = electionList.get(position);
        holder.tvElectionTitle.setText(election.title);
        holder.tvElectionType.setText(election.type);
        holder.tvElectionDate.setText(election.date);
        
        if ("State".equals(election.type)) {
            holder.tvElectionState.setVisibility(View.VISIBLE);
            holder.tvElectionState.setText(election.state);
        } else {
            holder.tvElectionState.setVisibility(View.GONE);
        }

        holder.chipStatus.setText(election.status);
        if ("Active".equals(election.status)) {
            holder.chipStatus.setChipBackgroundColorResource(android.R.color.holo_green_light);
        } else {
            holder.chipStatus.setChipBackgroundColorResource(android.R.color.darker_gray);
        }

        holder.btnDelete.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(election));

        holder.itemView.setOnClickListener(v -> listener.onElectionClick(election));
    }

    @Override
    public int getItemCount() {
        return electionList.size();
    }

    static class ElectionViewHolder extends RecyclerView.ViewHolder {
        TextView tvElectionTitle, tvElectionType, tvElectionState, tvElectionDate;
        Chip chipStatus;
        ImageButton btnDelete;

        public ElectionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvElectionTitle = itemView.findViewById(R.id.tvElectionTitle);
            tvElectionType = itemView.findViewById(R.id.tvElectionType);
            tvElectionState = itemView.findViewById(R.id.tvElectionState);
            tvElectionDate = itemView.findViewById(R.id.tvElectionDate);
            chipStatus = itemView.findViewById(R.id.chipStatus);
            btnDelete = itemView.findViewById(R.id.btnDeleteElection);
        }
    }
}

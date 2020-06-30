package com.example.chat_app.fragments.ui.pending;

import android.os.IInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat_app.R;
import com.example.chat_app.Recycler_Click;
import com.example.chat_app.fragments.ui.search.SearchAdapter;

public class PendingAdapter extends RecyclerView.Adapter<PendingAdapter.ViewHolder> {

    PendingDataHolder pendingDataHolder;
    Recycler_Click recycler_click;
    OnDeclineListener onDeclineListener;

    public PendingAdapter(Recycler_Click recycler_click, OnDeclineListener declineListener) {
        this.pendingDataHolder = PendingDataHolder.getInstance();
        this.recycler_click = recycler_click;
        this.onDeclineListener = declineListener;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pending_layout_container, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, recycler_click, onDeclineListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.username.setText(pendingDataHolder.getUsername().get(position));
        if (pendingDataHolder.getStatus_permissions().get(position)){
            holder.status.setVisibility(View.VISIBLE);
            holder.status.setText(pendingDataHolder.getStatus().get(position));
        } else {
            holder.status.setVisibility(View.GONE);
        }

        //Picasso.get().load(dataHolder.getPic_url().get(position)).into(holder.profilepicture);
        holder.profilepicture.setImageResource(R.drawable.cat_pp);
    }

    @Override
    public int getItemCount() {
        return pendingDataHolder.getUsername().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView profilepicture;
        TextView username;
        TextView status;
        Button button;
        Button decline;
        Recycler_Click recycler_click;
        OnDeclineListener declineListener;

        public ViewHolder(@NonNull View itemView, Recycler_Click recycler_click, OnDeclineListener onDeclineListener) {
            super(itemView);

            profilepicture = itemView.findViewById(R.id.imageView);
            username = itemView.findViewById(R.id.username);
            status = itemView.findViewById(R.id.status_text);
            button = itemView.findViewById(R.id.button_add);
            decline = itemView.findViewById(R.id.decline);
            this.recycler_click = recycler_click;
            this.declineListener = onDeclineListener;

            button.setOnClickListener(this);
            decline.setOnClickListener(decline_click);
        }

        @Override
        public void onClick(View v) {

            recycler_click.OnRecyclerClickListener(getAdapterPosition());
        }

        View.OnClickListener decline_click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                declineListener.OnDeclineClick(getAdapterPosition());
            }
        };

    }

    public interface OnDeclineListener{
        void OnDeclineClick(int position);
    }
}

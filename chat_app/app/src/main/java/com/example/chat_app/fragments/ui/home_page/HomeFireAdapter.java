package com.example.chat_app.fragments.ui.home_page;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat_app.R;
import com.example.chat_app.click_manager.Recycler_Click;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

public class HomeFireAdapter extends FirestoreRecyclerAdapter<HomeFireChats, HomeFireAdapter.ViewHolder> {

    Recycler_Click recycler_click;

    public HomeFireAdapter(@NonNull FirestoreRecyclerOptions<HomeFireChats> options, Recycler_Click recycler_click) {
        super(options);
        this.recycler_click = recycler_click;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull HomeFireChats model) {

        holder.username.setText(model.getUsername());
        holder.message.setText(model.getMessage());
        if (model.getPp_url().equals("")) {
            holder.pp.setImageResource(R.drawable.cat_pp);
        } else {
            Picasso.get().load(model.getPp_url()).into(holder.pp);
        }
        holder.invisible_url.setText(model.getPp_url());

        if (position == getItemCount()-1){
            holder.view_line.setVisibility(View.GONE);
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_chats_recycler, parent, false);
        return new ViewHolder(view, recycler_click);
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public TextView username;
        public TextView message;
        public ImageView pp;
        public TextView invisible_url;
        public ConstraintLayout constraintLayout;
        public View view_line;
        public View view_cover;
        Recycler_Click recycler_click;

        public ViewHolder(@NonNull View itemView, Recycler_Click recycler_click) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            message = itemView.findViewById(R.id.message);
            pp = itemView.findViewById(R.id.imageView_pp);
            invisible_url = itemView.findViewById(R.id.invisible_pp_url);
            constraintLayout = itemView.findViewById(R.id.layout_container);
            view_line = itemView.findViewById(R.id.view_line);
            view_cover = itemView.findViewById(R.id.view_cover);

            this.recycler_click = recycler_click;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            recycler_click.OnRecyclerClickListener(username.getText().toString(), invisible_url.getText().toString(), view_cover, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            recycler_click.OnRecyclerClickListener(view_cover, getAdapterPosition(), username);
            return true;
        }
    }

}

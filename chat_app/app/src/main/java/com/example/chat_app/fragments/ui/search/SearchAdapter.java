package com.example.chat_app.fragments.ui.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chat_app.R;
import com.example.chat_app.click_manager.Recycler_Click;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    SearchDataHolder searchDataHolder;
    Recycler_Click recycler_click;
    Context context;

    public SearchAdapter(Recycler_Click recycler_click, Context context) {
        searchDataHolder = SearchDataHolder.getInstance();
        this.recycler_click = recycler_click;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_layout_container, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, recycler_click);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.username.setText(searchDataHolder.getUsername().get(position));
        if (searchDataHolder.getStatus_permissions().get(position)){
            holder.status.setVisibility(View.VISIBLE);
            holder.status.setText(searchDataHolder.getStatus().get(position));
        } else {
            holder.status.setVisibility(View.GONE);
        }

        if (searchDataHolder.getPic_permissions().get(position)){
            if (!searchDataHolder.getPic_url().get(position).equals("")){
                Glide
                        .with(context)
                        .load(searchDataHolder.getPic_url().get(position))
                        .centerCrop()
                        .into(holder.profilepicture);
            }
        } else {
            holder.profilepicture.setImageResource(R.drawable.default_pp);
        }


    }



    @Override
    public int getItemCount() {
        return searchDataHolder.getUsername().size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView profilepicture;
        TextView username;
        TextView status;
        Button button;
        Recycler_Click recycler_click;

        public ViewHolder(@NonNull View itemView, Recycler_Click recycler_click) {
            super(itemView);

            profilepicture = itemView.findViewById(R.id.imageView);
            username = itemView.findViewById(R.id.username);
            status = itemView.findViewById(R.id.status_text);
            button = itemView.findViewById(R.id.button_add);
            this.recycler_click = recycler_click;

            button.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            recycler_click.OnRecyclerClickListener(getAdapterPosition());
        }
    }


}

package com.example.chat_app.fragments.ui.search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat_app.R;
import com.example.chat_app.Recycler_Click;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    SearchDataHolder dataHolder;
    Recycler_Click recycler_click;

    public SearchAdapter(Recycler_Click recycler_click) {
        dataHolder = SearchDataHolder.getInstance();
        this.recycler_click = recycler_click;
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

        holder.username.setText(dataHolder.getUsername().get(position));
        if (dataHolder.getStatus_permissions().get(position)){
            holder.status.setText(dataHolder.getStatus().get(position));
        } else {
            holder.status.setVisibility(View.GONE);
        }

        //Picasso.get().load(dataHolder.getPic_url().get(position)).into(holder.profilepicture);
        holder.profilepicture.setImageResource(R.drawable.cat_pp);

    }



    @Override
    public int getItemCount() {
        return dataHolder.getUsername().size();
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
            recycler_click.OnContactAddClickListener(getAdapterPosition());
        }
    }


}

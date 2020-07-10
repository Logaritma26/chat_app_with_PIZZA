package com.example.chat_app.fragments.ui.contact_list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat_app.R;
import com.example.chat_app.click_manager.Recycler_Click;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

public class FireAdapter_Contact extends FirestoreRecyclerAdapter<ContactInformation,FireAdapter_Contact.ViewHolder> {

    Recycler_Click recycler_click;

    public FireAdapter_Contact(@NonNull FirestoreRecyclerOptions<ContactInformation> options, Recycler_Click recycler_click) {
        super(options);
        this.recycler_click = recycler_click;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull ContactInformation model) {
        holder.username.setText(model.getUsername());
        holder.status.setText(model.getStatus());
        if (model.getPp().equals("")){
            holder.profile_picture.setImageResource(R.drawable.cat_pp);
        } else {
            Picasso.get().load(model.getPp()).into(holder.profile_picture);
        }
        holder.pp_url_ghost.setText(model.getPp());
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_recycler_container, parent, false);
        return new ViewHolder(view, recycler_click);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView profile_picture;
        public TextView username;
        public TextView status;
        public TextView pp_url_ghost;
        Recycler_Click recycler_click;


        public ViewHolder(@NonNull View itemView,Recycler_Click recycler_click) {
            super(itemView);

            profile_picture = itemView.findViewById(R.id.imageView_pp);
            username = itemView.findViewById(R.id.username);
            status = itemView.findViewById(R.id.status);
            pp_url_ghost = itemView.findViewById(R.id.invisible_pp_url);
            this.recycler_click = recycler_click;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            recycler_click.OnRecyclerClickListener(username.getText().toString(), pp_url_ghost.getText().toString());
        }
    }
}

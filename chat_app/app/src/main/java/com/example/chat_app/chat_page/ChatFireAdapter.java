package com.example.chat_app.chat_page;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.Image;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat_app.ContainerMethods;
import com.example.chat_app.R;
import com.example.chat_app.click_manager.Recycler_Click;
import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChatFireAdapter extends FirestoreRecyclerAdapter<ChatFireMessages, ChatFireAdapter.ViewHolder> {

    RecyclerView recyclerView;
    Recycler_Click recycler_click;
    String own_name;
    Context context;
    FirebaseFirestore db;
    String username;
    private int counter = 0;
    Integer adapter_position_seen = null;

    public ChatFireAdapter(@NonNull FirestoreRecyclerOptions<ChatFireMessages> options,
                           Recycler_Click recycler_click, String own_name, Context context, RecyclerView recyclerView,
                           FirebaseFirestore db, String username) {
        super(options);
        this.recycler_click = recycler_click;
        this.own_name = own_name;
        this.context = context;
        this.recyclerView = recyclerView;
        this.db = db;
        this.username = username;
        counter = getItemCount();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull ChatFireMessages model) {

        counter = getItemCount();

        holder.setIsRecyclable(false);
        holder.message.setText(model.getMessage());

        if (position == 0) {
            int margin = setMargin(24);
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(holder.constraintLayout);
            constraintSet.connect(R.id.message_text, ConstraintSet.TOP, R.id.message_layout, ConstraintSet.TOP, margin);
            constraintSet.applyTo(holder.constraintLayout);
        }


        if (model.getUsername().equals(own_name)) {

            if (position != 0) {
                if (getItem(position - 1).getUsername().equals(own_name)) {
                    holder.message.setBackgroundResource(R.drawable.curve11);
                } else {
                    holder.message.setBackground(context.getDrawable(R.drawable.curve1));
                }
            } else {
                holder.message.setBackground(context.getDrawable(R.drawable.curve1));
            }

            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(holder.constraintLayout);
            constraintSet.connect(R.id.message_text, ConstraintSet.END, R.id.message_layout, ConstraintSet.END, 12);
            constraintSet.applyTo(holder.constraintLayout);

            Boolean check = model.getSeen();

            if (check != null){
                if (model.getSeen()){
                    /*if (adapter_position_seen != null){
                        if (recyclerView != null){
                            if (recyclerView.getChildAt(adapter_position_seen) != null){
                                if (recyclerView.getChildAt(adapter_position_seen).findViewById(R.id.seen_image) != null){
                                    ImageView view = recyclerView.getChildAt(adapter_position_seen).findViewById(R.id.seen_image);
                                    view.setVisibility(View.GONE);
                                }
                            }
                        }
                    }
                    adapter_position_seen = position;*/
                    holder.seen_image.setVisibility(View.VISIBLE);
                }
            }

        }
        else {

            if (position != 0) {
                if (!getItem(position - 1).getUsername().equals(own_name)) {
                    holder.message.setBackgroundResource(R.drawable.curve22);
                } else {
                    holder.message.setBackground(context.getDrawable(R.drawable.curve2));
                }
            } else {
                holder.message.setBackground(context.getDrawable(R.drawable.curve2));
            }

            holder.message.setTextColor(Color.BLACK);

            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(holder.constraintLayout);
            constraintSet.connect(R.id.message_text, ConstraintSet.START, R.id.message_layout, ConstraintSet.START, 12);
            constraintSet.applyTo(holder.constraintLayout);

            if (position == getItemCount()-1){
                ContainerMethods.message_seen(db, username);
            }
        }

        //holder.message.requestLayout();
    }


    @Override
    public void onDataChanged() {
        super.onDataChanged();
        if (getItemCount() > counter) {
            recyclerView.scrollToPosition(getItemCount() - 1);
            counter = getItemCount();
            notifyDataSetChanged();
        } else {

        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messages_container, parent, false);
        return new ViewHolder(view, recycler_click);
    }

    @Override
    public void onChildChanged(@NonNull ChangeEventType type, @NonNull DocumentSnapshot snapshot, int newIndex, int oldIndex) {
        super.onChildChanged(type, snapshot, newIndex, oldIndex);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView message;
        public ConstraintLayout constraintLayout;
        public ImageView seen_image;
        Recycler_Click recycler_click;

        public ViewHolder(@NonNull View itemView, Recycler_Click recycler_click) {
            super(itemView);
            message = itemView.findViewById(R.id.message_text);
            constraintLayout = itemView.findViewById(R.id.message_layout);
            this.recycler_click = recycler_click;
            seen_image = itemView.findViewById(R.id.seen_image);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            recycler_click.OnRecyclerClickListener(message.getText().toString(), "none", "none");
        }
    }

    private int setMargin(int margin) {
        Resources r = context.getResources();
        int px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                margin,
                r.getDisplayMetrics()
        );
        return px;
    }

}

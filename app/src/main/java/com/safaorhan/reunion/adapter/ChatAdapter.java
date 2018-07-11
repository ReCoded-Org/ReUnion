package com.safaorhan.reunion.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.safaorhan.reunion.R;
import com.safaorhan.reunion.model.Message;

public class ChatAdapter extends FirestoreRecyclerAdapter<Message, ChatAdapter.ChatHolder> {

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ChatAdapter(@NonNull FirestoreRecyclerOptions<Message> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatHolder holder, int position, @NonNull Message model) {
        holder.bind(model);
    }

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ChatHolder(itemView);
    }

    public class ChatHolder extends RecyclerView.ViewHolder {
        View itemView;
        TextView chatNameText;
        TextView chatMessageText;

        public ChatHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            chatNameText = itemView.findViewById(R.id.chat_item_name);
            chatMessageText = itemView.findViewById(R.id.chat_item_message);
        }

        public void bind(final Message message) {
            message.getFrom().get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot snapshot) {
                    chatNameText.setText(snapshot.getString("name"));
                    chatMessageText.setText(message.getText());
                }
            });
        }
    }

    public static ChatAdapter get(DocumentReference documentReference) {
        Query query = FirebaseFirestore.getInstance().collection("messages")
                .whereEqualTo("conversation", documentReference)
                .orderBy("sentAt", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class)
                .build();
        return new ChatAdapter(options);
    }
}

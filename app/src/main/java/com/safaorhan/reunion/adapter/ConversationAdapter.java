package com.safaorhan.reunion.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.safaorhan.reunion.FirestoreHelper;
import com.safaorhan.reunion.R;
import com.safaorhan.reunion.activity.MessageActivity;
import com.safaorhan.reunion.model.Conversation;
import com.safaorhan.reunion.model.Message;
import com.safaorhan.reunion.model.User;

public class ConversationAdapter extends FirestoreRecyclerAdapter<Conversation, ConversationAdapter.ConversationHolder> {
    private static final String TAG = ConversationAdapter.class.getSimpleName();
    ConversationClickListener conversationClickListener;


    public ConversationAdapter(@NonNull FirestoreRecyclerOptions<Conversation> options) {
        super(options);
    }

    public ConversationClickListener getConversationClickListener() {
        if (conversationClickListener == null) {
            conversationClickListener = new ConversationClickListener() {
                @Override
                public void onConversationClick(DocumentReference documentReference) {
                    Log.e(TAG, "You need to call setConversationClickListener()" +
                            " to set the click listener of ConversationAdapter");
                }
            };
        }

        return conversationClickListener;
    }

    public void setConversationClickListener(ConversationClickListener conversationClickListener) {
        this.conversationClickListener = conversationClickListener;
    }

    public static ConversationAdapter get() {
        Query query = FirebaseFirestore.getInstance()
                .collection("conversations")
                //.orderBy("timestamp")
                .whereEqualTo(FirestoreHelper.getMe().getId(), true)
                .limit(50);

        FirestoreRecyclerOptions<Conversation> options = new FirestoreRecyclerOptions.Builder<Conversation>()
                .setQuery(query, Conversation.class)
                .build();

        return new ConversationAdapter(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ConversationHolder holder, int position,
                                    @NonNull Conversation conversation) {
        conversation.setId(getSnapshots().getSnapshot(position).getId());
        holder.bind(conversation);
    }

    @NonNull
    @Override
    public ConversationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversation, parent, false);
        return new ConversationHolder(itemView);
    }

    public class ConversationHolder extends RecyclerView.ViewHolder {

        View itemView;
        TextView opponentNameText;
        TextView lastMessageText;
        ImageView conversationImage;
        Context context;

        public ConversationHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            context = itemView.getContext();
            opponentNameText = itemView.findViewById(R.id.opponentNameText);
            lastMessageText = itemView.findViewById(R.id.lastMessageText);
            conversationImage = itemView.findViewById(R.id.conversation_image);

        }

        public void bind(final Conversation conversation) {

            itemView.setVisibility(View.INVISIBLE);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getConversationClickListener()
                            .onConversationClick(FirestoreHelper.getConversationRef(conversation));
                    Intent intent = new Intent(context, MessageActivity.class);
                    context.startActivity(intent);
                }
            });

            conversation.getOpponent().get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User opponent = documentSnapshot.toObject(User.class);
                    String opponentName = opponent.getName();
                    opponentNameText.setText(opponentName);
                    String firstChar = String.valueOf(opponent.getName().charAt(0));

                    // using the text drawable
                    ColorGenerator generator = ColorGenerator.MATERIAL;
                    int color = generator.getColor(opponent.getEmail());
                    TextDrawable drawable = TextDrawable.builder()
                            .buildRound(firstChar, color);
                    conversationImage.setImageDrawable(drawable);

                    itemView.setVisibility(View.VISIBLE);
                }
            });


            if (conversation.getLastMessage() != null) {
                conversation.getLastMessage().get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Message lastMessage = documentSnapshot.toObject(Message.class);
                        lastMessageText.setText(lastMessage.getText());
                    }
                });
            } else {
                lastMessageText.setText("Write something to start a conversation!");
            }

        }
    }

    public interface ConversationClickListener {
        void onConversationClick(DocumentReference conversationRef);
    }
}

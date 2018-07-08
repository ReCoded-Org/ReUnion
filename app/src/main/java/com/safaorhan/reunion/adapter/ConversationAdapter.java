package com.safaorhan.reunion.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.safaorhan.reunion.FirestoreHelper;
import com.safaorhan.reunion.R;
import com.safaorhan.reunion.model.Conversation;
import com.safaorhan.reunion.model.Message;
import com.safaorhan.reunion.model.User;

public class ConversationAdapter extends FirestoreRecyclerAdapter<Conversation, ConversationAdapter.ConversationHolder> {
    private static final String TAG = ConversationAdapter.class.getSimpleName();
    ConversationClickListener conversationClickListener;
    private Context context = null;

    public ConversationAdapter(@NonNull FirestoreRecyclerOptions<Conversation> options) {
        super(options);
    }


    public ConversationClickListener getConversationClickListener() {
        if (conversationClickListener == null) {
            conversationClickListener = new ConversationClickListener() {
                @Override
                public void onConversationClick(DocumentReference documentReference) {
                    Log.e(TAG, "You need to call setConversationClickListener() to set the click listener of ConversationAdapter");
                }
            };
        }

        return conversationClickListener;
    }

    public void setConversationClickListener(ConversationClickListener conversationClickListener) {
        this.conversationClickListener = conversationClickListener;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public static ConversationAdapter get() {
        Query query = FirebaseFirestore.getInstance()
                .collection("conversations")
                //.orderBy("timestamp")
                .limit(50);

        FirestoreRecyclerOptions<Conversation> options = new FirestoreRecyclerOptions.Builder<Conversation>()
                .setQuery(query, Conversation.class)
                .build();

        return new ConversationAdapter(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ConversationHolder holder, int position, @NonNull Conversation conversation) {
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
        TextView coloredCircleText;
        GradientDrawable coloredCircleDrawable;
        private User opponentUser = null;
        private Message lastMessage = null;
        private DocumentSnapshot opponentDS = null;


        public ConversationHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            opponentNameText = itemView.findViewById(R.id.opponentNameText);
            lastMessageText = itemView.findViewById(R.id.lastMessageText);
            coloredCircleText = itemView.findViewById(R.id.coloredCircleText);
            coloredCircleDrawable = (GradientDrawable) coloredCircleText.getBackground();
        }

        public void bind(final Conversation conversation) {
            if (opponentUser == null) {
                conversation.getOpponent().get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        opponentDS = documentSnapshot;
                        opponentUser = documentSnapshot.toObject(User.class);
                        opponentNameText.setText(opponentUser.getName());
                        coloredCircleText.setText(opponentUser.getName().substring(0, 1));
                        if (getContext() != null) {
                            //coloredCircleDrawable.setColor(ContextCompat.getColor(getContext(), getUserColor(opponentDS)));//TODO 2: Uncomment after applying todo 1.
                        }
                    }
                });
            } else {
                opponentNameText.setText(opponentUser.getName());
                coloredCircleText.setText(opponentUser.getName().substring(0, 1));
                if (getContext() != null) {
                    //coloredCircleDrawable.setColor(ContextCompat.getColor(getContext(), getUserColor(opponentDS)));//TODO 3: Uncomment after applying todo 2.
                }
            }
            if (lastMessage == null) {
                if (conversation.getLastMessage() != null) {
                    conversation.getLastMessage().get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            lastMessage = documentSnapshot.toObject(Message.class);
                            lastMessageText.setText(lastMessage.getText());
                        }
                    });
                } else {
                    lastMessageText.setText("Write something to start a conversation!");
                }
            } else {
                lastMessageText.setText(lastMessage.getText());
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getConversationClickListener().onConversationClick(FirestoreHelper.getConversationRef(conversation));
                }
            });
        }

        private int getUserColor(DocumentSnapshot opponentDS) {
            //TODO 1: return userColor AS int (ID).
            return 0;
        }
    }

    public interface ConversationClickListener {
        void onConversationClick(DocumentReference conversationRef);
    }
}
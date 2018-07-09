package com.safaorhan.reunion.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.google.firebase.firestore.DocumentReference;
import com.safaorhan.reunion.FirestoreHelper;
import com.safaorhan.reunion.R;
import com.safaorhan.reunion.model.Conversation;

public class ChatActivity extends AppCompatActivity {
    DocumentReference conversationRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        String conversationRefId = getIntent().getStringExtra(Conversation.CONVERSATION_KEY);
        conversationRef = FirestoreHelper.getConversationRefById(conversationRefId);
    }
}
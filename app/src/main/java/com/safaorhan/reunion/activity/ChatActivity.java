package com.safaorhan.reunion.activity;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.firestore.FirebaseFirestore;
import com.safaorhan.reunion.FirestoreHelper;
import com.safaorhan.reunion.R;
import com.safaorhan.reunion.adapter.ChatAdapter;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText chatMessageText;
    private ChatAdapter chatAdapter;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerView = findViewById(R.id.chat_rv);

        chatMessageText = findViewById(R.id.chat_message_et);
        FloatingActionButton fab = findViewById(R.id.chat_fab);
        final String documentPath = getIntent().getStringExtra("documentPath");
        String name= getIntent().getStringExtra("name");
        setTitle(getString(R.string.chat_with)+name);
        chatAdapter = ChatAdapter.get(FirebaseFirestore.getInstance().document(documentPath));
        chatAdapter.setListener(new ChatAdapter.OnChatMessageAddedListener() {
            @Override
            public void onChatMessageAdded(int lastPosition) {
                recyclerView.scrollToPosition(lastPosition);
            }
        });

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(chatAdapter);

        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if(bottom<oldBottom){
                    int messageCount = chatAdapter.getItemCount();
                    if (messageCount!=-1&&messageCount>=1){
                        recyclerView.smoothScrollToPosition(messageCount-1);
                    }
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(chatMessageText.getText().toString().trim())){
                    FirestoreHelper.sendMessage(chatMessageText.getText().toString().trim(), FirebaseFirestore.getInstance().document(documentPath));
                    chatMessageText.setText("");
                }else {
                    chatMessageText.setText("");
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        chatAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        chatAdapter.stopListening();
    }
}

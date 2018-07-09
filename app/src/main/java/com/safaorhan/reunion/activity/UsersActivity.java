package com.safaorhan.reunion.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.safaorhan.reunion.FirestoreHelper;
import com.safaorhan.reunion.R;
import com.safaorhan.reunion.adapter.UserAdapter;
import com.safaorhan.reunion.model.Conversation;
import com.safaorhan.reunion.model.User;

public class UsersActivity extends AppCompatActivity implements UserAdapter.UserClickListener {


    RecyclerView recyclerView;
    UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        recyclerView = findViewById(R.id.recyclerView);

        userAdapter = UserAdapter.get();
        userAdapter.setUserClickListener(this);
        userAdapter.setContext(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(userAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        userAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        userAdapter.stopListening();
    }

    @Override
    public void onUserClick(final DocumentReference userRef) {
        FirestoreHelper.findOrCreateConversation(userRef, new FirestoreHelper.DocumentReferenceCallback() {
            @Override
            public void onCompleted(DocumentReference conversationRef) {
                Intent intent = new Intent(UsersActivity.this, ChatActivity.class);
                intent.putExtra(Conversation.CONVERSATION_KEY, conversationRef.getId());
                intent.putExtra(User.USER_KEY, userRef.getId());
                startActivity(intent);
                finish();
            }
        });

    }
}

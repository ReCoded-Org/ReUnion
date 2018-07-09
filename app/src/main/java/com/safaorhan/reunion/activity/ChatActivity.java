package com.safaorhan.reunion.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.safaorhan.reunion.FirestoreHelper;
import com.safaorhan.reunion.R;
import com.safaorhan.reunion.model.Conversation;
import com.safaorhan.reunion.model.User;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = ChatActivity.class.getSimpleName();
    DocumentReference conversationRef;
    DocumentReference opponentUserRef;
    User opponentUser = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        String tempIdHolder = getIntent().getStringExtra(Conversation.CONVERSATION_KEY);
        conversationRef = FirestoreHelper.getConversationRefById(tempIdHolder);
        tempIdHolder = getIntent().getStringExtra(User.USER_KEY);
        opponentUserRef = FirestoreHelper.getUserRefById(tempIdHolder);
        //TODO: Caution!!! below call must be done after above call is finished!!
        opponentUserRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                opponentUser = documentSnapshot.toObject(User.class);
                if (opponentUser == null){
                    Toast.makeText(ChatActivity.this, getString(R.string.opponentUserEmptyError), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Error retraining data: ", e);
            }
        });
        //TODO: Caution!!! below setTitle() must be set after above call is finished and opponentUser is filled!!
        //setTitle(getString(R.string.chatActivityStarterTitle) + " " + opponentUser.getName());
    }
}
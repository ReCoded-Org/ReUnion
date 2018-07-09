package com.safaorhan.reunion;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.safaorhan.reunion.model.Conversation;
import com.safaorhan.reunion.model.Message;
import com.safaorhan.reunion.model.User;

import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Nullable;

public class FirestoreHelper {
    private static final String TAG = FirestoreHelper.class.getSimpleName();

    public static DocumentReference getMe() {
        String myId = FirebaseAuth
                .getInstance()
                .getUid();

        if (myId != null) {
            return getUsers()
                    .document(myId);
        }
        Log.e(TAG, "getMe: FirebaseAuth returned null!!");
        return null;
    }

    public static CollectionReference getUsers() {
        return FirebaseFirestore.getInstance()
                .collection("users");
    }

    public static void findOrCreateConversation(final DocumentReference opponentRef, final DocumentReferenceCallback callback) {
        getConversations()
                .whereEqualTo(getMe().getId(), true)
                .whereEqualTo(opponentRef.getId(), true)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if (snapshots == null || snapshots.isEmpty()) {
                            ArrayList<DocumentReference> participants = new ArrayList<>();
                            participants.add(getMe());
                            participants.add(opponentRef);

                            final Conversation conversation = new Conversation();
                            conversation.setParticipants(participants);
                            getConversations()
                                    .add(conversation)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(final DocumentReference conversationRef) {
                                            HashMap<String, Object> updateFields = new HashMap<>();
                                            updateFields.put(getMe().getId(), true);
                                            updateFields.put(opponentRef.getId(), true);
                                            conversationRef.update(updateFields)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            callback.onCompleted(conversationRef);
                                                        }
                                                    });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG, "onFailute", e);
                                        }
                                    });
                        } else {
                            DocumentSnapshot snapshot = snapshots.getDocuments().get(0);
                            Conversation conversation = snapshot.toObject(Conversation.class);
                            if (conversation != null) {
                                conversation.setId(snapshot.getId());
                                callback.onCompleted(getConversationRef(conversation));
                            }else {
                                Log.e(TAG, "findOrCreateConversation: Error Couldn't find or create Conversation!!");
                            }
                        }
                    }
                });
    }

    public static void sendMessage(final String messageText, final DocumentReference conversationRef, final ChatFeedBackCallback callback) {
        final Message message = new Message();
        message.setText(messageText);
        message.setFrom(getMe());
        message.setConversation(conversationRef);

        FirebaseFirestore.getInstance()
                .collection("messages")
                .add(message)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        conversationRef
                                .update("lastMessage", documentReference)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        callback.onMessageSentSuccessfully();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error adding document", e);
                    }
                });
    }

    public static CollectionReference getConversations() {
        return FirebaseFirestore.getInstance().collection("conversations");
    }

    public static DocumentReference getConversationRef(Conversation conversation) {
        return getConversations().document(conversation.getId());
    }

    public static DocumentReference getConversationRefById(String conversationId){
        return getConversations().document(conversationId);
    }

    public static DocumentReference getUserRef(User user) {
        return getUsers().document(user.getId());
    }

    public static DocumentReference getUserRefById(String userId) {
        return getUsers().document(userId);
    }

    public interface DocumentReferenceCallback {
        void onCompleted(DocumentReference documentReference);
    }

    public interface ChatFeedBackCallback {
        void onMessageSentSuccessfully();
    }
}
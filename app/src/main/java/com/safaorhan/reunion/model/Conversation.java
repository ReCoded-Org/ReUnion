package com.safaorhan.reunion.model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import java.util.List;

public class Conversation {
    public static final String CONVERSATION_KEY = "CONVERSATION";
    private String id;
    private DocumentReference lastMessage;
    private List<DocumentReference> participants;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DocumentReference getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(DocumentReference lastMessage) {
        this.lastMessage = lastMessage;
    }

    private List<DocumentReference> getParticipants() {
        return participants;
    }

    public void setParticipants(List<DocumentReference> participants) {
        this.participants = participants;
    }

    public DocumentReference getOpponent() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String myId = firebaseAuth.getUid();

        for (DocumentReference userRef : getParticipants()) {
            if (!userRef.getId().equals(myId)) {
                return userRef;
            }
        }

        return null;
    }
}

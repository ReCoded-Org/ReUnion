package com.safaorhan.reunion.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.ServerTimestamp;

public class Message {
    private DocumentReference conversation;
    private DocumentReference from;
    private String text;
    @ServerTimestamp
    private Timestamp sentAt;

    public DocumentReference getConversation() {
        return conversation;
    }

    public void setConversation(DocumentReference conversation) {
        this.conversation = conversation;
    }

    public DocumentReference getFrom() {
        return from;
    }

    public void setFrom(DocumentReference from) {
        this.from = from;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Timestamp getSentAt() {
        return sentAt;
    }

    public void setSentAt(Timestamp sentAt) {
        this.sentAt = sentAt;
    }
}

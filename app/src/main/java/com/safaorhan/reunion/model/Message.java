package com.safaorhan.reunion.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.ServerTimestamp;

public class Message {
    DocumentReference conversation;
    DocumentReference from;
    String text;
    @ServerTimestamp Timestamp sentAt;

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

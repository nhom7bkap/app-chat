package com.team7.app_chat.Util;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.team7.app_chat.CurrentUser;
import com.team7.app_chat.models.Member;
import com.team7.app_chat.models.Message;
import com.team7.app_chat.models.RoomChats;
import com.team7.app_chat.models.User;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * Manages data access for Firebase
 */
public class RoomChatRepository {

    private static final String TAG = "FirestoreRepository";

    private final Class<RoomChats> entityClass;

    private final CollectionReference collectionReference;
    private final String collectionName = "chatRooms";
    private final User user;
    private final FirebaseFirestore db;
    private DocumentReference currentUserRef;
    private UserRepository userChatRepository;

    public RoomChatRepository() {
        this.entityClass = RoomChats.class;
        this.db = FirebaseFirestore.getInstance();
        this.user = CurrentUser.user;
        this.collectionReference = db.collection(collectionName);
    }


    public Task<Boolean> exists(final String id) {
        DocumentReference documentReference = collectionReference.document(id);
        Log.i(TAG, "Checking existence of '" + id + "' in '" + collectionName + "'.");

        return documentReference.get().continueWith(new Continuation<DocumentSnapshot, Boolean>() {
            @Override
            public Boolean then(@NonNull Task<DocumentSnapshot> task) {
                Log.d(TAG, "Checking if '" + id + "' exists in '" + collectionName + "'.");
                return task.getResult().exists();
            }
        });
    }

    public Task<Boolean> checkMessages(final String id) {
        DocumentReference documentReference = collectionReference.document(id).collection("messages").document();
        Log.i(TAG, "Checking existence of '" + id + "' in '" + collectionName + "'.");

        return documentReference.get().continueWith(new Continuation<DocumentSnapshot, Boolean>() {
            @Override
            public Boolean then(@NonNull Task<DocumentSnapshot> task) {
                Log.d(TAG, "Checking if '" + id + "' exists in '" + collectionName + "'.");
                return task.getResult().exists();
            }
        });
    }

    public CollectionReference get() {
        return collectionReference;
    }

    public Task<RoomChats> get(String id) {
        DocumentReference documentReference = collectionReference.document(id);
        Log.i(TAG, "Getting '" + id + "' in '" + collectionName + "'.");

        return documentReference.get().continueWith(new Continuation<DocumentSnapshot, RoomChats>() {
            @Override
            public RoomChats then(@NonNull Task<DocumentSnapshot> task) throws Exception {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot.exists()) {
                    Log.d(TAG, "Success");
                    return documentSnapshot.toObject(entityClass);
                } else {
                    Log.d(TAG, "Document '" + id + "' does not exist in '" + collectionName + "'.");
                    return entityClass.newInstance();
                }
            }
        });
    }

    public DocumentReference getDocRf(String id) {
        final String documentName = id;
        DocumentReference documentReference = collectionReference.document(documentName);
        Log.i(TAG, "Getting '" + documentName + "' in '" + collectionName + "'.");
        return documentReference;
    }

    public Task<Void> create(RoomChats entity) {
        DocumentReference documentReference = collectionReference.document();
        Log.i(TAG, "Creating '" + "Random id" + "' in '" + collectionName + "'.");
        return documentReference.set(entity).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "There was an error creating '" + "Random id" + "' in '" + collectionName + "'!", e);
            }
        });
    }

    public Task<Void> createMessage(String roomId, Message entity) {
        DocumentReference documentReference = collectionReference.document(roomId).collection("messages").document();
        Log.i(TAG, "Creating '" + "Random id" + "' in '" + "messages" + "'.");
        return documentReference.set(entity).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "There was an error creating '" + "Random id" + "' in '" + "messages" + "'!", e);
            }
        });
    }

    public Task<Void> update(RoomChats entity, String id) {
        DocumentReference documentReference = collectionReference.document(id);
        Log.i(TAG, "Updating '" + id + "' in '" + collectionName + "'.");

        return documentReference.set(entity).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "There was an error updating '" + id + "' in '" + collectionName + "'.", e);
            }
        });
    }

    public Task<Void> delete(final String id) {
        DocumentReference documentReference = collectionReference.document(id);
        Log.i(TAG, "Deleting '" + id + "' in '" + collectionName + "'.");

        return documentReference.delete().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "There was an error deleting '" + id + "' in '" + collectionName + "'.", e);
            }
        });
    }

    public void removeMessage(String roomId, String messageId) {

        DocumentReference messageRef = collectionReference.document(roomId).collection("messages")
                .document(messageId);
        messageRef.update("text", user.getFullName() + " removed this message");
        messageRef.update("removed", true);
    }

    public Task<Void> deleteMessage(String roomId, String messageId) {
        DocumentReference documentReference = collectionReference.document(roomId).collection("messages").document(messageId);
        Log.i(TAG, "Deleting '" + messageId + "' in '" + "Messages" + "'.");
        return documentReference.delete().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "There was an error deleting '" + messageId + "' in '" + "Messages" + "'.", e);
            }
        });
    }

    public Task<Void> removeMember(String roomId, String memberId, String text) {
        userChatRepository.getDocRf(user.getId()).get().addOnSuccessListener(documentSnapshot -> {
            currentUserRef = documentSnapshot.getReference();
        });
        Message message = new Message();
        message.setNotify(true);
        message.setCreatedDate(new Date());
        message.setSendBy(currentUserRef);
        userChatRepository.getDocRf(memberId).get().addOnSuccessListener(documentSnapshot -> {
            if (text != null) {
                message.setText(text);
            } else {
                message.setText(user.getFullName() + " banned " + documentSnapshot.get("lastName"));
            }
            createMessage(roomId, message);
            documentSnapshot.getReference().collection("chatRooms").document(roomId).delete();
        });
        return collectionReference.document(roomId).collection("members").document(memberId).delete();
    }

    public void addMember(String roomId, DocumentSnapshot userSnap){
        userChatRepository.getDocRf(user.getId()).get().addOnSuccessListener(documentSnapshot -> {
            currentUserRef = documentSnapshot.getReference();
        });
        DocumentReference room = collectionReference.document(roomId);
        Member member = new Member(false, userSnap.getReference());
        room.collection("members").document(userSnap.getId()).set(member).addOnSuccessListener(unused -> {
            Map<String, Object> newRoom = new HashMap<>();
            newRoom.put("updatedAt", new Date());
            newRoom.put("room", room);
            userSnap.getReference().collection("chatRooms").document(roomId).set(newRoom);

            Message message = new Message();
            message.setNotify(true);
            message.setCreatedDate(new Date());
            message.setSendBy(currentUserRef);
            String fullName = userSnap.get("firstName") + " " + userSnap.get("lastName");
            if(user.getId().equals(userSnap.getId())){
                message.setText(user.getFullName() + " joined into this room");
            } else{
                message.setText(user.getFullName() + " added " + fullName + " into the room");
            }
            createMessage(roomId, message);
        });
    }

    public Task<Void> changePermission(String roomId, String memberId, boolean isMod){
        userChatRepository.getDocRf(user.getId()).get().addOnSuccessListener(documentSnapshot -> {
            currentUserRef = documentSnapshot.getReference();
        });
        Message message = new Message();
        message.setNotify(true);
        message.setCreatedDate(new Date());
        message.setSendBy(currentUserRef);
        userChatRepository.getDocRf(memberId).get().addOnSuccessListener(snapshot -> {
            if(isMod){
                message.setText(snapshot.get("lastName") + " has became a mod");
            } else{
                message.setText(snapshot.get("lastName") + " has lost mod permission");
            }
            createMessage(roomId, message);
        });

        return collectionReference.document(roomId).collection("members").document(memberId).update("mod", isMod);
    }
}

package com.team7.app_chat.Util;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.team7.app_chat.CurrentUser;
import com.team7.app_chat.models.FriendRequest;
import com.team7.app_chat.models.Message;
import com.team7.app_chat.models.RoomChat;


/**
 * Manages data access for Firebase
 */
public class RoomChatRepository {

    private static final String TAG = "FirestoreRepository";

    private final Class<RoomChat> entityClass;

    private final CollectionReference collectionReference;
    private final String collectionName = "chatRooms";
    private final String userId;
    private final FirebaseFirestore db;


    public RoomChatRepository() {
        this.entityClass = RoomChat.class;
        this.db = FirebaseFirestore.getInstance();
        this.userId = CurrentUser.user.getId();
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

    public CollectionReference get() {
        return collectionReference;
    }

    public Task<RoomChat> get(String id) {
        DocumentReference documentReference = collectionReference.document(id);
        Log.i(TAG, "Getting '" + id + "' in '" + collectionName + "'.");

        return documentReference.get().continueWith(new Continuation<DocumentSnapshot, RoomChat>() {
            @Override
            public RoomChat then(@NonNull Task<DocumentSnapshot> task) throws Exception {
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

    public Task<Void> create(RoomChat entity) {
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

    public Task<Void> update(RoomChat entity, String id) {
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

}

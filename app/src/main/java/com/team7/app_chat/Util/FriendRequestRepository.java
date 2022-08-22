package com.team7.app_chat.Util;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.team7.app_chat.models.Contact;
import com.team7.app_chat.models.FriendRequest;
import com.team7.app_chat.models.User;


/**
 * Manages data access for Firebase
 */
public class FriendRequestRepository {

    private static final String TAG = "FirestoreRepository";

    private final Class<FriendRequest> entityClass;

    private final CollectionReference collectionReference;
    private final String ParentCollectionName = "User";
    private final String collectionName = "friend_request";
    private final String userId;


    public FriendRequestRepository(String userId) {
        this.entityClass = FriendRequest.class;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        this.userId = userId;
        this.collectionReference = db.collection(this.ParentCollectionName);
    }


    public Task<Boolean> exists(final String id) {
        DocumentReference documentReference = collectionReference.document(userId).collection(this.collectionName).document(id);
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
        CollectionReference collectionRf = collectionReference.document(userId).collection(collectionName);
        return collectionRf;
    }

    public Task<FriendRequest> get(String id) {
        DocumentReference documentReference = collectionReference.document(userId).collection(collectionName).document(id);
        Log.i(TAG, "Getting '" + id + "' in '" + collectionName + "'.");

        return documentReference.get().continueWith(new Continuation<DocumentSnapshot, FriendRequest>() {
            @Override
            public FriendRequest then(@NonNull Task<DocumentSnapshot> task) throws Exception {
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
        DocumentReference documentReference = collectionReference.document(userId).collection(collectionName).document(id);
        Log.i(TAG, "Getting '" + id + "' in '" + collectionName + "'.");
        return documentReference;
    }

    public Task<Void> create(FriendRequest entity) {
        DocumentReference documentReference = collectionReference.document(entity.getReceiverId()).collection(collectionName).document(entity.getSenderId());
        Log.i(TAG, "Creating '" + "Random id" + "' in '" + collectionName + "'.");
        return documentReference.set(entity).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "There was an error creating '" + "Random id" + "' in '" + collectionName + "'!", e);
            }
        });
    }

    public Task<Void> update(FriendRequest entity) {
        String id = entity.getSenderId();
        DocumentReference documentReference = collectionReference.document(userId).collection(collectionName).document(id);
        Log.i(TAG, "Updating '" + id + "' in '" + collectionName + "'.");

        return documentReference.set(entity).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "There was an error updating '" + id + "' in '" + collectionName + "'.", e);
            }
        });
    }

    public Task<Void> delete(final String id) {
        DocumentReference documentReference = collectionReference.document(userId).collection(collectionName).document(id);
        Log.i(TAG, "Deleting '" + id + "' in '" + collectionName + "'.");

        return documentReference.delete().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "There was an error deleting '" + id + "' in '" + collectionName + "'.", e);
            }
        });
    }

}

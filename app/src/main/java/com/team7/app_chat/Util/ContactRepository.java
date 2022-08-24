package com.team7.app_chat.Util;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.team7.app_chat.models.Contact;


/**
 * Manages data access for Firebase
 */
public class ContactRepository {

    private static final String TAG = "FirestoreRepository";

    private final Class<Contact> entityClass;

    private final CollectionReference collectionReference;
    private final String ParentCollectionName = "User";
    private final String collectionName = "contacts";
    private final String userId;


    public ContactRepository(String userId) {
        this.entityClass = Contact.class;
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

    public Task<Contact> get(String id) {
        DocumentReference documentReference = collectionReference.document(userId).collection(collectionName).document(id);
        Log.i(TAG, "Getting '" + id + "' in '" + collectionName + "'.");

        return documentReference.get().continueWith(new Continuation<DocumentSnapshot, Contact>() {
            @Override
            public Contact then(@NonNull Task<DocumentSnapshot> task) throws Exception {
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

    public Task<Void> create(Contact entity, String id) {
        DocumentReference documentReference = collectionReference.document(userId).collection(collectionName).document();
        if (id != "") {
            documentReference = collectionReference.document(userId).collection(collectionName).document(id);
        }

        Log.i(TAG, "Creating '" + "Random id" + "' in '" + collectionName + "'.");
        return documentReference.set(entity).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "There was an error creating '" + "Random id" + "' in '" + collectionName + "'!", e);
            }
        });
    }

    public Task<Void> update(Contact entity) {
        final String id = entity.getUser().getId();
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

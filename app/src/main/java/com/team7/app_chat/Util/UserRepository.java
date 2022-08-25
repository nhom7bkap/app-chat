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
import com.google.firebase.firestore.Query;
import com.team7.app_chat.CurrentUser;
import com.team7.app_chat.models.Contact;
import com.team7.app_chat.models.User;

import java.util.List;
import java.util.stream.Collectors;


/**
 * Manages data access for Firebase
 */
public class UserRepository {

    private static final String TAG = "FirestoreRepository";

    private final Class<User> entityClass;

    private final CollectionReference collectionReference;
    private final String collectionName = "User";

    private IContactCallback callback;

    public interface IContactCallback {
        void loadContact(List<Contact> list);
    }

    /**
     * Initializes the repository storing the data in the given collection. Should be from {@link FirestoreRepository}.
     */
    public UserRepository() {
        this.entityClass = User.class;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        this.collectionReference = db.collection(this.collectionName);
    }

    public UserRepository(IContactCallback callback) {
        this.entityClass = User.class;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        this.collectionReference = db.collection(this.collectionName);

        this.callback = callback;
    }


    public Task<Boolean> exists(final String documentName) {
        DocumentReference documentReference = collectionReference.document(documentName);
        Log.i(TAG, "Checking existence of '" + documentName + "' in '" + collectionName + "'.");

        return documentReference.get().continueWith(new Continuation<DocumentSnapshot, Boolean>() {
            @Override
            public Boolean then(@NonNull Task<DocumentSnapshot> task) {
                Log.d(TAG, "Checking if '" + documentName + "' exists in '" + collectionName + "'.");
                return task.getResult().exists();
            }
        });
    }


    public CollectionReference get() {
        return collectionReference;
    }

    public Query getByEmail(String email) {
        return collectionReference.whereEqualTo("email", email);
    }

    public Task<User> get(String id) {
        final String documentName = id;
        DocumentReference documentReference = collectionReference.document(documentName);
        Log.i(TAG, "Getting '" + documentName + "' in '" + collectionName + "'.");

        return documentReference.get().continueWith(new Continuation<DocumentSnapshot, User>() {
            @Override
            public User then(@NonNull Task<DocumentSnapshot> task) throws Exception {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot.exists()) {
                    Log.d(TAG, "Success");
                    return documentSnapshot.toObject(entityClass);
                } else {
                    Log.d(TAG, "Document '" + documentName + "' does not exist in '" + collectionName + "'.");
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

    public void getContactsWithCallback(String userId) {
        collectionReference.document(userId).collection("contacts").get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Contact> list = queryDocumentSnapshots.getDocuments()
                    .stream()
                    .map(documentSnapshot -> documentSnapshot.toObject(Contact.class))
                    .collect(Collectors.toList());
            callback.loadContact(list);
        });
    }

    public CollectionReference getChatRoom() {
        return collectionReference.document(CurrentUser.user.getId()).collection("chatRoom");
    }

    public Task<Void> create(User entity) {
        DocumentReference documentReference = collectionReference.document();
        if (!entity.getId().isEmpty()) {
            documentReference = collectionReference.document(entity.getId());
        }
        Log.i(TAG, "Creating '" + "Random id" + "' in '" + collectionName + "'.");
        return documentReference.set(entity).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "There was an error creating '" + "Random id" + "' in '" + collectionName + "'!", e);
            }
        });
    }

    public Task<Void> update(User entity) {
        final String documentName = entity.getId();
        DocumentReference documentReference = collectionReference.document(documentName);
        Log.i(TAG, "Updating '" + documentName + "' in '" + collectionName + "'.");

        return documentReference.set(entity).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "There was an error updating '" + documentName + "' in '" + collectionName + "'.", e);
            }
        });
    }

    public Task<Void> delete(final String documentName) {
        DocumentReference documentReference = collectionReference.document(documentName);
        Log.i(TAG, "Deleting '" + documentName + "' in '" + collectionName + "'.");

        return documentReference.delete().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "There was an error deleting '" + documentName + "' in '" + collectionName + "'.", e);
            }
        });
    }

}

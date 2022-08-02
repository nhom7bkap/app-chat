package com.team7.app_chat.Util;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.team7.app_chat.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FiresBaseRepository<TEntity> {
    private DatabaseReference databaseReference;
    private static final String TAG = "FirestoreRepository";

    private final Class<TEntity> entityClass;

    private List<TEntity> lstData;


    public FiresBaseRepository(Class<TEntity> entityClass) {
        this.entityClass = entityClass;
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference(entityClass.getSimpleName());
    }

    public Task<TEntity> get(String key) {
        if (key == null) {
            return databaseReference.orderByKey().limitToFirst(8).get().continueWith(new Continuation<DataSnapshot, TEntity>() {
                @Override
                public TEntity then(@NonNull Task<DataSnapshot> task) throws Exception {
                    DataSnapshot dataSnapshot = task.getResult();
                    return dataSnapshot.getValue(entityClass);
                }
            });
        }
        return databaseReference.orderByKey().startAfter(key).limitToFirst(8).get().continueWith(new Continuation<DataSnapshot, TEntity>() {
            @Override
            public TEntity then(@NonNull Task<DataSnapshot> task) throws Exception {
                DataSnapshot dataSnapshot = task.getResult();
                return dataSnapshot.getValue(entityClass);
            }
        });
    }

    public List<TEntity> get() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Object object = dataSnapshot.getValue(Object.class);
                    String json = new Gson().toJson(object);
                    TEntity entity = new Gson().fromJson(json, entityClass);
                    Log.e("json", "json: " + entity.toString());
                    lstData.add(entity);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }


        });
        return lstData;
    }

    public Task<Void> add(TEntity entity) {
        return databaseReference.push().setValue(entity);
    }

    public Task<Void> update(String key, HashMap<String, Object> hashMap) {
        return databaseReference.child(key).updateChildren(hashMap);
    }

    public Task<Void> remove(String key) {
        return databaseReference.child(key).removeValue();
    }

}
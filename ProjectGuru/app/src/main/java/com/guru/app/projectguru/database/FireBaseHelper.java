package com.guru.app.projectguru.database;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.guru.app.projectguru.interfaces.LazyRequestCallback;
import com.guru.app.projectguru.interfaces.RequestCallback;
import com.guru.app.projectguru.models.Identity;

public class FireBaseHelper {
    public static StorageReference getFirebaseStorageReference() {
        return FirebaseStorage.getInstance().getReference();
    }

    public static DatabaseReference getFirebaseDatabaseReference(String ref) {
        return FirebaseDatabase.getInstance().getReference(ref);
    }

    public static void getSnapShotFromServer(String url, final RequestCallback requestCallback) {
        requestCallback.onStart();
        getFirebaseDatabaseReference(url).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                requestCallback.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                requestCallback.onError(databaseError);
            }
        });
    }

    public static void sendObjectToServer(String url, String child, Object data, final RequestCallback requestCallback) {
        requestCallback.onStart();
        getFirebaseDatabaseReference(url).child(child).setValue(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        requestCallback.onSuccess("Successfully Added");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        requestCallback.onError(e);
                    }
                });
    }

    public static void sendFileToServer(String url, Uri uri, final LazyRequestCallback lazyRequestCallback) {
        lazyRequestCallback.onStart();
        StorageReference sRef = getFirebaseStorageReference().child(url);
        sRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                lazyRequestCallback.onSuccess(taskSnapshot);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                lazyRequestCallback.onError(e);
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                lazyRequestCallback.onProgress(taskSnapshot);
            }
        });
    }

}
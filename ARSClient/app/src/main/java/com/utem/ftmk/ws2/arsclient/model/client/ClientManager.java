package com.utem.ftmk.ws2.arsclient.model.client;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.utem.ftmk.ws2.arsclient.assistant.FirebaseIdentifier;

import java.util.List;
import java.util.Objects;

// TODO: validate password in reset email

public class ClientManager {

    private static final FirebaseAuth sAuth = FirebaseAuth.getInstance();
    private static final DatabaseReference sClientDatabaseRef = FirebaseDatabase.getInstance()
            .getReference().child(FirebaseIdentifier.CLIENT);
    private static final StorageReference sClientStorageRef = FirebaseStorage.getInstance()
            .getReference().child(FirebaseIdentifier.CLIENT);

    private static int sCount;

    public static String getClientUid() {
        return sAuth.getUid();
    }

    public static boolean isClientLoggedIn() {
        return sAuth.getCurrentUser() != null;
    }

    public static boolean isClientVerified() {
        return Objects.requireNonNull(sAuth.getCurrentUser()).isEmailVerified();
    }

    public static void reloadClient(ClientCompleteListener<Void> listener) {
        sAuth.getCurrentUser().reload().addOnCompleteListener(listener::onComplete);
    }

    public static void reAuthenticateClient(String email, String password,
                                            ClientCompleteListener<Void> listener) {
        // Get auth credentials from the user for re-authentication.
        AuthCredential credential = EmailAuthProvider
                .getCredential(email, password);

        // Prompt the user to re-provide their sign-in credentials
        sAuth.getCurrentUser().reauthenticate(credential)
                .addOnCompleteListener(listener::onComplete);
    }

    public static void updateClientEmail(String email, ClientCompleteListener<Void> listener) {
        sAuth.getCurrentUser().updateEmail(email).addOnCompleteListener(listener::onComplete);
    }

    public static void updateClientPassword(String password,
                                            ClientCompleteListener<Void> listener) {
        sAuth.getCurrentUser().updatePassword(password).addOnCompleteListener(listener::onComplete);
    }

    public static void deleteStorageImage(String imageUri, ClientCompleteListener<Void> listener) {
        FirebaseStorage.getInstance().getReferenceFromUrl(imageUri).delete()
                .addOnCompleteListener(listener::onComplete);
    }

    public static void addStorageImage(Uri imageUri, ClientCompleteListener<Uri> listener) {
        StorageReference currentRef = sClientStorageRef
                .child(getClientUid()).child(imageUri.getLastPathSegment());
        currentRef.putFile(imageUri).continueWithTask(task1 -> currentRef.getDownloadUrl())
                .addOnCompleteListener(listener::onComplete);
    }

    public static String getClientEmail() {
        return sAuth.getCurrentUser().getEmail();
    }

    public static void getClient(ClientEventListener listener) {
        sClientDatabaseRef.child(getClientUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listener.onDataChange(snapshot.getValue(Client.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onCancelled(error);
            }
        });
    }

    public static void getClientOnce(ClientEventListener listener) {
        sClientDatabaseRef.child(getClientUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listener.onDataChange(snapshot.getValue(Client.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onCancelled(error);
            }
        });
    }

    public static void updateClient(Client client, ClientCompleteListener<Void> listener) {
        addClient(client, listener);
    }

    public static void addClient(Client client, ClientCompleteListener<Void> listener) {
        sClientDatabaseRef.child(Objects.requireNonNull(sAuth.getCurrentUser()).getUid())
                .setValue(client).addOnCompleteListener(listener::onComplete);
    }

    public static void registerClient(Client client, ClientCompleteListener<AuthResult> listener) {
        sAuth.createUserWithEmailAndPassword(client.getEmail(), client.getPassword())
                .addOnCompleteListener(listener::onComplete);
    }

    public static void loginClient(Client client, ClientCompleteListener<AuthResult> listener) {
        sAuth.signInWithEmailAndPassword(client.getEmail(), client.getPassword())
                .addOnCompleteListener(listener::onComplete);
    }

    public static void signOutClient() {
        sAuth.signOut();
    }

    public static void sendClientEmailVerification(ClientCompleteListener<Void> listener) {
        sAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(listener::onComplete);
    }

    public static void sendClientPasswordResetEmail(String email, ClientCompleteListener<Void> listener) {
        sAuth.sendPasswordResetEmail(email).addOnCompleteListener(listener::onComplete);
    }

    public interface ClientCompleteListener<T> {
        void onComplete(Task<T> task);
    }

    public interface ClientEventListener {
        void onDataChange(Client client);

        void onCancelled(DatabaseError error);
    }

}

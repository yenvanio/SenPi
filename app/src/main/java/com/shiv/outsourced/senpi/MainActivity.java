package com.shiv.outsourced.senpi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SettingsFragment.onSignOutListener, HomeFragment.qrCodeList, QRFragment.qrCodeList {

    private static final String TAG = "Firebase";
    private static final int RC_SIGN_IN = 9001;
    public User mUser;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private GoogleApiClient mGoogleApiClient;
    private List<QRCode> qrCodeList;
    private DatabaseReference mPostReference;
    private ValueEventListener mPostListener;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Fragment frag = new Fragment();

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    frag = new HomeFragment();
                    break;
                case R.id.navigation_qr:
                    frag = new QRFragment();
                    break;
                case R.id.navigation_settings:
                    frag = new SettingsFragment();
                    break;
            }

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Bundle b = new Bundle();
            b.putSerializable("user", mUser);
            frag.setArguments(b);
            ft.replace(R.id.fragment_container, frag);
//            ft.addToBackStack(null);
            ft.commit();

            return true;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        qrCodeList = new ArrayList();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                    mUser = new User(user.getUid());
                    mUser.setName(user.getDisplayName());
                    mUser.setEmail(user.getEmail());
                    mUser.setQrCodeList(qrCodeList);

                    mDatabase.child("users").child(mUser.getId()).setValue(mUser);

                    Fragment frag = new HomeFragment();

                    Bundle c = new Bundle();
                    c.putSerializable("user", mUser);
                    frag.setArguments(c);

                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.fragment_container, frag);
//                    ft.addToBackStack(null);
                    ft.commit();


                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Google Sign In")
                            .setCancelable(false)
                            .setPositiveButton("Sign In", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    signIn();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                // ...
            }
        };

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {

                    }
                });
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);

            } else {
                Toast.makeText(this, "Sign in Error", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public List<QRCode> getCodeList() {
        mPostReference = FirebaseDatabase.getInstance().getReference()
                .child("user");

        mPostReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                qrCodeList = user.getQrCodeList();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return qrCodeList; //pull list from firebase
    }

    @Override
    public void addQRCode(QRCode qrCode) {

        qrCodeList.add(qrCode); //push update to firebase
        mUser.setQrCodeList(qrCodeList);
        mDatabase.child("users").child(mUser.getId()).setValue(mUser);
        // Write a message to the database


    }

    @Override
    public void onBackPressed ()
    {
        super.onBackPressed();
    }

}

package com.example.logan.googlesignindemo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.logan.googlesignindemo.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.signin.SignIn;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private LinearLayout profileSection;
    private Button signOut;
    private SignInButton signIn;
    private TextView Name, Email;
    private ImageView profilePic;
    private GoogleApiClient googleApiClient;
    private static final int REQ_CODE = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        profileSection = (LinearLayout) findViewById(R.id.profInfo);
        signOut = (Button) findViewById(R.id.signOutBtn);
        signIn = (SignInButton) findViewById(R.id.signInBtn);
        Name = (TextView) findViewById(R.id.userName);
        Email = (TextView) findViewById(R.id.userEmail);
        profilePic = (ImageView) findViewById(R.id.pic);

        signIn.setOnClickListener(this);
        signOut.setOnClickListener(this);

        profileSection.setVisibility(View.GONE);
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions).build();



    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.signInBtn:
                signIn();
                break;

            case R.id.signOutBtn:
                signOut();
                break;
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }



    private void signIn(){
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent, REQ_CODE);
    }

    private void signOut(){
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                updateUI(false);
            }
        });

    }

    private void handleResult(GoogleSignInResult result){
        if(result.isSuccess()){
            GoogleSignInAccount account = result.getSignInAccount();
            String name = account.getDisplayName();
            String email = account.getEmail();
            Name.setText(name);
            Email.setText(email);
            if(account.getPhotoUrl() == null){

            }else{
                String img_url = account.getPhotoUrl().toString();
                Glide.with(this).load(img_url).into(profilePic);
            }
//            String img_url = account.getPhotoUrl().toString();

//            Glide.with(this).load(img_url).into(profilePic);
            updateUI(true);
        }else
        {
            updateUI(false);
        }

    }

    private void updateUI(boolean isLoggedIn){
        if(isLoggedIn){
            profileSection.setVisibility(View.VISIBLE);
            signIn.setVisibility(View.GONE);
        }else
        {
            profileSection.setVisibility(View.GONE);
            signIn.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQ_CODE){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResult(result);

        }

    }
}

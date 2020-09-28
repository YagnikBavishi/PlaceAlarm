package com.example.placealarm;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {
    public static final String GOOGLE_ACCOUNT = "google_account";
    public TextView profileName, profileEmail;
    public ImageView profileImage;
    public Button signOut;
    public GoogleSignInClient googleSignInClient;
    public static String personName;
    //public profileName = findViewById(R.id.profile_text);
    //public profileEmail = findViewById(R.id.profile_email);
    //public profileImage = findViewById(R.id.profile_image);
    private FirebaseAuth mAuth;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        profileName = findViewById(R.id.profile_text);
        profileEmail = findViewById(R.id.profile_email);
        profileImage = findViewById(R.id.profile_image);
        signOut=findViewById(R.id.sign_out);

        setDataOnView();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getResources().getString(R.string.web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this,gso);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
          /*
          Sign-out is initiated by simply calling the googleSignInClient.signOut API. We add a
          listener which will be invoked once the sign out is the successful
           */
                googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        //On Succesfull signout we navigate the user back to LoginActivity
                        Intent intent=new Intent(ProfileActivity.this, SignIn.class);
                        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        //Toast.makeText(ProfileActivity.this, "LOGOUT", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    public void setDataOnView() {

        GoogleSignInAccount googleSignInAccount = getIntent().getParcelableExtra(GOOGLE_ACCOUNT);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (account != null) {
             personName = account.getDisplayName();
            String personEmail = account.getEmail();
            Picasso.get().load(account.getPhotoUrl()).centerInside().fit().into(profileImage);
            Uri personPhoto = account.getPhotoUrl();
            profileName.setText(personName);
            profileEmail.setText(personEmail);
            //Toast.makeText(ProfileActivity.this, personPhoto.toString(), Toast.LENGTH_LONG).show();
        }
    }

}


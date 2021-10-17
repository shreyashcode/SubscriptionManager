package com.example.ott;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.ott.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout nameLogLy, emailLogLy, phoneNoLogLy, passwordLogLy;
    private MaterialButton loginButton;

    public static final String SP_KEY= "notesapp_Shared_preferences_access_key";
    public static final String SP_KEY_NAME= "notesapp_name";
    public static final String SP_KEY_EMAIL= "notesapp_email";
    public static final String SP_KEY_PHONE= "notesapp_phone";
    public static final String SP_KEY_PASSWORD= "notesapp_password";
    public static final String SP_KEY_IS_LOGGED_IN = "notesapp_login";
    public FirebaseFirestore db;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db = FirebaseFirestore.getInstance();

        Common.map = new HashMap<>();
        Common.map.put("Netflix", "https://i.postimg.cc/KcpJ4yyz/icons8-netflix-desktop-app-480.png");
        Common.map.put("Spotify", "https://i.postimg.cc/2jLZtV5h/icons8-spotify-240.png");
        Common.map.put("Prime", "https://i.postimg.cc/Mp4KPHRB/amazon-prime-video-logo.png");
        Common.map.put("Hotstar", "https://i.postimg.cc/5ynfw2b6/hotstar-logo-33159.jpg");
        Common.map.put("Youtube", "https://i.postimg.cc/G2RRVyp4/youtube-logo-png-2069.png");
        Common.map.put("LinkedIn", "https://i.postimg.cc/44BG2nR3/linkedin.png");


        nameLogLy = findViewById(R.id.nameLogLy);
        emailLogLy = findViewById(R.id.emailLogLy);
        phoneNoLogLy = findViewById(R.id.phoneLogLy);
        passwordLogLy = findViewById(R.id.passwordLogLy);
        loginButton = findViewById(R.id.LoginButton);

        // added new
        preferences = getSharedPreferences(SP_KEY, Context.MODE_PRIVATE);
        editor = preferences.edit();
        if(preferences.getBoolean(SP_KEY_IS_LOGGED_IN,false)){
            User userAlreadyLoggedIn = getUserDetailsFromSP();

            Log.d("hehe","USer loged in\n" + userAlreadyLoggedIn);
            Toast.makeText(this, "already in", Toast.LENGTH_SHORT).show();
//            loginButton.setEnabled(false);
            startTheNextActivity(userAlreadyLoggedIn);
        }


        loginButton.setOnClickListener(v -> {
            String name = nameLogLy.getEditText().getText().toString();
            String email = emailLogLy.getEditText().getText().toString();
            String phoneNo = phoneNoLogLy.getEditText().getText().toString();
            String password = passwordLogLy.getEditText().getText().toString();

            if(name.equals("") || email.equals("") || phoneNo.equals("") || password.equals("")){
                Toast.makeText(LoginActivity.this, "enter the details", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d("hehe",name);
            Log.d("hehe",password);
            Toast.makeText(LoginActivity.this, "LoggedIn", Toast.LENGTH_SHORT).show();

            editor.clear();
            editor.commit();
            editor.putString(SP_KEY_NAME, name);
            editor.putString(SP_KEY_EMAIL, email);
            editor.putString(SP_KEY_PHONE, phoneNo);
            editor.putString(SP_KEY_PASSWORD, password);
            editor.putBoolean(SP_KEY_IS_LOGGED_IN, true);;
            editor.commit();

            Log.d("DEBUG: Before", name + " " + email + " " + password + " " + phoneNo);

            User userSignedUp = getUserDetailsFromSP();

            startTheNextActivity(userSignedUp);

//            isUserPresent(name, phoneNo, email, password);
//            // todo set login in shared preferences:
//
//            finish();
//            startActivity(new Intent(this, MainActivity.class));
        });
    }

    // added new
    private void startTheNextActivity(User user) {
        isUserPresent(user.Name, user.phone, user.email, user.password);
        Common.USER_NAME = user.Name;
        Common.USER_PHONE = user.phone;
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
        finish();
    }

    private User getUserDetailsFromSP(){
        String name = preferences.getString(SP_KEY_NAME,"");
        String email =  preferences.getString(SP_KEY_EMAIL,"");
        String phoneNo =  preferences.getString(SP_KEY_PHONE,"");
        String password =  preferences.getString(SP_KEY_PASSWORD,"");
        return new User(name, email, phoneNo, password);
    }

    public void logOutUser(){
        editor.clear();
        editor.putBoolean(SP_KEY_IS_LOGGED_IN,false);
        editor.commit();
    }

    public void isUserPresent(String name, String phone, String email, String password){
        db.collection("USER")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean isOld = false;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name_ = document.getString("Name");
                            String phone_ = document.getString("Phone");
                            if(name.equals(name_) && phone.equals(phone_)){
                                isOld = true;
                                break;
                            }
                        }
                        if(!isOld){
                            Log.d("DEBUG: ", name + " " + email + " " + password + " " + phone);
                            Map<String, Object> k = new HashMap<>();
                            k.put("Name", name);
                            k.put("Email", email);
                            k.put("Password", password);
                            k.put("Phone", phone);

                            db.collection("USER").document(name + phone).set(k);
                        }
                        Common.USER_NAME = name;
                        Common.USER_PHONE = phone;

                    } else {
                        Toast.makeText(this, "Exception!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
package com.example.android.snapchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    var usernameEditText:EditText? = null
    var passwordEditText:EditText? = null
    private lateinit var mAuth: FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        title = "SnapChat"

        mAuth = Firebase.auth
        usernameEditText = findViewById(R.id.editTextUserName)
        passwordEditText = findViewById(R.id.editTextPassword)

        if (mAuth.currentUser != null) {
            logIn()
        }

    }

    fun goClicked(view: View) {

        if (usernameEditText?.text.toString().equals("") || passwordEditText?.text.toString().equals("")){
            Toast.makeText(this, "Email and Password are required",
                Toast.LENGTH_SHORT).show()
        }else {
            // Sign Up
            mAuth.createUserWithEmailAndPassword(
                usernameEditText?.text.toString(),
                passwordEditText?.text.toString()
            )
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseDatabase.getInstance().getReference().child("users")
                            .child(task.result!!.user!!.uid).child("email")
                            .setValue(usernameEditText?.text.toString())
                        logIn()
                        Toast.makeText(this, "Sign Up success ;)", Toast.LENGTH_SHORT).show()
                    } else {
                        // If sign up fails, login
                        mAuth.signInWithEmailAndPassword(
                            usernameEditText?.text.toString(),
                            passwordEditText?.text.toString()
                        )
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {
                                    logIn()
                                    Toast.makeText(this, "LogIn success ;)",
                                        Toast.LENGTH_SHORT).show()
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(
                                        baseContext, "Authentication failed.",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                }
                            }
                    }

                }
        }
    }

    fun logIn() {
        // move to the next activity
        val intent = Intent(this, SnapsActivity::class.java)
        startActivity(intent)
    }

}

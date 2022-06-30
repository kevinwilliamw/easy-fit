package com.example.easyfit

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

private var data = mutableListOf<String>()

class SignUp : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        db = FirebaseFirestore.getInstance()

        val editUsername = findViewById<EditText>(R.id.editUsername)
        val editName = findViewById<EditText>(R.id.editName)
        val editPassword = findViewById<EditText>(R.id.editPassword)
        val btnSignUp = findViewById<Button>(R.id.btnSignUp)

        btnSignUp.setOnClickListener {
            if(editUsername.text.isNotEmpty() && editName.text.isNotEmpty() && editPassword.text.isNotEmpty()) {
                val newuser = userFormat(editUsername.text.toString(), editName.text.toString(), editPassword.text.toString(), "0", "0")
                val existingUser = mutableListOf<String>()
                var userExisted = false
                db.collection("dataLogin")
                    .get()
                    .addOnSuccessListener { result ->
                        data.clear()
                        //add all existing username to array
                        for (document in result) {
                            existingUser.add(document.data["username"].toString())
                        }
                        //check if username existed
                        for(i in existingUser.indices) {
                            if(editUsername.text.toString() == existingUser[i]) {
                                userExisted = true
                            }
                        }
                        //if username not yet existed, add new user to database
                        if(!userExisted) {
                            db.collection("dataLogin").document(editUsername.text.toString()).set(newuser)
                                .addOnSuccessListener {
                                    Log.d(ContentValues.TAG, "Transaction successfully written!")
                                    Toast.makeText(this, "Sign up successful", Toast.LENGTH_SHORT).show()
                                    onBackPressed()
                                }
                                .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error writing document", e) }
                        }
                        else { //if username already existed
                            Toast.makeText(this, "Username already used, choose another username!", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d(ContentValues.TAG, "Error getting documents: ", exception)
                    }
            }
            else { //one of the editText is empty
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
package com.example.easyfit

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class buyPremiumActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_premium)

        val _btnBuyPremium = findViewById<Button>(R.id.btnBuyPremium)
        _btnBuyPremium.setOnClickListener {
            status = "1"
            db.collection("dataLogin").document(loggedInUser).update(mapOf(
                "status" to "1"
            ))
                .addOnSuccessListener {
                    Log.d(ContentValues.TAG, "Transaction successfully written!")
                    Toast.makeText(this, "You are upgraded to  PREMIUM!", Toast.LENGTH_SHORT).show()
                    onBackPressed()
                }
                .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error writing document", e) }
        }
    }
}
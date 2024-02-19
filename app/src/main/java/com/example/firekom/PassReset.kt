package com.example.firekom

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class PassReset : AppCompatActivity() {

    private lateinit var et: EditText

    private lateinit var pomoc: TextView

    private lateinit var bt: Button

    private lateinit var mAuth: FirebaseAuth

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pass_reset)

        val actionBar: ActionBar? = supportActionBar
        val colorDrawable = ColorDrawable(Color.parseColor("#0048FF"))
        actionBar?.setBackgroundDrawable(colorDrawable)
        window.statusBarColor = Color.parseColor("#0048FF")
        title = "Reset Hasła"


        mAuth = FirebaseAuth.getInstance()

        pomoc = findViewById(R.id.pomocc)
        et = findViewById(R.id.et)
        bt = findViewById(R.id.bt)

        bt.setOnClickListener {
            pomoc.text = ""
            val email = et.text.toString()
            if (email.isEmpty()) {
                pomoc.setText(R.string.podkreska)
            } else {
                mAuth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val alertDialog =
                            AlertDialog.Builder(this@PassReset).create()
                        alertDialog.setCanceledOnTouchOutside(false)
                        alertDialog.setTitle("Link został wysłany")
                        alertDialog.setMessage("Wysłano link do zmiany hasła na podany adres e-mail. Zmień hasło, a następnie się zaloguj.")
                        alertDialog.setButton(
                            AlertDialog.BUTTON_POSITIVE, "Ok"
                        ) { dialog, which ->
                            dialog.dismiss()
                            moveTaskToBack(true)
                            val `in` = Intent(this@PassReset, MainActivity::class.java)
                            startActivity(`in`)
                        }
                        alertDialog.show()
                    } else {
                        Toast.makeText(this@PassReset, "Błąd", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


    }
}
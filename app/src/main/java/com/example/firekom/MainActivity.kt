package com.example.firekom

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.firekom.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class MainActivity : AppCompatActivity() {

    private lateinit var b: ActivityMainBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var user_gmail:String
    private lateinit var user_name:String
    private lateinit var user_ID:String
    var doubleBackToExitPressedOnce = false
    private lateinit var mProgress: ProgressDialog

    private lateinit var dbRef: DatabaseReference

    private fun czy_jest_internet(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b=ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        val actionBar: ActionBar? = supportActionBar
        val colorDrawable = ColorDrawable(Color.parseColor("#0048FF"))
        actionBar?.setBackgroundDrawable(colorDrawable)
        window.statusBarColor = Color.parseColor("#0048FF")
        title = "Logowanie"

        mProgress = ProgressDialog(this)
        mProgress.setMessage("Logowanie...")
        mProgress.setCanceledOnTouchOutside(false)

        FirebaseDatabase.getInstance("https://firekom-49d0e-default-rtdb.europe-west1.firebasedatabase.app").reference

        dbRef = FirebaseDatabase.getInstance("https://firekom-49d0e-default-rtdb.europe-west1.firebasedatabase.app").getReference("baza")

        mAuth = FirebaseAuth.getInstance()

        b.reset.setOnClickListener{
            startActivity(Intent(this, PassReset::class.java))
        }

        b.but.setOnClickListener{
            startActivity(Intent(this, signup::class.java))
        }

        b.si.setOnClickListener{
            if (czy_jest_internet()) {
                val emajl: String = b.em.text.toString().trim { it <= ' ' }
                val hasl: String = b.ha.text.toString().trim { it <= ' ' }
                if (emajl.isEmpty() || hasl.isEmpty()) {
                    b.pomocnik.setText(R.string.podkreska)
                } else {
                    zaloguj_sie(emajl, hasl)
                }
            } else {
                internet_wylaczany()
            }
        }

    }



    fun internet_wylaczany() {
        val alertDialog = AlertDialog.Builder(this@MainActivity).create()
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.setTitle("Brak połączenia z internetem")
        alertDialog.setButton(
            AlertDialog.BUTTON_POSITIVE, "OK"
        ) { dialog, _ -> dialog.dismiss() }
        alertDialog.show()
    }

    fun zaloguj_sie(email: String?, haslo: String?) {
        if (email != null && haslo != null) {
            b.em.setText("")
            b.ha.setText("")
            mProgress.show()
            mAuth.signInWithEmailAndPassword(email, haslo)
                .addOnCompleteListener(this
                ) { task ->
                    if (task.isSuccessful) {
                        b.pomocnik.text = ""
                        val user: FirebaseUser? = mAuth.currentUser
                        updateUI(user)
                    } else {
                        b.pomocnik.text = "Błąd logowania. Spróbuj ponownie."
                        updateUI(null)
                    }
                }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish()
            moveTaskToBack(true)
        }
        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Naciśnij jeszcze raz, aby wyjść", Toast.LENGTH_SHORT).show()
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            var fb = false
            for (prof in FirebaseAuth.getInstance().currentUser!!.providerData) {
                if (prof.providerId == "facebook.com") {
                    fb = true
                }
            }
            if (fb || user.isEmailVerified) {
                przywitanie(user)
                user_gmail = user.email.toString()
                user_ID = user.uid
                user_name = user.displayName.toString()

                dbRef.child("users").child(user_ID).child("user_id").setValue(user_ID)/*.addOnCompleteListener {
                    Toast.makeText(this, "Udało sie", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {err ->
                    Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_SHORT).show()
                }*/
                dbRef.child("users").child(user_ID).child("nazwa").setValue(user_name)
                dbRef.child("users").child(user_ID).child("email").setValue(user_gmail)


                val intent = Intent(this, wyborCzatu::class.java)
                startActivity(intent)
            } else {
                b.pomocnik.text = "Zweryfikuj konto."
            }
        }
        mProgress.dismiss()
    }

    fun przywitanie(user: FirebaseUser) {
        val mystring = user.displayName
        val arr = mystring!!.split(" ".toRegex(), limit = 2).toTypedArray()
        val firstWord = arr[0]
        Toast.makeText(applicationContext, "Witaj $firstWord!", Toast.LENGTH_SHORT).show()
    }
}
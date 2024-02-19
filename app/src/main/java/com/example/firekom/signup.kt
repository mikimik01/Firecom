package com.example.firekom

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.firekom.databinding.ActivitySignupBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.*

class signup : AppCompatActivity() {


    private lateinit var b: ActivitySignupBinding
    private lateinit var mProgress:ProgressDialog
    private lateinit var mAuth: FirebaseAuth

    fun czy_jest_internet(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b= ActivitySignupBinding.inflate(layoutInflater)
        setContentView(b.root)

        val actionBar: ActionBar? = supportActionBar
        val colorDrawable = ColorDrawable(Color.parseColor("#0048FF"))
        actionBar?.setBackgroundDrawable(colorDrawable)

        mProgress = ProgressDialog(this)
        mProgress.setMessage("Momencik...")
        mProgress.setCanceledOnTouchOutside(false)

        mAuth = FirebaseAuth.getInstance()

        b.signUp.setOnClickListener{


   if (czy_jest_internet()) {
            mProgress.show()
            val mejl: String = b.email.text.toString().trim { it <= ' ' }
            val has: String = b.haslo.text.toString().trim { it <= ' ' }
            val im: String = b.imiee.text.toString().trim { it <= ' ' }
            val na: String = b.nazwiskoo.text.toString().trim { it <= ' ' }
            if (mejl.isEmpty() || has.isEmpty() || im.isEmpty() || na.isEmpty()) {
                mProgress.dismiss()
                b.pomoc.setText(R.string.podkreska)
            } else {
                nowy_user(mejl, has, im, na)
            }
        } else {
            val alertDialog = AlertDialog.Builder(this@signup).create()
            alertDialog.setCanceledOnTouchOutside(false)
            alertDialog.setTitle("Brak połączenia z internetem")
            alertDialog.setButton(
                AlertDialog.BUTTON_POSITIVE, "OK"
            ) { dialog, which -> dialog.dismiss() }
            alertDialog.show()
        }


        }
    }

    fun nowy_user(email:String, haslo:String, imie:String, nazwisko:String){
        mAuth.createUserWithEmailAndPassword(email, haslo)
            .addOnCompleteListener(
                OnCompleteListener<AuthResult?> { task ->
                    if (!task.isSuccessful) {
                        try {
                            throw task.exception!!
                        } // if user enters wrong email.
                        catch (weakPassword: FirebaseAuthWeakPasswordException) {
                            mProgress.dismiss()
                            b.pomoc.setText("Hasło jest zbyt słabe.")
                            // TODO: take your actions!
                        } // if user enters wrong password.
                        catch (malformedEmail: FirebaseAuthInvalidCredentialsException) {
                            mProgress.dismiss()
                            b.pomoc.setText("Błędny format e-maila.")

                            // TODO: Take your action
                        } catch (existEmail: FirebaseAuthUserCollisionException) {
                            mProgress.dismiss()
                            b.pomoc.setText("Podany email jest zajęty.")

                            // TODO: Take your action
                        } catch (e: Exception) {
                            mProgress.dismiss()
                            b.pomoc.setText("Błąd.")
                        }
                    } else {
                        val currentUser: FirebaseUser? = mAuth.currentUser
                        currentUser?.sendEmailVerification()?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                mProgress.dismiss()
                                b.pomoc.text = ""
                                val alertDialog = AlertDialog.Builder(this@signup).create()
                                alertDialog.setCanceledOnTouchOutside(false)
                                alertDialog.setTitle("Wysłano link do weryfikacji")
                                alertDialog.setMessage("Wysłano link do weryfikacyji na podany adres e-mail. Zweryfikuj konto, a następnie się zaloguj.")
                                alertDialog.setButton(
                                    AlertDialog.BUTTON_POSITIVE, "OK"
                                ) { dialog, _ ->
                                    dialog.dismiss()
                                    moveTaskToBack(true)
                                    val `in` = Intent(this@signup, MainActivity::class.java)
                                    startActivity(`in`)
                                }
                                alertDialog.show()
                            }
                        }
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName("$imie $nazwisko").build()
                        currentUser?.updateProfile(profileUpdates)
                            ?.addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    mProgress.dismiss()
                                }
                            }
                    }
                }
            )
    }
}
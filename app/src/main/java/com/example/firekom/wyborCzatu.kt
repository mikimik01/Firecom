package com.example.firekom

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.firekom.databinding.ActivityWyborCzatuBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class wyborCzatu : AppCompatActivity() {

    private lateinit var b: ActivityWyborCzatuBinding
    private lateinit var chatArrayList: ArrayList<Field>
    private lateinit var mAuth: FirebaseAuth

    private lateinit var dbRef: DatabaseReference

    private fun czy_jest_internet(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b=ActivityWyborCzatuBinding.inflate(layoutInflater)
        setContentView(b.root)

        val actionBar: ActionBar? = supportActionBar
        val colorDrawable = ColorDrawable(Color.parseColor("#0048FF"))
        actionBar?.setBackgroundDrawable(colorDrawable)
        actionBar?.addOnMenuVisibilityListener {  }

        window.statusBarColor = Color.parseColor("#0048FF")
        title = "FireKom"

        chatArrayList = ArrayList()
        dbRef = FirebaseDatabase.getInstance("https://firekom-49d0e-default-rtdb.europe-west1.firebasedatabase.app").getReference("baza")
        //pobieranie imienia usera
        mAuth = FirebaseAuth.getInstance()
        val user: FirebaseUser? = mAuth.currentUser
        val user_name = user?.displayName.toString()

        val name = arrayOf("miki","marcin")
        val tekst = arrayOf("siema","co tam")
        b.wyslij.isEnabled = false


        /*val f = Field(name[0], tekst[0])
        chatArrayList.add(f)*/



        b.tekstet.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                if(czy_jest_internet()) {
                    b.wyslij.isEnabled = count != 0
                }else{
                    internet_wylaczany()
                }
            }
        })

        var ile = "0"
        dbRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                ile = snapshot.child("ile").value as String

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })


        val msgs = dbRef.child("msgs")
        msgs.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                chatArrayList.clear()
                for (child in snapshot.children){
                    var imie = child.child("who").value
                    var m = child.child("msg").value
                    if(imie==null){
                        imie = "błąd"
                    }
                    if(m==null){
                        m = "błąd"
                    }
                    var field = Field(imie as String, m as String, imie==user_name)
                    //Toast.makeText(applicationContext, "imie: $imie " + "user: $user_name " +(imie==user_name).toString(), Toast.LENGTH_LONG).show()
                    chatArrayList.add(field)

                }
                b.myLv.adapter = MyAdapter(this@wyborCzatu, chatArrayList)

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        b.wyslij.setOnClickListener {
            if(czy_jest_internet()) {
                val wiadomosc = b.tekstet.text.toString()
                if (wiadomosc != "") {
                    dbRef.child("msgs").child("w${ile}").child("who").setValue(user_name)
                    dbRef.child("msgs").child("w${ile}").child("msg").setValue(wiadomosc)
                    var pomoc: Int = ile.toInt()
                    pomoc++
                    dbRef.child("ile").setValue("$pomoc")
                    b.tekstet.setText("")
                }
            }else{
                internet_wylaczany()
            }
        }




        b.myLv.adapter = MyAdapter(this, chatArrayList)
    }

    fun internet_wylaczany() {
        val alertDialog = AlertDialog.Builder(this@wyborCzatu).create()
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.setTitle("Brak połączenia z internetem")
        alertDialog.setButton(
            AlertDialog.BUTTON_POSITIVE, "OK"
        ) { dialog, _ -> dialog.dismiss() }
        alertDialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu1, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        dbRef.child("msgs").removeValue()
        dbRef.child("ile").setValue("0")
        return super.onOptionsItemSelected(item)
    }
}



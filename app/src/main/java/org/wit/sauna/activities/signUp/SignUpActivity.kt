package org.wit.sauna.activities.signUp

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import org.wit.sauna.R
import org.wit.sauna.activities.signIn.SignInActivity
import org.wit.sauna.models.credentials

class SignUpActivity : AppCompatActivity() {
    var ref: DatabaseReference? = null
    var fRef: DatabaseReference? = null
    var zref: DatabaseReference? = null
    var frefre: DatabaseReference? = null
    private var getemail: String? = null
    private var getpassword: String? = null
    private var getnamee: String? = null
    private var getidnumber: String? = null
    private var getage: String? = null
    var chec: CheckBox? = null
    var vsref: String? = null
    var name: EditText? = null
    var pass: EditText? = null
    var agee: EditText? = null
    var emaill: EditText? = null
    var str_email: String? = null
    var idnumberr: EditText? = null
    var login: TextView? = null
    var next: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        emaill = findViewById(R.id.email)
        chec = findViewById(R.id.checkBox)
        agee = findViewById(R.id.age)
        idnumberr = findViewById(R.id.idnumber)
        name = findViewById(R.id.setname)
        pass = findViewById(R.id.passw)
        agee = findViewById(R.id.age)
        next = findViewById<TextView>(R.id.nectact)
        login = findViewById<TextView>(R.id.loginbtn)
        next!!.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@SignUpActivity, SignInActivity::class.java)
            startActivity(intent)
        })
        //Database work
        getnamee = name!!.text.toString()
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val sname = preferences.getString("name", "")
        val sstatus = preferences.getString("status", "")
        val checktrue = preferences.getBoolean("check", false)
        zref = FirebaseDatabase.getInstance().getReference("vs")
        zref!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                vsref = snapshot.child("vscode").getValue(String::class.java)
                Log.i("ipwowowo", "$vsref/n")
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        ref = FirebaseDatabase.getInstance().reference
        getpassword = pass!!.getText().toString()
        login!!.setOnClickListener(View.OnClickListener {
            getemail = emaill!!.getText().toString()
            getpassword = pass!!.getText().toString()
            getage = agee!!.getText().toString()
            getidnumber = idnumberr!!.getText().toString()
            getnamee = name!!.getText().toString()
            if (getemail!!.isEmpty() or getpassword!!.isEmpty() or getage!!.isEmpty() or getidnumber!!.isEmpty() or getnamee!!.isEmpty()) {
                Toast.makeText(this@SignUpActivity, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            val a = getemail!!.replace(".", "")
            fRef = ref!!.child("users").child(a)
            frefre = ref!!.child("usersinfo").child(a)
            val status = FirebaseDatabase.getInstance().getReference("users").child(a)
            fRef!!.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    //                                getcnic = snapshot.child("name").getValue(String.class);
                    str_email = snapshot.child("email").getValue(
                        String::class.java
                    )
                    if (getemail == str_email) {
                        Toast.makeText(this@SignUpActivity, "user already exists", Toast.LENGTH_LONG).show()
                    } else {
                        val statuss = "user"
                        val cre = credentials(
                            getnamee,
                            getpassword,
                            getemail,
                            statuss,
                            getidnumber,
                            getage
                        )
                        status.setValue(cre)
                        Toast.makeText(this@SignUpActivity, "success", Toast.LENGTH_LONG).show()
                        val intent = Intent(this@SignUpActivity, SignInActivity::class.java)
                        startActivity(intent)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@SignUpActivity, "error$error", Toast.LENGTH_LONG).show()
                }
            })
        })

    }
}
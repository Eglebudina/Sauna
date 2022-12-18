package org.wit.sauna.display

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import org.wit.sauna.R
import org.wit.sauna.models.credentials
import org.wit.sauna.utils.Preferences

class displayad : AppCompatActivity() {
    var emaill: TextView? = null
    var ref: DatabaseReference? = null
    var fRef: DatabaseReference? = null
    var frefre: DatabaseReference? = null
    var update: Button? = null
    var delete: Button? = null
    var name: EditText? = null
    var pass: EditText? = null
    var agee: EditText? = null
    var idnumberr: EditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_displayad)
        emaill = findViewById(R.id.email)
        agee = findViewById(R.id.age)
        idnumberr = findViewById(R.id.idnumber)
        update = findViewById(R.id.update)
        name = findViewById(R.id.setname)
        pass = findViewById(R.id.passw)
        agee = findViewById(R.id.age)
        val email: String? = Preferences.readString(this@displayad, "gmail")
        val a = email!!.replace(".", "")
        ref = FirebaseDatabase.getInstance().reference
        fRef = ref!!.child("users").child(a)
        fRef!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name3 = snapshot.child("name").getValue(String::class.java)
                val pass3 = snapshot.child("pass").getValue(String::class.java)
                val id = snapshot.child("id").getValue(String::class.java)
                val age = snapshot.child("age").getValue(String::class.java)
                emaill!!.setText("" + email)
                agee!!.setText("" + age)
                name!!.setText("" + name3)
                pass!!.setText("" + pass3)
                idnumberr!!.setText("" + id)
            }

            override fun onCancelled(error: DatabaseError) {}
        })

/*
        delete!!.setOnClickListener(View.OnClickListener {
            val getemail = emaill!!.getText().toString()
            val getpassword = pass!!.getText().toString()
            val getage = agee!!.getText().toString()
            val getidnumber = idnumberr!!.getText().toString()
            val getnamee = name!!.getText().toString()
            if (getemail.isEmpty() or getpassword.isEmpty() or getage.isEmpty() or getidnumber.isEmpty() or getnamee.isEmpty()) {
                Toast.makeText(this@displayad, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            ref = FirebaseDatabase.getInstance().reference
            val a = getemail.replace(".", "")
            fRef = ref!!.child("users").child(a)
            fRef!!.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    fRef!!.removeValue()
                    Toast.makeText(this@displayad, "User Deleted", Toast.LENGTH_LONG).show()
                    finishAffinity()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@displayad, "error$error", Toast.LENGTH_LONG).show()
                }
            })
        })
*/

        update!!.setOnClickListener(View.OnClickListener {
            val getemail = emaill!!.getText().toString()
            val getpassword = pass!!.getText().toString()
            val getage = agee!!.getText().toString()
            val getidnumber = idnumberr!!.getText().toString()
            val getnamee = name!!.getText().toString()
            if (getemail.isEmpty() or getpassword.isEmpty() or getage.isEmpty() or getidnumber.isEmpty() or getnamee.isEmpty()) {
                Toast.makeText(this@displayad, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            ref = FirebaseDatabase.getInstance().reference
            val a = getemail.replace(".", "")
            fRef = ref!!.child("users").child(a)
            frefre = ref!!.child("usersinfo").child(a)
            val status = FirebaseDatabase.getInstance().getReference("users").child(a)
            fRef!!.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val statuss = "user"
                    val cre =
                        credentials(getnamee, getpassword, getemail, statuss, getidnumber, getage)
                    status.setValue(cre)
                    finish()
                    Toast.makeText(this@displayad, "User Updated", Toast.LENGTH_LONG).show()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@displayad, "error$error", Toast.LENGTH_LONG).show()
                }
            })
        })
    }
}
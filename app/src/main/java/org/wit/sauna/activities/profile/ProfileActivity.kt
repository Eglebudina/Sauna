package org.wit.sauna.activities.profile

import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import org.wit.sauna.R
import org.wit.sauna.utils.Preferences

class ProfileActivity : AppCompatActivity() {
    var namee: TextView? = null
    var idnumberr: TextView? = null
    var submit: TextView? = null
    var id: String? = null
    var sname: String? = null
    var bmi: String? = null
    var weight: String? = null
    var height: String? = null
    var btype: String? = null
    var xbmi: EditText? = null
    var xweight: EditText? = null
    var xheight: EditText? = null
    var xbtype: EditText? = null
    var ref: DatabaseReference? = null
    var fRef: DatabaseReference? = null
    var frefre: DatabaseReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        namee = findViewById<TextView>(R.id.getname)
        xbmi = findViewById<EditText>(R.id.bmi)
        xheight = findViewById<EditText>(R.id.height)
        xweight = findViewById<EditText>(R.id.weight)
        xbtype = findViewById<EditText>(R.id.btype)
        submit = findViewById<TextView>(R.id.start)
        idnumberr = findViewById<TextView>(R.id.idnumber)
        val email: String? = Preferences.readString(this@ProfileActivity, "gmail")
        val a = email!!.replace(".", "")
        ref = FirebaseDatabase.getInstance().reference
        fRef = ref!!.child("users").child(a)
        fRef!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name3 = snapshot.child("name").getValue(String::class.java)
                val pass3 = snapshot.child("pass").getValue(String::class.java)
                val id = snapshot.child("id").getValue(String::class.java)
                val age = snapshot.child("age").getValue(String::class.java)
                namee!!.text = "" + name3
                idnumberr!!.text = "" + id
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        val preferences = PreferenceManager.getDefaultSharedPreferences(this@ProfileActivity)
        sname = preferences.getString("name", "")
        id = preferences.getString("idnumberrrshare", "")
        if (sname!!.isEmpty() && id!!.isEmpty()) {
            namee!!.text = "" + sname
            idnumberr!!.text = "" + id
        }
        submit!!.setOnClickListener {
            bmi = xbmi!!.text.toString()
            weight = xweight!!.text.toString()
            height = xheight!!.text.toString()
            btype = xbtype!!.text.toString()
            if (bmi!!.isEmpty() || weight!!.isEmpty() || height!!.isEmpty() || btype!!.isEmpty()) {
                Toast.makeText(this@ProfileActivity, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                ref = FirebaseDatabase.getInstance().reference
                fRef = ref!!.child("users").child(a)
                frefre = ref!!.child("usersinfo").child(a)
                val status = FirebaseDatabase.getInstance().getReference("users").child(a)
                fRef!!.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        //                            credentials cre = new credentials(Integer.parseInt(bmi), weight, height, btype);
                        val postValues: MutableMap<String, Any> =
                            HashMap()
                        postValues["bmi"] = bmi!!
                        postValues["height"] = weight!!
                        postValues["weight"] = height!!
                        postValues["bloodtype"] = btype!!
                        status.updateChildren(postValues)
                        Toast.makeText(this@ProfileActivity, "User Updated", Toast.LENGTH_LONG).show()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@ProfileActivity, "error$error", Toast.LENGTH_LONG).show()
                    }
                })
            }
        }
    }
    }

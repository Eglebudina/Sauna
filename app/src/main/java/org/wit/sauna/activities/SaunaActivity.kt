package org.wit.sauna.activities

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import org.wit.sauna.R
import org.wit.sauna.activities.map.Constants
import org.wit.sauna.activities.map.MapActivity
import org.wit.sauna.databinding.ActivitySaunaBinding
import org.wit.sauna.main.MainApp
import org.wit.sauna.models.Location
import org.wit.sauna.models.SaunaModel
import org.wit.sauna.models.setdata
import org.wit.sauna.utils.Preferences
import timber.log.Timber.i
import java.io.IOException
import java.util.*

class SaunaActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySaunaBinding
    var sauna = SaunaModel()
    lateinit var app: MainApp
    var bitmap: Bitmap? = null
    var selectedImage: Uri? = null
    val PICK_IMAGE = 1
    var storageReference: StorageReference? = null
    var pdd: ProgressDialog? = null
    var lat : String = ""
    var lng : String = ""
    var egle = false

    private lateinit var imageIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var mapIntentLauncher : ActivityResultLauncher<Intent>
    var location = Location(52.245696, -7.139102, 15f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var edit = false

        binding = ActivitySaunaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbarAdd.title = title
        setSupportActionBar(binding.toolbarAdd)

        app = application as MainApp

        i("Sauna Activity started...")

        if (intent.hasExtra("sauna_edit")) {
            edit = true
            sauna = intent.extras?.getParcelable("sauna_edit")!!
            binding.saunaTitle.setText(sauna.title)
            binding.description.setText(sauna.description)
            binding.btnAdd.setText(R.string.save_sauna)
            Picasso.get()
                .load(sauna.image)
                .into(binding.saunaImage)
            if (sauna.image != Uri.EMPTY) {
                binding.chooseImage.setText(R.string.change_sauna_image)
            }
        }

/*
        binding.btnAdd.setOnClickListener() {
            sauna.title = binding.saunaTitle.text.toString()
            sauna.description = binding.description.text.toString()
            if (sauna.title.isEmpty()) {
                Snackbar.make(it,R.string.enter_sauna_title, Snackbar.LENGTH_LONG)
                    .show()
            } else {
                if (edit) {
                    app.saunas.update(sauna.copy())
                } else {
                    app.saunas.create(sauna.copy())
                }
            }
            i("add Button Pressed: $sauna")
            setResult(RESULT_OK)
            finish()
        }
*/
        storageReference = FirebaseStorage.getInstance().reference

        ///save button
        binding.btnAdd.setOnClickListener(View.OnClickListener {
            egle = true
            val rep: String? = Preferences.readString(this@SaunaActivity, "email")
            val i = (Random().nextInt(900000) + 100000).toString()

            val rep2 = rep!!.replace(".", "")
            val createpost =
                FirebaseDatabase.getInstance().reference.child("create").child("post").child(rep2).child(i);
            Log.i("egleclickcheck", "onClick: $rep2")
            val createpostforalluser =
                FirebaseDatabase.getInstance().reference.child("create").child("posts").child("all").child(i)
            Log.i("egle", "onCreate: "+rep2)
            if (binding.saunaTitle.text.toString().isNotEmpty() && binding.description.text.toString().isNotEmpty() && selectedImage != null){
             pdd = ProgressDialog(this@SaunaActivity)

            pdd!!.setTitle("Uploading Data.......")
                pdd!!.show()
                val randomkey = UUID.randomUUID().toString()
                val ref: StorageReference = storageReference!!.child("image/$randomkey")
                ref.putFile(selectedImage!!).addOnSuccessListener(OnSuccessListener<Any?> {
                    ref.downloadUrl.addOnSuccessListener(OnSuccessListener<Uri> { uri ->
                        createpost.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val dialog = Dialog(this@SaunaActivity)
                                Objects.requireNonNull(dialog.window)!!.setBackgroundDrawableResource(android.R.color.transparent)
                                dialog.setContentView(R.layout.prompt)
                                val ok = dialog.findViewById<Button>(R.id.yes)
                                val msg = dialog.findViewById<TextView>(R.id.textshow)
                               if (Constants.location != null) {
                                    LatLng(Constants.location!!.latitude, Constants.location!!.longitude)
                                   lat = sauna.lat.toString()
                                   lng= sauna.lng.toString()

                                } else {
                                    //To retrieve
                                     lat =
                                         Preferences.readString(this@SaunaActivity,"lat").toString() //0 is the default value
                                     lng =
                                         Preferences.readString(this@SaunaActivity,"lng").toString() //0 is the default value
                                }
                                if (egle) {
                                    val pd = setdata(
                                        binding.saunaTitle.text.toString(),
                                        uri.toString(),
                                        binding.description.text.toString(),
                                        i,
                                        lat,
                                        lng
                                    )
                                    createpost.setValue(pd)
                                    createpostforalluser.setValue(pd)
                                    egle = false
                                }
                                msg.text = "Data inserted Successfully"
                                //                                                    egle = true;
                                ok.setOnClickListener { dialog.dismiss() }
                                dialog.show()
                            }

                            override fun onCancelled(error: DatabaseError) {}
                        })
                    })
                    Snackbar.make(
                        findViewById(android.R.id.content),
                        "Image Uploaded.",
                        Snackbar.LENGTH_LONG
                    ).show()
                    pdd!!.dismiss()
                }).addOnFailureListener(OnFailureListener {
                    val dialog = Dialog(this@SaunaActivity)
                    Objects.requireNonNull(dialog.window)?.setBackgroundDrawableResource(android.R.color.transparent)
                    dialog.setContentView(R.layout.prompt)
                    val ok = dialog.findViewById<Button>(R.id.yes)
                    val msg = dialog.findViewById<TextView>(R.id.textshow)
                    msg.text = "Failed to upload"
                    ok.setOnClickListener { dialog.dismiss() }
                    dialog.show()
                    pdd!!.dismiss()
                }).addOnProgressListener { snapshot ->
                    val progresspercent: Double =
                        100.00 * snapshot.bytesTransferred / snapshot.totalByteCount
                    pdd!!.setMessage("Percentage: " + progresspercent.toInt() + "%")
                }
            } else {
                val dialog = Dialog(this@SaunaActivity)
                Objects.requireNonNull(dialog.window)?.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.setContentView(R.layout.prompt)
                val ok = dialog.findViewById<Button>(R.id.yes)
                val msg = dialog.findViewById<TextView>(R.id.textshow)
                msg.text = "Missing fields (text/picture)"
                ok.setOnClickListener { dialog.dismiss() }
                dialog.show()
            }
        })



/*        binding.chooseImage.setOnClickListener {
            showImagePicker(imageIntentLauncher)
        }*/

        binding.saunaLocation.setOnClickListener {
            val location = Location(52.245696, -7.139102, 15f)
            if (sauna.zoom != 0f) {
                location.lat =  sauna.lat
                location.lng = sauna.lng
                location.zoom = sauna.zoom
            }
            val launcherIntent = Intent(this, MapActivity::class.java)
                .putExtra("location", location)
            mapIntentLauncher.launch(launcherIntent)
        }

        //categoryworkend//
        binding.chooseImage.setOnClickListener(View.OnClickListener {
            val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(i,PICK_IMAGE)
        })

        registerImagePickerCallback()
        registerMapCallback()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_sauna, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_cancel -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun registerImagePickerCallback() {
        imageIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when(result.resultCode){
                    RESULT_OK -> {
                        if (result.data != null) {
                            i("Got Result ${result.data!!.data}")
                            sauna.image = result.data!!.data!!
                            Picasso.get()
                                .load(sauna.image)
                                .into(binding.saunaImage)
                            binding.chooseImage.setText(R.string.change_sauna_image)
                        } // end of if
                    }
                    RESULT_CANCELED -> { } else -> { }
                }
            }
    }

    private fun registerMapCallback() {
        mapIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when (result.resultCode) {
                    RESULT_OK -> {
                        if (result.data != null) {
                            i("Got Location ${result.data.toString()}")
                            location = result.data!!.extras?.getParcelable("location")!!
                            i("Location == $location")
                            sauna.lat = location.lat
                            sauna.lng = location.lng
                            sauna.zoom = location.zoom
                        } // end of if
                    }
                    RESULT_CANCELED -> { } else -> { }
                }
            }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImage = data.data
            try {
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImage)
                Toast.makeText(this@SaunaActivity, "Image has been selected", Toast.LENGTH_SHORT).show()
                binding.saunaImage.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
//        when (resultCode) {
//            RESULT_OK -> {
//                if (data != null) {
//                    if (data.data != null) {
//                        i("Got Location ${data.data.toString()}")
//                        location = data.extras?.getParcelable("location")!!
//                        i("Location == $location")
//                        sauna.lat = location.lat
//                        sauna.lng = location.lng
//                        sauna.zoom = location.zoom
//                    }
//                } // end of if
//            }
//            RESULT_CANCELED -> { } else -> { }
//        }
    }
}
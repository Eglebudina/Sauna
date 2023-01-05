package org.wit.sauna.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.TooltipCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.internal.NavigationMenuView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import org.wit.sauna.R
import org.wit.sauna.`interface`.MyInterface
import org.wit.sauna.activities.map.Constants
import org.wit.sauna.activities.map.MYLocation1
import org.wit.sauna.activities.map.MapActivity
import org.wit.sauna.activities.map.mapactivitys
import org.wit.sauna.activities.profile.ProfileActivity
import org.wit.sauna.adapters.SaunaListener
import org.wit.sauna.adapters.getpostdataforcategoriesforuser
import org.wit.sauna.databinding.ActivitySaunaBinding
import org.wit.sauna.databinding.ActivitySaunaListBinding
import org.wit.sauna.databinding.NavHeaderMainBinding
import org.wit.sauna.display.displayad
import org.wit.sauna.main.MainApp
import org.wit.sauna.models.SaunaModel
import org.wit.sauna.models.setdata
import org.wit.sauna.utils.Preferences
import timber.log.Timber
import java.io.IOException
import java.util.*

class SaunaListActivity : AppCompatActivity(), SaunaListener, MyInterface,
    NavigationView.OnNavigationItemSelectedListener {

    lateinit var app: MainApp
    private lateinit var binding: ActivitySaunaListBinding
    private lateinit var refreshIntentLauncher: ActivityResultLauncher<Intent>
    var drawerLayout: DrawerLayout? = null
    var btntogle: ImageView? = null
    var imagePicked: ImageView? = null
    private val PERMISSION_REQUEST_CODE = 1
    var drawerToggle: ActionBarDrawerToggle? = null
    var name: String? = null
    val filterArrayList: ArrayList<setdata> = ArrayList<setdata>()
    var lat: String = ""
    var lng: String = ""
    var url: String? = null
    var themeset: Boolean? = true
    var fRef: DatabaseReference? = null
    var ref: DatabaseReference? = null
    var recyclerView: RecyclerView? = null
    var rep: String = ""
    var ad: ArrayList<setdata> = ArrayList<setdata>()
    var myFirebase: DatabaseReference? = null
    var edit = false
    var imgShow = false
    var egle = false
    var searchUserOrder: androidx.appcompat.widget.SearchView? = null

    var sauna = SaunaModel()
    var bitmap: Bitmap? = null
    var selectedImage: Uri? = null
    val PICK_IMAGE = 1
    var storageReference: StorageReference? = null
    var pdd: ProgressDialog? = null
    private lateinit var imageIntentLauncher: ActivityResultLauncher<Intent>
    private lateinit var mapIntentLauncher: ActivityResultLauncher<Intent>
    var location = org.wit.sauna.models.Location(52.245696, -7.139102, 15f)

    private var postadapter: getpostdataforcategoriesforuser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sauna_list)
//        binding = ActivitySaunaListBinding.inflate(layoutInflater)
//        setContentView(binding.root)
        binding.toolbar.title = title
        myFirebase = FirebaseDatabase.getInstance().reference
        searchUserOrder = findViewById(R.id.search_user_order_tv)

        recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        rep = Preferences.readString(this@SaunaListActivity, "email").toString()
        val headerView = binding.navView.getHeaderView(0)
        val headerBinding: NavHeaderMainBinding = NavHeaderMainBinding.bind(headerView)
        ref = FirebaseDatabase.getInstance().reference
        recyclerView!!.layoutManager = LinearLayoutManager(this@SaunaListActivity)
        setSupportActionBar(binding.toolbar)
        //permissions
        drawerLayout = findViewById<View>(R.id.drawerLayout) as DrawerLayout
        drawerLayout!!.setScrimColor(Color.TRANSPARENT)
        //to open drawer
        btntogle = findViewById<ImageView>(R.id.btnToggle)
        btntogle!!.setOnClickListener(View.OnClickListener {
            //loading picture, name and email in drawer layout
            val a = rep.replace(".", "")
            fRef = ref!!.child("users").child(a)
            fRef!!.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    url = snapshot.child("url").getValue(String::class.java)
                    name = snapshot.child("name").getValue(String::class.java)
                    if (url != null) {
                        Picasso.get().load(url).into(headerBinding.imageView)
                    }
                    if (name != null) {
                        headerBinding.tvEmpName.text = name
                    }
                    headerBinding.tvEmpType.text = rep
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
            drawerLayout!!.openDrawer(Gravity.LEFT)
        })
        // For Navigation click
        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        val navMenuView = navigationView.getChildAt(0) as NavigationMenuView
        navMenuView.addItemDecoration(
            DividerItemDecoration(
                this@SaunaListActivity, DividerItemDecoration.VERTICAL
            )
        )
        navigationView.setNavigationItemSelectedListener { item: MenuItem? ->
            onNavigationItemSelected(
                item!!
            )
        }
        app = application as MainApp
        // to load data in recycler according to user email
        val rep2 = rep!!.replace(".", "")
        postadapter = getpostdataforcategoriesforuser(this@SaunaListActivity, ad, this)
        val cateDataa1 =
            FirebaseDatabase.getInstance().getReference("create").child("post").child(rep2)
        cateDataa1.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                ad.clear()
                for (petdatasnap in snapshot.children) {
                    val petData: setdata? = petdatasnap.getValue(setdata::class.java)
                    if (petData != null) {
                        ad.add(petData)
                    }
                }
                recyclerView!!.adapter = postadapter
                postadapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SaunaListActivity, "error", Toast.LENGTH_SHORT).show()
            }
        })
        registerRefreshCallback()
        checkInternet()
        registerMapCallback()
        searchUserOrder!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    if (newText.isEmpty()) {
                        postadapter!!.mfilterList(ad)
                        return false
                    }
                }
                filter(newText.toString())
                return false

            }
        })


//            object : TextWatcher {
//            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
//            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
//            override fun afterTextChanged(editable: Editable) {
//
//            }
//        })

    }

    fun filter(text: String) {
        filterArrayList.clear()
        for (item in ad) {
            try {

//                Log.i("tariq", "filter:vcheck "+item.title +" des "+item.description+" txt "+text)

                if (item.name!!.toLowerCase()
                        .contains(text.lowercase(Locale.getDefault())) || item.description!!.toLowerCase()
                        .contains(text.lowercase(Locale.getDefault()))
                ) {
                    filterArrayList.add(item)
                }
                postadapter!!.mfilterList(filterArrayList)

            } catch (e: Exception) {
//                Log.i("tariq", "filter: "+e)
            }

        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_add -> {
                val launcherIntent = Intent(this, SaunaActivity::class.java)
                refreshIntentLauncher.launch(launcherIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSaunaClick(sauna: SaunaModel) {
        val launcherIntent = Intent(this, SaunaActivity::class.java)
        launcherIntent.putExtra("sauna_edit", sauna)
        refreshIntentLauncher.launch(launcherIntent)
    }

    private fun registerRefreshCallback() {
        refreshIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { binding.recyclerView.adapter?.notifyDataSetChanged() }
    }

    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            this@SaunaListActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        return result == PackageManager.PERMISSION_GRANTED
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId
        if (id == R.id.nav_home) {
            val drawer = findViewById<View>(R.id.drawerLayout) as DrawerLayout
            drawer.closeDrawer(GravityCompat.START)
        }
        if (id == R.id.nav_maps) {
            val intent = Intent(this, mapactivitys::class.java)
            startActivity(intent)
        }
        if (id == R.id.nav_profile) {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
        if (id == R.id.nav_settings) {
            val intent = Intent(this, displayad::class.java)
            startActivity(intent)
        }
        if (id == R.id.nav_theme) {
            /*           themeset = if(themeset == true){
                           setTheme(R.style.Theme_Sauna_Dark)
                   //                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                           false
                       } else{
                           setTheme(R.style.Theme_Sauna)

                   //                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                           true


                       }*/
            setTheme(R.style.Theme_Sauna)

            setContentView(R.layout.activity_sauna_list)
            this@SaunaListActivity.recreate()

        }
        val drawer = findViewById<View>(R.id.drawerLayout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun configureDrawerView() {
        binding.navView.setNavigationItemSelectedListener(this)
        binding.navView.itemIconTintList = null
        drawerToggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, 0, 0)
        drawerToggle!!.isDrawerIndicatorEnabled = true
        TooltipCompat.setTooltipText(binding.navView, "more options")
        binding.drawerLayout.addDrawerListener(drawerToggle!!)
    }


    override fun onPostCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onPostCreate(savedInstanceState, persistentState)
        drawerToggle!!.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        drawerToggle!!.onConfigurationChanged(newConfig)
        super.onConfigurationChanged(newConfig)
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


    private fun checkGPSStatus() {
        var locationManager: LocationManager? = null
        var gps_enabled = false
        var network_enabled = false
        if (locationManager == null) {
            locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        }
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: Exception) {
        }
        try {
            network_enabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: Exception) {
        }
        if (!gps_enabled && !network_enabled) {
            val dialog = AlertDialog.Builder(this@SaunaListActivity)
            dialog.setMessage("GPS not enabled")
            dialog.setPositiveButton(
                "Ok"
            ) { dialog, which -> //this will navigate user to the device location settings screen
                val myIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                this@SaunaListActivity.startActivityForResult(myIntent, 100)
            }
            val alert = dialog.create()
            alert.show()
        } else {
            getLocation1()
            Handler().postDelayed({
                //goButton.setVisibility(View.VISIBLE);
                //                    progressBar.setVisibility(View.GONE);
                val sharedPref: SharedPreferences =
                    this@SaunaListActivity.getSharedPreferences("location", 0)
                val lat = sharedPref.getString("lat", "") //0 is the default value
                val lng = sharedPref.getString("lng", "") //0 is the default value
                if (lat != "" && lng != "") {
                    //   Double lati =Double.valueOf(lat);
                    //   Double longi =Double.valueOf(lng);
                }
            }, 2500)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        checkGPSStatus()
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImage = data.data
            try {
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImage)
                imgShow = true;
                Toast.makeText(
                    this@SaunaListActivity,
                    "Image has been selected",
                    Toast.LENGTH_SHORT
                ).show()
                if (imagePicked != null) {
                    imagePicked!!.setImageBitmap(bitmap)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        // checkGPSStatus();
    }

    private fun getLocation1() {
        val locationResult: MYLocation1.LocationResult = object : MYLocation1.LocationResult() {
            override fun gotLocation(location: Location?) {
                if (location != null) {
                    runOnUiThread {
                        // change UI elements here
                        Constants.location = location
                        //progressBar.setVisibility(View.GONE);
                        val sharedPref = applicationContext.getSharedPreferences("location", 0)
                        val editor = sharedPref.edit()
                        editor.putString("lat", java.lang.Double.toString(location.latitude))
                        editor.putString("lng", java.lang.Double.toString(location.longitude))
                        editor.apply()
                    }
                    //Got the location!
                } else {
                    runOnUiThread {
                        // change UI elements here
                        Toast.makeText(
                            this@SaunaListActivity, "Please try again", Toast.LENGTH_SHORT
                        ).show()
                        //     ShowDialogForDialog("start");
                    }
                }
            }
        }
        val myLocation = MYLocation1()
        myLocation.getLocation(this, locationResult)
    }

    private fun checkInternet() {
        if (isNetworkAvailable(this@SaunaListActivity)) {
            checkGPSStatus()
        } else {
            val alertDialog = AlertDialog.Builder(this@SaunaListActivity)
            alertDialog.setTitle("Internet Error")
            alertDialog.setMessage("Internet is not enabled! ")
            alertDialog.setPositiveButton(
                "Retry"
            ) { dialog, which ->
                dialog.cancel()
                checkInternet()
            }
            alertDialog.setNegativeButton(
                "Cancel"
            ) { dialog, which ->
                dialog.cancel()
                System.exit(0)
            }
            alertDialog.show()
        }
    }

    private fun isNetworkAvailable(context: Context?): Boolean {
        if (context == null) {
            return false
        }
        val connectivityManager =
            context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        // if no network is availablgoButton_ide networkInfo will be null, otherwise check if we are connected
        try {
            @SuppressLint("MissingPermission") val activeNetworkInfo =
                connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        } catch (e: java.lang.Exception) {
            Log.e("UtilsClass", "isNetworkAvailable()::::" + e.message)
        }
        return false
    }

    override fun click(model: setdata?) {
        TODO("Not yet implemented")
    }

    // Interface to delete item from recyclerview
    @SuppressLint("NotifyDataSetChanged")
    override fun delete(model: setdata?) {
        val key: String? = model!!.count
        val a = rep.replace(".", "")
        //Query to delete data fro, firebase
        myFirebase!!.child("create").child("post").child(a).child(key!!).removeValue()
            .addOnCompleteListener(
                OnCompleteListener<Void?> {
                    postadapter!!.notifyDataSetChanged()
                    Toast.makeText(this, "Removed successfully", Toast.LENGTH_SHORT).show()
                })
        myFirebase!!.child("create").child("posts").child("all").child(key).removeValue()
            .addOnCompleteListener(
                OnCompleteListener<Void?> { postadapter!!.notifyDataSetChanged() })
    }

    override fun update(model: setdata?) {
        var btnAdd: Button
        var getToPoint: String
        getToPoint = model!!.count.toString()
//        btnAdd = view.findViewById(R.id.btnAdd)
        storageReference = FirebaseStorage.getInstance().reference

        Toast.makeText(this, "work successfully", Toast.LENGTH_SHORT).show()
        val alert = AlertDialog.Builder(this)
//        var binding: ActivitySaunaBinding = ActivitySaunaBinding.inflate(layoutInflater)
        val bind: ActivitySaunaBinding =
            ActivitySaunaBinding.inflate(LayoutInflater.from(this@SaunaListActivity))
//        var binding: ActivitySaunaBinding = ActivitySaunaBinding.inflate(LayoutInflater.from(this@SaunaListActivity), R.layout. activity_sauna, null, false)

        setContentView(binding.root)
        /*     val inflater = LayoutInflater.from(this)
             val view: View = inflater.inflate(R.layout.activity_sauna, null)*/
        alert.setView(bind.root)
        val alertDialog: AlertDialog = alert.create()
        alertDialog.setCanceledOnTouchOutside(true)

        imagePicked = bind.saunaImage

        if (intent.hasExtra("sauna_edit")) {
            edit = true
            sauna = intent.extras?.getParcelable("sauna_edit")!!
            bind.saunaTitle.setText(sauna.title)
            bind.description.setText(sauna.description)
            bind.btnAdd.setText(R.string.save_sauna)
            Picasso.get()
                .load(sauna.image)
                .into(bind.saunaImage)
            if (sauna.image != Uri.EMPTY) {
                bind.chooseImage.setText(R.string.change_sauna_image)
            }
        }
        //if (intent.hasExtra("sauna_edit")) {
        bind.saunaTitle.setText(model.name)
        bind.description.setText(model.description)
        bind.btnAdd.setText(R.string.save_sauna)
//        Picasso.get()
//            .load(model.randomkey)
//            .into(bind.saunaImage)
//        selectedImage = Uri.parse(model.randomkey)
        // if (sauna.image != Uri.EMPTY) {
        bind.chooseImage.setText(R.string.change_sauna_image)
        // }
        //}


        bind.btnAdd.setOnClickListener(View.OnClickListener {
            egle = true
            val rep: String? = Preferences.readString(this@SaunaListActivity, "email")
            val i = (Random().nextInt(900000) + 100000).toString()

            val rep2 = rep!!.replace(".", "")
            val createpost =
                FirebaseDatabase.getInstance().reference.child("create").child("post").child(rep2)
                    .child(getToPoint)
            val createpostforalluser =
                FirebaseDatabase.getInstance().reference.child("create").child("posts").child("all")
                    .child(getToPoint)
            if (bind.saunaTitle.text.toString()
                    .isNotEmpty() && bind.description.text.toString()
                    .isNotEmpty() && selectedImage != null
            ) {
                pdd = ProgressDialog(this@SaunaListActivity)
                pdd!!.setTitle("Uploading Data.......")
                pdd!!.show()
                val randomkey = UUID.randomUUID().toString()
                val ref: StorageReference = storageReference!!.child("image/$randomkey")
                ref.putFile(selectedImage!!).addOnSuccessListener(OnSuccessListener<Any?> {
                    ref.downloadUrl.addOnSuccessListener(OnSuccessListener<Uri> { uri ->
                        createpost.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val dialog = Dialog(this@SaunaListActivity)
                                Objects.requireNonNull(dialog.window)!!
                                    .setBackgroundDrawableResource(android.R.color.transparent)
                                dialog.setContentView(R.layout.prompt)
                                val ok = dialog.findViewById<Button>(R.id.yes)
                                if (Constants.location != null) {
                                    val location = org.wit.sauna.models.Location(model.lat?.toDouble() ?: sauna.lat, model.lng?.toDouble() ?: sauna.lng, 15f)
                                    if (sauna.zoom != 0f) {
                                        location.lat = sauna.lat
                                        location.lng = sauna.lng
                                    }
                                    lat = location.lat.toString()
                                    lng = location.lng.toString()
                                } else {
                                    //To retrieve
                                    lat =
                                        Preferences.readString(this@SaunaListActivity, "lat")
                                            .toString() //0 is the default value
                                    lng =
                                        Preferences.readString(this@SaunaListActivity, "lng")
                                            .toString() //0 is the default value
                                }
                                val msg = dialog.findViewById<TextView>(R.id.textshow)
                                if (egle) {
                                    val map = HashMap<String, Any>()
                                    map["description"] = bind.description.text.toString()
                                    map["name"] = bind.saunaTitle.text.toString()
                                    map["randomkey"] = uri.toString()
                                    map["lat"] = lat
                                    map["lng"] = lng
                                    createpost.updateChildren(map)
                                    createpostforalluser.updateChildren(map)
                                    egle = false
                                }
                                msg.text = "Data inserted Successfully"
                                //                                                    egle = true;
                                ok.setOnClickListener {
                                    alertDialog.dismiss()
                                    dialog.dismiss()
                                }
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
                    val dialog = Dialog(this@SaunaListActivity)
                    Objects.requireNonNull(dialog.window)
                        ?.setBackgroundDrawableResource(android.R.color.transparent)
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
                val dialog = Dialog(this@SaunaListActivity)
                Objects.requireNonNull(dialog.window)
                    ?.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.setContentView(R.layout.prompt)
                val ok = dialog.findViewById<Button>(R.id.yes)
                val msg = dialog.findViewById<TextView>(R.id.textshow)
                msg.text = "Missing fields (text/picture)"
                ok.setOnClickListener { dialog.dismiss() }
                dialog.show()
            }
        })
        bind.saunaLocation.setOnClickListener {
            val location = org.wit.sauna.models.Location(model.lat?.toDouble() ?: sauna.lat, model.lng?.toDouble() ?: sauna.lng, 15f)
            if (sauna.zoom != 0f) {
                location.lat = sauna.lat
                location.lng = sauna.lng
                location.zoom = sauna.zoom
            }
            val launcherIntent = Intent(this, MapActivity::class.java)
                .putExtra("location", location)
            mapIntentLauncher.launch(launcherIntent)
        }

        //categoryworkend//
        bind.chooseImage.setOnClickListener(View.OnClickListener {
            val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(i, PICK_IMAGE)
        })
        // delay
        Handler().postDelayed({
            if (imgShow) {
                bind.saunaImage.setImageBitmap(bitmap)
                imgShow = false
            }
        }, 1000)


        alertDialog.show()

    }

    private fun registerMapCallback() {
        mapIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when (result.resultCode) {
                    RESULT_OK -> {
                        if (result.data != null) {
                            Timber.i("Got Location1 ${result.data.toString()}")
                            location = result.data!!.extras?.getParcelable("location")!!
                            Timber.i("Location == $location")
                            sauna.lat = location.lat
                            sauna.lng = location.lng
                            sauna.zoom = location.zoom
                        } // end of if
                    }
                    RESULT_CANCELED -> {}
                    else -> {}
                }
            }
    }


}
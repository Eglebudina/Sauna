package org.wit.sauna.activities.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import org.wit.sauna.R

class mapactivitys : AppCompatActivity() {
    var mMapView: MapView? = null
    var context: Context? = null
    var check = 0
    var sweetAlertDialogLoading: SweetAlertDialog? = null
    private var googleMap: GoogleMap? = null
    @SuppressLint("MissingPermission", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapactivitys)
        mMapView = findViewById(R.id.mapView)
        mMapView!!.onCreate(savedInstanceState)
        mMapView!!.onResume()
        this.context = applicationContext
        sweetAlertDialogLoading = SweetAlertDialog(this@mapactivitys, SweetAlertDialog.PROGRESS_TYPE)
        sweetAlertDialogLoading!!.progressHelper.barColor = Color.parseColor("#A5DC86")
        sweetAlertDialogLoading!!.titleText = "Fetching Latest Feed.."
        sweetAlertDialogLoading!!.contentText = "Please Wait..."
        sweetAlertDialogLoading!!.setCancelable(false)
        sweetAlertDialogLoading!!.show()
        try {
            MapsInitializer.initialize(applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }


        mMapView!!.getMapAsync(OnMapReadyCallback { mMap ->
            googleMap = mMap
            if (ActivityCompat.checkSelfPermission(
                    context!!,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context!!,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@OnMapReadyCallback
            }
            mMap.isMyLocationEnabled = true
            Log.e("-ppp", "loc: " + Constants.location)
            val sydney: LatLng = if (Constants.location != null) {
                LatLng(Constants.location!!.latitude, Constants.location!!.longitude)
            } else {
                //To retrieve
                val sharedPref = context!!.getSharedPreferences("location", 0)
                val lat = sharedPref.getString("lat", "0") //0 is the default value
                val lng = sharedPref.getString("lng", "0") //0 is the default value
                val lati = java.lang.Double.valueOf(lat)
                val longi = java.lang.Double.valueOf(lng)
                LatLng(lati, longi)
            }

            // For zooming automatically to the location of the marker
            val cameraPosition = CameraPosition.Builder().target(sydney).zoom(15f).build()
            googleMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            if (sweetAlertDialogLoading!!.isShowing) {
                sweetAlertDialogLoading!!.cancel()
            }
        })
    }
}
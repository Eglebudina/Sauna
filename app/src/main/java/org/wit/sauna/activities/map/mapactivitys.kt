package org.wit.sauna.activities.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.firebase.database.*
import org.wit.sauna.R
import org.wit.sauna.models.setdata
import java.util.*


class mapactivitys : AppCompatActivity() {
    var mMapView: MapView? = null
    var context: Context? = null
    var colours = arrayOf(
        "HUE_AZURE",
        "HUE_BLUE",
        "HUE_CYAN",
        "HUE_GREEN",
        "HUE_MAGENTA",
        "HUE_ORANGE",
        "HUE_RED",
        "HUE_ROSE",
        "HUE_VIOLET",
        "HUE_YELLOW"
    )
    val r: Random = Random()
    var bitmapFinal: Bitmap? = null

    var check = 0
    var ad: ArrayList<setdata> = ArrayList<setdata>()

    var fRef: DatabaseReference? = null
    var ref: DatabaseReference? = null
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
        sweetAlertDialogLoading =
            SweetAlertDialog(this@mapactivitys, SweetAlertDialog.PROGRESS_TYPE)
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
        ref = FirebaseDatabase.getInstance().reference

        fRef = ref!!.child("create").child("post")
        fRef!!.addValueEventListener(object : ValueEventListener {
            @SuppressLint("LogNotTimber")
            override fun onDataChange(snapshot: DataSnapshot) {
                //ad.clear()
                for (petdatasnap in snapshot.children) {
                    for (lastsnapshot in petdatasnap.children) {
                        Log.i("TAG", "onDataChange: " + lastsnapshot.value)
                        val petData: setdata? = lastsnapshot.getValue(setdata::class.java)
                        if (petData != null) {
                            ad.add(petData)
                            Log.i("TAG", "add Data: " + petData.name + " " + petData.lng)
                        }
                    }
                }
                for (i in 0 until ad.size) {
                    Log.i("tariq", "onResume: " + ad[i].lat!!.toDouble())
                    val ll = LatLng(ad[i].lat!!.toDouble(), ad[i].lng!!.toDouble())
                }
                // lets place some 5 markers
                // lets place some 5 markers
                for (i in 0 until ad.size) {
                    //BitmapDescriptorFactory.defaultMarker(Random().nextInt(360).toFloat())
                    googleMap!!.addMarker(
                        MarkerOptions().position(
                            LatLng(
                                ad[i].lat!!.toDouble(),
                                ad[i].lng!!.toDouble()
                            )
                        ).title(ad[i].title).icon(
                            BitmapDescriptorFactory.defaultMarker(
                                Random().nextInt(360).toFloat()
                            )
                        )
                    )

                    // Adding a marker
                    val marker = MarkerOptions().position(
                        LatLng(ad[i].lat!!.toDouble(), ad[i].lng!!.toDouble())
                    )
                        .title("Its  ${ad[i].name}")
                    marker.icon(
                        BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                    )
                    googleMap!!.addMarker(marker)

                    Glide.with(applicationContext)
                        .asBitmap()
                        .load(ad[i].randomkey)
                        .into(object : CustomTarget<Bitmap>(), GoogleMap.OnMarkerClickListener {
                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: Transition<in Bitmap>?
                            ) {

                                bitmapFinal =
                                    createUserBitmapFinal(resource)  //here we will insert the bitmap we got with the link in a placehold with white border.

                                val mark1 = googleMap!!.addMarker(
                                    MarkerOptions().position(
                                        LatLng(
                                            ad[i].lat!!.toDouble(),
                                            ad[i].lng!!.toDouble()
                                        )
                                    ).title("Its  ${ad.get(i).name}").icon(
                                        BitmapDescriptorFactory.fromBitmap(
                                            bitmapFinal!!
                                        )
                                    )
                                )

                                mark1!!.tag = 0
                                Log.d("TAG", "onResourceReady: " + mark1.tag)

//                                googleMap!!.setOnMarkerClickListener (this)

                            }

                            override fun onLoadCleared(placeholder: Drawable?) {
                                // this is called when imageView is cleared on lifecycle call or for
                                // some other reason.
                                // if you are referencing the bitmap somewhere else too other than this imageView
                                // clear it here as you can no longer have the bitmap
                            }

                            override fun onMarkerClick(p0: Marker): Boolean {
                                TODO("Not yet implemented")
                            }
                        })
                    // changing marker color
                    /*               if (i == 0)
                                   {
                                       marker.icon(
                                           BitmapDescriptorFactory
                                               .defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                                   }
                                   else{
                                       marker.icon(
                                           BitmapDescriptorFactory
                                               .defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                                   }


                                   if (i == 1) marker.icon(
                                       BitmapDescriptorFactory
                                           .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                                   )
                                   if (i == 2) marker.icon(
                                       BitmapDescriptorFactory
                                           .defaultMarker(BitmapDescriptorFactory.HUE_CYAN)
                                   )
                                   if (i == 3) marker.icon(
                                       BitmapDescriptorFactory
                                           .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                                   )
                                   if (i == 4) marker.icon(
                                       BitmapDescriptorFactory
                                           .defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)
                                   )*/
//                    googleMap!!.addMarker(marker)

                    // Move the camera to last position with a zoom level
                }


            }

            override fun onCancelled(error: DatabaseError) {
            }
        })


    }

    fun createUserBitmapFinal(bitmapInicial: Bitmap?): Bitmap? {
        var result: Bitmap? = null
        try {
            result = Bitmap.createBitmap(
                dp(62f),
                dp(76f),
                Bitmap.Config.ARGB_8888
            ) //change the size of the placeholder
            result.eraseColor(Color.TRANSPARENT)
            val canvas = Canvas(result)
            val drawable: Drawable = resources.getDrawable(R.drawable.ic_baseline_location_on_24)
            drawable.setBounds(
                0,
                0,
                dp(62f),
                dp(76f)
            ) //change the size of the placeholder, but you need to maintain the same proportion of the first line
            drawable.draw(canvas)
            val roundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            val bitmapRect = RectF()
            canvas.save()

            if (bitmapInicial != null) {
                val shader =
                    BitmapShader(bitmapInicial, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
                val matrix = Matrix()
                val scale: Float =
                    dp(104f) / bitmapInicial.width.toFloat()  //reduce or augment here change the size of the original bitmap inside the placehoder. But you need to adjust the line bitmapRect with the same proportion
                matrix.postTranslate(5f, 5f)
                matrix.postScale(scale, scale)
                roundPaint.shader = shader
                shader.setLocalMatrix(matrix)
                bitmapRect[10f, 10f, 104f + 10f] =
                    104f + 10f    //change here too to change the size
                canvas.drawRoundRect(bitmapRect, 56f, 56f, roundPaint)
            }

            canvas.restore()
            try {
                canvas.setBitmap(null)
            } catch (e: java.lang.Exception) {
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        return result
    }

    fun dp(value: Float): Int {
        return if (value == 0f) {
            0
        } else Math.ceil(resources.displayMetrics.density * value.toDouble()).toInt()
    }

    override fun onResume() {
        super.onResume()
        updateMarkers()
    }

    private fun updateMarkers() {
        Handler().postDelayed({
            Log.i("tariq", "onResume: delay ")

            for (i in 0 until ad.size) {
                BitmapDescriptorFactory.defaultMarker(Random().nextInt(360).toFloat())
                Log.i(
                    "tariq",
                    "onResume: ad ${ad[i].lat}  ${ad[i].lng}  ${ad[i].name}  ${ad[i].randomkey}"
                )
                // Adding a marker
                val marker = MarkerOptions().position(
                    LatLng(ad[i].lat!!.toDouble(), ad[i].lng!!.toDouble())
                )
                    .title("Its  ${ad[i].name}")
                marker.icon(
                    BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                )
                // changing marker color
                /*               if (i == 0)
                               {
                                   marker.icon(
                                       BitmapDescriptorFactory
                                           .defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                               }
                               else{
                                   marker.icon(
                                       BitmapDescriptorFactory
                                           .defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                               }


                               if (i == 1) marker.icon(
                                   BitmapDescriptorFactory
                                       .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                               )
                               if (i == 2) marker.icon(
                                   BitmapDescriptorFactory
                                       .defaultMarker(BitmapDescriptorFactory.HUE_CYAN)
                               )
                               if (i == 3) marker.icon(
                                   BitmapDescriptorFactory
                                       .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                               )
                               if (i == 4) marker.icon(
                                   BitmapDescriptorFactory
                                       .defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)
                               )*/
                googleMap!!.addMarker(marker)

                // Move the camera to last position with a zoom level
            }

            /*         if (ad.isNotEmpty()){
                     }
                     else{
                         Log.i("tariq", "onResume: empty ")

                     }  */
        }, 5000)
    }


}
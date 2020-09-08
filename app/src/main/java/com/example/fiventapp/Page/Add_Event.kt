package com.example.fiventapp.Page

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Log.e
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.fiventapp.Helper.PrefHelper
import com.example.fiventapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.add_event.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class Add_Event : AppCompatActivity() {
    lateinit var dbRef: DatabaseReference
    lateinit var helperPref: PrefHelper
    lateinit var filePathImage: Uri
    val REQUEST_CODE_IMAGE = 10002
    val PERMISSION_RC = 10003
    var value = 0.0
    lateinit var fAuth: FirebaseAuth
    lateinit var storageReference: StorageReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_event)
        fAuth = FirebaseAuth.getInstance()
        filePathImage = Uri.EMPTY
        helperPref = PrefHelper(this)
        storageReference = FirebaseStorage.getInstance().reference

        imgbrosur.setOnClickListener {
            when {
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) -> {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                        != PackageManager.PERMISSION_GRANTED
                    ) {
                        requestPermissions(
                            arrayOf(
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            ), PERMISSION_RC
                        )
                    } else {
                        imageChooser()
                    }
                }
                else -> {
                    imageChooser()
                }
            }
        }
        val calendar = Calendar.getInstance()
        val cyear = calendar.get(Calendar.YEAR)
        val cmonth = calendar.get(Calendar.MONTH)
        val cday = calendar.get(Calendar.DAY_OF_MONTH)

        et_open.setOnClickListener {
            val datepicker = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->
                    et_open.setText("" + mDay + "/" + (mMonth + 1) + "/" + mYear)
                },
                cyear,
                cmonth,
                cday
            )
            datepicker.show()
        }
        et_close.setOnClickListener {
            val datepicker = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->
                    et_close.setText("" + mDay + "/" + (mMonth + 1) + "/" + mYear)
                },
                cyear,
                cmonth,
                cday
            )
            datepicker.show()
        }

        btn_add.setOnClickListener {
            var open = et_open.text.toString()
            var close = et_close.text.toString()

            val dateFormat = SimpleDateFormat("dd/MM/yyyy")

            var dOpen = dateFormat.parse(open)
            var dClose = dateFormat.parse(close)
            var ddOpen = dOpen.time
            var ddClose = dClose.time
            e("cok", (ddOpen - ddClose).toString())
            if (ddOpen > ddClose) {
                Toast.makeText(this, "Tanggal open melebihi close", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            var brosur = imgbrosur.toString()
            var event_name = et_event.text.toString()
            var domisili = et_domisili.text.toString()
            var slot = et_slot.text.toString()
            var category = et_category.selectedItem.toString()
            var place = et_place.text.toString()
            var participant = et_participant.selectedItem.toString()

            var fee = et_fee.text.toString()
            if (event_name.isNotEmpty() && domisili.isNotEmpty() && slot.isNotEmpty() && category.isNotEmpty() && place.isNotEmpty() && open.isNotEmpty() && participant.isNotEmpty() && close.isNotEmpty()
                && fee.isNotEmpty()
            ) {
                addTourToFirebase(
                    event_name,
                    domisili,
                    slot,
                    category,
                    place,
                    participant,
                    open,
                    close,
                    fee
                )

            } else {
                Toast.makeText(
                    this,
                    "Fill Data",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun addTourToFirebase(
        event_name: String,
        domisili: String,
        slot: String,
        category: String,
        place: String,
        participant: String,
        open: String,
        close: String,
        fee: String

    ) {
        val nameXXX = UUID.randomUUID().toString()
        val id_event = UUID.randomUUID().toString()
        val uid = fAuth.currentUser?.uid
        if (filePathImage == Uri.EMPTY) {
            Toast.makeText(this, "Masukan brosur event", Toast.LENGTH_SHORT).show()
            return
        }
        val storageRef: StorageReference = storageReference
            .child("images/$uid/$nameXXX.${GetFileExtension(filePathImage)}")
        storageRef.putFile(filePathImage).addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener {
                dbRef = FirebaseDatabase.getInstance().getReference("event/$id_event")
                dbRef.child("brosur").setValue(it.toString())
                dbRef.child("id_event").setValue(id_event)
                dbRef.child("event_name").setValue(event_name)
                dbRef.child("domisili").setValue(domisili)
                dbRef.child("slot").setValue(slot)
                dbRef.child("category").setValue(category)
                dbRef.child("place").setValue(place)
                dbRef.child("participant").setValue(participant)
                dbRef.child("open_register").setValue(open)
                dbRef.child("close_register").setValue(close)
                dbRef.child("fee").setValue(fee)
                dbRef.child("tersisa").setValue((slot))
                FirebaseDatabase.getInstance().getReference("user/")
                    .child("${fAuth.uid}/id")
                    .addListenerForSingleValueEvent(
                        object : ValueEventListener {
                            override fun onDataChange(p0: DataSnapshot) {
                                dbRef.child("iduser").setValue(p0.value)
                            }

                            override fun onCancelled(p0: DatabaseError) {
                                Log.e("Error", p0.message)
                            }

                        })
            }
            Toast.makeText(
                this,
                "Success Upload",
                Toast.LENGTH_SHORT
            ).show()
            bar_add.visibility = View.GONE
            btn_add.visibility = View.VISIBLE

        }.addOnFailureListener {
            Log.e("TAG_ERROR", it.message)
        }.addOnProgressListener { taskSnapshot ->
            value = (100.0 * taskSnapshot
                .bytesTransferred / taskSnapshot.totalByteCount)
            btn_add.visibility = View.GONE
            bar_add.visibility = View.VISIBLE
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_RC -> {
                if (grantResults.isEmpty() ||
                    grantResults[0] == PackageManager.PERMISSION_DENIED
                ) {
                    Toast.makeText(
                        this,
                        "Ditolak",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    imageChooser()
                }
            }
        }
    }

    private fun imageChooser() {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(
            Intent.createChooser(intent, "Select Image"),
            REQUEST_CODE_IMAGE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        when (requestCode) {
            REQUEST_CODE_IMAGE -> {
                filePathImage = data?.data!!
                try {
                    val bitmap: Bitmap = MediaStore
                        .Images.Media.getBitmap(
                            this.contentResolver, filePathImage
                        )
                    Glide.with(this).load(bitmap)
                        .override(250, 250)
                        .centerCrop().into(imgbrosur)
                } catch (x: IOException) {
                    x.printStackTrace()
                }
            }
        }
    }


    fun GetFileExtension(uri: Uri): String? {
        val contentResolver = this.contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(
            R.anim.enter_top,
            R.anim.exit_bot
        )
    }
}
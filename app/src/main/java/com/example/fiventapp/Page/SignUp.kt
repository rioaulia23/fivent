package com.example.fiventapp.Page

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.fiventapp.Helper.PrefHelper
import com.example.fiventapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.signup.*
import java.io.IOException
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*

class SignUp : AppCompatActivity() {
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
        setContentView(R.layout.signup)
        fAuth = FirebaseAuth.getInstance()
        filePathImage = Uri.EMPTY
        helperPref = PrefHelper(this)
        storageReference = FirebaseStorage.getInstance().reference

        setSupportActionBar(toolbar_signup)
        supportActionBar!!.title = ""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        fun String.md5(): String {
            val md = MessageDigest.getInstance("MD5")
            return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
        }


        ava.setOnClickListener {
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

        btn_signup.setOnClickListener {
            var phone = et_phone.text.toString()
            var name = et_name.text.toString()
            var username = et_username.text.toString()
            var email = et_email.text.toString()
            var password = et_password.text.toString()

//            var born = et_date.text.toString()
//            var gender = spinner.selectedI<?xml version="1.0" encoding="utf-8"?>


            if (phone.isNotEmpty() && name.isNotEmpty() && username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                if (checkbox.isChecked) {
                    bar1.visibility = View.VISIBLE
                    btn_signup.visibility = View.GONE

                    fAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                simpanToFirebase(
                                    phone,
                                    name,
                                    username,
                                    email,
                                    password.md5()

                                )
                                Toast.makeText(this, "SignUp Success!", Toast.LENGTH_SHORT)
                                    .show()
                                onBackPressed()
                            } else {
                                bar1.visibility = View.GONE
                                btn_signup.visibility = View.VISIBLE


                                Toast.makeText(
                                    this,
                                    "Email already used",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Please agree Terms and Conditions", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                bar1.visibility = View.GONE
                btn_signup.visibility = View.VISIBLE
                Toast.makeText(this, "There's some empty input!", Toast.LENGTH_SHORT).show()
            }

        }


        val calendar = Calendar.getInstance()
        val cyear = calendar.get(Calendar.YEAR)
        val cmonth = calendar.get(Calendar.MONTH)
        val cday = calendar.get(Calendar.DAY_OF_MONTH)

//        et_date.setOnClickListener {
//            val datepicker = DatePickerDialog(
//                this,
//                DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->
//                    et_date.setText("" + mDay + "/" + (mMonth + 1) + "/" + mYear)
//                },
//                cyear,
//                cmonth,
//                cday
//            )
//            datepicker.show()
//        }

    }

    fun simpanToFirebase(
        phone: String,
        name: String,
        username: String,
        email: String,
        password: String
//        born: String
    ) {
        val nameXXX = UUID.randomUUID().toString()
        val uidUser = fAuth.currentUser.uid
        val uid = fAuth.currentUser.uid
        if (filePathImage == Uri.EMPTY) {
            Toast.makeText(this, "Masukan foto profile", Toast.LENGTH_SHORT).show()
            return
        }
        val storageRef: StorageReference = storageReference
            .child("ava/$uid/$nameXXX.${GetFileExtension(filePathImage)}")
        storageRef.putFile(filePathImage).addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener {
                dbRef = FirebaseDatabase.getInstance().getReference("user/$uidUser")
                dbRef.child("ava").setValue(it.toString())
                dbRef.child("/id").setValue(uidUser)
                dbRef.child("/phone").setValue(phone)
                dbRef.child("/name").setValue(name)
                dbRef.child("/username").setValue(username)
//        dbRef.child("/born").setValue(born)
                dbRef.child("/email").setValue(email)
                dbRef.child("/password").setValue(password)
                dbRef.child("/role").setValue("Reguler")



                finish()
            }
        }
    }

    fun GetFileExtension(uri: Uri): String? {
        val contentResolver = this.contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        overridePendingTransition(
            R.anim.enter_left,
            R.anim.exit_right
        )
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(
            R.anim.enter_left,
            R.anim.exit_right
        )
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
                filePathImage = data.data!!
                try {
                    val bitmap: Bitmap = MediaStore
                        .Images.Media.getBitmap(
                        this.contentResolver, filePathImage
                    )
                    Glide.with(this).load(bitmap)
                        .override(250, 250)
                        .centerCrop().into(ava)
                } catch (x: IOException) {
                    x.printStackTrace()
                }
            }
        }
    }
}


package com.example.fiventapp.Page

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
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
import kotlinx.android.synthetic.main.edit_profile.*
import java.io.IOException

class EditProfile : AppCompatActivity() {
    lateinit var preferences: PrefHelper
    val REQUEST_CODE_IMAGE = 10002
    val PERMISSION_RC = 10003
    var value = 0.0
    lateinit var filePathImage: Uri
    lateinit var firebaseStorage: FirebaseStorage
    lateinit var storageReference: StorageReference
    lateinit var dbRef: DatabaseReference
    private lateinit var fAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_profile)

        setSupportActionBar(toolbar_edit)
        supportActionBar!!.title = ""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        preferences = PrefHelper(this)
        fAuth = FirebaseAuth.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
        storageReference = firebaseStorage.reference


        img_edit.setOnClickListener {
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
        val userid = fAuth.currentUser.uid
        val dataUserRef = FirebaseDatabase.getInstance().getReference("user/$userid")


        FirebaseDatabase.getInstance().getReference("user/${fAuth.uid}")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {

                    et_name_edit.setText(p0.child("name").value.toString())
                    et_phone_edit.setText(p0.child("phone").value.toString())
                    et_username_edit.setText(p0.child("username").value.toString())
                    et_email_edit.setText(p0.child("email").value.toString())
                }

                override fun onCancelled(p0: DatabaseError) {
                }
            })
        dataUserRef.child("ava").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                Glide.with(this@EditProfile).load(p0.value.toString())
                    .centerCrop()
                    .error(R.drawable.person)
                    .into(img_edit)

            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.save, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item!!.itemId

        var name = et_name_edit.text.toString()
        var username = et_username_edit.text.toString()
        var email = et_email_edit.text.toString()

        if (id == R.id.save) {
            if (name.isNotEmpty() && username.isNotEmpty() && email.isNotEmpty()) {
                val userid = fAuth.currentUser.uid
                val eteditnamao = et_name_edit.text.toString()
                val edituser = et_username_edit
                dbRef = FirebaseDatabase.getInstance().reference
                try {
                    val storageRef: StorageReference = storageReference
                        .child(
                            "ava/$userid/${preferences.getUID()}.${GetFileExtension(
                                filePathImage
                            )}"
                        )
                    storageRef.putFile(filePathImage).addOnSuccessListener {
                        storageRef.downloadUrl.addOnSuccessListener {
                            dbRef.child("user/$userid/ava").setValue(it.toString())
                        }
                    }.addOnFailureListener {
                        Log.e("TAG_ERROR", it.message)
                    }.addOnProgressListener { taskSnapshot ->
                        value = (100.0 * taskSnapshot
                            .bytesTransferred / taskSnapshot.totalByteCount)
                    }
                } catch (e: UninitializedPropertyAccessException) {
                    Toast.makeText(this, "Sukses", Toast.LENGTH_SHORT).show()
                }
                dbRef.child("user/$userid/name").setValue(eteditnamao)
                dbRef.child("user/$userid/username").setValue(edituser)

                Toast.makeText(this, "Sukses", Toast.LENGTH_SHORT).show()
                Handler().postDelayed({
                    onBackPressed()
                }, 1000)

            }
        } else {

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        overridePendingTransition(
            R.anim.enter_left,
            R.anim.exit_right
        )
        return super.onSupportNavigateUp()
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

    fun GetFileExtension(uri: Uri): String? {
        val contentResolver = this.contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
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
                        .override(100, 100)
                        .centerCrop().into(img_edit)
                } catch (x: IOException) {
                    x.printStackTrace()
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(
            R.anim.enter_left,
            R.anim.exit_right
        )
    }


}
package com.example.fiventapp.Page

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.fiventapp.Helper.PrefHelper
import com.example.fiventapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.profile.*

class Profile : AppCompatActivity() {
    lateinit var preferences: PrefHelper
    lateinit var dbRef: DatabaseReference
    private lateinit var fAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile)

        bar_profile.visibility = View.VISIBLE
        rl_pro.visibility = View.GONE
        preferences = PrefHelper(this)
        fAuth = FirebaseAuth.getInstance()

        getData()

        btnEdit.setOnClickListener {
            startActivity(Intent(this@Profile, EditProfile::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        getData()
    }

    fun getData() {
        val userid = fAuth.currentUser?.uid
        val dataUserRef = FirebaseDatabase.getInstance().getReference("user/$userid")

        FirebaseDatabase.getInstance().getReference("user/${userid}")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    bar_profile.visibility = View.GONE
                    rl_pro.visibility = View.VISIBLE

                    tvNama.text = p0.child("name").value.toString()
                    tvPhone.text = p0.child("phone").value.toString()
                    usernameprofile.setText(p0.child("username").value.toString())
                    emailprofile.setText(p0.child("email").value.toString())
                }

                override fun onCancelled(p0: DatabaseError) {
                }
            })
        dataUserRef.child("ava").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                Glide.with(this@Profile).load(p0.value.toString())
                    .centerCrop()
                    .error(R.drawable.person)
                    .into(img_profile)
                img_profile.setOnClickListener {
                    Toast.makeText(this@Profile, "Profile", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@Profile, DetailFoto::class.java)
                    intent.putExtra("foto", p0.value.toString())
                    startActivity(intent)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }
}
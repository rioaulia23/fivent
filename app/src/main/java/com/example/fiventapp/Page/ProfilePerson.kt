package com.example.fiventapp.Page

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fiventapp.Adapter.ProfilePersonAdapter
import com.example.fiventapp.Helper.PrefHelper
import com.example.fiventapp.Model.EventModel
import com.example.fiventapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.profileperson.*

class ProfilePerson : AppCompatActivity() {

    lateinit var preferences: PrefHelper
    lateinit var storageReference: StorageReference
    lateinit var firebaseStorage: FirebaseStorage
    lateinit var dbRef: DatabaseReference
    private lateinit var fAuth: FirebaseAuth
    private var profilePerson: ProfilePersonAdapter? = null
    private var recyclerView: RecyclerView? = null
    private var list: MutableList<EventModel> = java.util.ArrayList()
    lateinit var filePathImage: Uri


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profileperson)

        rlprofileperson.visibility = View.GONE
        barprofileperseon.visibility = View.VISIBLE
        preferences = PrefHelper(this)
        fAuth = FirebaseAuth.getInstance()

        val userid = intent.getStringExtra("iduser")
        val dataUserRef = FirebaseDatabase.getInstance().getReference("user/$userid")


        FirebaseDatabase.getInstance().getReference("user/${userid}")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    rlprofileperson.visibility = View.VISIBLE
                    barprofileperseon.visibility = View.GONE
                    tvNama2.text = p0.child("name").value.toString()
                    tvPhone2.text = p0.child("phone").value.toString()

                }

                override fun onCancelled(p0: DatabaseError) {
                }
            })
        dataUserRef.child("ava").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                Glide.with(this@ProfilePerson).load(p0.value.toString())
                    .centerCrop()
                    .error(R.drawable.person)
                    .into(img_profile2)
                img_profile2.setOnClickListener {
                    Toast.makeText(this@ProfilePerson, "Profile", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@ProfilePerson, DetailFoto::class.java)
                    intent.putExtra("fotoprofil", p0.value.toString())
                    startActivity(intent)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })

        var linearLayoutManager = LinearLayoutManager(this)
        recyclerView = findViewById(R.id.rc_person)
        recyclerView!!.layoutManager = linearLayoutManager
        recyclerView!!.setHasFixedSize(true)
        dbRef = FirebaseDatabase.getInstance()
            .reference.child("event")
        dbRef.orderByChild("iduser").equalTo(userid).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                list = ArrayList()
                for (dataSnapshot in p0.children) {
                    val addDataAll = dataSnapshot.getValue(
                        EventModel::class.java
                    )
                    addDataAll!!.key = dataSnapshot.key
                    list.add(addDataAll)
                    profilePerson = ProfilePersonAdapter(this@ProfilePerson, list)
                    recyclerView!!.adapter = profilePerson
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.e(
                    "TAG_ERROR", p0.message
                )
            }
        })


    }
}
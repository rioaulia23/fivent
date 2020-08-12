package com.example.fiventapp.Page

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fiventapp.Adapter.MyEventAdapter
import com.example.fiventapp.Helper.PrefHelper
import com.example.fiventapp.Model.EventModel
import com.example.fiventapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*

class My_Event : AppCompatActivity() {
    lateinit var preferences: PrefHelper
    lateinit var dbRef: DatabaseReference
    private lateinit var fAuth: FirebaseAuth
    private var myEventAdapter: MyEventAdapter? = null
    private var recyclerView: RecyclerView? = null
    private var list: MutableList<EventModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_event)
        preferences = PrefHelper(this)
        fAuth = FirebaseAuth.getInstance()

        val userid = fAuth.currentUser.uid


        var linearLayoutManager = LinearLayoutManager(this)
        recyclerView = findViewById(R.id.rcv_profile)
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
                    myEventAdapter = MyEventAdapter(this@My_Event, list)
                    recyclerView!!.adapter = myEventAdapter
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
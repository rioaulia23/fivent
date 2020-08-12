package com.example.fiventapp.Page

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fiventapp.Adapter.EventAdapter
import com.example.fiventapp.Helper.PrefHelper
import com.example.fiventapp.Model.EventModel
import com.example.fiventapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.detail_categories.*
import java.util.*

class Category : AppCompatActivity() {
    lateinit var dbRef: DatabaseReference
    lateinit var fAuth: FirebaseAuth
    lateinit var pref: PrefHelper
    private var list: MutableList<EventModel> = ArrayList()
    private var eventAdapter: EventAdapter? = null
    private var recyclerView: RecyclerView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_categories)
        val cate = intent.getStringExtra("kategori")
        val dona = intent.getStringExtra("kategori1")
        val turney = intent.getStringExtra("kategori2")
        val sport = intent.getStringExtra("kategori3")
        val semi = intent.getStringExtra("kategori4")
        val job = intent.getStringExtra("kategori5")

        var linearLayoutManager = LinearLayoutManager(this)
        recyclerView = findViewById(R.id.rc_cat)
        recyclerView!!.layoutManager = linearLayoutManager
        recyclerView!!.setHasFixedSize(true)

        bar_cat.visibility = View.VISIBLE
        ll_rc.visibility = View.GONE
        fAuth = FirebaseAuth.getInstance()


        dbRef = FirebaseDatabase.getInstance()
            .reference.child("event")
        dbRef.orderByChild("category").equalTo(cate).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                bar_cat.visibility = View.GONE
                ll_rc.visibility = View.VISIBLE
                list = ArrayList()
                for (dataSnapshot in p0.children) {
                    val addDataAll = dataSnapshot.getValue(
                        EventModel::class.java
                    )
                    addDataAll!!.key = dataSnapshot.key
                    list.add(addDataAll)
                    eventAdapter = EventAdapter(this@Category, list)
                    recyclerView!!.adapter = eventAdapter
                }
//                tvcok.visibility = View.VISIBLE
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.e(
                    "TAG_ERROR", p0.message
                )
            }
        })
        dbRef = FirebaseDatabase.getInstance()
            .reference.child("event")
        dbRef.orderByChild("category").equalTo(dona).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                bar_cat.visibility = View.GONE
                ll_rc.visibility = View.VISIBLE
                list = ArrayList()
                for (dataSnapshot in p0.children) {
                    val addDataAll = dataSnapshot.getValue(
                        EventModel::class.java
                    )
                    addDataAll!!.key = dataSnapshot.key
                    list.add(addDataAll)
                    eventAdapter = EventAdapter(this@Category, list)
                    recyclerView!!.adapter = eventAdapter
                }
//                tvcok.visibility = View.VISIBLE
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.e(
                    "TAG_ERROR", p0.message
                )
            }
        })
        dbRef = FirebaseDatabase.getInstance()
            .reference.child("event")
        dbRef.orderByChild("category").equalTo(semi).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                bar_cat.visibility = View.GONE
                ll_rc.visibility = View.VISIBLE
                list = ArrayList()
                for (dataSnapshot in p0.children) {
                    val addDataAll = dataSnapshot.getValue(
                        EventModel::class.java
                    )
                    addDataAll!!.key = dataSnapshot.key
                    list.add(addDataAll)
                    eventAdapter = EventAdapter(this@Category, list)
                    recyclerView!!.adapter = eventAdapter
                }
//                tvcok.visibility = View.VISIBLE
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.e(
                    "TAG_ERROR", p0.message
                )
            }
        })
        dbRef = FirebaseDatabase.getInstance()
            .reference.child("event")
        dbRef.orderByChild("category").equalTo(sport).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                bar_cat.visibility = View.GONE
                ll_rc.visibility = View.VISIBLE
                list = ArrayList()
//                if (p0.value == 0){
//                    tvcok.visibility = View.VISIBLE
//                    ll_rc.visibility = View.GONE
//                }
                for (dataSnapshot in p0.children) {
                    val addDataAll = dataSnapshot.getValue(
                        EventModel::class.java
                    )
                    addDataAll!!.key = dataSnapshot.key
                    list.add(addDataAll)
                    eventAdapter = EventAdapter(this@Category, list)
                    recyclerView!!.adapter = eventAdapter
                }
//                tvcok.visibility = View.VISIBLE
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.e(
                    "TAG_ERROR", p0.message
                )
            }
        })
        dbRef = FirebaseDatabase.getInstance()
            .reference.child("event")
        dbRef.orderByChild("category").equalTo(turney).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                list = ArrayList()
                for (dataSnapshot in p0.children) {
                    val addDataAll = dataSnapshot.getValue(
                        EventModel::class.java
                    )
                    addDataAll!!.key = dataSnapshot.key
                    list.add(addDataAll)
                    eventAdapter = EventAdapter(this@Category, list)
                    recyclerView!!.adapter = eventAdapter
                }
//                tvcok.visibility = View.VISIBLE
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.e(
                    "TAG_ERROR", p0.message
                )
            }
        })
        dbRef = FirebaseDatabase.getInstance()
            .reference.child("event")
        dbRef.orderByChild("category").equalTo(job).addValueEventListener(object :
            ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {

                bar_cat.visibility = View.GONE
                ll_rc.visibility = View.VISIBLE
                list = ArrayList()

                for (dataSnapshot in p0.children) {
                    val addDataAll = dataSnapshot.getValue(
                        EventModel::class.java
                    )
                    addDataAll!!.key = dataSnapshot.key
                    list.add(addDataAll)
                    eventAdapter = EventAdapter(this@Category, list)
                    recyclerView!!.adapter = eventAdapter
                }

            }

            override fun onCancelled(p0: DatabaseError) {
                Log.e(
                    "TAG_ERROR", p0.message
                )
            }
        })


    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(
            R.anim.enter_left,
            R.anim.exit_right
        )
    }

}
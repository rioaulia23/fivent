package com.example.fiventapp.Page

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fiventapp.Adapter.EventAdapter
import com.example.fiventapp.Model.EventModel
import com.example.fiventapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*

class Wishlist : AppCompatActivity() {
    private var list: MutableList<EventModel> = ArrayList()
    private var eventAdapter: EventAdapter? = null
    private var recyclerView: RecyclerView? = null
    private lateinit var fAuth: FirebaseAuth
    lateinit var dbRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.wishlist)
        fAuth = FirebaseAuth.getInstance()

        var linearLayoutManager = LinearLayoutManager(this)
        recyclerView = findViewById(R.id.wishlist)
        recyclerView!!.layoutManager = linearLayoutManager
        recyclerView!!.setHasFixedSize(true)

        dbRef = FirebaseDatabase.getInstance()
            .reference.child("favorite")
        dbRef.orderByChild("${fAuth.currentUser?.uid}").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                list = ArrayList()
                for (dataSnapshot in p0.children) {
                    FirebaseDatabase.getInstance().reference.child(dataSnapshot.value.toString())
                        .addListenerForSingleValueEvent(
                            object : ValueEventListener {
                                override fun onDataChange(p0: DataSnapshot) {
                                    val addDataAll = p0.getValue(

                                        EventModel::class.java
                                    )
                                    addDataAll!!.key = dataSnapshot.key
                                    list.add(addDataAll)
                                }

                                override fun onCancelled(p0: DatabaseError) {

                                }

                            }
                        )
//
                }

                eventAdapter = EventAdapter(this@Wishlist, list)
                recyclerView!!.adapter = eventAdapter
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.e(
                    "TAG_ERROR", p0.message
                )
            }
        })


    }


//    fun getData() {
//        FirebaseDatabase.getInstance().getReference("favorite/${FirebaseAuth.getInstance().uid}")
//            .addValueEventListener(object : ValueEventListener {
//                override fun onDataChange(p0: DataSnapshot) {
//                    val list = ArrayList<EventModel>()
//                    Log.e("masuk", "iya")
//                    p0.children.forEach {
//                        Log.e("value", it.value.toString())
//                        if (it.exists()) {
//                            FirebaseDatabase.getInstance().getReference("user/${it.value.toString()}/")
//                                .addListenerForSingleValueEvent(object : ValueEventListener {
//                                    override fun onDataChange(p1: DataSnapshot) {
//                                        val data = p1.child("event").getValue(EventModel::class.java)
//
//                                        data!!.fav = true
//                                        data!!.favkey = it.key
//                                        data.email = p1.child("email").value.toString()
//                                        list.add(data)
//                                        lv_wishlist.adapter = EventAdapter(this@Wishlist!!, R.layout.item_wishlist, list)
//                                        lv_wishlist.setOnItemClickListener { parent, view, position, id ->
//                                            val mIntent = Intent(this@Wishlist, DetailEvent::class.java)
//                                            mIntent.putExtra("event", list.get(position))
//                                            startActivity(mIntent)
//                                        }
//                                    }
//
//                                    override fun onCancelled(p1: DatabaseError) {
//
//                                    }
//
//                                })
//                        }
//                    }
//                    eventAdapter = EventAdapter(this@Wishlist, list)
//                }
//
//                override fun onCancelled(p0: DatabaseError) {
//                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//                }
//            })
//    }

}
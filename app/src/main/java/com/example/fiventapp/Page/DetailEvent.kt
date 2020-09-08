package com.example.fiventapp.Page

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.fiventapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.detail_event.*


class DetailEvent : AppCompatActivity() {
    lateinit var dbRef: DatabaseReference

    lateinit var fAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_event)
//        val event = EventModel()
        fAuth = FirebaseAuth.getInstance()

//        val event = intent.getSerializableExtra("event")as EventModel
        val name = intent.getStringExtra("user_name")
        val iduser = intent.getStringExtra("iduser")
        val id_event = intent.getStringExtra("id_event")
        val ava = intent.getStringExtra("ava")
        val event_name = intent.getStringExtra("event_name")
        val brosur = intent.getStringExtra("brosur")
        val category = intent.getStringExtra("category")
        val domisili = intent.getStringExtra("domisili")
        val participant = intent.getStringExtra("participant")
        val slot = intent.getStringExtra("slot")
        val place = intent.getStringExtra("place")
        val tersisa = intent.getStringExtra("tersisa")
        val open_register = intent.getStringExtra("open")
        val close_register = intent.getStringExtra("close")
        val fee = intent.getStringExtra("fee")



        title_event.text = event_name
        detail_dom.text = domisili
        detail_name.text = name
        detail_cat.text = category
        detail_place.text = place
        detail_par.text = participant
        detail_slot.text = slot
        detail_sisa.text = tersisa
        detail_open.text = open_register
        detail_close.text = close_register
        detail_fee.text = fee
        Glide.with(this@DetailEvent)
            .load(ava).error(R.drawable.user)
            .centerCrop().into(ava2)
        Picasso.with(this@DetailEvent)
            .load(brosur).error(R.drawable.user)
            .centerCrop().resize(200, 200)
            .into(brosur2)

        back.setOnClickListener {
            onBackPressed()
            overridePendingTransition(
                R.anim.enter_left,
                R.anim.exit_right
            )
        }
        brosur2.setOnClickListener {
            Toast.makeText(this@DetailEvent, "Brosur", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@DetailEvent, DetailFoto::class.java)
            intent.putExtra("brosur", brosur)
            startActivity(intent)
        }
        btn_chat.setOnClickListener {
            try {

//                Log.e("phone", intent.getStringExtra("phone"))
                var toNumber = intent.getStringExtra("phone")

                val intent = Intent(Intent.ACTION_VIEW)
                if (!toNumber.take(2).contains("62")) {
                    if (toNumber.take(1) == "0") {
                        toNumber = toNumber.replaceFirst("0", "62")
                    } else {
                        toNumber = "62" + toNumber
                    }
                }
                intent.data =
                    Uri.parse("http://api.whatsapp.com/send?phone=+$toNumber")
                startActivity(intent)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        if (fAuth.currentUser?.uid != iduser) {
            if (tersisa!!.toInt() == 0) {
                btn_chat.visibility = View.GONE
            }
            viewprofile.setOnClickListener {
                val intent = Intent(this@DetailEvent, ProfilePerson::class.java)
                intent.putExtra("iduser", iduser)
                startActivity(intent)
            }
        } else {
            btn_chat.visibility = View.GONE
//            btnfav2.visibility = View.GONE
            viewprofile.setOnClickListener {
                val intent = Intent(this@DetailEvent, Profile::class.java)
                startActivity(intent)
            }
        }


//        if (event.fav!!) {
//            btnfav2.setImageResource(R.drawable.fav2)
//        }
//        btnfav2.setOnClickListener {
//            if (event.fav!!) {
//                btnfav2.setImageResource(R.drawable.fav_red)
//                val db = FirebaseDatabase.getInstance()
//                    .getReference("favorite/${FirebaseAuth.getInstance().uid}/${event.favkey}")
//                db.removeValue()
//                event.fav = false
//            } else {
//                val uuid = UUID.randomUUID().toString()
//                val db = FirebaseDatabase.getInstance()
//                    .getReference("favorite/${FirebaseAuth.getInstance().uid}/$uuid")
//                db.setValue(event.key)
//                db.push()
//                btnfav2.setImageResource(R.drawable.fav2)
//                event.fav = true
//                event.favkey = uuid
//            }
//        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(
            R.anim.enter_left,
            R.anim.exit_right
        )
    }
}
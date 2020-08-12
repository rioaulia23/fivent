package com.example.fiventapp.Adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fiventapp.Helper.PrefHelper
import com.example.fiventapp.Model.EventModel
import com.example.fiventapp.Model.UserModel
import com.example.fiventapp.Page.DetailEvent
import com.example.fiventapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView

class EventAdapter : RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    lateinit var mCtx: Context
    lateinit var itemEvent: List<EventModel>
    lateinit var pref: PrefHelper
    lateinit var dbRef: DatabaseReference
    lateinit var fauth: FirebaseAuth

    constructor()
    constructor(mCtx: Context, list: List<EventModel>) {
        this.mCtx = mCtx
        this.itemEvent = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)
        val bukuViewHolder = EventViewHolder(view)
        return bukuViewHolder
    }

    override fun getItemCount(): Int {
        return itemEvent.size
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val eventModel: EventModel = itemEvent.get(position)
        fauth = FirebaseAuth.getInstance()
        FirebaseDatabase.getInstance()
            .getReference("user/")
            .child(eventModel.iduser!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(data2: DataSnapshot) {
                    val userData =
                        data2.getValue(UserModel::class.java)
                    eventModel.userModel = userData
                    Glide.with(mCtx).load(eventModel.userModel!!.ava)
                        .centerCrop()
                        .error(R.mipmap.ic_launcher)
                        .into(holder.image)
                    holder.namePanitia.text = eventModel.userModel!!.name

                }

                override fun onCancelled(holder: DatabaseError) {
                    Log.e("cok", holder.message)
                }
            })
        Glide.with(mCtx).load(eventModel.brosur)
            .centerCrop()
            .error(R.mipmap.ic_launcher)
            .into(holder.image_event)
        holder.category.text = eventModel.category
        holder.event_name.text = eventModel.event_name
        holder.close.text = eventModel.close_register
        holder.domisili.text = eventModel.domisili
        holder.tersisa.text = eventModel.tersisa
        holder.fee.text = eventModel.fee

        holder.ll.setOnClickListener {
            Toast.makeText(mCtx, "Detail", Toast.LENGTH_SHORT).show()
            val intent = Intent(mCtx, DetailEvent::class.java)

            intent.putExtra("user_name", eventModel.userModel!!.name)
            intent.putExtra("ava", eventModel.userModel!!.ava)
            intent.putExtra("iduser", eventModel.iduser)
            intent.putExtra("id_event", eventModel.id_event)
            intent.putExtra("event_name", eventModel.event_name)
            intent.putExtra("brosur", eventModel.brosur)
            intent.putExtra("category", eventModel.category)
            intent.putExtra("domisili", eventModel.domisili)
            intent.putExtra("participant", eventModel.participant)
            intent.putExtra("slot", eventModel.slot)
            intent.putExtra("tersisa", eventModel.tersisa)
            intent.putExtra("open", eventModel.open_register)
            intent.putExtra("close", eventModel.close_register)
            intent.putExtra("fee", eventModel.fee)
            intent.putExtra("place", eventModel.place)
            intent.putExtra("phone", eventModel.userModel!!.phone)
            intent.putExtra("event", itemEvent.get(position))
            mCtx.startActivity(intent)
        }
        if (fauth.currentUser!!.uid == eventModel.iduser) {
            holder.update.visibility = View.VISIBLE

        } else {
            if (eventModel.tersisa!!.toInt() == 0) {

                holder.update.visibility = View.GONE
            } else {
                holder.update.visibility = View.GONE

            }
        }

        holder.update.setOnClickListener {
            val builder = AlertDialog.Builder(mCtx)
            val view = LayoutInflater.from(mCtx).inflate(R.layout.update_slot, null)
            builder.setView(view)
            builder.setMessage("Update Slot")
            val tss = eventModel.tersisa
            val stk = eventModel.slot
            val etstok = view.findViewById<EditText>(R.id.et_stok)
            etstok.setText(tss)


            builder.setPositiveButton("No") { dialog, i ->
                dialog.dismiss()
            }
            builder.setNegativeButton("Yes") { dialog, i ->
                val sl = eventModel.slot.toString()
                val stok = etstok.text.toString()
                if (stok.isNotEmpty()) {
                    if (stok.toInt() > sl.toInt()) {
                        Toast.makeText(
                            mCtx,
                            "Melebihi Slot",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        dbRef = FirebaseDatabase.getInstance()
                            .getReference("event")
                        dbRef.child(eventModel.key!!).child("tersisa").setValue(stok)
                        dbRef.push()
                        Toast.makeText(
                            mCtx,
                            "Update Success",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(mCtx, "slot harus di isi", Toast.LENGTH_SHORT).show()
                }

            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ll: CardView
        var image: CircleImageView
        var event_name: TextView
        var namePanitia: TextView
        var image_event: ImageView
        var category: TextView
        var domisili: TextView
        var tersisa: TextView
        var close: TextView
        var fee: TextView
        //        var fav: FloatingActionButton
//        var ikut: Button
        var update: Button

        init {
            ll = itemView.findViewById(R.id.ll)
            image = itemView.findViewById(R.id.profilePanitia)
            event_name = itemView.findViewById(R.id.event_name)
            namePanitia = itemView.findViewById(R.id.account_name)
            image_event = itemView.findViewById(R.id.image_event)
            category = itemView.findViewById(R.id.category)
            close = itemView.findViewById(R.id.close)
            domisili = itemView.findViewById(R.id.domisili)
            tersisa = itemView.findViewById(R.id.slot)
//            fav = itemView.findViewById(R.id.btn_fav)
            fee = itemView.findViewById(R.id.fee)
//            ikut = itemView.findViewById(R.id.ikutTour)
            update = itemView.findViewById(R.id.updateSlot)
        }
    }
}
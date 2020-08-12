package com.example.fiventapp.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fiventapp.Helper.PrefHelper
import com.example.fiventapp.Model.EventModel
import com.example.fiventapp.Model.UserModel
import com.example.fiventapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView

class WishlistAdapter : RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder> {

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

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WishlistAdapter.WishlistViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)
        val bukuViewHolder = WishlistViewHolder(view)
        return bukuViewHolder
    }

    override fun getItemCount(): Int {
        return itemEvent.size
    }

    override fun onBindViewHolder(holder: WishlistViewHolder, position: Int) {
        val eventModel: EventModel = itemEvent.get(position)
        fauth = FirebaseAuth.getInstance()
        FirebaseDatabase.getInstance()
            .getReference("favorite/")
            .child(eventModel.favkey!!)
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

    }

    inner class WishlistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ll: CardView
        var image: CircleImageView
        var event_name: TextView
        var namePanitia: TextView
        //        var image_event: ImageView
        var category: TextView
        //        var domisili: TextView
        var tersisa: TextView
        var close: TextView
//        var fee: TextView
        //        var fav: FloatingActionButton
//        var ikut: Button
//        var update: Button

        init {
            ll = itemView.findViewById(R.id.ll_wish)
            image = itemView.findViewById(R.id.imgwishlist)
            event_name = itemView.findViewById(R.id.namaEvent)
            namePanitia = itemView.findViewById(R.id.user)
//            image_event = itemView.findViewById(R.id.image_event)
            category = itemView.findViewById(R.id.cat)
            close = itemView.findViewById(R.id.Close_register)

            tersisa = itemView.findViewById(R.id.slot)
//            fav = itemView.findViewById(R.id.btn_fav)

        }
    }
}
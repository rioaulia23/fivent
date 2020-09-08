package com.example.fiventapp.Page

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.example.fiventapp.Adapter.EventAdapter
import com.example.fiventapp.Helper.PrefHelper
import com.example.fiventapp.Model.EventModel
import com.example.fiventapp.R
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.synnapps.carouselview.CarouselView
import com.synnapps.carouselview.ViewListener
import kotlinx.android.synthetic.main.dashboard.*
import kotlinx.android.synthetic.main.header_drawer.*
import kotlinx.android.synthetic.main.home.*
import java.util.*

class Home : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var dbRef: DatabaseReference
    lateinit var fAuth: FirebaseAuth
    lateinit var pref: PrefHelper
    internal lateinit var carouselView: CarouselView
    internal lateinit var viewPager: ViewPager
    private var eventAdapter: EventAdapter? = null
    private var recyclerView: RecyclerView? = null
    private var list: MutableList<EventModel> = ArrayList()

    var sample = intArrayOf(
        R.drawable.pemerintah,
        R.drawable.pemerintah2,
        R.drawable.pemerintah3
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard)
        val eventModel: EventModel

        var ct = findViewById<View>(R.id.ct) as CollapsingToolbarLayout
        ct.title = ""

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.title = ""

        bar_home.visibility = View.VISIBLE
        content_dashboard.visibility = View.GONE

        fAuth = FirebaseAuth.getInstance()
        pref = PrefHelper(this)


        expo.setOnClickListener {
            val intent = Intent(this@Home, Category::class.java)
            intent.putExtra("kategori", "Expo")
            startActivity(intent)
            overridePendingTransition(
                R.anim.enter_right,
                R.anim.exit_left
            )
        }
        donation.setOnClickListener {
            val intent = Intent(this@Home, Category::class.java)
            intent.putExtra("kategori1", "Donation")
            startActivity(intent)
            overridePendingTransition(
                R.anim.enter_right,
                R.anim.exit_left
            )
        }
        turney.setOnClickListener {
            val intent = Intent(this@Home, Category::class.java)
            intent.putExtra("kategori2", "Tournament")
            startActivity(intent)
            overridePendingTransition(
                R.anim.enter_right,
                R.anim.exit_left
            )
        }
        sport.setOnClickListener {
            val intent = Intent(this@Home, Category::class.java)
            intent.putExtra("kategori3", "Sport")
            startActivity(intent)
            overridePendingTransition(
                R.anim.enter_right,
                R.anim.exit_left
            )
        }
        seminar.setOnClickListener {
            val intent = Intent(this@Home, Category::class.java)
            intent.putExtra("kategori4", "Seminar")
            startActivity(intent)
            overridePendingTransition(
                R.anim.enter_right,
                R.anim.exit_left
            )
        }
        job.setOnClickListener {
            val intent = Intent(this@Home, Category::class.java)
            intent.putExtra("kategori5", "Job Fair")
            startActivity(intent)
            overridePendingTransition(
                R.anim.enter_right,
                R.anim.exit_left
            )
        }

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        getDataDrawer()

        navView.setNavigationItemSelectedListener(this)



        carouselView = findViewById(R.id.carousel)
        carouselView.pageCount = sample.size
//        val adapter2 = ViewPagerAdapter(this)
        carouselView.setViewListener(imageListener)

//        viewPager = findViewById<View>(R.id.viewpager) as ViewPager
//        val adapter = ViewPagerAdapter(this)
//        viewPager.adapter = adapter


        var linearLayoutManager = LinearLayoutManager(this)
        recyclerView = findViewById(R.id.rc_event)
        recyclerView!!.layoutManager = linearLayoutManager
        recyclerView!!.setHasFixedSize(true)


        fAuth = FirebaseAuth.getInstance()
        getData()


    }


    private var imageListener: ViewListener = ViewListener { position ->
        val item = layoutInflater.inflate(R.layout.item_slider, null)
        val image = item.findViewById<View>(R.id.img_slider) as ImageView

        image.setImageResource(sample[position])

        item
    }
//    private fun setViewForPosition(position: Int): View {
//        val item = layoutInflater!!.inflate(R.layout.item_slider, null)
//        val image = item.findViewById<View>(R.id.img_slider) as ImageView
//        image.setImageResource(sample[position])
//        return item
//    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add, menu)
        return super.onCreateOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        startActivity(Intent(this, Add_Event::class.java))
        overridePendingTransition(
            R.anim.enter_bot,
            R.anim.exit_top
        )
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        nav_view.menu.getItem(0).isChecked = false
        nav_view.menu.getItem(1).isChecked = false
        nav_view.menu.getItem(2).isChecked = false

        getDataDrawer()
        getData()
    }


    fun getDataDrawer() {
        FirebaseDatabase.getInstance().getReference("user/${fAuth.currentUser?.uid}")
            .child("ava").addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(p0: DataSnapshot) {
                    Glide.with(this@Home).load(p0.value.toString())
                        .centerCrop()
                        .error(R.mipmap.ic_launcher)
                        .into(profileOnDashboard)
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
        FirebaseDatabase.getInstance().getReference("user/${fAuth.currentUser?.uid}")
            .child("name").addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(p0: DataSnapshot) {
                    nameOnDashboard.text = p0.value.toString()
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
        FirebaseDatabase.getInstance().getReference("user/${fAuth.currentUser?.uid}")
            .child("email").addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(p0: DataSnapshot) {
                    emailOnDashboard.text = p0.value.toString()
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })

    }


    private fun showDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Logout?")
        builder.setMessage("Logout from this account?")
        builder.setPositiveButton("YES") { dialog, which ->
            pref.setStatusPembeli(false)
            fAuth.signOut()
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
        builder.setNegativeButton("No") { dialog, which ->

        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_profile -> {
                startActivity(Intent(this@Home, Profile::class.java))
            }
//            R.id.nav_fav -> {
//                startActivity(Intent(this@Home, Wishlist::class.java))
//            }
            R.id.nav_event -> {
                startActivity(Intent(this@Home, My_Event::class.java))
            }
            R.id.nav_logout -> showDialog()

        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun getData() {
        dbRef = FirebaseDatabase.getInstance()
            .reference.child("event")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                list = ArrayList()
                for (dataSnapshot in p0.children) {
                    val addDataAll = dataSnapshot.getValue(
                        EventModel::class.java
                    )
                    addDataAll!!.key = dataSnapshot.key
                    list.add(addDataAll)
                    eventAdapter = EventAdapter(this@Home, list)
                    recyclerView!!.adapter = eventAdapter
                }
                bar_home.visibility = View.GONE
                content_dashboard.visibility = View.VISIBLE
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.e(
                    "TAG_ERROR", p0.message
                )
            }
        })
    }


}
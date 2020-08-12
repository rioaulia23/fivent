package com.example.fiventapp.Page

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.fiventapp.R
import kotlinx.android.synthetic.main.detail_foto.*

class DetailFoto : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_foto)
        back.setOnClickListener {
            onBackPressed()
        }
        val foto = intent.getStringExtra("fotoprofil")
        Glide.with(this@DetailFoto)
            .load(foto).error(R.drawable.belum)
            .into(fotofoto)
        val brosur = intent.getStringExtra("brosur")
        Glide.with(this@DetailFoto)
            .load(brosur).error(R.drawable.person)
            .into(fotofoto)
    }
}
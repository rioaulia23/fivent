package com.example.fiventapp.Page

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fiventapp.Helper.PrefHelper
import com.example.fiventapp.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.login.*

class Login : AppCompatActivity() {
    private val RC_SIGN_IN = 7
    private lateinit var mGoogleSignIn: GoogleSignInClient
    private lateinit var fAuth: FirebaseAuth
    private lateinit var helperPref: PrefHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)


        helperPref = PrefHelper(this)
        fAuth = FirebaseAuth.getInstance()
        if (!helperPref.cekStatusPembeli()!!) {

        } else {
            if (fAuth.currentUser != null) {
                startActivity(
                    Intent(
                        this, Home::class.java
                    )
                )
                finish()
            } else {
                Toast.makeText(this, "failed login", Toast.LENGTH_SHORT).show()
            }


        }


        btn_login.setOnClickListener {
            val email = et_email_login.text.toString()
            val pass = et_password_login.text.toString()

            if (email.isNotEmpty() || pass.isNotEmpty() ||
                !email.equals("") || !pass.equals("")
            ) {
                bar.visibility = View.VISIBLE
                btn_login.visibility = View.GONE
                tv_signup.visibility = View.GONE
//                et_email_login.focusable = View.NOT_FOCUSABLE
//                et_password_login.focusable = View.NOT_FOCUSABLE
                fAuth.signInWithEmailAndPassword(email, pass)
                    .addOnSuccessListener {
                        FirebaseDatabase.getInstance().getReference("user/${fAuth.uid}")
                            .child("id").addListenerForSingleValueEvent(object :
                                ValueEventListener {
                                override fun onCancelled(p0: DatabaseError) {
                                }

                                override fun onDataChange(p0: DataSnapshot) {
                                    val id = p0.value.toString()
                                    FirebaseDatabase.getInstance().getReference("user/${id}")
                                        .child("role").addListenerForSingleValueEvent(object :
                                            ValueEventListener {
                                            override fun onDataChange(p0: DataSnapshot) {
                                                val role = p0.value.toString()
                                                if (p0.value.toString() == "Premium") {
                                                    FirebaseDatabase.getInstance()
                                                        .getReference("user/${id}")
                                                        .child("name")
                                                        .addListenerForSingleValueEvent(object :
                                                            ValueEventListener {
                                                            override fun onCancelled(p0: DatabaseError) {
                                                            }

                                                            override fun onDataChange(p0: DataSnapshot) {
                                                                val user = fAuth.currentUser
                                                                helperPref.setStatusPenjual(true)
                                                                helperPref.setStatusPembeli(false)
                                                                updateUI(user)
                                                                finish()
                                                                Toast.makeText(
                                                                    applicationContext,
                                                                    "Welcome ${p0.value.toString()}!",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            }
                                                        })
                                                } else if (role == "Reguler") {
                                                    FirebaseDatabase.getInstance()
                                                        .getReference("user/${id}")
                                                        .child("name")
                                                        .addListenerForSingleValueEvent(object :
                                                            ValueEventListener {
                                                            override fun onCancelled(p0: DatabaseError) {

                                                            }

                                                            override fun onDataChange(p0: DataSnapshot) {
                                                                val user = fAuth.currentUser
                                                                helperPref.setStatusPenjual(false)
                                                                helperPref.setStatusPembeli(true)
                                                                updateUI(user)
                                                                finish()
                                                                Toast.makeText(
                                                                    applicationContext,
                                                                    "Welcome ${p0.value.toString()}!",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            }
                                                        })
                                                } else {
                                                    Toast.makeText(
                                                        applicationContext,
                                                        "Username atau Password salah!",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    bar.visibility = View.GONE
                                                    btn_login.visibility = View.VISIBLE
                                                }
                                            }

                                            override fun onCancelled(p0: DatabaseError) {

                                            }
                                        })

                                }
                            })
                    }.addOnFailureListener {
                        Toast.makeText(
                            this,
                            "Username or Password incorrect!",
                            Toast.LENGTH_SHORT
                        ).show()
                        bar.visibility = View.GONE
                        btn_login.visibility = View.VISIBLE
                        tv_signup.visibility = View.VISIBLE
                    }
            } else {

                Toast.makeText(
                    this,
                    "Inset your email and password to login",
                    Toast.LENGTH_SHORT
                ).show()


            }
        }
        tv_signup.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
            this.overridePendingTransition(
                R.anim.enter_right,
                R.anim.exit_left
            )

        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        Log.d("FAUTH_LOGIN", "firebaseAuth : ${account.id}")

        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

        fAuth.signInWithCredential(credential).addOnCompleteListener(this) {
            if (it.isSuccessful) {
//                Toast.makeText(this, "Login Successful Welcome ${fAuth.currentUser!!.displayName}",
//                    Toast.LENGTH_SHORT).show()
                val user = fAuth.currentUser
                updateUI(user)
            } else {
                Toast.makeText(
                    this, "Failed to Login",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            if (helperPref.cekStatusPenjual()!!) {
                startActivity(Intent(this, Home::class.java))
                finish()
            } else if (helperPref.cekStatusPembeli()!!) {
                startActivity(Intent(this, Home::class.java))
                finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                Log.e("AUTH_LOGIN", "LOGIN FAILED", e)
            }
        }
    }
}


package com.example.ott

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.bumptech.glide.Glide
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore
import java.text.Format
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private var nextPayTime: Long? = null
    private lateinit var db: FirebaseFirestore
    private var oldOTTName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        db = FirebaseFirestore.getInstance()

        val intent = intent;
        val subsName = intent.getStringExtra("OTT_NAME")
//        Log.d("DEBUG: ", subsName!!)

        if(subsName != "NULL"){
            setData(subsName!!)
        }

        val save: Button = findViewById(R.id.save)

        save.setOnClickListener {
            getOrSetData(null);
        }

        findViewById<Button>(R.id.renew).setOnClickListener {
            showDateDialog()
        }
    }


    private fun setData(subName: String){
        val mainImage: ImageView = findViewById(R.id.mainImage)
        var url: String = "https://i.postimg.cc/KcpJ4yyz/icons8-netflix-desktop-app-480.png";
        if(Common.map.containsKey(subName)){
            url = Common.map[subName].toString()
        }

        Glide.with(this)
            .load(url)
            .into(mainImage);



        db.collection("USER")
            .document("${Common.USER_NAME}${Common.USER_PHONE}")
            .collection("SUBS")
            .document(subName)
            .get()
            .addOnSuccessListener {
                Log.d("U_DEBUG: ", "${Common.USER_NAME}${Common.USER_PHONE} ___ $subName")

                val name = it.getString("name")!!
                val cost = it.get("cost")!!.toString().toInt()
                val fav = it.getString("favouriteShow")!!
                val renew = it.getLong("renewDate")!!

                Log.d("U_DEBUG: ", "$name, $cost, $fav, $renew")

                val sub = Subscription(name, cost.toString().toInt(), fav, renew)
                oldOTTName = sub.Name
                getOrSetData(sub)
            }
            .addOnFailureListener {
                Log.d("U_DEBUG: ", it.message.toString())
            }
    }

    private fun showDateDialog(){
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

        datePicker.show(supportFragmentManager, "TAG")

        datePicker.addOnPositiveButtonClickListener {
            val date: String = convertTime(it)!!
            nextPayTime = it
            findViewById<TextView>(R.id.show_date).text = date
        }
    }

    private fun getOrSetData(subscription: Subscription?){
        val name: TextInputLayout = findViewById(R.id.name)
        val cost: TextInputLayout = findViewById(R.id.cost)
        val renew: Button = findViewById(R.id.renew)
        val show: TextInputLayout = findViewById(R.id.favourite)
        if(subscription != null){
            name.editText!!.setText(subscription.Name)
            cost.editText!!.setText(subscription.Cost.toString())
            show.editText!!.setText(subscription.FavouriteShow)
            return
        }

        var name_ = name.editText!!.text.toString()
        var cost_ = cost.editText!!.text.toString()
        var fav_ = show.editText!!.text.toString()

        var isTrue:Boolean = true;
        if(name_ == ""){
            name.error = "Invalid"
            isTrue = false
        }
        if(cost_ == ""){
            cost.error = "Invalid"
            isTrue = false
        }
        if(fav_ == ""){
            show.error = "Invalid"
            isTrue = false
        }
        if(nextPayTime == null){
            isTrue = false
            Toast.makeText(this, "Enter next payment date!", Toast.LENGTH_SHORT).show()
        }
        if(!isTrue){
            return
        }
        name.error = null
        cost.error = null
        show.error = null

        if(subscription == null){
            var subs = Subscription(name_, Integer.parseInt(cost_), fav_)
            Toast.makeText(this, "Ready@ $subs", Toast.LENGTH_SHORT).show()
        }
        name_ = name.editText!!.text.toString()
        cost_ = cost.editText!!.text.toString()
        fav_ = show.editText!!.text.toString()
        val sub = Subscription(name_, Integer.parseInt(cost_), fav_, nextPayTime!!)
        updateToFirebase(sub)
    }

    private fun updateToFirebase(sub: Subscription){
        if(oldOTTName == null){
            // new subscription
            db.collection("USER").document("${Common.USER_NAME}${Common.USER_PHONE}")
                .collection("SUBS")
                .document(sub.Name)
                .set(sub)
            finish()
            Log.d("Firebase_debug", "NEW ADDED")
            return
        }

        db.collection("USER").document("${Common.USER_NAME}${Common.USER_PHONE}")
            .collection("SUBS")
            .document(oldOTTName!!)
            .delete()
            .addOnSuccessListener{

                // ADD new/updated subscription
                db.collection("USER").document("${Common.USER_NAME}${Common.USER_PHONE}")
                    .collection("SUBS")
                    .document(sub.Name)
                    .set(sub)
                Log.d("Firebase_debug", "NEW ADDED")
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }

    }

    private fun convertTime(time: Long): String? {
        val date = Date(time)
        val format: Format = SimpleDateFormat("dd-MM-yyyy")
        return format.format(date)
    }

    fun showToast(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}

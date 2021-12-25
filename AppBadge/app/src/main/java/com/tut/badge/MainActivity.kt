package com.tut.badge

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat

import androidx.core.content.ContextCompat
import android.app.PendingIntent
import android.graphics.drawable.Animatable
import android.view.MenuItem
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.databinding.DataBindingUtil
import com.tut.badge.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    val CHANNEL_ID = "msg_count"
    var msgs = 0
    lateinit var tv: TextView
    lateinit var notificationManager: NotificationManager

    lateinit var binding: ActivityMainBinding

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        Toast.makeText(this, item.toString(), Toast.LENGTH_SHORT).show()
        when(item.itemId){
            R.id.miNotification->{
                val im: ActionMenuItemView = binding.toolbar.findViewById(R.id.miNotification)
                /*  No Repeat / One time animation
                im.animate().rotationBy(90f)
                    .setDuration(300)
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .start()
                */
                /*  Value Animator
                val valueAnimator = ValueAnimator.ofFloat(-20f,20f)
                valueAnimator.addUpdateListener {
                    im.pivotX = 65f
                    im.pivotY = 35f
                    it.duration = 300
                    it.repeatMode = ValueAnimator.REVERSE
                    it.repeatCount = 5
                    it.interpolator = AccelerateDecelerateInterpolator()
                    im.rotation = it.animatedValue as Float
                }
                valueAnimator.addListener(object: Animator.AnimatorListener{
                    override fun onAnimationStart(animation: Animator?) {
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        val valueAnimator = ValueAnimator.ofFloat(-20f,0f)
                        valueAnimator.addUpdateListener {
//                    im.pivotY
                            it.interpolator = AccelerateDecelerateInterpolator()
                            im.rotation = it.animatedValue as Float
                        }
                        valueAnimator.start()
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                    }

                    override fun onAnimationRepeat(animation: Animator?) {
                    }

                })

                valueAnimator.start()
                */
//  region              /*  AnimatorSet with ObjectAnimator and ValueAnimator in XML
                (AnimatorInflater.loadAnimator(this, R.animator.bell_animator) as AnimatorSet)
                    .apply {
                        setTarget(im)
                        start()
                    }
//  endregion              */
                /*  Frame Animation- NOT WORKING
                im.setBackgroundResource(R.drawable.anim_bell)
                val imBack = im.foreground
                if(imBack is Animatable){
                    imBack.start()
                }
                */
                /*  View Animation from XML
                val anim = AnimationUtils.loadAnimation(this, R.anim.ring)
                anim.interpolator = AccelerateDecelerateInterpolator()
                im.startAnimation(anim)
                */
            }
            R.id.miAddPlot->{
                val im: ActionMenuItemView = binding.toolbar.findViewById(R.id.miAddPlot)
                im.animate().apply {
                    rotationBy(135f)
                    duration = 300
                    interpolator = AccelerateDecelerateInterpolator()
                }.start()
            }
        }
        Log.d(this.localClassName, (item.actionView != null).toString())
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.toolbar.logo = getDrawable(R.drawable.ic_launcher_foreground)
        setSupportActionBar(binding.toolbar)
        tv = binding.tvCount
        tv.text = msgs.toString()
        binding.btnSetBadge.setOnClickListener{
            msgs+=1
            tv.text = msgs.toString()
            updateMessageCount(msgs)
        }
        //  region  Taking the required permissions
        val permission_list = arrayOf(Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS)
        for (permission in permission_list){
            val grant = ContextCompat.checkSelfPermission(this, permission)
            if (grant != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permission_list, 1)
            }
        }
//  endregion
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        setNotificationChannel()
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter("android.provider.Telephony.SMS_RECEIVED")
        registerReceiver(SMSReceiver(), filter)
    }

    private fun updateMessageCount(num_messages: Int) {
        print("Unread messages = $num_messages")
        val notification = Notification.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle("New Message")
            .setContentText("You've unread message $num_messages")
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.ic_launcher)
            .setNumber(num_messages)
            .build()

        val notificationIntent = Intent(applicationContext, MainActivity::class.java)
        notificationIntent.flags = (Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val intent = PendingIntent.getActivity(
            applicationContext, 0,
            notificationIntent, 0
        )
        notification.contentIntent = intent
        notificationManager.notify(CHANNEL_ID, 5, notification)
    }

    private fun setNotificationChannel() {
        val mChannel = NotificationChannel(CHANNEL_ID, "Unread Message",
            NotificationManager.IMPORTANCE_LOW).apply {
                setShowBadge(true)
            }
        notificationManager.createNotificationChannel(mChannel)
    }
}
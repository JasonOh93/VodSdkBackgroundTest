package com.ppfriends.pp.vodsdkbackgroundtest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.work.WorkManager
import com.ppfriends.pp.vodsdkbackgroundtest.databinding.ActivityMain2Binding
import com.ppfriends.pp.vodsdkbackgroundtest.databinding.ActivityMainBinding
import ly.img.android.pesdk.backend.model.EditorSDKResult
import ly.img.android.pesdk.backend.operator.headless.DocumentRenderWorker

class MainActivity2 : AppCompatActivity() {

    var binding : ActivityMain2Binding? = null

    val TAG = MainActivity2::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding!!.root)

        if(binding != null){
            binding!!.text.text = intent.getStringExtra("aaa")
        }

    }

}
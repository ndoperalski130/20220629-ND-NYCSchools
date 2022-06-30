package com.example.a20220629_noahdoperalski_nycschools

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.a20220629_noahdoperalski_nycschools.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    //private var _binding : ActivityMainBinding? = null
//    private val binding : ActivityMainBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //_binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(R.layout.activity_main)
    }

    override fun onDestroy() {
        super.onDestroy()
        //_binding = null
    }
}
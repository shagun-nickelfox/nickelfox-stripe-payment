package com.example.paymentapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.paymentapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        return setContentView(binding.root)
    }
}
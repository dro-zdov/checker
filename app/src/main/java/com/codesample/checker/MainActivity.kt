package com.codesample.checker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.codesample.checker.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }

    companion object {
        const val FLAG_NAVIGATE_TO_TRACKED_LIST = "navigateToTrackedList"
    }
}
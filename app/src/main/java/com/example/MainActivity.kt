package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.data.local.AppDatabase
import com.example.data.repository.DocumentRepositoryImpl
import com.example.presentation.MainViewModel
import com.example.presentation.MainViewModelFactory
import com.example.presentation.WeftScreen
import com.example.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "weft-db"
        )
        .fallbackToDestructiveMigration()
        .build()
        val documentRepository = DocumentRepositoryImpl(db.documentDao())
        
        val prefs = com.example.data.local.UserPreferencesRepository(applicationContext)
        val retrofit = retrofit2.Retrofit.Builder()
            .baseUrl("https://openrouter.ai/api/v1/")
            .addConverterFactory(retrofit2.converter.moshi.MoshiConverterFactory.create())
            .build()
        val openRouterApi = retrofit.create(com.example.data.remote.OpenRouterApi::class.java)
        val aiRepository = com.example.domain.repository.AiRepository(openRouterApi, prefs)

        val factory = MainViewModelFactory(documentRepository, aiRepository, prefs)
        val viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        setContent {
            AppTheme {
                WeftScreen(viewModel = viewModel)
            }
        }
    }
}

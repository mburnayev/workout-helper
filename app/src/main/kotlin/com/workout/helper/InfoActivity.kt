package com.workout.helper

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

class InfoActivity : AppCompatActivity() {

    private lateinit var infoText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        infoText = findViewById(R.id.infoText)
        
        // Display user info
        displayUserInfo()
        
        // Example: Query database (uncomment when you have a table set up)
        // queryDatabase()
    }

    private fun displayUserInfo() {
        lifecycleScope.launch {
            try {
                val user = SupabaseClient.client.auth.currentUserOrNull()
                if (user != null) {
                    infoText.text = "You made it to info!\n\nLogged in as: ${user.email}"
                } else {
                    infoText.text = "You made it to info!"
                }
            } catch (e: Exception) {
                infoText.text = "You made it to info!"
            }
        }
    }

    // Example database query function
    // Uncomment and modify based on your Supabase table structure
    /*
    private fun queryDatabase() {
        lifecycleScope.launch {
            try {
                // Example: Query a "workouts" table
                val workouts = SupabaseClient.client
                    .from("workouts")
                    .select()
                    .decodeList<Workout>()
                
                // Process your workouts data here
                Toast.makeText(this@InfoActivity, "Found ${workouts.size} workouts", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@InfoActivity, "Database query failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
    
    // Example data class for Supabase table
    @Serializable
    data class Workout(
        val id: Int,
        val name: String,
        val exercises: String
    )
    */
}

package com.workout.helper

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

class InfoActivity : AppCompatActivity() {

    private lateinit var infoText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        infoText = findViewById(R.id.infoText)
        
        displayUserInfo()
        fetchDailyWorkout()
    }

    private fun displayUserInfo() {
        lifecycleScope.launch {
            try {
                val user = SupabaseClient.client.auth.currentUserOrNull()
                if (user != null) {
                    infoText.text = "Logged in as: ${user.email}\n\nLoading workout..."
                } else {
                    infoText.text = "Not logged in"
                }
            } catch (e: Exception) {
                infoText.text = "Error getting user info"
            }
        }
    }

    private fun fetchDailyWorkout() {
        lifecycleScope.launch {
            try {
                val client = SupabaseClient.client
                
                // 1. SELECT * FROM workout_exercises WHERE day = 'Push' AND "group" = 'Compound' ORDER BY RANDOM() LIMIT 2
                // Logic: Fetch all matching, shuffle, take 2
                val compoundList = client.from("workout_exercises").select {
                    filter {
                        eq("day", "Push")
                        eq("group", "Compound")
                    }
                }.decodeList<JsonObject>()
                val compound = compoundList.shuffled().take(2)

                // 2. SELECT * FROM workout_exercises WHERE day = 'Push' and "group" = 'Abs'
                val abs = client.from("workout_exercises").select {
                    filter {
                        eq("day", "Push")
                        eq("group", "Abs")
                    }
                }.decodeList<JsonObject>()

                // 3. SELECT * FROM workout_exercises WHERE day = 'Push' AND "group" = 'Isolated' ORDER BY RANDOM() LIMIT 1
                val isolatedList = client.from("workout_exercises").select {
                    filter {
                        eq("day", "Push")
                        eq("group", "Isolated")
                    }
                }.decodeList<JsonObject>()
                val isolated = isolatedList.shuffled().take(1)

                val combined = compound + abs + isolated
                
                // Display results
                val sb = StringBuilder()
                sb.append(infoText.text).append("\n\n--- Today's Workout ---\n")
                
                if (combined.isEmpty()) {
                    sb.append("No exercises found.")
                } else {
                    combined.forEach { json ->
                        // Try to get "name" or "exercise" field, otherwise dump json
                        // Accessing generic JsonObject
                        val nameElement = json["name"] ?: json["exercise"]
                        val name = nameElement.toString().trim('"')
                        val group = json["group"].toString().trim('"')
                        sb.append("• $name ($group)\n")
                    }
                }
                
                infoText.text = sb.toString()

            } catch (e: Exception) {
                e.printStackTrace()
                infoText.append("\nError fetching workout: ${e.message}")
            }
        }
    }
}

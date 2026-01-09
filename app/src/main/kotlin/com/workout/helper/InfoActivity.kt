package com.workout.helper

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
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

                // 1. SELECT * FROM workout_exercises WHERE day = 'Push' AND "group" = 'Compound'
                // ORDER BY RANDOM() LIMIT 2
                // Logic: Fetch all matching, shuffle, take 2
                val compoundList =
                        client.from("workout_exercises")
                                .select {
                                    filter {
                                        eq("day", "Push")
                                        eq("group", "Compound")
                                    }
                                }
                                .decodeList<JsonObject>()
                val compound = compoundList.shuffled().take(2)

                // 2. SELECT * FROM workout_exercises WHERE day = 'Push' and "group" = 'Abs'
                val abs =
                        client.from("workout_exercises")
                                .select {
                                    filter {
                                        eq("day", "Push")
                                        eq("group", "Abs")
                                    }
                                }
                                .decodeList<JsonObject>()

                // 3. SELECT * FROM workout_exercises WHERE day = 'Push' AND "group" = 'Isolated'
                // ORDER BY RANDOM() LIMIT 1
                val isolatedList =
                        client.from("workout_exercises")
                                .select {
                                    filter {
                                        eq("day", "Push")
                                        eq("group", "Isolated")
                                    }
                                }
                                .decodeList<JsonObject>()
                val isolated = isolatedList.shuffled().take(1)

                val combined = compound + abs + isolated

                // Display results
                val sb = StringBuilder()
                sb.append(infoText.text).append("\n\n--- Today's Workout ---\n")

                var iteration = 0
                combined.forEach { json ->
                    // Try to get "name" or "exercise" field, otherwise dump json
                    // Accessing generic JsonObject
                    val nameElement = json["exercise"]
                    val name = nameElement.toString().trim('"')
                    val weight = json["weight"]
                    val sets = if (iteration == 2) 3 else 4
                    val reps = if (iteration == 0 || iteration == 2) 8 else 10
                    sb.append("- $name, $sets x $reps @ $weight\n")
                    iteration += 1
                }

                infoText.text = sb.toString()
            } catch (e: Exception) {
                e.printStackTrace()
                infoText.append("\nError fetching workout: ${e.message}")
            }
        }
    }
}

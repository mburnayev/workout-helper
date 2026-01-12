package com.workout.helper

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import kotlinx.datetime.*
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

    //  querying based on: day of week, week of month (roughly)
    //  day -> week cycle: Legs/Pull/Push, Pull/Legs/Push, Legs/Push/Pull, Push/Legs/Pull
    //  week -> month cycle: d->w cycle x 13 weeks = 52 weeks, one year plan
    private fun fetchDailyWorkout() {
        lifecycleScope.launch {
            try {
                val client = SupabaseClient.client

                // dayOfWeek/isoDayNumber docs: MONDAY - SUNDAY/1-7
                val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
                val todayEnum = today.dayOfWeek
                val todayNum = todayEnum.isoDayNumber

                val anchorDate = LocalDate(2026, 1, 5) // a Monday
                val weeksSinceAnchor = today.minus(anchorDate).days / 7
                val weekCycle = weeksSinceAnchor % 4

                // Default fallback
                var muscleGroup = "Push"

                val sb = StringBuilder()
                sb.append(infoText.text).append("\n\n--- Today's Workout ---\n")

                if (todayNum == 2 || todayNum == 4) {
                    sb.append("Cardio day, go run 2-3 miles")
                } else if (todayNum == 6 || todayNum == 7) {
                    sb.append("Rest time :)")
                } else {
                    muscleGroup =
                            when (weekCycle) {
                                0 -> { // Week 1
                                    when (todayNum) {
                                        1 -> "Legs"
                                        3 -> "Pull"
                                        else -> "Push"
                                    }
                                }
                                1 -> { // Week 2
                                    when (todayNum) {
                                        1 -> "Pull"
                                        3 -> "Legs"
                                        else -> "Push"
                                    }
                                }
                                2 -> { // Week 3
                                    when (todayNum) {
                                        1 -> "Legs"
                                        3 -> "Push"
                                        else -> "Pull"
                                    }
                                }
                                else -> { // Week 4
                                    when (todayNum) {
                                        1 -> "Push"
                                        3 -> "Legs"
                                        else -> "Pull"
                                    }
                                }
                            }

                    // 1. SELECT * FROM workout_exercises WHERE day = 'Push' AND "group" =
                    // 'Compound' ORDER BY RANDOM() LIMIT 2
                    val compoundList =
                            client.from("workout_exercises")
                                    .select {
                                        filter {
                                            eq("day", "$muscleGroup")
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
                                            eq("day", "$muscleGroup")
                                            eq("group", "Abs")
                                        }
                                    }
                                    .decodeList<JsonObject>()

                    // 3. SELECT * FROM workout_exercises WHERE day = 'Push' AND "group" =
                    // 'Isolated' ORDER BY RANDOM() LIMIT 1
                    val isolatedList =
                            client.from("workout_exercises")
                                    .select {
                                        filter {
                                            eq("day", "$muscleGroup")
                                            eq("group", "Isolated")
                                        }
                                    }
                                    .decodeList<JsonObject>()
                    val isolated = isolatedList.shuffled().take(1)

                    val combined = compound + abs + isolated

                    // Display results
                    combined.forEach { json ->
                        val nameElement = json["exercise"]
                        val name = nameElement.toString().trim('"')
                        val weight = json["weight"]
                        val sets = json["sets"]
                        val reps = json["reps"]
                        sb.append("- $name, $sets x $reps @ $weight\n")
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

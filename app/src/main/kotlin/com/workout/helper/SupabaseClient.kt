package com.workout.helper

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClient {
    val client by lazy {
        createSupabaseClient(
            supabaseUrl = "https://qunlvbcbdxdbyhzqlzor.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InF1bmx2YmNiZHhkYnloenFsem9yIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTYzMjA5OTMsImV4cCI6MjA3MTg5Njk5M30.IJcWQU0xe_2DQhFSqHMjehQtI-pOVutj4mCiCC_egd4"
        ) {
            install(Auth)
            install(Postgrest)
        }
    }
}

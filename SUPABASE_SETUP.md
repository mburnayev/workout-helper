# Workout Helper - Supabase Setup Guide

## App Overview
Your Kotlin Android app has been successfully created and deployed to your device! The app includes:

✅ **Login Page** - Email/password authentication UI
✅ **Info Page** - Success screen saying "You made it to info!"
✅ **Supabase Integration** - Auth and Postgrest libraries configured
✅ **Material Design 3** - Modern, clean UI

## Next Steps: Configure Supabase

To make authentication work, you need to add your Supabase credentials:

### 1. Update Supabase Credentials

Edit the file: `app/src/main/kotlin/com/workout/helper/SupabaseClient.kt`

Replace these placeholders:
```kotlin
supabaseUrl = "https://your-project.supabase.co"  // Your Supabase project URL
supabaseKey = "your-anon-key"                      // Your Supabase anon key
```

You can find these in your Supabase project dashboard under **Settings > API**.

### 2. Supabase Auth Setup

1. Go to your Supabase dashboard
2. Navigate to **Authentication > Settings**
3. Enable **Email** provider
4. Configure email templates if desired

### 3. Database Queries (Optional)

The `InfoActivity.kt` file includes commented example code for querying your database:

```kotlin
private fun queryDatabase() {
    lifecycleScope.launch {
        try {
            val workouts = SupabaseClient.client
                .from("workouts")
                .select()
                .decodeList<Workout>()
            
            // Process your data...
        } catch (e: Exception) {
            // Handle error...
        }
    }
}
```

To use this:
1. Create a table in your Supabase database (e.g., "workouts")
2. Add `@Serializable` annotation to your data class
3. Import `kotlinx.serialization.Serializable`
4. Uncomment the code in `InfoActivity.kt`

## App Features

### LoginActivity
- Email and password input fields
- **Login** button - Signs in existing users
- **Sign Up** button - Creates new user accounts
- Error handling with Toast messages
- Auto-navigation to Info page on successful login

### InfoActivity
- Welcome message: "You made it to info!"
- Displays logged-in user's email
- Ready for database queries

## Libraries Included

✅ **Supabase Kotlin SDK**
  - `gotrue-kt` - Authentication
  - `postgrest-kt` - Database operations
  
✅ **AndroidX**
  - Material Design 3 components
  - Lifecycle & ViewModel support
  - ConstraintLayout for UI
  
✅ **Kotlin Coroutines** - For async operations

✅ **Ktor Client** - HTTP client for Supabase

## Rebuilding the App

After making changes, rebuild and redeploy:

```bash
./gradlew assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk
```

Or use the `-r` flag to reinstall:
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## File Structure

```
workout-helper/
├── app/
│   ├── src/main/
│   │   ├── kotlin/com/workout/helper/
│   │   │   ├── LoginActivity.kt       # Login screen
│   │   │   ├── InfoActivity.kt        # Success screen
│   │   │   └── SupabaseClient.kt      # Supabase configuration
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   ├── activity_login.xml  # Login UI
│   │   │   │   └── activity_info.xml   # Info UI
│   │   │   └── values/
│   │   │       ├── strings.xml
│   │   │       ├── colors.xml
│   │   │       └── themes.xml
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts              # Dependencies
├── build.gradle.kts
└── settings.gradle.kts
```

## Testing Without Supabase

The app will run and show the UI immediately, but authentication won't work until you:
1. Add your Supabase credentials
2. Set up email authentication in Supabase

The Info page can be accessed directly for UI testing by temporarily removing the authentication check.

## Troubleshooting

**Build errors?**
- Run: `./gradlew clean assembleDebug`

**Installation fails?**
- Check device connection: `adb devices`
- Uninstall old version first: `adb uninstall com.workout.helper`

**Supabase errors?**
- Verify your credentials are correct
- Check that email auth is enabled
- Ensure your device has internet connection

---


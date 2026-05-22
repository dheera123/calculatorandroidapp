# Calculator Android App

A simple Android calculator app built with Kotlin and Jetpack Compose.

## Features

- Basic arithmetic: add, subtract, multiply, divide
- Percentage support
- Clear and delete controls
- Responsive Compose UI

## Build & run

1. Install Android Studio and the Android SDK.
2. Open this folder as an Android project.
3. Use the Gradle wrapper or a local Gradle installation:

   ```bash
   ./gradlew assembleDebug
   ```

4. Install the debug APK on a device or emulator:

   ```bash
   ./gradlew installDebug
   ```

## Play Store deployment

- Set a unique `applicationId` in `app/build.gradle.kts`
- Increase `versionCode` and `versionName` before publishing
- Create a Play Store app bundle with:

  ```bash
  ./gradlew bundleRelease
  ```

- Sign the release build with a production keystore before upload.

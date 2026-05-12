# Habit Tracker Android

A modern Android application for tracking daily habits and building streaks. Built with Jetpack
Compose, Material 3 design, and Hilt dependency injection for a clean, scalable architecture.

## Features

- ✨ **Create & Manage Habits** – Add, edit, and delete habits with a smooth bottom-sheet interface
- 📊 **Habit Tracking** – Check in daily to build habit streaks
- 🔥 **Streak Counter** – Visual streak display with animated fire icon that scales on new check-ins
- 🔄 **Pull-to-Refresh** – Refresh habit list with Material 3 pull-to-refresh gesture
- 📱 **Responsive Design** – Optimized for various screen sizes
- 🌓 **Dark Mode Support** – Full dark mode with optimized contrast ratios
- 🔐 **Secure Storage** – Encrypted local storage using Tink and Android Security Crypto
- 🎯 **Pagination** – Efficient list loading with Paging 3
- ♿ **Accessibility** – Custom accessibility actions and semantic annotations

## Tech Stack

### Architecture & UI

- **Jetpack Compose** – Modern declarative UI framework
- **Material 3** – Latest Material Design components and theming
- **Jetpack Navigation Compose** – Type-safe navigation
- **Jetpack Hilt** – Dependency injection framework
- **Paging 3** – Efficient pagination with lazy loading

### Data & Storage

- **Room** – Local SQLite database with Kotlin coroutines support
- **Retrofit** – Type-safe HTTP client for API calls
- **Tink Android** – Encrypted storage for sensitive data
- **Android Security Crypto** – Secure SharedPreferences wrapper

### Testing

- **JUnit 4** – Unit testing framework
- **Mockk** – Mocking library for Kotlin
- **Turbine** – Flow testing utility
- **Espresso Core** – UI testing framework
- **Hilt Android Testing** – Dependency injection for tests
- **LeakCanary** – Memory leak detection (debug builds)

### Minimum Requirements

- **Minimum SDK:** 26 (Android 8.0)
- **Target SDK:** 36
- **Compile SDK:** 36
- **Java:** Version 17

## Project Structure

```
build.gradle.kts                        # Root Gradle build configuration
settings.gradle.kts                     # Module/plugin settings
gradle.properties                       # Shared Gradle flags and behavior
gradlew                                 # Unix Gradle wrapper
gradlew.bat                             # Windows Gradle wrapper
app/
├── build.gradle.kts                    # App module build configuration
├── proguard-rules.pro                  # ProGuard/R8 rules
└── src/
    ├── main/
    │   ├── AndroidManifest.xml         # App manifest and entry points
    │   ├── java/com/davidread/habittracker/
    │   │   ├── common/                 # Shared app infrastructure and utilities
    │   │   ├── list/                   # Habit list feature module
    │   │   ├── login/                  # Login feature module
    │   │   ├── settings/               # Settings feature module
    │   │   └── signup/                 # Sign-up feature module
    │   └── res/                        # Android resources
    │       ├── values/                 # Strings, colors, themes
    │       └── drawable/               # Images and vector assets
    ├── test/                           # Unit tests
    └── androidTest/                    # Instrumentation tests
gradle/
├── libs.versions.toml                  # Centralized dependency management
└── wrapper/                            # Gradle wrapper files
```

## Building & Running

### Prerequisites

- JDK 17 or higher
- Android Studio (latest stable recommended) compatible with AGP `9.2.0` (or IntelliJ IDEA with
  Android plugin)
- Android SDK Platform 36

### Build Variants

The project includes multiple build variants for different environments:

- **debug** – Local development build
- **localHostDebug** – Debug build connected to localhost API (clear text traffic enabled)
- **mockInAppDebug** – Debug build with mock in-app responses
- **release** – Production release build

### Build Instructions

1. **Clone the repository:**
   ```bash
   git clone https://github.com/david-read-iii/habit-tracker-android
   cd habit-tracker-android
   ```

2. **Build debug APK:**
   ```bash
   ./gradlew :app:assembleDebug
   ```

3. **Run on connected device or emulator:**
   ```bash
   ./gradlew :app:installDebug
   ```

4. **Build release APK:**
   ```bash
   ./gradlew :app:assembleRelease
   ```

### IDE Setup (Android Studio)

1. Open the project in Android Studio
2. Wait for Gradle sync to complete
3. Select a build variant (debug, localHostDebug, etc.) from **Build → Select Build Variant**
4. Run the app using **Run → Run 'app'** or **Shift+F10**

## Testing

### Run all tests:

```bash
./gradlew test
```

### Run unit tests only:

```bash
./gradlew :app:testDebugUnitTest
```

### Run instrumentation tests on a device:

```bash
./gradlew :app:connectedAndroidTest
```

### Run debug-specific instrumentation tests:

```bash
./gradlew :app:connectedDebugAndroidTest
```

## Architecture

The app follows **MVVM with MVI (Model-View-Intent)** pattern:

- **ViewModel** – Manages UI state and processes user intents
- **ViewState** – Immutable state representation
- **ViewIntent** – User actions/events
- **ViewEffect** – One-time side effects (navigation, snackbars)

### Data Flow

```
UI Event → Intent → ViewModel → State Update → Compose Recomposition → UI
```

## Theming

The app uses Material 3 dynamic theming with custom color schemes:

- **Light Theme** – Optimized for daylight viewing
- **Dark Theme** – Enhanced dark mode with improved contrast for text buttons
- **Accessible Colors** – WCAG AA compliant contrast ratios
- **Customizable** – Theme tokens in `common/ui/theme/`

## Accessibility

The app includes comprehensive accessibility support:

- **Content Descriptions** – All icons and images have descriptive labels
- **Custom Accessibility Actions** – Swipe actions exposed via accessibility APIs
- **Semantic Annotations** – Proper role and traversal order assignments
- **Announcements** – Screen reader announcements for key events

## Development Guidelines

### Code Style

- Follow Kotlin style conventions
- Use meaningful variable and function names
- Prefer immutable data structures
- Keep composables focused and small

### Composable Best Practices

- Use `remember` for state management
- Leverage composition locals for theme access
- Provide default parameters for preview compatibility
- Add `@Preview` composables for UI verification

### Testing Best Practices

- Write unit tests for ViewModels and business logic
- Test state transitions and side effects using Turbine
- Use Mockk for dependency mocking
- Add instrumentation tests for critical user flows

## Build Variants Details

### Debug

Standard development build with debugging enabled.

### LocalHostDebug

Connects to a local API server. Clear text traffic is enabled for `localhost`. Use this when
developing against a local backend.

### MockInAppDebug

Uses mock responses for API calls without needing a backend server. Useful for UI development and
testing.

### Release

Production build with ProGuard/R8 optimizations. Debugging is disabled.

## Performance Considerations

- **Paging** – Large habit lists are paginated to reduce memory footprint
- **Lazy Loading** – Compose only renders visible items in lazy lists
- **LeakCanary** – Memory leaks are detected in debug builds
- **R8/ProGuard** – Code shrinking and obfuscation in release builds

## Security

- **Encrypted Storage** – Sensitive data is encrypted using Tink and `EncryptedSharedPreferences`
- **Network Security** – HTTPS enforced in production
- **Clear Text Traffic** – Disabled by default, only enabled in localhost debug variant
- **Dependency Check** – Regular updates of third-party libraries

## Troubleshooting

### Build Issues

**Gradle sync fails:**

- Clean and rebuild project artifacts: `./gradlew clean :app:assembleDebug`
- Invalidate caches in Android Studio: **File → Invalidate Caches**

**Compose compiler errors:**

- Ensure Compose Compiler plugin is applied: `kotlin-plugin-compose`
- Clear build cache: `./gradlew clean`

### Runtime Issues

**No habits showing:**

- Check database initialization
- Verify API connection
- Check logcat for errors

**Accessibility features not working:**

- Enable TalkBack or system accessibility service
- Verify semantic annotations in UI

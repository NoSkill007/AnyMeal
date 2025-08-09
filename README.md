# AnyMeal - University Recipe Management App

A modern Android application built with Kotlin and Jetpack Compose for managing recipes, meal planning, and shopping lists. This university project demonstrates clean architecture principles and modern Android development practices.

## Features

### 🍽️ Core Functionality
- **Recipe Discovery**: Browse and search through a comprehensive recipe database
- **Meal Planning**: Plan weekly meals with an intuitive calendar interface
- **Shopping Lists**: Generate and manage shopping lists based on planned meals
- **Favorites**: Save and organize your favorite recipes
- **Recipe Details**: View detailed cooking instructions, ingredients, and nutritional information

### 👤 User Experience
- **User Authentication**: Secure login and registration system
- **Profile Management**: Customize user profiles and preferences
- **Achievements System**: Track cooking milestones and unlock achievements
- **Dark/Light Theme**: Toggle between dark and light themes
- **Search & Filter**: Advanced recipe search with multiple filters

### 📱 Modern UI
- Material Design 3 components
- Smooth navigation with Jetpack Navigation Compose
- Responsive layouts optimized for different screen sizes
- Interactive components with smooth animations

## Technologies Used

### 🛠️ Core Technologies
- **Kotlin** - Primary programming language
- **Jetpack Compose** - Modern UI toolkit for native Android
- **Material Design 3** - Google's latest design system

### 🏗️ Architecture & Libraries
- **MVVM Architecture** - Clean separation of concerns
- **Hilt** - Dependency injection framework
- **Navigation Compose** - Type-safe navigation
- **Retrofit** - HTTP client for API communication
- **OkHttp** - Network interceptor and logging
- **Coil** - Image loading library for Compose
- **Gson** - JSON serialization/deserialization

### 🔧 Development Tools
- **Android Studio** - IDE
- **Gradle** (Kotlin DSL) - Build system
- **Kapt** - Kotlin annotation processing
- **JUnit** - Unit testing framework
- **Espresso** - UI testing framework

## Architecture & Folder Structure

The app follows **MVVM (Model-View-ViewModel)** architecture with clean code principles:

```
app/src/main/java/com/noskill/anymeal/
├── data/                    # Data layer (repositories, data sources)
├── dto/                     # Data Transfer Objects
├── navigation/              # Navigation setup and routes
│   ├── AppNavGraph.kt      # Main navigation graph
│   └── AppNavigation.kt    # Screen definitions and routes
├── ui/                     # UI layer
│   ├── components/         # Reusable UI components
│   ├── models/            # UI models and state
│   ├── screens/           # App screens/composables
│   │   ├── AuthScreen.kt
│   │   ├── HomeScreen.kt
│   │   ├── PlanScreen.kt
│   │   ├── RecipeDetailScreen.kt
│   │   ├── RecipeSearchScreen.kt
│   │   ├── ShoppingListScreen.kt
│   │   ├── ProfileScreen.kt
│   │   └── ...
│   └── theme/             # App theming and styling
├── util/                  # Utility classes and extensions
├── viewmodel/            # ViewModels for business logic
└── MainActivity.kt       # Main activity entry point
```

### Key Architectural Components
- **Data Layer**: Handles API calls and data management
- **ViewModel Layer**: Manages UI state and business logic
- **UI Layer**: Compose screens and reusable components
- **Navigation**: Type-safe navigation between screens
- **Dependency Injection**: Hilt for managing dependencies

## Installation & Running

### Prerequisites
- **Android Studio** Hedgehog (2023.1.1) or later
- **JDK 8** or higher
- **Android SDK** (minimum API 26, target API 35)
- **Git** for version control

### Setup Instructions

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd AnyMeal
   ```

2. **Open in Android Studio**
    - Launch Android Studio
    - Select "Open an existing project"
    - Navigate to the cloned directory and select it

3. **Sync Project**
    - Android Studio will automatically prompt to sync Gradle
    - Click "Sync Now" to download dependencies

4. **Build the project**
   ```bash
   ./gradlew build
   ```

5. **Run on Emulator or Device**
    - Connect an Android device or start an emulator
    - Click the "Run" button in Android Studio
    - Or use command line: `./gradlew installDebug`

### Minimum Requirements
- **Android 8.0** (API level 26) or higher
- **2GB RAM** recommended
- **Internet connection** for recipe data

## Environment Variables / Configurations

### Network Configuration
The app requires internet connectivity for API calls. Network security is configured in:
- `app/src/main/res/xml/network_security_config.xml`

### API Configuration
If using external APIs, configure endpoints in:
- Create `local.properties` file (if not exists)
- Add API keys or base URLs as needed:
  ```properties
  # Example configuration
  API_BASE_URL="https://your-api-endpoint.com/"
  API_KEY="your-api-key-here"
  ```

### Build Variants
- **Debug**: Development build with logging enabled
- **Release**: Production build with ProGuard optimization

### Permissions
The app requires the following permissions:
- `INTERNET` - For API communication and image loading

## Screenshots

### Main Screens
![Splash Screen](screenshots/splash_screen.png)
*App splash screen with branding*

![Home Screen](screenshots/home_screen.png)
*Main dashboard with recipe recommendations*

![Recipe Search](screenshots/recipe_search.png)
*Search and filter recipes by various criteria*

![Recipe Detail](screenshots/recipe_detail.png)
*Detailed recipe view with ingredients and instructions*

### Planning & Organization
![Meal Planning](screenshots/meal_planning.png)
*Weekly meal planning interface*

![Shopping List](screenshots/shopping_list.png)
*Generated shopping list from planned meals*

![Favorites](screenshots/favorites.png)
*User's saved favorite recipes*

### User Features
![Profile Screen](screenshots/profile_screen.png)
*User profile and settings*

![Achievements](screenshots/achievements.png)
*Cooking achievements and progress tracking*

![Dark Theme](screenshots/dark_theme.png)
*Dark theme variant of the app*

---

## Development Notes

### Code Style
- Follow Kotlin coding conventions
- Use Compose best practices
- Implement proper error handling
- Write meaningful comments in Spanish (as per project requirements)

### Testing
- Unit tests in `src/test/`
- UI tests in `src/androidTest/`
- Run tests: `./gradlew test`

### Contributing
This is a university project. For educational purposes, ensure to:
1. Follow clean architecture principles
2. Write comprehensive documentation
3. Implement proper error handling
4. Use modern Android development practices

---

**Project Status**: Active Development  
**Version**: 1.0  
**Target SDK**: 35  
**Minimum SDK**: 26

*Built with ❤️ using Kotlin and Jetpack Compose*

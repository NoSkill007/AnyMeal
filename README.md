# AnyMeal

A modern Android application for meal planning and recipe management, designed to help users discover, organize, and plan their meals efficiently. Built with Kotlin and Jetpack Compose for a seamless and intuitive user experience.

## Main Features

- **Recipe Discovery & Search** – Browse and search through a comprehensive recipe database
- **Meal Planning** – Create and manage weekly meal plans with ease
- **Favorites Management** – Save and organize your favorite recipes
- **Shopping List Generator** – Automatically generate shopping lists from meal plans
- **User Profile Management** – Personalized user accounts with profile customization
- **Achievement System** – Unlock achievements and track cooking milestones
- **Dark/Light Theme** – Adaptive UI theme based on user preference
- **User Authentication** – Secure login and registration system
- **Recipe Details** – Detailed recipe information with ingredients and instructions

## Technologies Used

- **Kotlin** – Primary programming language
- **Jetpack Compose** – Modern UI toolkit for native Android development
- **Material Design 3** – Google's latest design system
- **Dagger Hilt** – Dependency injection framework
- **Retrofit** – HTTP client for API communication
- **OkHttp** – Networking library with logging interceptor
- **Navigation Compose** – Type-safe navigation for Compose
- **Coil** – Image loading library for Compose
- **Gson** – JSON serialization/deserialization
- **ViewModel & LiveData** – Architecture components for UI state management

## Architecture & Folder Structure

The project follows **MVVM (Model-View-ViewModel)** architecture pattern with clean separation of concerns:

```
app/src/main/java/com/noskill/anymeal/
├── data/              # Data layer (repositories, data sources)
├── dto/               # Data Transfer Objects
├── navigation/        # Navigation components and routing
├── ui/
│   ├── components/    # Reusable UI components
│   ├── models/        # UI state models
│   ├── screens/       # Compose screens
│   └── theme/         # App theming and styling
├── util/              # Utility classes and extensions
├── viewmodel/         # ViewModels for business logic
└── MainActivity.kt    # Main entry point
```

**Key Architecture Components:**
- **ViewModels** – Handle business logic and UI state
- **Repositories** – Abstract data access layer
- **Compose Screens** – UI layer with declarative components
- **Navigation Graph** – Centralized navigation management
- **Dependency Injection** – Hilt for managing dependencies

## Screenshots

### Home & Recipe Discovery
![Home Screen](screenshots/home_screen.png)
![Recipe Search](screenshots/recipe_search.png)

### Meal Planning & Lists
![Meal Planner](screenshots/meal_planner.png)
![Shopping List](screenshots/shopping_list.png)

### User Profile & Settings
![Profile Screen](screenshots/profile_screen.png)
![Dark Theme](screenshots/dark_theme.png)

---

*Developed as part of a university project showcasing modern Android development practices.*

## Theme Mode Implementation Summary

### What Was Added:

#### 1. **ThemeManager.java** (New Utility Class)
- Location: `app/src/main/java/com/example/moviesphere/ThemeManager.java`
- Handles theme management across the application
- Methods:
  - `applyTheme()` - Applies the saved theme to an activity
  - `applyDarkTheme()` - Sets dark theme colors
  - `applyLightTheme()` - Sets light theme colors
  - `saveTheme()` - Saves theme preference to SharedPreferences
  - `getCurrentTheme()` - Retrieves the current theme setting
- Constants: `THEME_DARK` and `THEME_LIGHT`

#### 2. **ProfileActivity.java** (Modified)
- Added new imports for Spinner and ArrayAdapter
- Added new variables: `themeLayout`, `themeSpinner`
- Updated `onCreate()` to initialize theme spinner
- Added `setupThemeSpinner()` method that:
  - Creates an ArrayAdapter with "Dark Mode" and "Light Mode" options
  - Sets the current theme selection based on saved preference
  - Listens for spinner item selection changes
  - Saves the selected theme to SharedPreferences
  - Applies the theme immediately when changed
  - Shows a toast notification confirming the theme change

#### 3. **activity_profile.xml** (Modified Layout)
- Added a new Theme Card (CardView) with:
  - A star icon to match the app's design
  - "Theme" label text
  - A Spinner widget with ID `@+id/themeSpinner`
  - Positioned between "My Favourites" card and Logout button
  - Styled to match the existing dark theme (background: #1B263B)
  - Proper spacing and elevation

### How It Works:

1. **User navigates to Profile page** → The ProfileActivity checks the saved theme preference from SharedPreferences
2. **User clicks the Theme Card** → A Spinner appears with two options: "Dark Mode" and "Light Mode"
3. **User selects an option** → The theme is immediately applied and saved
4. **Toast notification** → Confirms the theme change
5. **Persistence** → The theme preference is saved in SharedPreferences and will be restored when the app restarts

### Theme Details:

**Dark Mode:**
- Navigation bar color: #1B263B
- Default theme of the app

**Light Mode:**
- Navigation bar color: #F5F5F5 (Light gray)
- Can be extended to change other UI elements as needed

### Future Enhancement Notes:

To make the theme apply globally across all activities:
1. Call `ThemeManager.applyTheme()` in the `onCreate()` method of every activity
2. Extend theme application to include background colors, text colors, and drawable tints
3. Create theme resource files (colors.xml) for better organization

### Files Modified:
- ✅ `app/src/main/java/com/example/moviesphere/ProfileActivity.java`
- ✅ `app/src/main/res/layout/activity_profile.xml`

### Files Created:
- ✅ `app/src/main/java/com/example/moviesphere/ThemeManager.java`


# MovieSphere - Color & Styling Quick Reference

## 🎨 Color Palette

### Core Colors
| Color | Hex Code | Usage |
|-------|----------|-------|
| Navy (Primary) | #0D1B2A | Background, deep elements |
| Dark Blue (Secondary) | #1B263B | Cards, headers, containers |
| Gold (Accent) | #FFD700 | Buttons, highlights, interactive |
| Light Gray (Text Primary) | #E0E1DD | Main text |
| Medium Gray (Text) | #B0BAC9 | Secondary text |
| Blue Gray (Hint) | #415A77 | Hints, disabled text |
| Darker Gray | #778DA9 | Less important text |

### Gradients
```xml
<!-- Header Gradient -->
<gradient android:type="linear" android:angle="135"
    android:startColor="#1B263B"
    android:centerColor="#2D3E50"
    android:endColor="#0D1B2A" />

<!-- Poster Overlay -->
<gradient android:type="linear" android:angle="90"
    android:startColor="#00000000"
    android:centerColor="#44000000"
    android:endColor="#88000000" />
```

---

## 📐 Dimensions

### Spacing (dp)
- Extra Small: 4dp
- Small: 8dp
- Medium: 12dp
- Large: 15dp
- Extra Large: 20-25dp
- Huge: 30dp

### Border Radius (dp)
- Buttons: 12dp
- Cards: 12-16dp
- Search Bar: 12dp
- Icons: 25dp (50% = circle)

### Elevation (dp)
- Standard Card: 4dp
- Header: 8dp
- Floating: 8-16dp

---

## 🎯 Button Styles

### Gold Button (Filled)
```xml
<Button
    android:background="@drawable/rounded_button_gold"
    android:textColor="#0D1B2A"
    android:textStyle="bold" />
```

### Outline Button
```xml
<Button
    android:background="@drawable/rounded_button_outline"
    android:textColor="#FFD700" />
```

### Default Rounded Button
```xml
android:cornerRadius="12dp"
android:backgroundTint="#1B263B"
android:textColor="#E0E1DD"
```

---

## 📝 Typography

### Text Sizes
| Purpose | Size | Style |
|---------|------|-------|
| Logo | 40sp | Bold |
| Headings | 24-28sp | Bold |
| Section Titles | 18-20sp | Bold |
| Regular Text | 16sp | Normal |
| Secondary | 14sp | Normal |
| Small/Hints | 12sp | Normal |

### Font Colors
- **Primary Text**: #E0E1DD (40 - 14sp)
- **Secondary Text**: #B0BAC9 (14 - 12sp)
- **Hint Text**: #415A77
- **Gold Accent**: #FFD700 (section titles, labels)

---

## 🎬 Component Specifications

### Movie Card Layout
```
┌─────────────────────────────┐
│ ┌─────┐                     │
│ │     │  Title              │
│ │ 100 │  2023 | Movie       │
│ │ x   │                     │
│ │ 150 │  [❤️ 📤]           │
│ └─────┘                     │
└─────────────────────────────┘
Elevation: 8dp, Radius: 12dp
Margin: 4-8dp
```

### Info Card (Movie Details)
```
┌──────────┐
│    📅    │
│   2020   │
└──────────┘
Elevation: 4dp, Radius: 8dp
Padding: 12dp
Gravity: Center
```

### Search Bar
```
🔍 Search movies...
Background: #1B263B with radius 12dp
Height: 50dp
Padding: 12dp
Icon: 🔍 gray
```

---

## 🎪 Component Icons & Emojis

### Emojis Used
| Icon | Usage |
|------|-------|
| 🎬 | Action genre |
| 😂 | Comedy genre |
| 🎭 | Drama genre |
| 😱 | Horror genre |
| 🚀 | Sci-Fi genre |
| ⚡ | Thriller genre |
| ✨ | Trending/Special |
| 💖 | Favorite/Love |
| 📺 | History/Watch |
| 👤 | Profile/User |
| ⭐ | Rating |
| 📅 | Year/Date |
| ⏱️ | Runtime |
| 🔐 | Password |
| 📧 | Email |
| 📤 | Share |
| 🤍 | Unfavorited |
| ❤️ | Favorited |
| 🏠 | Home |
| ⬅️ | Back |
| → | Next/Forward |

---

## 📱 Responsive Layout

### Movie Card Heights
- Poster: 100x150dp
- Card Total: ~160-170dp
- Padding: 4-8dp around each

### Bottom Navigation
- Height: 50-60dp with padding
- Button Width: Equal weight distribution
- Text Size: 12sp
- Icon + Text format

### Header
- Height: Wrap content + padding
- Padding: 15dp
- Elevation: 8dp
- Logo Size: 28-40sp

---

## 🔄 State Changes

### Button States
- **Normal**: Standard color
- **Pressed**: Ripple effect (material)
- **Focused**: Ripple effect visible

### Navigation States
- **Active**: Gold (#FFD700)
- **Inactive**: Gray (#778DA9)

### Favorite Button
- **Unfavorited**: "🤍 Favourite"
- **Favorited**: "❤️ Favourited"

---

## 🛠️ How to Apply Styles

### CardView
```xml
<androidx.cardview.widget.CardView
    app:cardCornerRadius="12dp"
    app:cardElevation="8dp">
```

### Gradient Background
```xml
android:background="@drawable/gradient_header"
```

### Rounded Button
```xml
android:cornerRadius="12dp"
```

### Rounded Rectangle
```xml
android:background="@drawable/rounded_search_background"
```

---

## ⚡ Quick Copy-Paste Templates

### Rounded Button
```xml
<Button
    android:layout_width="match_parent"
    android:layout_height="55dp"
    android:text="Button Text"
    android:textSize="18sp"
    android:textStyle="bold"
    android:textColor="#0D1B2A"
    android:background="@drawable/rounded_button_gold"
    android:cornerRadius="12dp"/>
```

### Card Container
```xml
<androidx.cardview.widget.CardView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:cardCornerRadius="12dp"
    app:cardElevation="4dp">
    <!-- Content here -->
</androidx.cardview.widget.CardView>
```

### Section Title
```xml
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="✨ Section Title"
    android:textSize="18sp"
    android:textStyle="bold"
    android:textColor="#FFD700"
    android:layout_marginBottom="15dp"/>
```

---

## 📋 Checklist for New Features

When adding new screens, ensure:
- [ ] Gradient header with proper colors
- [ ] Back button with ⬅️ emoji
- [ ] CardView containers for content
- [ ] 12dp corner radius on cards
- [ ] Proper spacing (15-25dp padding)
- [ ] Gold accent for highlights
- [ ] Section titles with emoji prefix
- [ ] Rounded buttons with proper colors
- [ ] Ripple effects on interactive elements

---

**Last Updated:** March 12, 2026
**Version:** 1.0 - Complete Design System


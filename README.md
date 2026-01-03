# 📱 Expense Tracker App

A native **Android application built with Java** designed to help users track their daily spending, categorize expenses, and manage their personal finances efficiently.

---

## ✨ Features

- **Add Expenses**  
  Quickly input amounts and descriptions for new transactions.

- **Categorization**  
  Organize spending into predefined categories (Food, Transport, Utilities, etc.) using efficient Kotlin Collections.

- **User-Friendly Interface**  
  Clean XML layouts following Material Design guidelines.

- **Kotlin First**  
  Utilizes modern Kotlin features like immutable and mutable maps for data handling.

---

## 🛠 Tech Stack

- **Language:** Java
- **Platform:** Android  
- **Minimum SDK:** 24  

### Components Used
- Android Activities & Fragments  
- ConstraintLayout  
- Standard Collections (Maps, Lists)

---

## 🚀 Roadmap & Future Additions

We are actively working on the following features to improve the app:

- 💾 **Data Persistence**  
  Integration of Room Database to permanently store expenses on the device.

- 📊 **Analytics Dashboard**  
  Visual charts (Pie / Bar) to show spending trends using MPAndroidChart.

- 📷 **Receipt Scanning**  
  OCR implementation using ML Kit to scan receipts and auto-fill expense details.

- 🌍 **Multi-Currency Support**  
  Track expenses in multiple currencies for travel and international usage.

- 📍 **Geolocation**  
  Auto-tag transaction locations using Google Maps SDK.

- 🌑 **Dark Mode**  
  System-wide dark theme support.

---

## 💻 Code Snippet

Example of category-to-icon mapping using Kotlin’s `mapOf`:

```kotlin
// Example from AddExpenseActivity
val categoryIcons = mapOf(
    "Food" to R.drawable.ic_food,
    "Transport" to R.drawable.ic_bus,
    "Utilities" to R.drawable.ic_water
)

```
---
## ⚙️ Installation & Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/m-ather-47/expensetracker.git
   ```
3. **Open in Android Studio**

- Launch Android Studio
- Click File → Open
- Select the cloned project directory

4. **Sync Gradle**
- Android Studio will prompt to sync Gradle
- Allow it to download all required dependencies

5. **Run the Application**
- Connect a physical Android device or start an emulator
- Click the green Run ▶ button or press Shift + F10

---
## 🤝 Contributing
Contributions are welcome and appreciated!

1. Fork the repository

2. Create a new feature branch

```bash
git checkout -b feature/AmazingFeature
```
3. Commit your changes
```bash
git commit -m "Add AmazingFeature"
```
4. Push the branch
```bash
git push origin feature/AmazingFeature
```
5. Open a Pull Request on GitHub
---


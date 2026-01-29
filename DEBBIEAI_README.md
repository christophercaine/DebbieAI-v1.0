# DebbieAI - Unified Android Application

**Version:** 1.0.0  
**Date:** January 29, 2026  
**Package:** `com.debbiedoesit.debbieai`

---

## Overview

DebbieAI is a unified Android application combining five integrated modules for contractors:

| Module | Package | Status | Description |
|--------|---------|--------|-------------|
| 👥 **Contacts** | `contacts/` | ✅ MVP Ready | Customer CRM |
| 📸 **Photos** | `photos/` | ✅ MVP Ready | Job site photos |
| 💼 **Jobs** | `jobs/` | 🔲 Phase 2 | Project management |
| 📋 **Details** | `details/` | 🔲 Phase 5 | Tasks & calendar |
| 📐 **Drafting** | `drafting/` | 🔲 Phase 4 | Estimates & floor plans |
| 🔍 **Data** | `data_ai/` | 🔲 Phase 3 | AI enrichment |

---

## Project Structure

```
com.debbiedoesit.debbieai/
│
├── MainActivity.kt                 # App entry point with bottom nav
│
├── core/                           # Shared infrastructure
│   ├── database/
│   │   └── DebbieDatabase.kt       # Unified Room database (16 entities)
│   └── ui/
│       └── theme/
│           └── Theme.kt            # Debbie brand colors & typography
│
├── navigation/
│   └── DebbieNavigation.kt         # All routes + bottom nav
│
├── contacts/                       # 👥 CRM Module
│   ├── data/model/                 # Contact, Address
│   ├── data/dao/                   # ContactDao, AddressDao
│   ├── data/repository/            # ContactRepository
│   ├── data/sync/                  # ContactSyncService
│   ├── ui/screens/                 # List, Detail, Add, Duplicates
│   └── viewmodel/                  # ContactViewModel
│
├── photos/                         # 📸 Photos Module
│   ├── data/model/                 # Photo, Album
│   ├── data/dao/                   # PhotoDao, AlbumDao
│   ├── data/repository/            # PhotoRepository
│   ├── data/sync/                  # PhotoImportService
│   ├── ui/screens/                 # Gallery, Detail, Albums
│   └── viewmodel/                  # PhotoViewModel
│
├── jobs/                           # 💼 Jobs Module
│   ├── data/model/                 # Job, JobStatus
│   ├── data/dao/                   # JobDao
│   ├── data/repository/            # JobRepository
│   ├── ui/screens/                 # List, Detail, Dashboard
│   └── viewmodel/                  # JobViewModel
│
├── details/                        # 📋 Tasks/Calendar Module
│   ├── data/model/                 # Task, Event, Reminder, CrewMember
│   ├── data/dao/                   # TaskDao, EventDao, etc.
│   ├── data/repository/            # DetailsRepository
│   ├── ui/screens/                 # Tasks, Calendar, Briefing
│   └── viewmodel/                  # DetailsViewModel
│
├── drafting/                       # 📐 Estimates Module
│   ├── data/model/                 # Estimate, LineItem, Material
│   ├── data/dao/                   # EstimateDao, MaterialDao
│   ├── data/repository/            # DraftingRepository
│   ├── ui/screens/                 # Estimates, Builder, Materials
│   └── viewmodel/                  # DraftingViewModel
│
└── data_ai/                        # 🔍 AI Module
    ├── data/model/                 # AiAnalysis, BusinessInsight
    ├── data/dao/                   # AiAnalysisDao
    ├── data/repository/            # AiRepository
    ├── service/                    # VisionService, OcrService
    └── ui/screens/                 # Insights, Analysis Queue
```

---

## Database Schema

### Entities (16 total)

**Contacts Module:**
- `Contact` - Customer/vendor with multiple phones, emails
- `Address` - Linked addresses (home, work, billing, property)

**Jobs Module:**
- `Job` - Central linking entity for projects

**Photos Module:**
- `Photo` - Job site photos with EXIF, AI fields
- `Album` - Photo organization

**Details Module:**
- `Task` - To-do items with assignments
- `Event` - Calendar events
- `CrewMember` - Team members
- `Reminder` - Scheduled notifications

**Drafting Module:**
- `Estimate` - Quotes with room measurements
- `EstimateLineItem` - Materials & labor line items
- `Material` - Pricing database
- `EstimateTemplate` - Reusable templates

**AI Module:**
- `AiAnalysis` - Photo analysis results
- `BusinessInsight` - Analytics insights

### Key Relationships

```
Contact ──┬── Address (1:many)
          ├── Job (1:many)
          ├── Photo (1:many)
          ├── Task (1:many)
          └── Estimate (1:many)

Job ──────┬── Photo (1:many)
          ├── Album (1:many)
          ├── Task (1:many)
          └── Estimate (1:many)

Photo ────┬── Album (many:1)
          ├── AiAnalysis (1:many)
          └── Before/After linking

Estimate ─┬── EstimateLineItem (1:many)
          └── RoomMeasurement (embedded)
```

---

## Navigation

### Bottom Navigation (5 tabs)

| Tab | Route | Module |
|-----|-------|--------|
| 🏠 Home | `home` | Dashboard |
| 💼 Jobs | `jobs` | Jobs list |
| 📸 Photos | `photos` | Photo gallery |
| ✅ Tasks | `tasks` | Task list |
| ☰ More | `more` | Menu |

### All Routes

```kotlin
// Home
"home"

// Contacts
"contacts"
"contacts/{contactId}"
"contacts/add"
"contacts/duplicates"

// Photos
"photos"
"photos/{photoId}"
"photos/albums"
"photos/albums/{albumId}"
"photos/search"
"photos/favorites"
"photos/trash"

// Jobs
"jobs"
"jobs/{jobId}"
"jobs/{jobId}/dashboard"
"jobs/add"

// Tasks
"tasks"
"tasks/{taskId}"
"tasks/add"
"calendar"
"briefing"
"crew"

// Estimates
"estimates"
"estimates/{estimateId}"
"estimates/builder"
"materials"
"templates"

// AI
"insights"
"analysis"

// Settings
"settings"
"profile"
```

---

## Theme & Branding

### Debbie Brand Colors

```kotlin
// Primary (Blue - logo text)
DebbieBlue = #3B5998
DebbieBlueLight = #5C7BC0
DebbieBlueDark = #1A3A6E

// Accent (Red - shirt/banner)
DebbieRed = #C62828
DebbieRedLight = #E15353

// Secondary (Teal - glasses)
DebbieTeal = #00796B
DebbieTealLight = #48A999
```

### Module Colors

```kotlin
contacts = DebbieTeal     // 👥
photos = DebbieBlue       // 📸
details = DebbieOrange    // 📋
drafting = DebbiePurple   // 📐
data = DebbieGreen        // 🔍
jobs = DebbieRed          // 💼
```

---

## Setup Instructions

### 1. Copy Files to Android Project

Copy the following structure into your existing project:

```
app/src/main/java/com/debbiedoesit/debbieai/
├── MainActivity.kt
├── core/
│   ├── database/DebbieDatabase.kt
│   └── ui/theme/Theme.kt
├── navigation/DebbieNavigation.kt
├── contacts/        # Copy existing Contacts code
└── photos/          # Copy existing Photos code
```

### 2. Update build.gradle.kts

```kotlin
dependencies {
    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.6")
    
    // Compose
    implementation("androidx.compose.material3:material3:1.1.2")
    implementation("androidx.compose.material:material-icons-extended:1.5.4")
    
    // Image loading
    implementation("io.coil-kt:coil-compose:2.5.0")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}
```

### 3. Update AndroidManifest.xml

```xml
<uses-permission android:name="android.permission.READ_CONTACTS" />
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"
    android:maxSdkVersion="32" />

<application ...>
    <activity
        android:name=".MainActivity"
        android:exported="true"
        android:theme="@style/Theme.DebbieAI">
        <intent-filter>
            <action android:name="android.intent.action.MAIN" />
            <category android:name="android.intent.category.LAUNCHER" />
        </intent-filter>
    </activity>
</application>
```

### 4. Update Package References

Search and replace in existing code:
- `com.debbiedoesdetails.app` → `com.debbiedoesit.debbieai`

---

## Integration Guide

### Linking a Photo to a Contact

```kotlin
// In PhotoViewModel
fun linkToContact(photoIds: List<Long>, contactId: Long) {
    viewModelScope.launch {
        photoIds.forEach { photoId ->
            photoRepository.linkToContact(photoId, contactId)
        }
    }
}

// In ContactDetailScreen
val photos by photoViewModel.getPhotosByContact(contact.id)
    .collectAsState(initial = emptyList())
```

### Creating a Job from Contact

```kotlin
// In JobViewModel
fun createJobForContact(contactId: Long, title: String) {
    viewModelScope.launch {
        val job = Job(
            contactId = contactId,
            title = title,
            status = JobStatus.LEAD.name
        )
        jobRepository.insert(job)
    }
}
```

### Viewing Job Dashboard

```kotlin
// Shows: contact info, photos, tasks, estimates
navController.navigate(DebbieRoutes.jobDashboard(jobId))
```

---

## Implementation Priority

### Phase 1: Merge Contacts + Photos ✅ (Current)
1. ✅ Unified database schema
2. ✅ Unified theme
3. ✅ Unified navigation
4. ✅ MainActivity with bottom nav
5. 🔄 Migrate existing Contacts screens
6. 🔄 Migrate existing Photos screens
7. 🔄 Wire up cross-linking

### Phase 2: Jobs Module
1. Create JobViewModel & Factory
2. Create Job screens (List, Detail, Dashboard)
3. Link jobs to contacts, photos, tasks

### Phase 3: AI/Data Module
1. Google Vision service
2. Photo analysis queue
3. Auto-tagging

### Phase 4: Drafting Module
1. Estimate builder
2. Material database
3. PDF export

### Phase 5: Details Module
1. Task management
2. Calendar sync
3. Notifications

---

## Files Included

```
debbieai-unified/
├── ARCHITECTURE.md           # Full architecture documentation
├── README.md                 # This file
├── MainActivity.kt           # App entry point
├── core/
│   ├── database/
│   │   └── DebbieDatabase.kt # All entities, DAOs, converters
│   └── ui/
│       └── theme/
│           └── Theme.kt      # Debbie brand theme
└── navigation/
    └── DebbieNavigation.kt   # Routes & bottom nav
```

---

## Next Steps

1. **Copy these files** to your Android project
2. **Migrate existing Contacts code** into `contacts/` package
3. **Migrate existing Photos code** into `photos/` package
4. **Update imports** to use new database & theme
5. **Test navigation** between modules
6. **Build and run**

---

*"If Debbie can't do it, it probably shouldn't be done!"*

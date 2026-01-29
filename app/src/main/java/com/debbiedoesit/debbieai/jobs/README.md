# Jobs Module - DebbieAI

The central hub that links **Contacts**, **Photos**, **Estimates**, and **Tasks** together.

## Features

### Job Tracking
- Full job lifecycle: Lead → Quoted → Scheduled → In Progress → Completed → Paid
- 20+ job types (Flooring, Kitchen, Bathroom, Roofing, etc.)
- Priority levels (Low, Normal, High, Urgent)
- Auto-generated job numbers (2026-0001, 2026-0002, etc.)

### Financial Tracking
- Estimated vs actual costs
- Deposit tracking (amount & payment status)
- Final payment tracking
- Revenue summaries

### Scheduling
- Start/end dates
- Scheduled time
- Crew assignment
- Estimated vs actual hours

### Lead Source Tracking
- Track where leads come from
- Referral tracking
- ROI analysis (future)

### Views
- **List View**: Standard scrollable list
- **Kanban View**: Columns by status (like Trello)
- **Calendar View**: (Coming soon)
- **Map View**: (Future)

## File Structure

```
jobs/
├── data/
│   ├── local/
│   │   ├── Job.kt              # Entity + enums
│   │   └── JobDao.kt           # Database queries
│   └── repository/
│       └── JobRepository.kt    # Business logic
├── viewmodel/
│   ├── JobViewModel.kt         # State management
│   └── JobViewModelFactory.kt  # DI factory
└── ui/
    ├── components/
    │   ├── JobCard.kt          # Job display cards
    │   └── JobFilterChips.kt   # Filter controls
    ├── screens/
    │   ├── JobListScreen.kt    # Main list + views
    │   ├── JobDetailScreen.kt  # Full job details
    │   └── CreateJobScreen.kt  # Create/Edit forms
    └── navigation/
        └── JobNavigation.kt    # Route definitions
```

## Job Statuses

| Status | Description |
|--------|-------------|
| Lead | Initial inquiry |
| Quoted | Estimate sent |
| Approved | Customer accepted |
| Scheduled | Date set |
| In Progress | Work started |
| On Hold | Paused |
| Completed | Work finished |
| Invoiced | Bill sent |
| Paid | Payment received |
| Cancelled | Job cancelled |

## Job Types

Flooring, Kitchen, Bathroom, Basement, Roofing, Siding, Painting, Deck/Patio, Electrical, Plumbing, HVAC, Drywall, Tile, Carpentry, Windows/Doors, Landscaping, Inspection, Estimate Only, Warranty Work, General, Other

## Usage

```kotlin
// Create ViewModel
val viewModel = ViewModelProvider(
    this,
    JobViewModelFactory(applicationContext)
)[JobViewModel::class.java]

// Create a job
viewModel.createJob(
    title = "Kitchen Flooring - Johnson",
    contactId = 123,
    jobType = JobType.FLOORING,
    estimatedCost = 2500.00,
    source = LeadSource.REFERRAL
)

// Update status
viewModel.updateStatus(jobId, JobStatus.IN_PROGRESS)

// Complete job
viewModel.completeJob(jobId, actualHours = 8.0)

// Mark as paid
viewModel.markFinalPaid(jobId)
```

## Integration Points

### With Contacts
```kotlin
// Get jobs for a customer
viewModel.getJobsByContact(contactId)

// Create job for contact
navController.navigate(JobRoutes.createForContact(contactId))
```

### With Photos
```kotlin
// Photos linked via jobId field
// Job detail shows photo count
```

### With Estimates
```kotlin
// Estimates linked via jobId field
// Job detail shows estimate count
```

## Dependencies

- Room Database
- Kotlin Coroutines + Flow
- Jetpack Compose
- Material 3

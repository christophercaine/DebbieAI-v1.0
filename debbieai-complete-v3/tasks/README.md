# Debbie Does Tasks - Task Management Module

**Part of DebbieAI Suite**

Smart task management for contractors with reminders, scheduling, and integration with contacts and jobs.

## Features

- ✅ Full task lifecycle (Pending → In Progress → Waiting → Completed)
- ✅ 14 task categories (Call, Email, Site Visit, Estimate, Follow-up, etc.)
- ✅ 4 priority levels (Low, Normal, High, Urgent)
- ✅ Due dates with time support
- ✅ Smart reminders with snooze
- ✅ Recurring tasks support
- ✅ Link to Contacts and Jobs
- ✅ Assignment to crew members
- ✅ Time tracking (estimated vs actual)
- ✅ Quick task templates
- ✅ Multiple view modes (List, Today, Calendar, Kanban)
- ✅ Full search functionality

## File Structure

```
tasks/
├── data/
│   ├── local/
│   │   ├── Task.kt              # Entity + enums
│   │   └── TaskDao.kt           # Database queries
│   └── repository/
│       └── TaskRepository.kt    # Business logic
├── viewmodel/
│   ├── TaskViewModel.kt         # State management
│   └── TaskViewModelFactory.kt  # DI factory
└── ui/
    ├── components/
    │   ├── TaskCard.kt          # Task cards & list items
    │   └── TaskFilterChips.kt   # Filters, stats, templates
    ├── screens/
    │   ├── TaskListScreen.kt    # Main list with views
    │   ├── TaskDetailScreen.kt  # View/edit task
    │   ├── CreateTaskScreen.kt  # New task form
    │   └── TaskSearchScreen.kt  # Search tasks
    └── navigation/
        └── TaskNavigation.kt    # Navigation graph
```

## Task Statuses

| Status | Description |
|--------|-------------|
| PENDING | Not started (default) |
| IN_PROGRESS | Currently working on |
| WAITING | Blocked/waiting for something |
| COMPLETED | Done |
| CANCELLED | No longer needed |

## Task Categories

- **General** - Default category
- **Call** - Phone calls
- **Email** - Emails to send
- **Meeting** - In-person meetings
- **Site Visit** - Go to job site
- **Estimate** - Create/send estimate
- **Follow Up** - Follow up on something
- **Purchase** - Buy materials/supplies
- **Inspection** - Inspect work
- **Scheduling** - Schedule jobs/crew
- **Paperwork** - Documents, permits
- **Payment** - Collect/send payment
- **Warranty** - Warranty work
- **Other** - Everything else

## Quick Task Templates

Pre-built templates for common contractor tasks:

- Call customer
- Send estimate
- Follow up on estimate
- Schedule job
- Site visit
- Order materials
- Send invoice
- Collect payment
- Final walkthrough
- Warranty check

## View Modes

1. **List** - Standard task list with filters
2. **Today** - Focus on today's tasks + overdue
3. **Calendar** - Week view with date selection
4. **Kanban** - Columns by status

## Usage Examples

### Create Task
```kotlin
viewModel.createTask(
    title = "Call Johnson about flooring",
    category = TaskCategory.CALL,
    priority = TaskPriority.HIGH,
    dueDate = LocalDate.now().plusDays(1),
    contactId = 123L,
    reminderMinutesBefore = 30
)
```

### Create from Template
```kotlin
viewModel.createFromTemplate(
    template = QuickTaskTemplate.FOLLOW_UP_ESTIMATE,
    contactId = 123L,
    dueDate = LocalDate.now().plusDays(3)
)
```

### Complete Task
```kotlin
viewModel.completeTask(
    taskId = 456L,
    notes = "Customer accepted quote"
)
```

## Integration

### With Contacts
```kotlin
// Get tasks for a contact
val tasks = viewModel.getActiveTasksByContact(contactId)

// Create task for contact
navController.navigate(TaskRoutes.createForContact(contactId))
```

### With Jobs
```kotlin
// Get tasks for a job
val tasks = viewModel.getActiveTasksByJob(jobId)

// Create task for job
navController.navigate(TaskRoutes.createForJob(jobId))
```

## Navigation Routes

| Route | Description |
|-------|-------------|
| `tasks/list` | Main task list |
| `tasks/detail/{taskId}` | Task details |
| `tasks/create` | New task |
| `tasks/create/contact/{id}` | New task linked to contact |
| `tasks/create/job/{id}` | New task linked to job |
| `tasks/edit/{taskId}` | Edit task |
| `tasks/search` | Search tasks |

## Stats Available

```kotlin
data class TaskSummary(
    val totalTasks: Int,
    val pendingTasks: Int,
    val completedToday: Int,
    val overdueTasks: Int,
    val dueTodayTasks: Int,
    val dueThisWeekTasks: Int,
    val tasksByCategory: Map<TaskCategory, Int>,
    val tasksByPriority: Map<TaskPriority, Int>
)
```

## Dependencies

- Room Database
- Kotlin Coroutines + Flow
- Jetpack Compose
- Material 3
- Navigation Compose

---

*Part of the DebbieAI contractor productivity suite*

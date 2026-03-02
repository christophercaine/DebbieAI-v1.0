# DebbieAI Multi-Machine Sync Workflow

## Quick Reference

### First Time Setup (On Laptop)

```powershell
# Navigate to where you want the project
cd C:\Users\Superuser\Desktop

# Clone the repository
git clone https://github.com/YOUR-USERNAME/debbieai-android.git

# Enter the project
cd debbieai-android

# Verify everything is there
ls
```

### Daily Workflow

#### Starting Work (Pull Latest Changes)

```powershell
# Navigate to project
cd C:\Users\Superuser\Desktop\AndroidProjects

# Pull latest changes from GitHub
git pull origin main

# You're now up to date!
```

#### After Making Changes (Push Your Work)

```powershell
# Check what you changed
git status

# Stage all changes
git add .

# Commit with a message
git commit -m "Your description of changes"

# Push to GitHub
git push origin main
```

## Common Scenarios

### Scenario 1: Made changes on desktop, now working on laptop

**On Desktop:**
```powershell
cd C:\Users\Superuser\Desktop\AndroidProjects
git add .
git commit -m "Fixed KAPT issues and added Converters.kt"
git push origin main
```

**On Laptop:**
```powershell
cd C:\Users\Superuser\Desktop\AndroidProjects
git pull origin main
# Your changes are now on laptop!
```

### Scenario 2: Check if you have uncommitted changes

```powershell
git status
# Shows: modified files, new files, deleted files
```

### Scenario 3: See what changed in a file

```powershell
git diff path/to/file.kt
# Shows line-by-line changes
```

### Scenario 4: Undo changes to a file (before commit)

```powershell
# Discard changes to specific file
git checkout -- path/to/file.kt

# Discard ALL uncommitted changes (careful!)
git reset --hard
```

### Scenario 5: Merge Conflict (rare but happens)

If you edited the same file on both machines:

```powershell
git pull origin main
# ERROR: Merge conflict in SomeFile.kt

# Open the file, look for:
# <<<<<<< HEAD
# Your changes
# =======
# Changes from other machine
# >>>>>>> 

# Edit the file to keep what you want
# Then:
git add SomeFile.kt
git commit -m "Resolved merge conflict"
git push origin main
```

## Best Practices

### ✅ DO:
- **Pull before you start working** (`git pull`)
- **Commit frequently** with clear messages
- **Push at end of work session**
- **Use descriptive commit messages** ("Fixed Room database KAPT error" not "changes")

### ❌ DON'T:
- Don't work on both machines simultaneously without pulling
- Don't commit broken code (test builds first)
- Don't commit secrets/API keys (they're in .gitignore)
- Don't force push (`git push --force`) unless you know what you're doing

## Commit Message Examples

Good commit messages:
```
git commit -m "Added TypeConverters for Room database"
git commit -m "Fixed KAPT annotation processing error"
git commit -m "Implemented contact sync service"
git commit -m "Updated UI for contact detail screen"
git commit -m "Phase 1 complete - Contacts MVP working"
```

Bad commit messages:
```
git commit -m "changes"
git commit -m "fix"
git commit -m "update"
git commit -m "asdf"
```

## Checking Your Status

### See commit history
```powershell
git log --oneline -10
# Shows last 10 commits
```

### See what branch you're on
```powershell
git branch
# Should show: * main
```

### See remote repository URL
```powershell
git remote -v
# Shows GitHub URL
```

## Emergency Commands

### Forgot to pull, made changes, now can't push

```powershell
# Save your changes temporarily
git stash

# Pull latest
git pull origin main

# Reapply your changes
git stash pop

# Resolve any conflicts, then commit and push
```

### Accidentally committed something

```powershell
# Undo last commit but keep changes
git reset --soft HEAD~1

# Edit files as needed
git add .
git commit -m "Better commit message"
```

### Want to start fresh from GitHub

```powershell
# WARNING: This deletes ALL local changes!
git fetch origin
git reset --hard origin/main
```

## Workflow Diagram

```
Desktop                    GitHub                    Laptop
   |                          |                         |
   |-- git push ----------->  |                         |
   |                          |  <----- git pull -------|
   |                          |                         |
   |                          |  <----- git push -------|
   |-- git pull ----------->  |                         |
   |                          |                         |
```

## File Organization

### What Gets Synced
- ✅ All `.kt` source files
- ✅ `build.gradle.kts` files
- ✅ `AndroidManifest.xml`
- ✅ Resource files (layouts, strings, etc.)
- ✅ Documentation (`.md` files)
- ✅ Scripts (`.ps1`, `.bat` files)

### What Doesn't Get Synced (in .gitignore)
- ❌ Build artifacts (`build/`, `.gradle/`)
- ❌ Log files (`*.log`, `*.txt`)
- ❌ IDE settings (`.idea/`)
- ❌ Local configuration (`local.properties`)
- ❌ Compiled APKs

## Quick Troubleshooting

### "fatal: not a git repository"
```powershell
# You're in the wrong directory
cd C:\Users\Superuser\Desktop\AndroidProjects
```

### "Permission denied (publickey)"
```powershell
# Need to set up SSH keys or use HTTPS
# Use HTTPS URL: https://github.com/username/repo.git
```

### "Your branch is ahead of origin/main"
```powershell
# You have local commits not pushed
git push origin main
```

### "Your branch is behind origin/main"
```powershell
# GitHub has changes you don't have
git pull origin main
```

## Next Steps

1. **Set up GitHub repository** (see setup guide)
2. **Make initial commit** from desktop
3. **Clone on laptop**
4. **Test the workflow** with a small change
5. **Build confidence** with daily use

---

**Remember:** Git is your safety net. Commit often, push regularly, and you'll never lose work!

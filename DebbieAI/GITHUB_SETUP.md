# DebbieAI GitHub Setup Guide

## Prerequisites

- [ ] GitHub account created
- [ ] Git installed on Windows
- [ ] Project cleaned up (logs removed)

## Step 1: Create GitHub Repository

### Option A: Via GitHub Website

1. Go to https://github.com
2. Click the **+** icon (top right) → **New repository**
3. Fill in:
   - **Repository name:** `debbieai-android`
   - **Description:** "DebbieAI - AI-Powered Contractor Assistant Suite for Android"
   - **Visibility:** Private (recommended) or Public
   - **DO NOT** initialize with README, .gitignore, or license (we already have these)
4. Click **Create repository**
5. Copy the repository URL (should be: `https://github.com/YOUR-USERNAME/debbieai-android.git`)

### Option B: Via GitHub CLI (if installed)

```powershell
gh repo create debbieai-android --private --description "DebbieAI - AI-Powered Contractor Assistant Suite"
```

## Step 2: Configure Git (First Time Only)

```powershell
# Set your name (shows in commits)
git config --global user.name "Your Name"

# Set your email (use your GitHub email)
git config --global user.email "your.email@example.com"

# Verify configuration
git config --list
```

## Step 3: Initialize and Connect Repository

```powershell
# Navigate to project
cd C:\Users\Superuser\Desktop\AndroidProjects

# Check git status
git status

# If not initialized, initialize git
git init

# Add all files (respecting .gitignore)
git add .

# Create initial commit
git commit -m "Initial commit - DebbieAI Phase 1 Contacts MVP"

# Add GitHub as remote (replace YOUR-USERNAME)
git remote add origin https://github.com/YOUR-USERNAME/debbieai-android.git

# Verify remote
git remote -v

# Push to GitHub
git push -u origin main
```

## Step 4: Verify Upload

1. Go to `https://github.com/YOUR-USERNAME/debbieai-android`
2. You should see:
   - ✅ `app/` folder with source code
   - ✅ `build.gradle.kts`
   - ✅ `settings.gradle.kts`
   - ✅ `README.md`
   - ✅ `debbieai-complete-v3/` folder
   - ❌ NO `build/` folders
   - ❌ NO `.log` files
   - ❌ NO `.idea/` folder

## Step 5: Clone on Laptop

**On your laptop:**

```powershell
# Navigate to where you want the project
cd C:\Users\Superuser\Desktop

# Clone the repository
git clone https://github.com/YOUR-USERNAME/debbieai-android.git

# Enter the project
cd debbieai-android

# Verify files are there
ls

# Try building
.\gradlew clean assembleDebug
```

## Step 6: Test Sync Workflow

**On Desktop:**
```powershell
cd C:\Users\Superuser\Desktop\AndroidProjects

# Make a small change (e.g., add a comment to README.md)
echo "# Test change from desktop" >> README.md

# Commit and push
git add README.md
git commit -m "Test sync from desktop"
git push origin main
```

**On Laptop:**
```powershell
cd C:\Users\Superuser\Desktop\debbieai-android

# Pull the change
git pull origin main

# Check README.md - should have the new comment
cat README.md
```

## Troubleshooting

### "fatal: not a git repository"

```powershell
# Initialize git
git init
```

### "remote origin already exists"

```powershell
# Remove old remote
git remote remove origin

# Add new remote
git remote add origin https://github.com/YOUR-USERNAME/debbieai-android.git
```

### "failed to push some refs"

```powershell
# Pull first, then push
git pull origin main --rebase
git push origin main
```

### "Permission denied (publickey)"

You need to authenticate. Options:

**Option 1: Use HTTPS with Personal Access Token**
1. Go to GitHub → Settings → Developer settings → Personal access tokens
2. Generate new token (classic)
3. Give it `repo` permissions
4. Copy the token
5. When pushing, use token as password

**Option 2: Set up SSH keys**
```powershell
# Generate SSH key
ssh-keygen -t ed25519 -C "your.email@example.com"

# Copy public key
cat ~/.ssh/id_ed25519.pub

# Add to GitHub → Settings → SSH and GPG keys
```

## Authentication Setup

### Using Personal Access Token (Recommended for HTTPS)

1. **Create token:**
   - GitHub → Settings → Developer settings → Personal access tokens → Tokens (classic)
   - Click "Generate new token (classic)"
   - Name: "DebbieAI Development"
   - Expiration: 90 days (or longer)
   - Select scopes: `repo` (all)
   - Click "Generate token"
   - **COPY THE TOKEN** (you won't see it again!)

2. **Use token:**
   ```powershell
   # When git asks for password, paste the token
   git push origin main
   # Username: your-github-username
   # Password: <paste-token-here>
   ```

3. **Cache credentials (optional):**
   ```powershell
   # Store credentials for 1 hour
   git config --global credential.helper cache

   # Or store permanently (Windows)
   git config --global credential.helper wincred
   ```

## What Gets Synced

### ✅ Synced to GitHub
- All `.kt` source files
- `build.gradle.kts` files
- `AndroidManifest.xml`
- Resource files
- Documentation
- `.gitignore`

### ❌ NOT Synced (in .gitignore)
- `build/` folders
- `.gradle/` cache
- `.idea/` IDE settings
- `*.log` files
- `local.properties`
- APK files

## Repository Structure on GitHub

```
debbieai-android/
├── .gitignore
├── README.md
├── SYNC_WORKFLOW.md
├── build.gradle.kts
├── settings.gradle.kts
├── gradle/
├── gradlew
├── gradlew.bat
├── app/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           └── java/com/debbiedoesdetails/app/
│               ├── MainActivity.kt
│               ├── data/
│               ├── ui/
│               └── viewmodel/
└── debbieai-complete-v3/
    ├── MainActivity.kt
    ├── contacts/
    ├── photos/
    ├── jobs/
    └── core/
```

## Next Steps

1. ✅ Create GitHub repository
2. ✅ Configure Git
3. ✅ Push initial commit
4. ✅ Clone on laptop
5. ✅ Test sync workflow
6. 🎯 Start using daily workflow (see SYNC_WORKFLOW.md)

---

**You're all set!** You can now work on DebbieAI from both machines seamlessly.

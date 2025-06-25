# Gitlet - A Mini Git Version Control System in Java

**Gitlet** is a simplified version control system inspired by Git, implemented in **Java**.  
It supports basic Git operations such as `init`, `add`, `commit`, `log`, `branch`, `checkout`, and `merge`.

This project is meant for learning purposes and understanding how a version control system works under the hood — without using any of Git's actual internals.

---

## ✨ Features

- `init` — Initialize a new Gitlet repository
- `add` — Stage files for commit
- `commit` — Commit staged changes with a message
- `log` — View the commit history
- `checkout` — Restore files or switch branches
- `branch` — Create and manage branches
- `merge` — Merge two branches
- SHA-1 based commit tracking
- Custom directory structure for internal storage

---

## 🚀 Getting Started

### ✅ Requirements

- Java 11 or higher
- JDK and a terminal/command line interface

### 📦 Compile the project

```bash
javac gitlet/*.java

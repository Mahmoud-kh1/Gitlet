# Gitlet

[![Build](https://img.shields.io/badge/build-Maven-blue)](https://github.com/Mahmoud-kh1/Gitlet)  [![Language](https://img.shields.io/badge/language-Java-orange)](https://github.com/Mahmoud-kh1/Gitlet)

**Gitlet** is an educational Git-like version control system written in Java. It re-creates core VCS functionality (init, add, commit, log, branch, checkout, merge) to demonstrate how version control primitives work in a compact, readable codebase.

---

## Requirements & Installation

Minimum requirements (inferred):

- Java JDK 8+ (11+ recommended)  
- Apache Maven  
- Git (to clone)  
- Optional: Python 3 (helper scripts), GNU Make (Makefile)

Clone and build:

```bash
git clone https://github.com/Mahmoud-kh1/Gitlet.git
cd Gitlet
mvn -DskipTests package
```

## Features

Gitlet comes with a comprehensive set of version-control features:

### Commit Management
- Capture snapshots of the project as commits.  
- Restore either specific files or the entire working directory to a prior commit.  
- Review commit history with the `log` command for a chronological view of changes.  

### Branching & Merging
- Create, switch, and manage multiple branches to work on features independently.  
- Merge branches back together, generating merge commits when needed.  

### Staging Area
- Stage files for addition or removal before committing, providing fine-grained control over commits.  
- Automatically clears the staging area after each commit to keep it in sync.  

### File Tracking & Conflicts
- Continuously track file changes across commits for easy inspection.  
- Detect conflicting modifications during merges and mark them clearly for resolution.  

### Commit Metadata
- Each commit includes a timestamp, message, and links to its parent commit(s).  
- All commits and file objects are identified using SHA-1 hashes for uniqueness and data integrity.  

### Error Handling
- Provides clear messages and safeguards when encountering invalid commands, missing files, or untracked files.

- ---
## usage
### Initialize repository
```bash
java gitlet.Main init
```
### Staging for Add
```bash
java gitlet.Main add <file>
```

### commit 
```bash
java gitlet.Main commit "message"
```

### View branch commit history
```bash
java gitlet.Main log
```

### View all commits globally
```bash
java gitlet.Main global-log
```
### Find commits by message
```bash
java gitlet.Main find "message"
```
### Create a new branch
```bash
java gitlet.Main branch <branch>
```
### Switch branches
```bash
java gitlet.Main checkout <branch>
```

### Checkout a file from latest commit
```bash
java gitlet.Main checkout -- <file>
```


### Checkout a file from latest commit
```bash
java gitlet.Main checkout -- <file>
```

### Checkout a file from specific commit
```bash
java gitlet.Main checkout <commit-id> -- <file>
```


### Merge a branch into current branch
```bash
java gitlet.Main merge <branch>
```









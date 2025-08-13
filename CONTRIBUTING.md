# Contributing to barK 🐕

Thank you for your interest in contributing to barK! We welcome contributions from the community and appreciate your help in making barK better.

## 📋 Table of Contents

- [Getting Started](#getting-started)
- [Development Setup](#development-setup)
- [How to Contribute](#how-to-contribute)
- [Coding Guidelines](#coding-guidelines)
- [Testing](#testing)
- [Submitting Changes](#submitting-changes)
- [Release Process](#release-process)
- [Getting Help](#getting-help)

## 🚀 Getting Started

### Prerequisites

- **JDK 17** or later
- **Android Studio** (latest stable version)
- **Xcode** (for iOS development, macOS only)
- **Git**

### Development Setup

1. **Fork and Clone**
   ```bash
   git clone https://github.com/YOUR_USERNAME/barK.git
   cd barK
   ```

2. **Set up upstream remote**
   ```bash
   git remote add upstream https://github.com/ivangarzab/barK.git
   ```

3. **Open in Android Studio**
    - Open the project in Android Studio
    - Let Gradle sync complete
    - Ensure the project builds successfully

4. **Verify Setup**
   ```bash
   ./gradlew build
   ./gradlew :shared:test
   ```

## 🤝 How to Contribute

### Types of Contributions

We welcome several types of contributions:

- 🐛 **Bug fixes**
- ✨ **New features** (please discuss in an issue first)
- 📚 **Documentation improvements**
- 🧪 **Test coverage improvements**
- 🎨 **Code quality improvements**
- 🔧 **Build/CI improvements**

### Before You Start

1. **Check existing issues** - Look for existing issues or discussions
2. **Create an issue** - For new features or significant changes, create an issue first
3. **Get feedback** - Discuss your approach before investing significant time

## 🌿 Branching Strategy

We use **Git Flow** for development:

- `main` - Production-ready releases (protected)
- `develop` - Integration branch for features (base for PRs)
- `feature/description` - Feature development branches
- `release/x.x.x` - Release preparation (automated)

### Workflow

1. **Start from develop**
   ```bash
   git checkout develop
   git pull upstream develop
   ```

2. **Create feature branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

3. **Make your changes**
    - Write code
    - Add tests
    - Update documentation

4. **Keep up to date**
   ```bash
   git fetch upstream
   git rebase upstream/develop
   ```

## 📝 Coding Guidelines

### Kotlin Style

- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable and function names
- Prefer `val` over `var` when possible
- Use trailing commas in multiline constructs

### Code Organization

```kotlin
// Good: Clear, descriptive naming
class AndroidLogTrainer(
    override val volume: Level = Level.VERBOSE
) : Trainer {
    override fun handle(level: Level, tag: String, message: String, throwable: Throwable?) {
        if (level.ordinal < volume.ordinal) return
        // Implementation
    }
}
```

### Documentation

- Add KDoc for public APIs
- Include usage examples for new features
- Update README.md if adding user-facing features

```kotlin
/**
 * Handles logging to Android's Logcat system.
 *
 * This trainer outputs logs using Android's built-in Log class, making
 * them visible in Android Studio's Logcat window and device logs.
 *
 * @param volume Minimum log level to output (defaults to VERBOSE)
 */
class AndroidLogTrainer(override val volume: Level = Level.VERBOSE) : Trainer
```

## 🧪 Testing

### Running Tests

```bash
# Run all tests
./gradlew test

# Run specific module tests
./gradlew :shared:test
```

### Writing Tests

- **Unit tests** for all new functionality
- **Integration tests** for trainer behavior
- **Sample app tests** for realistic usage scenarios

```kotlin
@Test
fun `trainer should respect volume filtering`() {
    val trainer = AndroidLogTrainer(volume = Level.WARNING)
    
    // Test that DEBUG messages are filtered
    trainer.handle(Level.DEBUG, "TestTag", "Debug message", null)
    
    // Verify filtering behavior
    // ...
}
```

### Test Coverage

- Aim for **80%+ test coverage** on new code
- Include **edge cases** and **error scenarios**
- Test **both happy path and failure modes**

## 📤 Submitting Changes

### Pull Request Process

1. **Ensure tests pass**
   ```bash
   ./gradlew build test
   ```

2. **Create Pull Request**
    - Target the `develop` branch
    - Use a descriptive title
    - Fill out the PR template
    - Reference any related issues

3. **PR Title Format**
   ```
   feat: add support for custom log formatters
   fix: resolve memory leak in test trainer
   docs: improve README installation instructions
   test: add coverage for exception logging
   ```

### PR Template

When creating a PR, please include:

```markdown
## Description
Brief description of changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Documentation update
- [ ] Test improvement

## Testing
- [ ] Unit tests pass
- [ ] Manual testing completed
- [ ] Added new tests for changes

## Checklist
- [ ] Code follows project guidelines
- [ ] Self-review completed
- [ ] Documentation updated
- [ ] No breaking changes (or clearly documented)
```

### Code Review

- All PRs require review before merging
- Address feedback promptly and professionally
- Tests must pass before merging
- Maintain backward compatibility unless discussed

## 🚀 Release Process

Releases are automated and handled by maintainers:

1. **Version bumping** - Automated via release scripts
2. **GitHub releases** - Auto-created when tags are pushed
3. **JitPack publishing** - Triggered automatically
4. **Documentation updates** - Updated with each release

Contributors don't need to worry about releases - just focus on great code!

## 🆘 Getting Help

### Communication

- **GitHub Issues** - Bug reports and feature requests
- **GitHub Discussions** - Questions and general discussion
- **Pull Request comments** - Code-specific discussions

### Resources

- [Kotlin Multiplatform Documentation](https://kotlinlang.org/docs/multiplatform.html)
- [Android Development Guide](https://developer.android.com/guide)
- [barK Sample App](./sample-android) - See realistic usage examples

## 🎉 Recognition

Contributors are recognized in:
- **Release notes** for significant contributions
- **README contributors section** (coming soon)
- **GitHub contributor graphs**

## 📜 Code of Conduct

This project follows a simple code of conduct:

- **Be respectful** and inclusive
- **Be constructive** in feedback
- **Be patient** with newcomers
- **Be professional** in all interactions

## 🙏 Thank You

Every contribution, no matter how small, helps make barK better for everyone. We appreciate your time and effort!

---

**Questions?** Feel free to open an issue or start a discussion. We're here to help! 🐕
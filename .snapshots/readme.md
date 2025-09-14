# Snapshots for AI

## Configuration

The `config.json` file allows you to customize the behavior of the Snapshots for AI plugin.

### Options

- `excluded_patterns`: A list of patterns to exclude from the project structure snapshot. Patterns include:
  - `.git`
  - `.gitignore`
  - `gradle`
  - `gradlew`
  - `gradlew.*`
  - `node_modules`
  - `vendor`
  - `.snapshots`
  - `.idea`
  - `.vscode`
  - `*.log`
  - `*.tmp`
  - `target`
  - `dist`
  - `build`
  - `.DS_Store`
  - `*.bak`
  - `*.swp`
  - `*.swo`
  - `*.lock`
  - `*.iml`
  - `coverage`
  - `*.min.js`
  - `*.min.css`
  - `netlify.toml`
  - `package-lock.json`
  - `__pycache__`
  - `LICENSE`

- `included_patterns`: A list of patterns to include in the project structure snapshot. Patterns include:
  - `build.gradle`
  - `settings.gradle`
  - `gradle.properties`
  - `pom.xml`
  - `Makefile`
  - `CMakeLists.txt`
  - `package.json`
  - `yarn.lock`
  - `requirements.txt`
  - `Pipfile`
  - `Pipfile.lock`
  - `Gemfile`
  - `Gemfile.lock`
  - `composer.json`
  - `composer.lock`
  - `.editorconfig`
  - `.eslintrc.json`
  - `.eslintrc.js`
  - `.prettierrc`
  - `.babelrc`
  - `.env`
  - `.dockerignore`
  - `.gitattributes`
  - `.stylelintrc`
  - `.npmrc`

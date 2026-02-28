# jCamelDecompiler

![Version](https://img.shields.io/badge/version-2026.1.0-blue)
![Java](https://img.shields.io/badge/Java-21-orange)

A light, simple, multi-engine Java decompiler GUI application built with Java 21 and Swing.

## Features

- **Multi-Engine Support**: Integrated with multiple popular decompiler engines.
- **Batch Processing**: Decompile entire folders of `.class` files or JAR contents.
- **Filtering**: Filter specific files for decompilation using a text-based file list.
- **Real-time Progress**: Visual progress bars and logs for tracking decompilation status.
- **User-Friendly GUI**: Simple and intuitive Swing-based interface.
- **State Persistence**: Remembers your last used folders and decompiler settings.

## Supported Decompilers

The application acts as a wrapper for the following decompiler engines:

- [Procyon](https://github.com/mstrobel/procyon)
- [CFR](http://github.com/leibnitz27/cfr)
- [FernFlower](https://github.com/JetBrains/fernflower) (The decompiler used in IntelliJ IDEA)

## Prerequisites

- **Java Runtime Environment (JRE) 21** or higher.

## Getting Started

### Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/nmhillusion/jCamelDecompiler.git
   cd jCamelDecompiler
   ```

2. Build the distribution:
   ```bash
   ./gradlew installDist
   ```

### Running the Application

After building, you can find the executable scripts in:
`build/install/jCamelDecompilerApp/bin/`

Alternatively, run directly via Gradle:

```bash
./gradlew run
```

## Configuration

Decompiler engines and their settings are managed via `src/main/resources/decompiler/decompilers.config.yml`.

### Execution Settings

- `maxinum_exec_time_in_sec`: The maximum allowed time (in seconds) for a single decompiler execution before it is timed out.

### Decompiler Engines

Each entry in the `decompilers` list defines a supported engine:

- `id`: Unique identifier for the engine.
- `name`: Display name shown in the GUI.
- `options`: Additional specific command-line flags or arguments for the decompiler.
- `libFilename`: The name of the JAR file located in the `decompiler` resource folder.
- `execScriptFilename`: The batch script file name (`.bat`) used to invoke the decompiler execution.

## Author

- **nmhillusion**

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

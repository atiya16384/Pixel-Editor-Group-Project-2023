![Pipeline Status](https://scc-source.lancs.ac.uk/scc210-2022-23/scc210-2223-grp-31/badges/dev/pipeline.svg)

# Pixel Editor (com.group31.editor)

A lightweight and stadanlone image editor.

## Description

com.group31.editor is a simple pixel editor that allows users to import Bitmap images and work with editor project files (*.epj). This program was developed as a group project for a university course, and is intended to provide a basic introduction to image editing and file manipulation.

## Installation

1. Download the latest build from [the releases page](https://scc-source.lancs.ac.uk/scc210-2022-23/scc210-2223-grp-31/-/releases).
2. Run the JAR executable

## Usage

To use com.group31.editor, follow these steps:

1. Open the program by double-clicking the JAR file (or run via the `javaw` command-line utility)
2. Enter the necessary project setup details in the fields provided.
3. Import and export bitmap images and save/load your projects from the File menu.
4. Use the tools provided in the toolbar to edit your image.

## Compile locally

Building the project locally is a breeze thanks to maven.

```bash
mvn clean compile
mvn package
java -jar target/pixel-editor-[Version]-with-dependencies.jar
```

## Contributing

com.group31.editor is built with openjdk17.0.2 and maven-3.8.6.

This project uses maven (`mvn`), lifecycle defaults are used.

Here are a few examples of options available to you:

```shell
mvn test-compile # Tests if the project compiles
mvn compile # Compiles the project
mvn test # Runs included tests
mvn package # Builds an executable jar file
mvn deploy # Runs the current project
```

## Data Usage

We use the Sentry.io service to help us improve our software and identify and fix errors. Sentry.io collects information about your device and how our software is running on it, including information about any errors or crashes that occur. This information is used to help us improve our software and make it more stable.

Please note that the information collected by Sentry.io is not personally identifiable and is used solely for the purpose of improving our software. If you have any questions or concerns about how your data is used, please contact [Brendan Jennings \<b.jennings3@lancaster.ac.uk&gt;](mailto:b.jennings3@lancaster.ac.uk).

Disable sentry data submissions by selecting the box shown on start-up.

See the Sentry data [security](https://sentry.io/security/) and [privacy](https://sentry.io/privacy/) notice.

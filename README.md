# Android Build Variant Selector Plugin

An Android Studio plugin that provides a simple and efficient way to switch between build variants using a dropdown menu in the main toolbar.

![Plugin Demo](https://github.com/velgun/AndroidBuildVariantPlugin/blob/main/media/plguin.gif?raw=true)

## Why Use This Plugin?

In a typical Android development workflow, switching between build variants requires navigating to the "Build Variants" tool window and selecting the desired option. This process can be slow and interrupt your coding flow, especially if you need to switch variants frequently.

This plugin streamlines that process by adding a dedicated dropdown menu to the main toolbar. This allows you to see the current build variant at a glance and switch to a different one with a single click, saving you time and effort.

## Features

*   **Quick Variant Switching:** A dropdown in the main toolbar lists all available build variants for the current project.
*   **Easy to Use:** Simply select the desired variant from the list to initiate a Gradle sync.
*   **Seamless Integration:** The plugin integrates directly into the Android Studio toolbar for a native feel.

## Installation

You can install the plugin directly from the JetBrains Marketplace:

**[Install from JetBrains Marketplace](https://plugins.jetbrains.com/plugin/23946-android-build-variant-selector)**

Alternatively, you can install it from within Android Studio:

1.  Open Android Studio and go to **File** > **Settings** (or **Android Studio** > **Preferences** on macOS).
2.  Navigate to the **Plugins** section.
3.  Click on the **Marketplace** tab.
4.  Search for "Android Build Variant Selector".
5.  Click the **Install** button next to the plugin.
6.  Restart Android Studio to complete the installation.

## How to Use

1.  Once installed and enabled, a new dropdown menu will appear in the main toolbar.
2.  This dropdown will automatically be populated with all the build variants defined in your project's `build.gradle` file (e.g., `debug`, `release`, `demoDebug`).
3.  Click on the dropdown to see the list of available variants.
4.  Select the build variant you want to switch to.
5.  The project will automatically sync with the newly selected build variant.

## License

This project is licensed under the [MIT License](https://github.com/velgun/AndroidBuildVariantPlugin?tab=MIT-1-ov-file).

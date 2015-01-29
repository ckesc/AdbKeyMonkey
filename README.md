<p align="center">  
    <a href="https://github.com/ckesc/AdbKeyMonkey/releases">
        <img src="https://raw.githubusercontent.com/ckesc/AdbKeyMonkey/master/logo.png" style="height:auto; width: 100%">  
    </a>
</p>
# Adb Key Monkey [![Build Status](https://travis-ci.org/ckesc/AdbKeyMonkey.svg?branch=master)](https://travis-ci.org/ckesc/AdbKeyMonkey)

*Fastest Adb Keyboard*

Tool for control android device via PC keyboard

## Requirement:
* Java 7 runtime *(JRE7)* or higher
* Sdk/platform-tools has to be in PATH env variable in order to find adb

## How to run
1. [Download](https://github.com/ckesc/AdbKeyMonkey/releases)
2. Run: `java -jar adbKeyMonkey.jar`

## How to use
When program starts, it connects to first device in adb.
So before run, connect your device via ADB. Execute `adb devices` to verify.

## Keys
* `Up`, `Left`, `Right`, `Down` = `DPAD_UP`, `DPAD_LEFT`, `DPAD_RIGHT`, `DPAD_DOWN`
* `Esc` = Android `BACK`
* `Enter` = `DPAD_CENTER`
* `Backspace` = `Backspace`
* Letter keys = input letter in android

## Config
At first run app put `config.json` at working dir.
You can configure:

`reconnectPeriod`
App checks connection every 5 sec (by default) and reconnect if no connection
This setting defines time in seconds between checking connectivity

`keyMap`
Map pc key to android KeyEvents.
Left: pc; Right: Android
For pc keycodes look [here (Java FX doc)](https://docs.oracle.com/javafx/2/api/javafx/scene/input/KeyCode.html)
For android KeyEvent look at [Android doc](http://developer.android.com/reference/android/view/KeyEvent.html)

## Build
1. Got jdk 7 or higher
2. Run `./gradlew build`
3. Compiled files will be at `./build/distributions`

## How it`s works
Speed of operation is achieved through the use of [MonkeyRunner API] (http://developer.android.com/tools/help/monkeyrunner_concepts.html)

![demo](http://g.recordit.co/0vtYFKl1Qb.gif)


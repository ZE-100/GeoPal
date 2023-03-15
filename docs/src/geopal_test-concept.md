# Test Concept

## Test environment

The tests can be executed on either an emulator or any physical device, running Android (as long as its not restricted by any geopolitical laws, which prohibit the usage of Google Play Services (e.g. CCP).

The tests by the application's developer were done on both a physical device (Samsung Galaxy S22) and an emulator (Google Pixel 6).

### The specs of both devices

#### Physical device

Brand   : Samsung
Model   : Galaxy S22
OS      : Android
Version : 13

#### Emulated device

Brand   : Google
Model   : Pixel 6
OS      : Android
Version : 11

## Types of tests

There are two kinds of tests. **Unit Tests (UT)** and **User Acceptance Tests (UAT)**.

#### Unit Tests

**UT**s are automatically executed by the application itself. **UT**s test each of the components of the app, except UI related classes and methods; they'll target services, which e.g. save reminder-based information to the local storage.

## User Acceptance Tests

**UAT**s are done by, as the name implies, users: Someone, e.g. the developer,  will go through the list of "to-be-done-uats" and execute each of the test cases by hand.

## Test cases

* Name
  * Requirements
  * Steps to do
    * Step 1
    * Step 2
  * Excpected result

* Grant WiFi access
  * User grants WiFi-list-read access privileges
  * Steps:
    1. Open application
    2. (Pop-up appears)
    3. Click 'Allow'
  * App should now have access to the WiFi list

* Grant GPS access
  * User grants location (GPS)  access privileges
  * Steps:
    1. Open application
    2. (Pop-up appears)
    3. Click 'Allow'
  * App should now have access to the device's GPS coordinates

* Create GPS reminder
  * User creates a reminder, which will send a notification
  * Steps:
    1. Open application
    2. Click 'Create new reminder'
    3. Create title
    4. Click GPS based
    5. Enter location
    6. Click 'Create'
  * A new reminder should've been created
    * Also: Notification should be sent up on entering location

* Create WiFi reminder
  * User creates a reminder, which will send a notification
  * Steps:
    1. Open application
    2. Click 'Create new reminder'
    3. Create title
    4. Click WiFi based
    5. Chose a WiFi
    6. Click 'Create'
  * A new reminder should've been created
    * Also: Notification should be sent up on entering location

* Delete any reminder
  * User deletes a reminder
  * Steps:
    1. Open application
    2. Long-press any reminder
    3. Click delete icon
  * The reminder should no longer exist

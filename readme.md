# **myDrive: An App for Auto Synchronization on Google Drive**

## Problem Statement:
There is possibilies that electronic files/folders might be erase or misplaced from your device. So in this scenario portability is an issue. But we can save our important data on Google drive. So I have made an app to solve this issue.

## Overview of Solution:
I developed an App which will sync internal and external data with scheduled time and day on your device.The App has provision where user can select a specific folder or file and shedule a particular time & day to synchronize it over Google drive.
## Solution:
1) Google Sign In:
https://developers.google.com/identity/sign-in/android/start-integrating
Reffering to this documentation you can create Sign In method where user can choose one account from all accounts that have linked to his/her device's google account.
2) Enabling Drive Api: https://developers.google.com/drive/api/v3/quickstart/java
Using this website we can enable an API for Google drive. To enable this API we have to generate a SHA-1 Key using Android Studio.
3) Syncing Data:https://developer.android.com/training/sync-adapters/creating-sync-adapter?authuser=1 for syncing data to drive.
4) Schedule repeating method: https://developer.android.com/training/scheduling/alarms Reffering this documentation we made a schedule for uploding data to drive.
5) Background Processing: https://developer.android.com/guide/background
6) I enabled all required permission in manifest of Android studio.
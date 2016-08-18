# diuit.api.demo.android
This is an example for integration of Diuit API 

# Getting Started

Diuit provides a simple and powerful API to enable real-time communication in web and mobile apps, or any other Internet connected device.
This document provides a guide on how to get you start integrating and interacting with Diuit API.  

## Prerequisites

- We do not support Java outside of Android at the moment.
- A recent version of the Android SDK
- We support all Android versions since API Level 14 (Android 4.0 & above).

## Installation
You can use Maven to add library in your project.

**Maven**

1.  Navigate to your build.gradle file at the app level (not project level) and ensure that you include the following:

    ` maven { url "https://dl.bintray.com/duolc/maven"}`

2. Add `compile 'com.duolc.diuitapi:messageui:0.1.9'` to the dependencies of your project
3. In the Android Studio Menu: Tools -> Android -> Sync Project with Gradle Files


## Real-time Communication

## Authentication for Socket.IO

```
    //@param authToken, the token of the login device
    DiuitMessagingAPI.loginWithAuthToken(new DiuitMessagingAPICallback<JSONObject>()
    {
        @Override
        public void onSuccess(final JSONObject result)
        {
            // put your code
            //register receiving listener
            DiuitMessagingAPI.registerReceivingMessage(chatReceivingCallback);
        }
        @Override
        public void onFailure(final int code, final JSONObject result)
        {
            // put your code
        }
    }, authToken););
```


## Listing Chat Room

```
    // In Android, if you have already authenticated the user’s device, you can easily list all the chat room.

    DiuitMessagingAPI.listChats(new DiuitMessagingAPICallback<ArrayList<DiuitChat>>()
    {
        @Override
        public void onSuccess(final ArrayList<DiuitChat> chatArrayList)
        {
            // if success, return chatArrayList
        }

        @Override
        public void onFailure(final int code, final JSONObject resultObj)
        {
            // if failure, it will return error code and result
        }
    });

```





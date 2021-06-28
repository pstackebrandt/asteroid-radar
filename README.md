# Asteroid Radar

## Overview

Android App, coded with Kotlin

This app bases on a training project by Udacity/Google.
I extended the base code by Udacity heavily.

### Main features (implemented by Peter)
- Load asteroids data from NASA server
    - Preload data once a day if device is idle
    - Delete old NASA pictures of the day. Last image shouldn't be deleted.
- Load Nasa picture of the day
- App works offline
    - Hold data in database
- Filter asteroids
    - Start asteroid filters by use of overflow menu
- Show asteroid data in recycler view by LiveData and observers
- Opitimize screens for TalkBack

### More features (implemented by Peter)
- Show asteroids as list and in detail view
- Navigate from overview to detail view by use of Navigation component
- Parse data prepared by Moshi
- Use view model and repository
- Show always data fetched from database
- Use binding adapters to prepare view data
- Optimize UI (Theme, Styles)

### Usefull functionality NOT yet implemented:
- If the app runs initially and has no internet connection the database is empty and has no cached pictures. 
    - In this case app looks empty and should remark lack of internet. 
    - App should refresh automatically more often or has a button for refresh.
    - Picture of the day could use local data.
- Support second language

## Installation

To get the project running on your local machine, you need to follow these steps:

**Step 1: Clone the repository **

**Step 2: Check out the ‘master’ branch**

**Step 3: Open in Android studio **
    The project is in folder "asteroid-radar/radar/" !

## Getting Started (by Udacity)

Asteroid Radar is an app to view the asteroids detected by NASA that pass near Earth, you can view all the detected asteroids in a period of time, their data (Size, velocity, distance to Earth) and if they are potentially hazardous.

The app consists of two screens: A Main screen with a list of all the detected asteroids and a Details screen that is going to display the data of that asteroid once it´s selected in the Main screen list. The main screen will also show the NASA image of the day to make the app more striking.

This kind of app is one of the most usual in the real world, what you will learn by doing this are some of the most fundamental skills you need to know to work as a professional Android developer, as fetching data from the internet, saving data to a database, and display the data in a clear, clear, compelling UI.

### Screenshots (by Udacity)

![Screenshot 1](starter/screenshots/screen_1.png)

![Screenshot 2](starter/screenshots/screen_2.png)

![Screenshot 3](starter/screenshots/screen_3.png)

![Screenshot 4](starter/screenshots/screen_4.png)


## Project Instructions for the starter code (by udacity)

You will be provided with a starter code, which includes the necessary dependencies and plugins that you have been using along the courses and that you are going to need to complete this project. 

The most important dependencies we are using are:
- Retrofit to download the data from the Internet.
- Moshi to convert the JSON data we are downloading to usable data in form of custom classes.
- Glide to download and cache images.
- RecyclerView to display the asteroids in a list.

We recommend you following the guidelines seen in the courses, as well as using the components from the Jetpack library:
- ViewModel
- Room
- LiveData
- Data Binding
- Navigation

Android Studio could display a message to update Gradle plugin, or another thing like Kotlin, although it is recommended to have the last versions, it could be you have to do other things in order to make it work.

The application you will build must:
- Include Main screen with a list of clickable asteroids as seen in the provided design.
- Include a Details screen that displays the selected asteroid data once it’s clicked in the Main screen as seen in the provided design. The images in the details screen are going to be provided here, an image for a potentially hazardous asteroids and another one for the non potentially hazardous ones.
- Download and parse data from the NASA NeoWS (Near Earth Object Web Service) API.
- Save the selected asteroid data in the database using a button in details screen.
- Once you save an asteroid in the database, you should be able to display the list of asteroids from web or the database in the main screen top menu.
- Be able to cache the asteroids data by using a worker, so it downloads and saves week asteroids in background when device is charging and wifi is enabled.
- App works in multiple screen sizes and orientations, also it provides talk back and push button navigation.

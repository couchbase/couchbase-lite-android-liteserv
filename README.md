

LiteServAndroid is an HTTP (ReST) interface to the Couchbase-Lite database running on the device/emulator.  

Here's how it fits into the picture:

![architecture.png](http://cl.ly/image/3i400h2Z0f1f/lite-serv-android.png)

LiteServAndroid effectively _wraps_ CouchbaseLite and provides an API that can be accessed via HTTP.

LiteServAndroid is useful for:

* Using Couchbase-Lite inside of a LiteGap app (eg, a Couchbase-Lite app based on PhoneGap)
* Easy access to data in a Couchbase-Lite database via curl or httpie for debugging
* Testing 
* Doing _pull_ replications where something else is pulling data from Couchbase-Lite 

## Building LiteServAndroid

LiteServAndroid requires the code for Couchbase-Lite to be on the filesystem, and it builds the Couchbase-Lite code directly as part of the build process.  This enables easy debugging and hacking on Couchbase-Lite.  

* `git clone https://github.com/couchbaselabs/LiteServAndroid.git` to clone the git repo.

* `cd LiteServAndroid` 

* `cp local.properties.example local.properties` and customize local.properties to point to your SDK location if it is different than the default.

* `cd ..` followed by `git clone https://github.com/couchbase/couchbase-lite-android.git` to get the Couchbase-Lite code in the place expected by CouchChat.  Your directory structure should look like this:

```
.
|-- LiteServAndroid
|   |-- LiteServAndroid
|   |   |-- build.gradle
|   |   |-- libs
|   |   `-- ...
|   |-- README.md
|   |-- build.gradle
|   |-- gradle
|   |-- gradlew
|   `-- settings.gradle
`-- couchbase-lite-android
    |-- CouchbaseLiteProject
    |   |-- CBLite
    |   |-- ...
    `-- README.md
```

* `cd LiteServAndroid` followed by `./gradlew build` to build the code

## Running LiteServAndroid via Gradle command line

* Define an AVD (android virtual device) if you have not already done so.  This can be done by running `android avd` to launch the UI

* Launch the emulator with that AVD.  This can be done in the AVD manager UI, or on the command line via `emulator64-arm -avd <avd_name> -netspeed full -netdelay none` where `avd_name` is the name of the AVD you created in the previous step.

* Call `run_android_liteserv.sh 8080` to run it on port 8080.  This should install the app into the emulator and launch it, and then setup a port forwarding rule so that 8080 on your workstation is mapped to port 8080 on the emulator.


## Opening LiteServAndroid in Android Studio

* Run Android Studio

* Choose File / Open .. or Open Project if you are on the Welcome screen.

* Choose the top level LiteServAndroid directory (not the LiteServAndroid/LiteServAndroid subdirectory)

## Running LiteServAndroid via Android Studio

* Go to Tools / Run or Tools / Debug menu


## Enable browsing/debugging source code via Android Studio

See the instructions in the [CouchChat README](https://github.com/couchbaselabs/CouchChatAndroid), as they are nearly identical.




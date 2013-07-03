

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

* `cd CouchChatAndroid` 

* `cp local.properties.example local.properties` and customize local.properties to point to your SDK location if it is different than the default.

* `cd ..` followed by `git clone https://github.com/couchbase/couchbase-lite-android.git` to get the Couchbase-Lite code in the place expected by CouchChat.  Your directory structure should look like this:

```
.
|-- CouchChatAndroid
|   |-- CouchChatAndroid
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

* `cd CouchChatAndroid` followed by `./gradlew build` to build the code




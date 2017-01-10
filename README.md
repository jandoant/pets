Pets App
===================================

This app displays a list of pets and their related data that the user inputs.
Used in a Udacity course in the Android Basics Nanodegree by Google.

Pre-requisites
--------------

- Android SDK v24
- Android Build Tools v23.0.3
- Android Support Repository v24.1.1

Getting Started
---------------

This sample uses the Gradle build system. To build this project, use the
"gradlew build" command or use "Import Project" in Android Studio.

Support
-------

- Google+ Community: https://plus.google.com/communities/105153134372062985968
- Stack Overflow: http://stackoverflow.com/questions/tagged/android

Patches are encouraged, and may be submitted by forking this project and
submitting a pull request through GitHub. Please see CONTRIBUTING.md for more details.

License
-------

Copyright 2016 The Android Open Source Project, Inc.

Licensed to the Apache Software Foundation (ASF) under one or more contributor
license agreements.  See the NOTICE file distributed with this work for
additional information regarding copyright ownership.  The ASF licenses this
file to you under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License.  You may obtain a copy of
the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
License for the specific language governing permissions and limitations under
the License.


###v.01 - read from and write to database
* created Database Helper class
* adds Pet Object
* database info is shown in simple Text View

###v.02 read operation via Content Provider
* read all Pets provided through content provider instead ofdirect database access

###v.03 insert operation via Content Provider

###v.04 update operation via Content Provider

###v.05 delete operation via Content Provider

###v.06. Finished Content Provider Implementation

###v.07 Display Data in  List View
* create List View in Layout of CatalogActivity
* create List Item XML
* create EmptyView
* create Pet Cursor Adapter

###v.08 Using a Cursor Loader

###v.09 update Editor Activity to insert AND edit data
* depending on mode (insert/update):
  * display ActionBarTitle
  * prepopulate Edit Text Fields
  * insert on save / update on save

### v.10 fix some little bugs
  * when entering no weight value -> save 0kg
  * dont create new Pet when no Data is entered

### v.11 warn user about unsaved changes
   * Alert Dialog
   * on Back- an Up-Navigation
   * if user has clicked into any edit Field








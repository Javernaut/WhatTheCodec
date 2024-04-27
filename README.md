# WhatTheCodec

[![Build](https://github.com/Javernaut/WhatTheCodec/actions/workflows/Build.yml/badge.svg?branch=main)](https://github.com/Javernaut/WhatTheCodec/actions/workflows/Build.yml)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/db175d84403d4a76a77f09036f3ef913)](https://app.codacy.com/gh/Javernaut/WhatTheCodec/dashboard)
[![MIT license](http://img.shields.io/badge/license-MIT-blue.svg)](https://github.com/Javernaut/WhatTheCodec/blob/main/LICENSE.txt)
[![Android Weekly #378](https://androidweekly.net/issues/issue-378/badge)](https://androidweekly.net/issues/issue-378)
[![Android Weekly #396](https://androidweekly.net/issues/issue-396/badge)](https://androidweekly.net/issues/issue-396)

This app shows information about all media streams inside a video or audio files. It is available in these markets:

[<img src="images/badges/google.png" alt="Get it on Google Play" height="80">](https://play.google.com/store/apps/details?id=com.javernaut.whatthecodec)&nbsp;
[<img src="images/badges/amazon.png" alt="Available at Amazon Appstore" height="80">](http://www.amazon.com/gp/mas/dl/android?p=com.javernaut.whatthecodec.amzn)&nbsp;
[<img src="images/badges/huawei.png" alt="Explore it on AppGallery" height="80">](https://appgallery.cloud.huawei.com/marketshare/app/C102794269)

## Screenshots

<img src="images/screenshots.png" alt="Screenshot">

## Technical overview

The main purpose of the app is to show how to use the output of [ffmpeg-android-maker](https://github.com/Javernaut/ffmpeg-android-maker) by using its wrapped version - [MediaFile](https://github.com/Javernaut/MediaFile) library.

Main features:
- Single Activity with Jetpack Compose for the whole UI and Navigation
- Dagger/Hilt for DI
- DataStore for settings storing
- Dependabot and Github Actions for automating some housekeeping, static analysis, release management

## License

WhatTheCodec app's source code is available under the MIT license. See the [LICENSE.txt](https://github.com/Javernaut/WhatTheCodec/blob/main/LICENSE.txt) file for more details.

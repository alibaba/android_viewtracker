# ViewTracker-Android

## Overview
`ViewTracker` is a data collection library for click and exposure event in user interaction, based on the view event delegate and filter, used to insert statistical code tracelessly and noninvasively.

## Features
* Two platform support (iOS & Android);
* Collect click and exposure event tracelessly, noninvasively;
* Support multiple application scenarios（begin to scroll, end to scroll, auto scroll, window replace inside page, switch page in the TabActivity, enter into the next page, switch back and forth when press Home button);
* Caller can set the custom data commit method;
* Custom exposure event, including exposure time threshold and dimension threshold, support server configuration;
* Little impact on the frame FPS(Frame Per Second) performance;

## Get started

#### Import dependencies

use gradle:

```groovy
    compile('com.alibaba.android:android_viewtracker:1.0.0@aar')
```

use maven:

```xml
    <dependency>
        <groupId>com.alibaba.android</groupId>
        <artifactId>android_viewtracker</artifactId>
        <version>1.0.0</version>
        <type>aar</type>
    </dependency>
```

#### Init configuration when app start

```java
/**
 * init SDK
 *
 * @param mContext   global application
 * @param mTrackerOpen whether or not track click event
 * @param mTrackerExposureOpen whether or not track exposure event
 * @param printLog       whether or not print the log
 */
TrackerManager.getInstance().init(mContext, mTrackerOpen, mTrackerExposureOpen, printLog);
```

#### Dynamic configuration(optional)

JSON server configuration for click event:

```json
{
"masterSwitch": true, // whether or not track click event
"sampling":100
}
```
JSON server configuration for exposure event:

```json
{
"masterSwitch": true, // whether or not track exposure event
"timeThreshold": 100, // time threshold
"dimThreshold": 0.8, // dimension threshold
"exposureSampling": 100, // sampling frequency
"batchOpen":false // whether or not commit the exposure event log in batch or one by one
}
```
The app send a broadcast after pull configuration, internal receiver get it to modify configuration.

```java
JSONObject config = new JSONObject();
// get server configuration for click event
...
JSONObject exposureConfig = new JSONObject();
// get server configuration for exposure event
...
Intent intent = new Intent(ConfigReceiver.ACTION_CONFIG_CHANGED);
intent.putExtra(ConfigReceiver.VIEWTRACKER_CONFIG_KEY, config.toString());
intent.putExtra(ConfigReceiver.VIEWTRACKER_EXPOSURE_CONFIG_KEY, exposureConfig.toString());
context.sendBroadcast(intent);
```

#### Set common info inside page, including page name.(optional)
Generally call this in onReumse(),

```java
HashMap<String, String> args = new HashMap<String, String>();
// set page name
args.put(TrackerConstants.PAGE_NAME, pageName);
// set attached common info 
...
TrackerManager.getInstance().setCommonInfoMap(args);
```
 
#### Set commit method externally, implement IDataCommit interface.
```java
Class DataCommit implments IDataCommit {}
TrackerManager.getInstance().setCommit(new DataCommit());
```

#### Caller set tag for view.
* Only set view name for collected views.

```java
String viewName = "Button-1";
view.setTag(TrackerConstants.VIEW_TAG_UNIQUE_NAME, viewName);
```

* set attached info for collected views.

```java
HashMap<String, String> args = new HashMap<String, String>();
args.put(key, value);
...
view.setTag(TrackerConstants.VIEW_TAG_PARAM, args);
```

* Set like this if views inside page need to attach common info.

```java
HashMap<String, String> args = new HashMap<String, String>();
args.put(key, value);
...
getWindow().getDecorView().setTag(TrackerConstants.DECOR_VIEW_TAG_COMMON_INFO, args);
```

## [Principle](Docs/viewtracker_principle.md)

## [Performance](Docs/viewtracker_performance.md)

## Author
mengge,yihai,yuanxiu

## [License](LICENSE.txt)
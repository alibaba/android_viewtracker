ViewTracker-Android
=====

:book: English Documentation | [:book: Chinese Documentation](README-CN.md)

<!-- @import "[TOC]" {cmd="toc" depthFrom=1 depthTo=6 orderedList=false} -->
<!-- code_chunk_output -->

* [0 Abstract](#0-abstract)
	* [0.1 Functions](#01-functions)
	* [0.2 Design Principles](#02-design-principles)
	* [0.3 Basic Architecture](#03-basic-architecture)
	* [0.4 Flow Chart](#04-flow-chart)
* [1 Life-Cycle](#1-life-cycle)
	* [1.1 System Properties](#11-system-properties)
	* [1.2 View Properties](#12-view-properties)
* [2 Collection Specification](#2-collection-specification)
	* [2.1 The Specification Of Click Event](#21-the-specification-of-click-event)
	* [2.2 The Specification Of Exposure Event](#22-the-specification-of-exposure-event)
* [3 Developer Guide](#3-developer-guide)
	* [3.1 Dependency configuration](#31-dependency-configuration)
	* [3.2 Initialization](#32-initialization)
		* [3.2.1 Startup Initialization(required)](#321-startup-initializationrequired)
		* [3.2.2 Data Submission(optional)](#322-data-submissionoptional)
	* [3.3 Tag Binding](#33-tag-binding)
		* [3.3.1 The Tag Of Click & Exposure(required)](#331-the-tag-of-click-exposurerequired)
		* [3.3.2 The Tag Of Extended Information(optional)](#332-the-tag-of-extended-informationoptional)
		* [3.3.3 The Tag Of Page Common Information(optional)](#333-the-tag-of-page-common-informationoptional)
		* [3.3.4 The Tag Of Run-time Information(optional)](#334-the-tag-of-run-time-informationoptional)
* [4 Performance Testing](#4-performance-testing)
	* [4.1 The Goal Of Testing](#41-the-goal-of-testing)
	* [4.2 The Environment Of Testing](#42-the-environment-of-testing)
	* [4.3 The Results Of Testing](#43-the-results-of-testing)
* [5 Authors](#5-authors)
* [6 License](#6-license)
* [7 WeChat Group](#7-wechat-group)

<!-- /code_chunk_output -->

# 0 Abstract

`ViewTracker` is used to collect the mobile client of click and exposure event log automatically. It has been used in tmall App production environment from March 2016.

## 0.1 Functions

* Support `Android` & `iOS`([the github repo of `iOS` SDK](https://github.com/alibaba/TMViewTrackerSDK)).
* Support click & exposure events collection.
* Support the common information of page.
* Support multiple scenes, e.g. slides, automatic scrolling, window switch, tab switch, page jump, front and back switch.
* Support extension, e.g. data submission, customization for the exposure rules, sampling rate, etc.

## 0.2 Design Principles

* Keep It Simple, Stupid(`KISS`) Principle: Avoid code complexity, class name, package name etc have good readability.
* Single Responsibility Principle(`SRP`): A piece of code function that explicitly performs a single task, such as click and exposure.
* Open/Closed Principle(`OCP`): Maximize extensibility for user, such as you can replace the implementation of `IDataCommit` interface  data submission; providing receiver of dynamic config, you can integrate with your own config system.

## 0.3 Basic Architecture

![](viewtracker/img/viewtracker-Architecture.png)

## 0.4 Flow Chart

![](viewtracker/img/viewtracker-workflow.png)

# 1 Life-Cycle

## 1.1 System Properties

* Application `onCreate`
* FrameLayout `onLayout`/`onFling`/`disPatchWindowFocusChanged`/`dispatchVisibilityChanged`
* GestureDetector `onGestureListener`

## 1.2 View Properties

* View `accessibilityDelegate`

# 2 Collection Specification

Data collection is closely related to data analysis statistics, the collection Specification of different events is critical.

## 2.1 The Specification Of Click Event

* EventId: `2101`
* ControlName: `button-1`
* args: `key1=value,key2=value`

## 2.2 The Specification Of Exposure Event

* EventId: `2201`
* ControlName: `button-1`
* exposureTime: `500`
* args: `exposureIndex=1,key1=value`

# 3 Developer Guide

## 3.1 Dependency configuration

To add a dependency using `Gradle`:

```groovy
compile('com.tmall.android:viewtracker:1.0.0@aar')
```

## 3.2 Initialization

### 3.2.1 Startup Initialization(required)

When the application starts, call the following:

```java
/**
 * SDK initilization
 *
 * @param mContext              global context
 * @param mTrackerOpen          click event switch,yes means open
 * @param mTrackerExposureOpen  exposure event switch,yes means open
 * @param printLog              debug log switch,yes means open
 */
TrackerManager.getInstance().init(mContext, mTrackerOpen, mTrackerExposureOpen, printLog);
```

### 3.2.2 Data Submission(optional)

Implement the IDataCommit interface to set up data submission.

```java
Class DataCommit implments IDataCommit {
	...
	// your implementation here
	...
}
TrackerManager.getInstance().setCommit(new DataCommit());
```

**CAUTION**:

You should implement the interface `IDataCommit` in production environment, because the collected log data need be saved on your own server-side.

## 3.3 Tag Binding

Tag needs to be bound to the view that needs to be collected. Look at the following situations.

### 3.3.1 The Tag Of Click & Exposure(required)

```java
String viewName = "button-1";
view.setTag(TrackerConstants.VIEW_TAG_UNIQUE_NAME, viewName);
```

### 3.3.2 The Tag Of Extended Information(optional)

The extended information is bound to the view

```java
HashMap<String, String> args = new HashMap<String, String>();
args.put(key, value);
...
view.setTag(TrackerConstants.VIEW_TAG_PARAM, args);
```

### 3.3.3 The Tag Of Page Common Information(optional)

All the views of the page are required to be reported,you can look at the following code.

```java
HashMap<String, String> args = new HashMap<String, String>();
args.put(key, value);
...
getWindow().getDecorView().setTag(TrackerConstants.DECOR_VIEW_TAG_COMMON_INFO, args);
```

### 3.3.4 The Tag Of Run-time Information(optional)

The server-side click event configuration `JSON` format is as follows:

```js
{
    "masterSwitch": true, // Click event switch
    "sampling":100 // Click event sampling rate
}
```

The server-side exposure event configuration `JSON` format is as follows:

```js
{
    "masterSwitch": true, // exposure event switch
    "timeThreshold": 100, // Valid exposure time threshold
    "dimThreshold": 0.8, // Valid exposure area threshold
    "exposureSampling": 100, // Exposure event sampling rate
    "batchOpen":false //Batch report switch
}
```

After receiving the server configuration, the application sends the broadcast, and the SDK can receive and dynamically modify the configuration.

```java
//get click config from server-side
JSONObject config = new JSONObject();

// get exposure config from server-side
...
JSONObject exposureConfig = new JSONObject();

// send broadcast
...
Intent intent = new Intent(ConfigReceiver.ACTION_CONFIG_CHANGED);
intent.putExtra(ConfigReceiver.VIEWTRACKER_CONFIG_KEY, config.toString());
intent.putExtra(ConfigReceiver.VIEWTRACKER_EXPOSURE_CONFIG_KEY, exposureConfig.toString());
context.sendBroadcast(intent);
```

# 4 Performance Testing

As each page has attached a `TrackerFrameLayout`, in the UI main thread of the event processing methods have exposure time calculation operation, may have a certain impact on the page fluency, so the need for performance testing.

## 4.1 The Goal Of Testing

The test the buried two ways of uses the `viewtracker` collection and the original code to submit for the FPS effect.

## 4.2 The Environment Of Testing

* Phone Model: xiaomi2
* System Version: `Android 5.0`
* App: Tmall Android
* App Version: `5.32.0`
* Page: `HomePage`

## 4.3 The Results Of Testing

Index          | Traditional Code    | use `ViewTracker`
---------      | ------------------- | -------------
Number Of Test | 20                  | 20
MAX_FPS        | 60                  | 63
MIN_FPS        | 50                  | 47
AVG_FPS        | 54.81               | 53.90

Result: `ViewTracker`has no significant effect on `FPS`.

# 5 Authors

- [@yihai](https://github.com/deanhust) lizhiyonghust \<at> gmail \<dot> com
- [@mengge](https://github.com/denneyliu) lmaz \<at> 163 \<dot> com
- @yuanxiu

# 6 License

`ViewTracker` is available under the Apache License 2.0. See the [LICENSE](https://github.com/alibaba/android_viewtracker/blob/master/LICENSE.txt) file for more info.

# 7 WeChat Group

Because the QR code of WeChat Group is valid for a short period , you can join us by search `Sunshine07de` in WeChat.

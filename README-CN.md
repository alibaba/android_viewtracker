

ViewTracker-Android
======

[:book: English Documentation](README.md) | :book: 中文文档
<!-- @import "[TOC]" {cmd="toc" depthFrom=1 depthTo=6 orderedList=false} -->
<!-- code_chunk_output -->

* [0 概述](#0-概述)
	* [0.1 功能特性](#01-功能特性)
	* [0.2 设计原则](#02-设计原则)
	* [0.3 整体架构](#03-整体架构)
	* [0.4 工作流程](#04-工作流程)
* [1 生命周期](#1-生命周期)
	* [1.1 系统属性](#11-系统属性)
	* [1.2 View属性](#12-view属性)
* [3 采集规范](#3-采集规范)
	* [3.1 点击事件规范](#31-点击事件规范)
	* [3.2 曝光事件规范](#32-曝光事件规范)
* [4 开发接入](#4-开发接入)
	* [4.1 依赖配置](#41-依赖配置)
	* [4.2 应用启动时初始化配置](#42-应用启动时初始化配置)
		* [4.2.1 启动初始化(必选)](#421-启动初始化必选)
		* [4.2.2 数据提交方式注入(可选)](#422-数据提交方式注入可选)
	* [4.3 标记绑定](#43-标记绑定)
		* [4.3.1 点击&曝光标记绑定(必选)](#431-点击曝光标记绑定必选)
		* [4.3.2 附加信息绑定(可选)](#432-附加信息绑定可选)
		* [4.3.3 页面公共信息绑定(可选)](#433-页面公共信息绑定可选)
		* [4.3.4 运行时信息设置方式（可选）](#434-运行时信息设置方式可选)
* [5 性能测试](#5-性能测试)
	* [5.1 性能测试目标](#51-性能测试目标)
	* [5.2 性能测试环境](#52-性能测试环境)
	* [5.3 性能测试结果](#53-性能测试结果)
* [6 开发作者](#6-开发作者)
* [7 许可证](#7-许可证licensetxt)
* [8 微信交流群](#8-微信交流群)

<!-- /code_chunk_output -->

## 0 概述

`ViewTracker`是用于自动化的采集用户UI交互过程中的点击和曝光事件，基于view事件代理及过滤的数据采集库;

### 0.1 功能特性

  * 支持两个平台 (IOS & Android）
  * 支持点击、曝光、页面公共信息
  * 支持多个场景：列表滑动，列表自动滚动，页面内Window切换，Tab页切换，进入下一个页面，应用前后台切换
  * 支持扩展：数据提交、曝光规则自定义(时间阈值和宽高阈值)、采样率定义

### 0.2 设计原则

  * 简单原则:避免代码复杂化，类名、包名等都语义化，可读性好；
  * 单一责任原则:某块代码功能，明确执行单一任务，如Click与Expourse区分；
  * 开闭原则:最大化支持用户扩展开发，如数据提交DataCommitImpl接口定义，动态配置监控的receiver提供；

### 0.3 整体架构

  ![](viewtracker/img/viewtracker-Architecture.png)  

### 0.4 工作流程

  ![](viewtracker/img/viewtracker-workflow.png)  

## 1 生命周期

### 1.1 系统属性

  * Application onCreate
  * FrameLayout onLayout onFling disPatchWindowFocusChanged dispatchVisibilityChanged
  * GestureDetector onGestureListener

### 1.2 View属性

  * View AccessibilityDelegate

## 3 采集规范

  数据采集与后续的数据分析统计息息相关，不同事件的采集规范的约定至关重要。

### 3.1 点击事件规范
  EventId:2101
  ControlName:icon1
  args:key1=value,key2=value
### 3.2 曝光事件规范
  EventId:2201
  ControlName:icon1
  exposureTime:500
  args:exposureIndex=1,key1=value

## 4 开发接入

### 4.1 依赖配置

使用`gradle`:

```groovy
compile('com.tmall.android:viewtracker:1.0.0@aar')
```

### 4.2 应用启动时初始化配置
#### 4.2.1 启动初始化(必选)
  在应用启动时，调用如下代码：
```java
/**
 * SDK的初始化
 *
 * @param mContext   全局的application
 * @param mTrackerOpen 是否开启无痕点击埋点
 * @param mTrackerExposureOpen 是否开启无痕曝光埋点
 * @param printLog       是否输出调试log
 */
TrackerManager.getInstance().init(mContext, mTrackerOpen, mTrackerExposureOpen, printLog);
```

#### 4.2.2 数据提交方式注入(可选)
   外部注入提交方式，实现IDataCommit接口

```java
Class DataCommit implments IDataCommit {}
TrackerManager.getInstance().setCommit(new DataCommit());
```

### 4.3 标记绑定
    业务方对于需要采集行为的view上绑定tag，分为以下几种场景；

#### 4.3.1 点击&曝光标记绑定(必选)
    对于需要埋点的view，仅需要绑定view埋点名称

```java
String viewName = "Button-1";
view.setTag(TrackerConstants.VIEW_TAG_UNIQUE_NAME, viewName);
```
#### 4.3.2 附加信息绑定(可选)
    需要埋点的view还可以绑定附加扩展信息

```java
HashMap<String, String> args = new HashMap<String, String>();
args.put(key, value);
...
view.setTag(TrackerConstants.VIEW_TAG_PARAM, args);
```

#### 4.3.3 页面公共信息绑定(可选)
* 如果需要同一页面中的所有view上报都需要带上的信息，可以这样绑定(如果需要)

```java
HashMap<String, String> args = new HashMap<String, String>();
args.put(key, value);
...
getWindow().getDecorView().setTag(TrackerConstants.DECOR_VIEW_TAG_COMMON_INFO, args);
```
#### 4.3.4 运行时信息设置方式（可选）

服务端配置无痕点击`JSON`配置格式如下：

```js
{
    "masterSwitch": true, // 是否打开无痕点击事件上报
    "sampling":100 // 点击采样率
}
```
服务端配置无痕曝光JSON配置格式如下：

```js
{
    "masterSwitch": true, // 是否打开曝光事件上报
    "timeThreshold": 100, // view曝光时长阈值
    "dimThreshold": 0.8, // view曝光宽高阈值
    "exposureSampling": 100, // 曝光采样率
    "batchOpen":false // 是否打开批量上报，即页面离开时，所有view上报一次曝光总时长
}
```

应用接收到服务端配置后，发送广播，SDK内部可以接收到，动态修改配置。
```java
JSONObject config = new JSONObject();

// 获取服务端无痕点击配置
...
JSONObject exposureConfig = new JSONObject();

// 获取服务端无痕曝光配置
...
Intent intent = new Intent(ConfigReceiver.ACTION_CONFIG_CHANGED);
intent.putExtra(ConfigReceiver.VIEWTRACKER_CONFIG_KEY, config.toString());
intent.putExtra(ConfigReceiver.VIEWTRACKER_EXPOSURE_CONFIG_KEY, exposureConfig.toString());
context.sendBroadcast(intent);
```

## 5 性能测试
  由于每一个本地页面都attach了一个`TrackerFrameLayout`，在UI主线程的事件处理方法中有曝光时间的计算操作，可能会对页面流畅度有一定影响，所以需要进行性能测试。

### 5.1 性能测试目标
  帧率，监测使用viewtracker采集和原始代码提交埋点方式是否FPS有差别。

### 5.2 性能测试环境
  手机型号：小米2   
  系统版本：Android 5.0
  APP版本：天猫Android 5.32.0
  页面：首页

### 5.3 性能测试结果
类目      | 未使用ViewTracker | 使用ViewTracker
---------|-------------------|-------------
测试次数  | 20   | 20
MAX_FPS  | 60   | 63
MIN_FPS  | 50   | 47
AVG_FPS  |54.81 |53.90
测试数据来看，使用该无痕采集方式与传统代买提交方式FPS上无明显影响。

## 6 开发作者

- [意海](https://github.com/deanhust) lizhiyonghust@gmail.com
- [蒙戈](https://github.com/denneyliu)
lmaz@163.com
- 元休

## 7 [许可证](LICENSE.txt)
  `Viewtracker`遵循Apache License 2.0协议，查看更多[协议](https://github.com/alibaba/android_viewtracker/blob/master/LICENSE.txt)信息。

## 8 微信交流群
- 微信群二维码容易过期，可在微信中搜索Sunshine07de  

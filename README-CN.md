# ViewTracker-Android

## 概述
`ViewTracker`是用于自动化的采集用户UI交互过程中的点击和曝光事件，基于view事件代理及过滤的数据采集库，对业务层无痕、无侵入埋点。

## 特性
* 支持两个平台 (iOS & Android）；
* 点击和曝光事件的无痕采集；
* 支持多种应用场景（列表滑动，列表自动滚动，页面内Window切换，Tab页切换，进入下一个页面，应用前后台切换）；
* 调用方可以设置采集数据的提交方式；
* 可自定义业务层曝光事件，包括曝光时间阈值和宽高阈值，支持服务端配置；
* 对页面帧率性能影响小；

## 使用方式

#### 引入依赖

使用gradle:

```groovy
    compile('com.tmall.android:tmallandroid_viewtracker:1.0.11.55@aar')
```

使用maven:

```xml
    <dependency>
        <groupId>com.tmall.android</groupId>
        <artifactId>tmallandroid_viewtracker</artifactId>
        <version>1.0.11.55</version>
        <type>aar</type>
    </dependency>
```

#### 应用启动时初始化配置

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

#### 动态修改配置（可选）

服务端配置无痕点击JSON配置格式如下：

```json
{
"masterSwitch": true, // 是否打开无痕点击事件上报
"sampling":100 // 点击采样率
}
```
服务端配置无痕曝光JSON配置格式如下：

```json
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

#### 设置页面通用信息，包含页面名称等（可选）
一般在页面内onResume方法中调用
```java
HashMap<String, String> args = new HashMap<String, String>();
// 设置页面名称
args.put(TrackerConstants.PAGE_NAME, pageName);
// 设置附带的页面通用信息
...
TrackerManager.getInstance().setCommonInfoMap(args);
```
 
#### 外部注入提交方式，实现IDataCommit接口
```java
Class DataCommit implments IDataCommit {}
TrackerManager.getInstance().setCommit(new DataCommit());
```

#### 业务方为view设置Tag
* 对于需要埋点的view，仅需要设置view埋点名称；

```java
String viewName = "Button-1";
view.setTag(TrackerConstants.VIEW_TAG_UNIQUE_NAME, viewName);
```

* 需要埋点的view还可以设置附加信息；

```java
HashMap<String, String> args = new HashMap<String, String>();
args.put(key, value);
...
view.setTag(TrackerConstants.VIEW_TAG_PARAM, args);
```

* 如果需要同一页面中view上报事件都带上通用信息，可以这样设置；

```java
HashMap<String, String> args = new HashMap<String, String>();
args.put(key, value);
...
getWindow().getDecorView().setTag(TrackerConstants.DECOR_VIEW_TAG_COMMON_INFO, args);
```

## [实现原理](Docs/viewtracker_principle_CN.md)

## [性能测试](Docs/viewtracker_performance_CN.md)

## 作者
蒙戈，意海，元休

## [许可证](LICENSE.txt)
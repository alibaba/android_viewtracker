## 支持事件

1. 点击事件：Android系统定义，用户触摸屏幕，产生了MotionEvent.ACTION_DOWN与MotionEvent.ACTION_UP。
2. 曝光事件：应用层定义，当view在屏幕上出现宽、高都大于view自身宽、高的80%，且停留时间超过100ms时，认为是一次有效曝光；其中时间阈值(100ms)和宽高阈值(0.8)可以自行设置。

## 实现方案
### 前提
1. APP启动时，初始化SDK，在Application.ActivityLifecycleCallbacks的回调方法onActivityResumed()里attach一个自定义的TrackerFrameLayout，这个FrameLayout再add页面的contentView，这样本地view的Touch事件，窗口变化都会通过这个父容器TrackerFrameLayout来传递。
2. 在回调方法onActivityDestroyed()里remove掉TrackerFrameLayout。

### 触发点击事件
1. 在TrackerFrameLayout中dispatchTouchEvent()中接收Touch事件。
2. 根据MotionEvent的X、Y坐标找到被点击的且带Tag的view，设置AccessibilityDelegate。
3. 判断eventType是否是点击事件，提交数据。

* 流程图如下：

![](click_event_flow_chart.jpg)

### 触发曝光事件
1. 在TrackerFrameLayout中遍历view树，保存带Tag的View，需要在业务侧代码中为需要统计的View自己设置Tag。
2. 在APP的各种应用场景下，触发曝光计算，统计各个view的开始曝光时间和结束曝光时间，以出现在屏幕上为准，计算出每一小段曝光时间和曝光次数，提交数据。

* 流程图如下：

![](exposure_event_flow_chart.jpg)

### 曝光事件支持的应用场景
1. 窗口级别变化引起的整体view结束曝光，如：按HOME键应用前后台切换，同一页面AB页切换，tab页切换，进入下一个页面。
2. view级别变化引起的结束曝光，如列表滑动，banner自动滚动，且支持列表复用。
3. 暂不支持Dialog，PopupWindow等view覆盖的场景。

## 基本架构

![](architecture.jpg)

* 其中调用方使用TrackerConstants中的key为需要采集数据的view设置tag，并实现IDataCommit接口来提交数据。

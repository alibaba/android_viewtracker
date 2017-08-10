## 原因
由于每一个本地页面都attach了一个TrackerFrameLayout，在UI主线程的事件处理方法中有曝光时间的计算操作，可能会对页面流畅度有一定影响，所以需要进行性能测试。

## 测试场景
手机型号：小米2 <br>
系统版本：Android 5.0 <br>
APP版本：天猫Android 5.32.0 <br>
页面：首页

## 测试指标
#### 帧率
使用monkeyrunner执行Python脚本（Docs/scrolltest.zip），首页上下各滑动20次的平均帧率（FPS） 不使用ViewTracker VS 使用ViewTracker：

不使用ViewTracker Avg_FPS | 使用ViewTracker Avg_FPS
-------------|-------------
59           | 59
56           | 57
53           | 53
56           | 54
50           | 56
59           | 54
52           | 49
57           | 52
50           | 51
55           | 55
56           | 56
51           | 54
53           | 53
52           | 47
57           | 47
55           | 58
54           | 53
57           | 55
53           | 53
56           | 53
60           | 63

## Reason
As each native page attachs a `TrackerFrameLayout `, and that exposure calculation process on UI main thread, may have impact on the FPS performance.

## Test scene
Phone model: Xiaomi 2 <br>
System version: Android 5.0 <br>
APP version: Tmall Android 5.32.0 <br>
Page: Homepage

## Test index
#### FPS
Use `monkeyrunner` to execute Python scripts（Docs/scrolltest.zip）, scroll homepage top and bottom 20 times. Average FPS not use `ViewTracker` VS use `ViewTracker`:

not use `ViewTracker` Avg_FPS | use `ViewTracker` Avg_FPS
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

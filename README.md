# YtDlp-kt
[![](https://jitpack.io/v/dinaraparanid/YtDlp-kt.svg)](https://jitpack.io/#dinaraparanid/YtDlp-kt)

This library is a kotlin wrapper for [YtDlp](https://github.com/yt-dlp/yt-dlp) command line project.

### Setup

To integrate a library into your *build.gradle*:
1. Add the JitPack repository to your build file

```groovy
allprojects {
    repositories {
        // ...
        maven { url 'https://jitpack.io' }
    }
}
```

2. Add the dependency

```groovy
dependencies {
    implementation 'com.github.dinaraparanid:YtDlp-kt:v1.0.0.0'
}
```

For other configuration go [here](https://jitpack.io/#dinaraparanid/YtDlp-kt)

### Example

```kotlin
import com.dinaraparanid.ytdlp_kt.YtDlp
import com.dinaraparanid.ytdlp_kt.YtDlpRequest

suspend fun main() {
    YtDlp.updateAsync().join() // or YtDlp.update()

    val videoUrl = "https://www.youtube.com/watch?v=K0HSD_i2DvA"

    val request = YtDlpRequest(videoUrl).apply {
        setOption("--audio-format", "mp3")
        setOption("--socket-timeout", "1")
        setOption("--retries", "infinite")
        setOption("--extract-audio")
        setOption("--format", "best")
    }

    assert(YtDlp.execute(request) == YtDlp.executeAsync(request).await())
    assert(YtDlp.getVideoData(videoUrl) == YtDlp.getVideoDataAsync(videoUrl).await())
}
```

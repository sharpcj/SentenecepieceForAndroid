# TCL SentencePiece Android Library

这是一个基于 Google SentencePiece 的 Android 原生库，提供了完整的文本编码和解码功能。

## 功能特性

- ✅ 加载 SentencePiece 模型文件
- ✅ 文本编码为字符串数组 (tokens)
- ✅ 文本编码为整数 ID 数组 (token IDs)
- ✅ 字符串数组解码为文本
- ✅ 整数 ID 数组解码为文本
- ✅ 资源释放管理
- ✅ 支持 arm64-v8a 架构

## API 接口

### SpmNative

```kotlin
object SpmNative {
    // 加载模型
    external fun loadModel(path: String): Boolean
    
    // 编码接口
    external fun encode(text: String): Array<String>
    external fun encodeToIds(text: String): IntArray
    
    // 解码接口
    external fun decode(pieces: Array<String>): String
    external fun decodeFromIds(ids: IntArray): String
    
    // 资源管理
    external fun release()
}
```

## 使用示例

```kotlin
import com.tcl.sentenecepiece.SpmNative

class Example {
    fun demonstrateUsage() {
        // 1. 加载模型
        val success = SpmNative.loadModel("/path/to/model.model")
        if (!success) {
            Log.e("SpmNative", "模型加载失败")
            return
        }
        
        // 2. 文本编码
        val text = "Hello World"
        val tokens = SpmNative.encode(text)        // ["Hello", "World"]
        val ids = SpmNative.encodeToIds(text)      // [15496, 2159]
        
        // 3. 解码验证
        val decodedFromTokens = SpmNative.decode(tokens)
        val decodedFromIds = SpmNative.decodeFromIds(ids)
        
        Log.i("SpmNative", "原始文本: $text")
        Log.i("SpmNative", "编码 tokens: ${tokens.toList()}")
        Log.i("SpmNative", "编码 IDs: ${ids.toList()}")
        Log.i("SpmNative", "解码结果: $decodedFromTokens")
        
        // 4. 释放资源
        SpmNative.release()
    }
}
```

## 集成方式

### 1. 添加依赖

在 `app/build.gradle.kts` 中：

```kotlin
dependencies {
    implementation(project(":tcl-sentenecepiece"))
}
```

### 2. 模型文件

将 SentencePiece 模型文件（`.model` 格式）放置在应用的 assets 目录或文件系统中。

### 3. 权限

确保应用有读取模型文件的权限。

## 注意事项

1. **模型文件格式**：支持标准的 SentencePiece `.model` 文件
2. **架构支持**：目前仅支持 `arm64-v8a` 架构
3. **内存管理**：使用完毕后调用 `release()` 方法释放资源
4. **线程安全**：当前实现不是线程安全的，建议在单线程中使用

## 构建要求

- Android NDK 27.0.12077973+
- CMake 3.22.1+
- Kotlin 2.0.21+
- Android Gradle Plugin 8.13.0+

## 许可证

基于 Apache License 2.0 开源协议。

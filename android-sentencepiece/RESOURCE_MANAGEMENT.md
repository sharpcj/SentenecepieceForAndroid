# SentencePiece 资源管理说明

## 资源管理机制

### RAII 模式
Google SentencePiece 使用 RAII（Resource Acquisition Is Initialization）模式进行资源管理：

```cpp
class SentencePieceProcessor {
private:
  std::unique_ptr<ModelInterface> model_;
  std::unique_ptr<normalizer::Normalizer> normalizer_;
  std::unique_ptr<normalizer::Normalizer> denormalizer_;
  std::unique_ptr<ModelProto> model_proto_;
};
```

### 自动资源释放
- 所有资源都通过 `std::unique_ptr` 智能指针管理
- 当 `SentencePieceProcessor` 对象被销毁时，智能指针会自动释放资源
- 构造函数和析构函数都是空的：`{}`，依赖智能指针的自动管理

## JNI 实现

### 全局处理器实例
```cpp
static sentencepiece::SentencePieceProcessor g_processor;
```

### 资源释放方法
```cpp
extern "C" JNIEXPORT void JNICALL
Java_com_tcl_sentenecepiece_SpmNative_release(JNIEnv* /*env*/, jobject /*thiz*/) {
    // SentencePiece 使用 RAII 模式，通过 std::unique_ptr 自动管理资源
    // 重置处理器会触发智能指针的析构，自动释放所有资源
    g_processor = sentencepiece::SentencePieceProcessor();
}
```

## 工作原理

1. **加载模型时**：
   - `Load()` 方法创建新的 `ModelInterface`、`Normalizer` 等对象
   - 这些对象被包装在 `std::unique_ptr` 中

2. **释放资源时**：
   - 通过赋值操作 `g_processor = sentencepiece::SentencePieceProcessor()`
   - 创建新的空处理器实例
   - 旧的处理器实例被销毁，触发智能指针析构
   - 所有资源（模型数据、规范化器等）自动释放

3. **内存安全**：
   - 使用智能指针确保没有内存泄漏
   - 异常安全：即使发生异常，智能指针也会正确释放资源

## 使用建议

### 何时调用 release()
- 应用退出前
- 需要释放大量内存时
- 切换不同的模型时
- 长时间不使用时

### 调用 release() 后的行为
- 所有已加载的模型数据被释放
- 处理器回到初始状态
- 可以重新调用 `loadModel()` 加载新模型

### 示例用法
```kotlin
// 加载模型
val success = SpmNative.loadModel("model.model")

// 使用模型
val tokens = SpmNative.encode("Hello World")

// 释放资源
SpmNative.release()

// 重新加载
val reloadSuccess = SpmNative.loadModel("new_model.model")
```

## 注意事项

1. **线程安全**：当前实现不是线程安全的
2. **异常处理**：release() 方法不会抛出异常
3. **性能影响**：频繁调用 release() 和 loadModel() 可能影响性能
4. **内存占用**：模型加载后会占用一定内存，release() 可以释放这些内存

## 验证方法

可以通过以下方式验证资源是否正确释放：

1. **重新加载测试**：调用 release() 后重新加载模型
2. **内存监控**：观察应用内存使用情况
3. **功能测试**：确保释放后重新加载的功能正常

# SentencePiece Android 测试应用

这是一个用于测试 `android-sentenecepiece` library 的 Android 应用。

## 功能特性

- ✅ 测试 SentencePiece 模型加载
- ✅ 测试文本编码为字符串数组 (tokens)
- ✅ 测试文本编码为整数 ID 数组 (token IDs)
- ✅ 测试字符串数组解码为文本
- ✅ 测试整数 ID 数组解码为文本
- ✅ 测试编码解码一致性
- ✅ 测试边界情况（空字符串、空数组）
- ✅ 测试资源释放

## 测试内容

### 1. 多语言文本测试
- 英文: "Hello World"
- 中文: "这是一个测试"
- 混合: "SentencePiece is awesome!"
- 中文标点: "你好，世界！"

### 2. 编码解码测试
- 文本 → tokens → 文本
- 文本 → IDs → 文本
- 一致性验证

### 3. 边界情况测试
- 空字符串编码
- 空数组解码
- 异常处理

## 使用方法

1. **准备模型文件**
   - 将 SentencePiece 模型文件（`.model` 格式）命名为 `sentencepiece.bpe.model`
   - 放置在应用的 `assets` 目录中

2. **运行测试**
   - 启动应用
   - 点击"测试 SentencePiece 编码解码"按钮
   - 查看 Logcat 输出（标签: `sharpcj`）

3. **查看结果**
   ```
   I/sharpcj: === SentencePiece 测试开始 ===
   I/sharpcj: 模型文件已从assets复制到: /data/user/0/com.sharpcj.sentenecepiece/files/sentencepiece.bpe.model
   I/sharpcj: 模型加载成功: /data/user/0/com.sharpcj.sentenecepiece/files/sentencepiece.bpe.model

   I/sharpcj: 
   --- 测试文本: Hello World ---
   I/sharpcj: 编码为 tokens: [Hello, World]
   I/sharpcj: 编码为 IDs: [15496, 2159]
   I/sharpcj: 从 tokens 解码: Hello World
   I/sharpcj: 从 IDs 解码: Hello World
   I/sharpcj: tokens 编码解码一致性: true
   I/sharpcj: IDs 编码解码一致性: true

   I/sharpcj: 
   --- 测试文本: 这是一个测试 ---
   I/sharpcj: 编码为 tokens: [这是, 一个, 测试]
   I/sharpcj: 编码为 IDs: [3174, 1358, 2065]
   I/sharpcj: 从 tokens 解码: 这是一个测试
   I/sharpcj: 从 IDs 解码: 这是一个测试
   I/sharpcj: tokens 编码解码一致性: true
   I/sharpcj: IDs 编码解码一致性: true

   I/sharpcj: 
   --- 边界情况测试 ---
   I/sharpcj: 空字符串 tokens: []
   I/sharpcj: 空字符串 IDs: []
   I/sharpcj: 空数组解码结果: ''
   I/sharpcj: 空数组解码结果: ''

   I/sharpcj: 准备释放资源...
   I/sharpcj: 资源已释放
   I/sharpcj: 测试释放后重新加载...
   I/sharpcj: 模型加载成功: /data/user/0/com.sharpcj.sentenecepiece/files/sentencepiece.bpe.model
   I/sharpcj: 重新加载成功
   I/sharpcj: 重新加载后编码测试: [Test, after, reload]
   I/sharpcj: === SentencePiece 测试完成 ===
   ```

## 注意事项

1. **模型文件**: 确保模型文件存在且格式正确
2. **权限**: 应用需要读取文件权限
3. **架构**: 目前仅支持 `arm64-v8a` 架构
4. **日志**: 所有测试结果都会输出到 Logcat

## 故障排除

### 模型加载失败
- 检查模型文件路径是否正确
- 确认模型文件格式是否为 `.model`
- 验证文件权限

### 编码解码不一致
- 检查模型文件是否损坏
- 确认模型类型是否匹配
- 查看是否有特殊字符处理问题

### 应用崩溃
- 检查 Logcat 中的异常信息
- 确认 native 库是否正确加载
- 验证 JNI 接口是否匹配

## 构建和运行

### 环境要求
- Android Studio 版本 2024.2 或更高
- JDK 17+
- Android SDK 36
- NDK 27.0.12077973+

### 构建项目
```bash
./gradlew build
```

### 安装到设备
```bash
./gradlew installDebug
```

### 项目结构
```
app/
├── src/
│   ├── main/
│   │   ├── java/com/sharpcj/sentenecepiece/
│   │   │   └── MainActivity.kt  # 主要的测试界面和逻辑
│   │   └── res/                 # 应用资源文件
│   └── test/                    # 单元测试
└── README.md
```
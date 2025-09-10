package com.sharpcj.sentenecepiece

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.io.File
import com.sharpcj.sentenecepiece.SpmNative
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SentencePieceTheme {
                MainScreen(onClick = {
                    lifecycleScope.launch {
                        testSentencePiece()
                    }
                })
            }
        }
    }
    
    private suspend fun testSentencePiece() {
        try {
            Log.i("sharpcj", "=== SentencePiece 测试开始 ===")
            
            // 测试模型加载
            // val modelPath = File(filesDir, "sentencepiece.bpe.model").absolutePath
            // Log.i("sharpcj", "尝试加载模型: $modelPath")
            //
            // val success = SpmNative.loadModel(modelPath)
            // if (!success) {
            //     Log.e("sharpcj", "模型加载失败，请确保模型文件存在")
            //     return
            // }
            //
            // Log.i("sharpcj", "模型加载成功")

            val success = loadModelFromAssets("sentencepiece.bpe.model", "sentencepiece.bpe.model")
            if (!success) {
                Log.e("sharpcj", "模型加载失败，请确保assets目录中存在模型文件")
                return
            }


            // 测试文本
            val testTexts = listOf(
                "Hello World",
                "这是一个测试",
                "SentencePiece is awesome!",
                "你好，世界！"
            )
            
            for (text in testTexts) {
                Log.i("sharpcj", "\n--- 测试文本: $text ---")
                
                // 1. 编码测试
                val tokens = SpmNative.encode(text)
                val ids = SpmNative.encodeToIds(text)
                
                Log.i("sharpcj", "编码为 tokens: ${tokens.toList()}")
                Log.i("sharpcj", "编码为 IDs: ${ids.toList()}")
                
                // 2. 解码测试
                val decodedFromTokens = SpmNative.decode(tokens)
                val decodedFromIds = SpmNative.decodeFromIds(ids)
                
                Log.i("sharpcj", "从 tokens 解码: $decodedFromTokens")
                Log.i("sharpcj", "从 IDs 解码: $decodedFromIds")
                
                // 3. 一致性检查
                val tokensConsistent = text == decodedFromTokens
                val idsConsistent = text == decodedFromIds
                
                Log.i("sharpcj", "tokens 编码解码一致性: $tokensConsistent")
                Log.i("sharpcj", "IDs 编码解码一致性: $idsConsistent")
                
                if (!tokensConsistent || !idsConsistent) {
                    Log.w("sharpcj", "警告: 编码解码不一致!")
                }
            }
            
            // 4. 边界情况测试
            Log.i("sharpcj", "\n--- 边界情况测试 ---")
            
            // 空字符串测试
            val emptyTokens = SpmNative.encode("")
            val emptyIds = SpmNative.encodeToIds("")
            Log.i("sharpcj", "空字符串 tokens: ${emptyTokens.toList()}")
            Log.i("sharpcj", "空字符串 IDs: ${emptyIds.toList()}")
            
            // 空数组解码测试
            val decodedEmptyTokens = SpmNative.decode(emptyTokens)
            val decodedEmptyIds = SpmNative.decodeFromIds(emptyIds)
            Log.i("sharpcj", "空数组解码结果: '$decodedEmptyTokens'")
            Log.i("sharpcj", "空数组解码结果: '$decodedEmptyIds'")
            
            // 5. 释放资源测试
            Log.i("sharpcj", "准备释放资源...")
            SpmNative.release()
            Log.i("sharpcj", "资源已释放")
            
            // 6. 验证释放后是否能重新加载
            Log.i("sharpcj", "测试释放后重新加载...")
            // val reloadSuccess = SpmNative.loadModel(modelPath)
            val reloadSuccess = loadModelFromAssets("sentencepiece.bpe.model", "sentencepiece.bpe.model")
            if (reloadSuccess) {
                Log.i("sharpcj", "重新加载成功")
                val testAfterReload = SpmNative.encode("Test after reload")
                Log.i("sharpcj", "重新加载后编码测试: ${testAfterReload.toList()}")
            } else {
                Log.e("sharpcj", "重新加载失败")
            }
            
            Log.i("sharpcj", "=== SentencePiece 测试完成 ===")
            
        } catch (e: Exception) {
            Log.e("sharpcj", "测试过程中发生异常", e)
        }
    }

    private suspend fun loadModelFromAssets(assetFileName: String, targetFileName: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val targetFile = File(filesDir, targetFileName)

                // 如果目标文件不存在或者需要更新，则从assets复制
                if (!targetFile.exists()) {
                    assets.open(assetFileName).use { input ->
                        targetFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    Log.i("sharpcj", "模型文件已从assets复制到: ${targetFile.absolutePath}")
                }

                // 加载模型
                val success = SpmNative.loadModel(targetFile.absolutePath)
                if (success) {
                    Log.i("sharpcj", "模型加载成功: ${targetFile.absolutePath}")
                } else {
                    Log.e("sharpcj", "模型加载失败")
                }
                success
            } catch (e: Exception) {
                Log.e("sharpcj", "从assets加载模型时发生异常", e)
                false
            }
        }
    }

}

@Composable
fun MainScreen(onClick: () -> Unit) {

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "SentencePiece Android",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            
            Button(
                onClick = onClick
            ) {
                Text("测试 SentencePiece 编码解码")
            }
        }
    }
}

@Composable
fun SentencePieceTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        content = content
    )
}


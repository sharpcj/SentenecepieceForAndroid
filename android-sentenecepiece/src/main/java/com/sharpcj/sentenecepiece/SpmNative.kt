package com.sharpcj.sentenecepiece

object SpmNative {
	init {
		System.loadLibrary("spm_android")
	}

	/**
	 * 加载 SentencePiece 模型
	 * @param path 模型文件路径
	 * @return 是否加载成功
	 */
	external fun loadModel(path: String): Boolean

	/**
	 * 将文本编码为字符串数组
	 * @param text 输入文本
	 * @return 分词后的字符串数组
	 */
	external fun encode(text: String): Array<String>

	/**
	 * 将文本编码为整数 ID 数组
	 * @param text 输入文本
	 * @return 分词后的整数 ID 数组
	 */
	external fun encodeToIds(text: String): IntArray

	/**
	 * 将字符串数组解码为文本
	 * @param pieces 分词后的字符串数组
	 * @return 解码后的文本
	 */
	external fun decode(pieces: Array<String>): String

	/**
	 * 将整数 ID 数组解码为文本
	 * @param ids 分词后的整数 ID 数组
	 * @return 解码后的文本
	 */
	external fun decodeFromIds(ids: IntArray): String

	/**
	 * 释放 SentencePiece 处理器资源
	 */
	external fun release()
}
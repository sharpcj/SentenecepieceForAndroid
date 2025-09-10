#include <jni.h>
#include <string>
#include <vector>
#include "sentencepiece_processor.h"

static sentencepiece::SentencePieceProcessor g_processor;

extern "C" JNIEXPORT jboolean JNICALL
Java_com_sharpcj_sentenecepiece_SpmNative_loadModel(JNIEnv* env, jobject /*thiz*/, jstring path_) {
	const char* path = env->GetStringUTFChars(path_, nullptr);
	auto status = g_processor.Load(path);
	env->ReleaseStringUTFChars(path_, path);
	return status.ok() ? JNI_TRUE : JNI_FALSE;
}

extern "C" JNIEXPORT jobjectArray JNICALL
Java_com_sharpcj_sentenecepiece_SpmNative_encode(JNIEnv* env, jobject /*thiz*/, jstring text_) {
	const char* text = env->GetStringUTFChars(text_, nullptr);
	std::vector<std::string> pieces;
	g_processor.Encode(text, &pieces);
	env->ReleaseStringUTFChars(text_, text);
	jclass stringClass = env->FindClass("java/lang/String");
	jobjectArray array = env->NewObjectArray(static_cast<jsize>(pieces.size()), stringClass, nullptr);
	for (jsize i = 0; i < static_cast<jsize>(pieces.size()); ++i) {
		jstring s = env->NewStringUTF(pieces[i].c_str());
		env->SetObjectArrayElement(array, i, s);
		env->DeleteLocalRef(s);
	}
	return array;
}

extern "C" JNIEXPORT jintArray JNICALL
Java_com_sharpcj_sentenecepiece_SpmNative_encodeToIds(JNIEnv* env, jobject /*thiz*/, jstring text_) {
	const char* text = env->GetStringUTFChars(text_, nullptr);
	std::vector<int> ids;
	g_processor.Encode(text, &ids);
	env->ReleaseStringUTFChars(text_, text);
	
	jintArray result = env->NewIntArray(static_cast<jsize>(ids.size()));
	if (result != nullptr) {
		env->SetIntArrayRegion(result, 0, static_cast<jsize>(ids.size()), ids.data());
	}
	return result;
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_sharpcj_sentenecepiece_SpmNative_decode(JNIEnv* env, jobject /*thiz*/, jobjectArray pieces_) {
	if (pieces_ == nullptr) {
		return env->NewStringUTF("");
	}
	
	jsize length = env->GetArrayLength(pieces_);
	std::vector<std::string> pieces;
	pieces.reserve(length);
	
	for (jsize i = 0; i < length; ++i) {
		jstring piece = static_cast<jstring>(env->GetObjectArrayElement(pieces_, i));
		if (piece != nullptr) {
			const char* pieceStr = env->GetStringUTFChars(piece, nullptr);
			if (pieceStr != nullptr) {
				pieces.emplace_back(pieceStr);
				env->ReleaseStringUTFChars(piece, pieceStr);
			}
			env->DeleteLocalRef(piece);
		}
	}
	
	std::string result;
	g_processor.Decode(pieces, &result);
	return env->NewStringUTF(result.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_sharpcj_sentenecepiece_SpmNative_decodeFromIds(JNIEnv* env, jobject /*thiz*/, jintArray ids_) {
	if (ids_ == nullptr) {
		return env->NewStringUTF("");
	}
	
	jsize length = env->GetArrayLength(ids_);
	jint* ids = env->GetIntArrayElements(ids_, nullptr);
	
	if (ids == nullptr) {
		return env->NewStringUTF("");
	}
	
	std::vector<int> idVector(ids, ids + length);
	env->ReleaseIntArrayElements(ids_, ids, JNI_ABORT);
	
	std::string result;
	g_processor.Decode(idVector, &result);
	return env->NewStringUTF(result.c_str());
}

extern "C" JNIEXPORT void JNICALL
Java_com_sharpcj_sentenecepiece_SpmNative_release(JNIEnv* /*env*/, jobject /*thiz*/) {
	// SentencePiece 使用 RAII 模式，通过 std::unique_ptr 自动管理资源
	// 重置处理器会触发智能指针的析构，自动释放所有资源
}
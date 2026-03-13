package com.xingheyuzhuan.shiguangschedule.data.ai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit

/**
 * AI 聊天服务
 * 支持 OpenAI 兼容的 API 接口，包括流式输出（SSE）
 */
class AiChatService(private val configManager: AiConfigManager) {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Serializable
    data class ChatMessage(
        val role: String,
        val content: String
    )

    @Serializable
    data class ChatRequest(
        val model: String,
        val messages: List<ChatMessage>,
        val stream: Boolean = true,
        val max_tokens: Int = 2048,
        val temperature: Float = 0.7f
    )

    /**
     * 发送聊天请求并以流式方式返回结果
     * @param messages 对话历史
     * @return Flow<String> 逐字返回的文本流
     */
    fun chatStream(messages: List<ChatMessage>): Flow<String> = flow {
        if (!configManager.isConfigured()) {
            emit("[错误] 请先在设置中配置 AI 接口地址和 API Key")
            return@flow
        }

        val allMessages = mutableListOf<ChatMessage>()
        // 添加系统提示
        val systemPrompt = configManager.systemPrompt
        if (systemPrompt.isNotBlank()) {
            allMessages.add(ChatMessage(role = "system", content = systemPrompt))
        }
        allMessages.addAll(messages)

        val requestBody = ChatRequest(
            model = configManager.modelName,
            messages = allMessages,
            stream = true,
            max_tokens = configManager.maxTokens,
            temperature = configManager.temperature
        )

        val jsonBody = json.encodeToString(requestBody)
        val apiUrl = "${configManager.apiUrl}/v1/chat/completions"

        val request = Request.Builder()
            .url(apiUrl)
            .addHeader("Authorization", "Bearer ${configManager.apiKey}")
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "text/event-stream")
            .post(jsonBody.toRequestBody("application/json".toMediaType()))
            .build()

        try {
            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                val errorBody = response.body?.string() ?: "未知错误"
                emit("[错误] API 请求失败 (${response.code}): $errorBody")
                response.close()
                return@flow
            }

            val body = response.body ?: run {
                emit("[错误] 响应体为空")
                return@flow
            }

            val reader = BufferedReader(InputStreamReader(body.byteStream()))
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                val currentLine = line ?: continue

                if (!currentLine.startsWith("data: ")) continue

                val data = currentLine.removePrefix("data: ").trim()

                if (data == "[DONE]") break

                try {
                    val jsonObj = json.parseToJsonElement(data).jsonObject
                    val choices = jsonObj["choices"]?.jsonArray ?: continue
                    if (choices.isEmpty()) continue

                    val delta = choices[0].jsonObject["delta"]?.jsonObject ?: continue
                    val content = delta["content"]?.jsonPrimitive?.content ?: continue

                    if (content.isNotEmpty()) {
                        emit(content)
                    }
                } catch (e: Exception) {
                    // 跳过解析失败的行
                    continue
                }
            }

            reader.close()
            response.close()
        } catch (e: java.net.UnknownHostException) {
            emit("[错误] 无法连接到服务器，请检查 API 地址是否正确")
        } catch (e: java.net.SocketTimeoutException) {
            emit("[错误] 连接超时，请检查网络或稍后重试")
        } catch (e: Exception) {
            emit("[错误] ${e.message ?: "未知错误"}")
        }
    }.flowOn(Dispatchers.IO)

    /**
     * 发送非流式聊天请求
     * @param messages 对话历史
     * @return 完整的回复文本
     */
    suspend fun chat(messages: List<ChatMessage>): String {
        if (!configManager.isConfigured()) {
            return "[错误] 请先在设置中配置 AI 接口地址和 API Key"
        }

        val allMessages = mutableListOf<ChatMessage>()
        val systemPrompt = configManager.systemPrompt
        if (systemPrompt.isNotBlank()) {
            allMessages.add(ChatMessage(role = "system", content = systemPrompt))
        }
        allMessages.addAll(messages)

        val requestBody = ChatRequest(
            model = configManager.modelName,
            messages = allMessages,
            stream = false,
            max_tokens = configManager.maxTokens,
            temperature = configManager.temperature
        )

        val jsonBody = json.encodeToString(requestBody)
        val apiUrl = "${configManager.apiUrl}/v1/chat/completions"

        val request = Request.Builder()
            .url(apiUrl)
            .addHeader("Authorization", "Bearer ${configManager.apiKey}")
            .addHeader("Content-Type", "application/json")
            .post(jsonBody.toRequestBody("application/json".toMediaType()))
            .build()

        return try {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: return "[错误] 响应体为空"

            if (!response.isSuccessful) {
                return "[错误] API 请求失败 (${response.code}): $responseBody"
            }

            val jsonObj = json.parseToJsonElement(responseBody).jsonObject
            val choices = jsonObj["choices"]?.jsonArray ?: return "[错误] 无效的响应格式"
            if (choices.isEmpty()) return "[错误] 无回复内容"

            val message = choices[0].jsonObject["message"]?.jsonObject ?: return "[错误] 无效的消息格式"
            message["content"]?.jsonPrimitive?.content ?: "[错误] 无回复内容"
        } catch (e: Exception) {
            "[错误] ${e.message ?: "未知错误"}"
        }
    }
}

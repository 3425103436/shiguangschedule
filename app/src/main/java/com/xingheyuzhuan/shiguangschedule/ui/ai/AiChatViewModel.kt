package com.xingheyuzhuan.shiguangschedule.ui.ai

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.xingheyuzhuan.shiguangschedule.data.ai.AiChatService
import com.xingheyuzhuan.shiguangschedule.data.ai.AiConfigManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * AI 聊天 ViewModel
 * 管理对话状态、消息历史和流式输出
 */
class AiChatViewModel(application: Application) : AndroidViewModel(application) {

    val configManager = AiConfigManager(application.applicationContext)
    private val chatService = AiChatService(configManager)

    private val _uiState = MutableStateFlow(AiChatUiState())
    val uiState: StateFlow<AiChatUiState> = _uiState.asStateFlow()

    private var currentStreamJob: Job? = null

    /**
     * 发送用户消息
     */
    fun sendMessage(content: String) {
        if (content.isBlank()) return
        if (_uiState.value.isLoading) return

        // 添加用户消息
        val userMessage = ChatMessageUi(
            id = UUID.randomUUID().toString(),
            role = MessageRole.USER,
            content = content
        )

        _uiState.update { state ->
            state.copy(
                messages = state.messages + userMessage,
                isLoading = true,
                inputText = ""
            )
        }

        // 创建 AI 回复占位
        val aiMessage = ChatMessageUi(
            id = UUID.randomUUID().toString(),
            role = MessageRole.ASSISTANT,
            content = "",
            isStreaming = true
        )

        _uiState.update { state ->
            state.copy(messages = state.messages + aiMessage)
        }

        // 开始流式请求
        currentStreamJob = viewModelScope.launch {
            val apiMessages = _uiState.value.messages
                .filter { it.role != MessageRole.SYSTEM && it.content.isNotBlank() }
                .dropLast(1) // 去掉空的 AI 占位消息
                .map { msg ->
                    AiChatService.ChatMessage(
                        role = when (msg.role) {
                            MessageRole.USER -> "user"
                            MessageRole.ASSISTANT -> "assistant"
                            MessageRole.SYSTEM -> "system"
                        },
                        content = msg.content
                    )
                }

            val contentBuilder = StringBuilder()

            chatService.chatStream(apiMessages).collect { chunk ->
                contentBuilder.append(chunk)
                val currentContent = contentBuilder.toString()

                _uiState.update { state ->
                    val updatedMessages = state.messages.toMutableList()
                    val lastIndex = updatedMessages.lastIndex
                    if (lastIndex >= 0 && updatedMessages[lastIndex].role == MessageRole.ASSISTANT) {
                        updatedMessages[lastIndex] = updatedMessages[lastIndex].copy(
                            content = currentContent,
                            isStreaming = true
                        )
                    }
                    state.copy(messages = updatedMessages)
                }
            }

            // 流式输出完成
            _uiState.update { state ->
                val updatedMessages = state.messages.toMutableList()
                val lastIndex = updatedMessages.lastIndex
                if (lastIndex >= 0 && updatedMessages[lastIndex].role == MessageRole.ASSISTANT) {
                    updatedMessages[lastIndex] = updatedMessages[lastIndex].copy(
                        isStreaming = false
                    )
                }
                state.copy(
                    messages = updatedMessages,
                    isLoading = false
                )
            }
        }
    }

    /**
     * 停止当前流式输出
     */
    fun stopStreaming() {
        currentStreamJob?.cancel()
        currentStreamJob = null

        _uiState.update { state ->
            val updatedMessages = state.messages.toMutableList()
            val lastIndex = updatedMessages.lastIndex
            if (lastIndex >= 0 && updatedMessages[lastIndex].role == MessageRole.ASSISTANT) {
                updatedMessages[lastIndex] = updatedMessages[lastIndex].copy(
                    isStreaming = false
                )
            }
            state.copy(
                messages = updatedMessages,
                isLoading = false
            )
        }
    }

    /**
     * 更新输入文本
     */
    fun updateInputText(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    /**
     * 清空对话历史
     */
    fun clearMessages() {
        currentStreamJob?.cancel()
        _uiState.update {
            AiChatUiState()
        }
    }

    /**
     * 切换设置面板显示
     */
    fun toggleSettings() {
        _uiState.update { it.copy(showSettings = !it.showSettings) }
    }

    /**
     * 关闭设置面板
     */
    fun closeSettings() {
        _uiState.update { it.copy(showSettings = false) }
    }

    /**
     * 保存 API 配置
     */
    fun saveConfig(apiUrl: String, apiKey: String, modelName: String) {
        configManager.apiUrl = apiUrl
        configManager.apiKey = apiKey
        configManager.modelName = modelName
    }
}

/**
 * AI 聊天 UI 状态
 */
data class AiChatUiState(
    val messages: List<ChatMessageUi> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = false,
    val showSettings: Boolean = false
)

/**
 * 聊天消息 UI 模型
 */
data class ChatMessageUi(
    val id: String,
    val role: MessageRole,
    val content: String,
    val isStreaming: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * 消息角色
 */
enum class MessageRole {
    USER, ASSISTANT, SYSTEM
}

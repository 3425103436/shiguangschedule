package com.xingheyuzhuan.shiguangschedule.data.ai

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * AI 配置管理器
 * 使用 EncryptedSharedPreferences 安全存储 API Key
 * 如果加密存储不可用，则降级为普通 SharedPreferences
 */
class AiConfigManager(private val context: Context) {

    private val prefs: SharedPreferences by lazy {
        try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            EncryptedSharedPreferences.create(
                context,
                "ai_config_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            // 降级为普通 SharedPreferences
            context.getSharedPreferences("ai_config_prefs", Context.MODE_PRIVATE)
        }
    }

    companion object {
        private const val KEY_API_URL = "ai_api_url"
        private const val KEY_API_KEY = "ai_api_key"
        private const val KEY_MODEL_NAME = "ai_model_name"
        private const val KEY_SYSTEM_PROMPT = "ai_system_prompt"
        private const val KEY_MAX_TOKENS = "ai_max_tokens"
        private const val KEY_TEMPERATURE = "ai_temperature"

        private const val DEFAULT_API_URL = "https://api.openai.com"
        private const val DEFAULT_MODEL = "gpt-3.5-turbo"
        private const val DEFAULT_SYSTEM_PROMPT = "你是拾光课程表的 AI 助手，一个友好、专业的学习伙伴。你可以帮助用户解答学习问题、提供课程建议、辅导作业等。请用简洁、清晰的中文回答。"
        private const val DEFAULT_MAX_TOKENS = 2048
        private const val DEFAULT_TEMPERATURE = 0.7f
    }

    var apiUrl: String
        get() = prefs.getString(KEY_API_URL, DEFAULT_API_URL) ?: DEFAULT_API_URL
        set(value) = prefs.edit().putString(KEY_API_URL, value.trimEnd('/')).apply()

    var apiKey: String
        get() = prefs.getString(KEY_API_KEY, "") ?: ""
        set(value) = prefs.edit().putString(KEY_API_KEY, value.trim()).apply()

    var modelName: String
        get() = prefs.getString(KEY_MODEL_NAME, DEFAULT_MODEL) ?: DEFAULT_MODEL
        set(value) = prefs.edit().putString(KEY_MODEL_NAME, value.trim()).apply()

    var systemPrompt: String
        get() = prefs.getString(KEY_SYSTEM_PROMPT, DEFAULT_SYSTEM_PROMPT) ?: DEFAULT_SYSTEM_PROMPT
        set(value) = prefs.edit().putString(KEY_SYSTEM_PROMPT, value).apply()

    var maxTokens: Int
        get() = prefs.getInt(KEY_MAX_TOKENS, DEFAULT_MAX_TOKENS)
        set(value) = prefs.edit().putInt(KEY_MAX_TOKENS, value).apply()

    var temperature: Float
        get() = prefs.getFloat(KEY_TEMPERATURE, DEFAULT_TEMPERATURE)
        set(value) = prefs.edit().putFloat(KEY_TEMPERATURE, value).apply()

    /**
     * 检查 AI 配置是否完整
     */
    fun isConfigured(): Boolean {
        return apiUrl.isNotBlank() && apiKey.isNotBlank() && modelName.isNotBlank()
    }

    /**
     * 重置为默认配置
     */
    fun resetToDefaults() {
        apiUrl = DEFAULT_API_URL
        modelName = DEFAULT_MODEL
        systemPrompt = DEFAULT_SYSTEM_PROMPT
        maxTokens = DEFAULT_MAX_TOKENS
        temperature = DEFAULT_TEMPERATURE
    }
}

package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.TokenTransaction
import com.example.data.model.TransactionType
import com.example.data.model.UserAccount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class TokenLedgerManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("muse_token_ledger", Context.MODE_PRIVATE)

    private val _account = MutableStateFlow(loadAccount())
    val account: StateFlow<UserAccount> = _account.asStateFlow()

    private val _transactions = MutableStateFlow(loadTransactions())
    val transactions: StateFlow<List<TokenTransaction>> = _transactions.asStateFlow()

    private val _videoCost = MutableStateFlow(prefs.getLong("config_video_cost", 1L))
    val videoCost: StateFlow<Long> = _videoCost.asStateFlow()

    private val _dailyBonusAmount = MutableStateFlow(prefs.getLong("config_daily_bonus", 10_000L))
    val dailyBonusAmount: StateFlow<Long> = _dailyBonusAmount.asStateFlow()

    init {
        // If this is first launch, log initial 1,000,000,000 token grant
        if (_transactions.value.isEmpty()) {
            recordTransaction(
                type = TransactionType.INITIAL_CREDIT,
                amount = 1_000_000_000L,
                description = "Welcome Bonus: Initial 1,000,000,000 Production Tokens Granted"
            )
        }
    }

    private fun loadAccount(): UserAccount {
        val balance = prefs.getLong("account_balance", 1_000_000_000L)
        val username = prefs.getString("account_username", "Muse VIP Director") ?: "Muse VIP Director"
        val email = prefs.getString("account_email", "director@muse.studio") ?: "director@muse.studio"
        val totalGen = prefs.getInt("account_total_gen", 0)
        val lastBonus = prefs.getLong("account_last_daily_bonus", 0L)
        val isGuest = prefs.getBoolean("account_is_guest", false)

        return UserAccount(
            username = username,
            email = email,
            tokenBalance = balance,
            totalGenerations = totalGen,
            lastDailyBonusTimestamp = lastBonus,
            isGuest = isGuest
        )
    }

    private fun loadTransactions(): List<TokenTransaction> {
        val jsonStr = prefs.getString("transactions_json", null) ?: return emptyList()
        val list = mutableListOf<TokenTransaction>()
        try {
            val jsonArray = JSONArray(jsonStr)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    TokenTransaction(
                        id = obj.getString("id"),
                        timestamp = obj.getLong("timestamp"),
                        type = TransactionType.valueOf(obj.getString("type")),
                        amount = obj.getLong("amount"),
                        balanceAfter = obj.getLong("balanceAfter"),
                        description = obj.getString("description")
                    )
                )
            }
        } catch (_: Exception) {
            // fallback
        }
        return list
    }

    private fun saveAccount(account: UserAccount) {
        prefs.edit()
            .putLong("account_balance", account.tokenBalance)
            .putString("account_username", account.username)
            .putString("account_email", account.email)
            .putInt("account_total_gen", account.totalGenerations)
            .putLong("account_last_daily_bonus", account.lastDailyBonusTimestamp)
            .putBoolean("account_is_guest", account.isGuest)
            .apply()
        _account.value = account
    }

    private fun saveTransactions(list: List<TokenTransaction>) {
        val jsonArray = JSONArray()
        list.take(100).forEach { tx ->
            val obj = JSONObject().apply {
                put("id", tx.id)
                put("timestamp", tx.timestamp)
                put("type", tx.type.name)
                put("amount", tx.amount)
                put("balanceAfter", tx.balanceAfter)
                put("description", tx.description)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString("transactions_json", jsonArray.toString()).apply()
        _transactions.value = list
    }

    @Synchronized
    private fun recordTransaction(type: TransactionType, amount: Long, description: String) {
        val current = _account.value
        val newBalance = current.tokenBalance
        val tx = TokenTransaction(
            id = UUID.randomUUID().toString().take(8),
            timestamp = System.currentTimeMillis(),
            type = type,
            amount = amount,
            balanceAfter = newBalance,
            description = description
        )
        val updatedList = listOf(tx) + _transactions.value
        saveTransactions(updatedList)
    }

    @Synchronized
    fun deductForGeneration(serviceName: String, cost: Long = _videoCost.value): Boolean {
        val current = _account.value
        if (current.tokenBalance < cost) {
            return false
        }
        val newBalance = current.tokenBalance - cost
        val updated = current.copy(
            tokenBalance = newBalance,
            totalGenerations = current.totalGenerations + 1
        )
        saveAccount(updated)

        val tx = TokenTransaction(
            id = UUID.randomUUID().toString().take(8),
            timestamp = System.currentTimeMillis(),
            type = TransactionType.GENERATION_DEBIT,
            amount = -cost,
            balanceAfter = newBalance,
            description = "AI Studio Generation: $serviceName (-$cost Token)"
        )
        saveTransactions(listOf(tx) + _transactions.value)
        return true
    }

    @Synchronized
    fun refundGeneration(serviceName: String, cost: Long = _videoCost.value, reason: String = "Generation failed or cancelled") {
        val current = _account.value
        val newBalance = current.tokenBalance + cost
        val updated = current.copy(tokenBalance = newBalance)
        saveAccount(updated)

        val tx = TokenTransaction(
            id = UUID.randomUUID().toString().take(8),
            timestamp = System.currentTimeMillis(),
            type = TransactionType.REFUND,
            amount = cost,
            balanceAfter = newBalance,
            description = "Automatic Refund for $serviceName: $reason (+$cost Token)"
        )
        saveTransactions(listOf(tx) + _transactions.value)
    }

    fun canClaimDailyBonus(): Boolean {
        val lastClaim = _account.value.lastDailyBonusTimestamp
        val now = System.currentTimeMillis()
        val twentyFourHours = 24 * 60 * 60 * 1000L
        return (now - lastClaim) >= twentyFourHours
    }

    fun getDailyBonusTimeRemainingMillis(): Long {
        val lastClaim = _account.value.lastDailyBonusTimestamp
        val now = System.currentTimeMillis()
        val twentyFourHours = 24 * 60 * 60 * 1000L
        val diff = (lastClaim + twentyFourHours) - now
        return if (diff > 0) diff else 0L
    }

    @Synchronized
    fun claimDailyBonus(): Boolean {
        if (!canClaimDailyBonus()) return false
        val current = _account.value
        val bonus = _dailyBonusAmount.value
        val newBalance = current.tokenBalance + bonus
        val now = System.currentTimeMillis()

        val updated = current.copy(
            tokenBalance = newBalance,
            lastDailyBonusTimestamp = now
        )
        saveAccount(updated)

        val tx = TokenTransaction(
            id = UUID.randomUUID().toString().take(8),
            timestamp = now,
            type = TransactionType.DAILY_BONUS,
            amount = bonus,
            balanceAfter = newBalance,
            description = "Daily Studio Bonus Claimed (+$bonus Tokens)"
        )
        saveTransactions(listOf(tx) + _transactions.value)
        return true
    }

    @Synchronized
    fun adminGrantTokens(amount: Long, note: String) {
        val current = _account.value
        val newBalance = current.tokenBalance + amount
        saveAccount(current.copy(tokenBalance = newBalance))

        val tx = TokenTransaction(
            id = UUID.randomUUID().toString().take(8),
            timestamp = System.currentTimeMillis(),
            type = TransactionType.ADMIN_ADJUSTMENT,
            amount = amount,
            balanceAfter = newBalance,
            description = "Admin Credit: $note"
        )
        saveTransactions(listOf(tx) + _transactions.value)
    }

    fun updateConfig(newCost: Long, newDailyBonus: Long) {
        prefs.edit()
            .putLong("config_video_cost", newCost)
            .putLong("config_daily_bonus", newDailyBonus)
            .apply()
        _videoCost.value = newCost
        _dailyBonusAmount.value = newDailyBonus
    }

    fun updateUser(name: String, email: String) {
        saveAccount(_account.value.copy(username = name, email = email, isGuest = false))
    }
}

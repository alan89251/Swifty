package com.team2.handiwork.models

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.team2.handiwork.R
import com.team2.handiwork.enums.TransactionEnum
import com.team2.handiwork.enums.TransactionEnumDeserializeAdapter
import com.team2.handiwork.enums.TransactionEnumSerializeAdapter
import java.io.Serializable


class Transaction : Serializable {
    var missionId: String = ""
    var amount: Int = 0
    var title: String = ""
    var firstName: String = ""
    var lastName: String = ""
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()
    var type: TransactionEnum = TransactionEnum.PAYMENT

    fun isExpense(): Boolean {
        return type == TransactionEnum.CASH_OUT
                || type == TransactionEnum.PAYMENT
                || type == TransactionEnum.WITHDRAW
    }

    fun getHistoryCreditDisplay(): String {
        return if (isExpense()) {
            "- $amount"
        } else {
            "+ $amount"
        }
    }

    fun getIcon(): Int {
        return when (type) {
            TransactionEnum.ERAN -> R.drawable.add_dollar
            TransactionEnum.TOP_UP -> R.drawable.coins
            TransactionEnum.CASH_OUT -> R.drawable.initiate_money_transfer
            else -> R.drawable.salary_male
        }
    }


    fun serialize(): Map<String, Any> {
        val gson = GsonBuilder()
            .registerTypeAdapter(
                TransactionEnum::class.java,
                TransactionEnumSerializeAdapter(),
            ).create()
        val string = gson.toJson(this)
        val map: Map<String, Any> = HashMap()
        return gson.fromJson(string, map.javaClass)
    }

    companion object {
        fun deserialize(transaction: Map<String, Any>): Transaction {
            val json = Gson().toJson(transaction)
            val gson = GsonBuilder()
                .registerTypeAdapter(
                    TransactionEnum::class.java,
                    TransactionEnumDeserializeAdapter(),
                ).create()
            return gson.fromJson(json, Transaction::class.java)
        }
    }
}


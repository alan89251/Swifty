package com.team2.handiwork.models

import com.team2.handiwork.R
import com.team2.handiwork.enums.TransactionEnum


class Transaction {
    var missionId: String = ""
    var amount: Int = 0
    var title: String = ""
    var firstName: String = ""
    var lastName: String = ""
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()
    var transType: TransactionEnum = TransactionEnum.PAYMENT
        set(value) {
            field = value
            type = transType.value
        }

    var type = transType.value


    fun isExpense(): Boolean {
        return transType == TransactionEnum.CASH_OUT || transType == TransactionEnum.PAYMENT
    }

    fun getHistoryCreditDisplay(): String {
        return if (isExpense()) {
            "- $amount"
        } else {
            "+ $amount"
        }
    }

    fun getTransType(t: Int): TransactionEnum {
        return when (t) {
            TransactionEnum.CASH_OUT.value -> TransactionEnum.CASH_OUT
            TransactionEnum.TOP_UP.value -> TransactionEnum.TOP_UP
            TransactionEnum.PAYMENT.value -> TransactionEnum.PAYMENT
            TransactionEnum.ERAN.value -> TransactionEnum.ERAN
            TransactionEnum.WITHDRAW.value -> TransactionEnum.WITHDRAW
            else -> throw IllegalArgumentException("Invalid value")
        }
    }


    fun getIcon(): Int {
        return if (transType == TransactionEnum.ERAN) {
            R.drawable.add_dollar
        } else if (transType == TransactionEnum.TOP_UP) {
            R.drawable.coins
        } else if (transType == TransactionEnum.CASH_OUT) {
            R.drawable.initiate_money_transfer
        } else {
            R.drawable.salary_male
        }
    }

    fun toHashMap(): Map<String, Any> {
        return hashMapOf<String, Any>(
            "amount" to this.amount,
            "missionId" to this.missionId,
            "title" to this.title,
            "firstName" to this.firstName,
            "lastName" to this.lastName,
            "type" to this.type,
            "updatedAt" to this.updatedAt,
            "createdAt" to this.createdAt,
        )
    }

    companion object {
        fun toObject(trans: Map<String, Any>): Transaction {
            val transaction = Transaction()
            transaction.amount = (trans["amount"] as Long).toInt()
            transaction.missionId = trans["missionId"] as String
            transaction.title = trans["title"] as String
            transaction.firstName = trans["firstName"] as String
            transaction.lastName = trans["lastName"] as String
            transaction.transType = transaction.getTransType((trans["type"] as Long).toInt())
            transaction.updatedAt = (trans["updatedAt"] as Long)
            transaction.createdAt = (trans["createdAt"] as Long)
            return transaction
        }
    }
}


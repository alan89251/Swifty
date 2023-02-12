package com.team2.handiwork.models

import com.team2.handiwork.R
import com.team2.handiwork.enum.TransactionEnum
import java.io.Serializable

class Transaction : Serializable {
    var missionId: String = ""
    var amount: Int = 0
    var title: String = ""
    var firstName: String = ""
    var lastName: String = ""
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()
    var transType: TransactionEnum = TransactionEnum.PAYMENT

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
}

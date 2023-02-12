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
    private var transactionType: TransactionEnum = getTransactionType()

    fun getHistoryCreditDisplay(): String {
        return if (amount > 0) {
            "+ $amount"
        } else {
            "- $amount"
        }
    }

    fun getIcon(): Int {
        return if (transactionType == TransactionEnum.ERAN) {
            R.drawable.add_dollar
        } else if (transactionType == TransactionEnum.TOP_UP) {
            R.drawable.coins
        } else if (transactionType == TransactionEnum.CASH_OUT) {
            R.drawable.initiate_money_transfer
        } else {
            R.drawable.salary_male
        }
    }

    private fun getTransactionType(): TransactionEnum {
        return if (missionId.isEmpty()) {
            if (amount > 0) {
                TransactionEnum.TOP_UP
            } else {
                TransactionEnum.CASH_OUT
            }
        } else {
            if (amount > 0) {
                TransactionEnum.ERAN
            } else {
                TransactionEnum.PAYMENT
            }
        }
    }
}

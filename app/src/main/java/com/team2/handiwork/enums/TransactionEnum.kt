package com.team2.handiwork.enums

import com.google.gson.*
import java.lang.reflect.Type

enum class TransactionEnum(val value: Int) {
    CASH_OUT(0),
    TOP_UP(1),
    PAYMENT(2),
    ERAN(3),
    WITHDRAW(4);

}

class TransactionEnumSerializeAdapter : JsonSerializer<TransactionEnum> {
    override fun serialize(
        src: TransactionEnum?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src?.ordinal)
    }
}

class TransactionEnumDeserializeAdapter : JsonDeserializer<TransactionEnum> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): TransactionEnum {
        return TransactionEnum.values()[json?.asInt ?: 0]
    }
}
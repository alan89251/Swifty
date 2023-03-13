package com.team2.handiwork.enums

import com.google.gson.*
import java.lang.reflect.Type

enum class MissionStatusEnum(var value: Int) {
    OPEN(0),
    PENDING_ACCEPTANCE(1),
    CONFIRMED(2),
    DISPUTED(3),
    CANCELLED(4),
    COMPLETED(5),
    ENROLLED(6),

    // show error status
    ERROR(7);

    companion object {
        fun parse(value: Int) = values().first { it.value == value }
    }

}

class MissionStatusEnumSerializeAdapter : JsonSerializer<MissionStatusEnum> {

    override fun serialize(
        src: MissionStatusEnum?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src?.ordinal)
    }
}

class MissionStatusEnumDeserializeAdapter : JsonDeserializer<MissionStatusEnum> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): MissionStatusEnum {
        return MissionStatusEnum.values()[json?.asInt ?: 0]
    }
}
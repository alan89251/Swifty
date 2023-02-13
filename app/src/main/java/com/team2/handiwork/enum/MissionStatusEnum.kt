package com.team2.handiwork.enum

enum class MissionStatusEnum(val  value: Int) {
    OPEN(0),
    PENDING_ACCEPTANCE(1),
    CONFIRMED(2),
    DISPUTED(3),
    CANCELLED(4),
    COMPLETED(5)
}
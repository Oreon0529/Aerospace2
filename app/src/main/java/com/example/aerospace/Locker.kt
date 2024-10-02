package com.example.aerospace

data class Locker(
    val number: Int,          // 사물함 번호
    val isUsed: Boolean,      // 사용 여부 (true = 사용 중, false = 비어 있음)
    val useUntil: String?,    // 언제까지 사용하는지 (null = 비어 있음, 날짜 형식의 문자열)
    val studentId: String?    // 학번 (null = 학번이 없음)
)

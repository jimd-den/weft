package com.example.domain.model

data class SourceNode(
    val id: String,
    val title: String,
    val content: String,
    val readingTimeMn: Int,
    val yearTag: String,
    val stage: Stage
)

enum class Stage(val title: String, val tag: String) {
    SURVEY("SURVEY", "0x1A·00"),
    QUESTION("QUESTION", "0x1A·01"),
    READ("READ", "0x1A·03"),
    RECORD("RECORD", "0x1A·07"),
    RECITE("RECITE", "0x1A·09"),
    REVIEW("REVIEW", "QUEUE·12"),
    REFLECT("REFLECT", "WEAVE");
    
    companion object {
        fun fromIndex(index: Int): Stage {
            return entries[index.coerceIn(0, entries.size - 1)]
        }
    }
}

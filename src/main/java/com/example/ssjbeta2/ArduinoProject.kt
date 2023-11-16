data class ArduinoProject(
    val title: String,
    val description: String? = null,
    val codeExplanation: String? = null,
    val imageResourceIds: List<Int> = emptyList(),
    val aboutImages: List<String> = emptyList(),  // This list contains descriptions for each image
    val websiteUrl: String? = null,
    val linkText: String? = null
)




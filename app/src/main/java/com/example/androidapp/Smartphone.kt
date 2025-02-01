package com.example.androidapp

data class Smartphone(
    // Basic data
    val name: String? = null,
    val manufacturerCode: String? = null,
    val phoneBrand: String? = null,
    val phoneModel: String? = null,
    val ean: String? = null,

    // Physical parameters
    val color: String? = null,
    val material: String? = null,
    val width: Int = 0,
    val height: Int = 0,
    val depth: Int = 0,
    val weight: Int = 0,

    // Display
    val screenDiagonal: Int = 0,
    val displayType: String? = null,
    val touchScreen: String? = null,
    val pixelDensity: Int = 0,
    val screenResolution: String? = null,

    // Apparatus
    val rearCameraResolution: Int = 0,
    val frontCameraResolution: Int = 0,
    val cameraFeatures: String? = null,
    val resolutionOfRecordedVideos: String? = null,

    // Processor
    val processor: String? = null,
    val processorFrequency: Int = 0,
    val numberOfProcessorCores: Int = 0,

    // Detailed information
    val builtInMemory: Int = 0,
    val ramMemory: Int = 0,
    val operatingSystem: String? = null,
    val batteryCapacity: Int = 0,
    val connectors: String? = null,

    val base64Images: List<String> = emptyList()
)
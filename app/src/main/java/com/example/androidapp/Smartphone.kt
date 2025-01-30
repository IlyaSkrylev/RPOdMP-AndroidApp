package com.example.androidapp

data class Smartphone(
    // Basic data
    val name: String? = null,
    val manufacturerCode: String? = null,
    val phoneBrand: String? = null,
    val phoneModel: String? = null,
    val ean: Int,

    // Physical parameters
    val color: String? = null,
    val material: String? = null,
    val width: Int,
    val height: Int,
    val depth: Int,
    val weight: Int,

    // Display
    val screenDiagonal: Int,
    val displayType: String? = null,
    val touchScreen: String? = null,
    val pixelDensity: Int,
    val screenResolution: String? = null,

    // Apparatus
    val rearCameraResolution: Int,
    val frontCameraResolution: Int,
    val cameraFeatures: String? = null,
    val resolutionOfRecordedVideos: String? = null,

    // Processor
    val processor: Int,
    val processorFrequency: Int,
    val numberOfProcessorCores: Int,

    // Detailed information
    val builtInMemory: Int,
    val ramMemory: Int,
    val operatingSystem: String? = null,
    val batteryCapacity: Int,
    val connectors: String? = null
)
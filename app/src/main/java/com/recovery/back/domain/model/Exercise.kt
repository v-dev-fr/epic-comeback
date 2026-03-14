package com.recovery.back.domain.model

data class Exercise(
    val id: String,
    val name: String,
    val description: String,
    val cue: String? = null,
    val sets: String = "3",
    val reps: String = "10",
    val phase: Int
)

val ExerciseList = listOf(
    // Phase 1
    Exercise("mckenzie_prone", "McKenzie Prone Lying", "Lying flat on stomach to reduce disc pressure.", "Relax back muscles completely.", phase = 1),
    Exercise("cobra_press", "Prone Press-Up (Cobra)", "Slowly press up using arms while keeping hips down.", "Stop if pain radiates down leg.", phase = 1),
    
    // Phase 2
    Exercise("mcgill_curlup", "McGill Curl-Up", "Brace core and lift head slightly.", "Breathe behind the shield.", "3", "10s hold", phase = 2),
    Exercise("side_bridge_knees", "Side Bridge (Knees)", "Support weight on knees and forearm.", "Maintain straight line from head to knees.", phase = 2),
    
    // Phase 4 - McGill Big 3
    Exercise("mcgill_big_3", "McGill Big 3", "Combination of Curl-Up, Side Bridge, and Bird-Dog.", "Breathe behind the shield — maintain brace.", "3", "8->6->4 reps", phase = 4),
    Exercise("glute_bridge", "Glute Bridge", "Lifting hips while squeezing glutes.", "Do not arch lower back.", "3", "15", phase = 4)
)

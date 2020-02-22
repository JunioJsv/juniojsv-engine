package juniojsv.engine.constants

import juniojsv.engine.Model

val DRAGON_MODEL by lazy {
    Model.fromResources("models/dragon.obj")
}
val ROCK_MODEL by lazy {
    Model.fromResources("models/rock.obj")
}
val SPHERE_MODEL by lazy {
    Model.fromResources("models/sphere.obj")
}
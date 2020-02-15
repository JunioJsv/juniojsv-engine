package juniojsv.engine.constants

import juniojsv.engine.Model

val DRAGON_MODEL by lazy {
    Model.fromResources("dragon.obj")
}
val TERRAIN_MODEL by lazy {
    Model.fromResources("terrain.obj")
}
val ROCK_MODEL by lazy {
    Model.fromResources("rock.obj")
}
val SPHERE_MODEL by lazy {
    Model.fromResources("sphere.obj")
}
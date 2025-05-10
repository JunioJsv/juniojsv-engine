package juniojsv.engine.features.render

import juniojsv.engine.features.context.IWindowContext
import juniojsv.engine.features.utils.IDisposable

abstract class Render : IDisposable {
    lateinit var context: IWindowContext
        private set

    var didSetup: Boolean = false
        protected set

    protected open fun setup(context: IWindowContext) {
        didSetup = true
        this.context = context
    }

    open fun render(context: IWindowContext) {
        if (!didSetup) setup(context)
    }
}
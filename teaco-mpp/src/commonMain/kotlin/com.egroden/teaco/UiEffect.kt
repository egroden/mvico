package com.egroden.teaco

class UiEffect<T>(initialValue: T) {
    var value = initialValue
        set(value) {
            if (field != value)
                field = value
            change?.invoke(value)
        }

    var change: ((T) -> Unit)? = null

    infix fun bind(body: (T) -> Unit) {
        change = body
    }

    fun unbind() {
        change = null
    }

}

class UiEffectScope {
    private val list = arrayListOf<UiEffect<*>>()

    fun <T> uiEffect(initialValue: T): UiEffect<T> =
        UiEffect(initialValue).also { list.add(it) }

    fun unbindAll() {
        for (uiEffect in list)
            uiEffect.unbind()
    }
}
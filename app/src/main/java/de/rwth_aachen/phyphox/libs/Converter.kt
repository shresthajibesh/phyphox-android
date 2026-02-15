package de.rwth_aachen.phyphox.libs

abstract class Converter<in I, out O> {
    abstract fun convert(item: I): O
    fun convert(items:List<I>):List<O>{
        return items.map { convert(it) }
    }
}

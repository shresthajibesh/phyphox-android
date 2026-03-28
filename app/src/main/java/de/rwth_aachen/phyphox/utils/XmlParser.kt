package de.rwth_aachen.phyphox.utils

interface XmlParser<in I, out O>{
    fun parse(input: I): O
}

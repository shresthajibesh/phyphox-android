package de.rwth_aachen.phyphox.features.experiment.old.parser.model

import java.io.InputStream

class PhyphoxStream {
    //is the stream a local resource? (asset or private file)
    var isLocal: Boolean = false

    //the input stream or null on error
    var inputStream: InputStream? = null

    //A copy of the input for non-local sources
    var source: ByteArray? = null

    //Error message that can be displayed to the user
    var errorMessage: String = ""

    //Local folder that holds the resources required by the experiment (for example images)
    var resourceFolder: String? = null
    var crc32: Long = 0


}

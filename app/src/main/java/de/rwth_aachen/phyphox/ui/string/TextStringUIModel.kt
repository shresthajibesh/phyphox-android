package shresthajibesh.cookiejar.lib.text

import android.content.Context
import de.rwth_aachen.phyphox.ui.string.StringUIModel

class TextStringUIModel(val value: String) : StringUIModel() {
    override fun resolve(context: Context): String = value
}

package com.sc.tmp_translate.utils.record

object AudioCardUtils {

    data class SoundCardInfo(
        val index: Int,
        val name: String,
        val description: String
    ) {
        override fun toString(): String {
            return "SoundCardInfo(index=$index, name='$name', description='$description')"
        }
    }

    interface OnCardInfoReadListener {
        fun onReadCompleted(allCards: List<SoundCardInfo>, usbCards: List<SoundCardInfo>)
        fun onReadFailed(error: String)
    }

    fun readSoundCards(listener: OnCardInfoReadListener) {
        Thread {
            try {
                val process = Runtime.getRuntime().exec("cat /proc/asound/cards")
                val reader = process.inputStream.bufferedReader()
                val output = reader.readLines()
                reader.close()
                process.waitFor()

                val allCards = mutableListOf<SoundCardInfo>()
                var i = 0
                while (i < output.size) {
                    val line = output[i].trim()
                    val nextLine = if (i + 1 < output.size) output[i + 1].trim() else ""

                    // 解析如：0 [UACDemo        ]: USB-Audio - UACDemo
                    val regex = Regex("""(\d+)\s+\[(.+?)\]:\s+(.+?)\s+-\s+(.+)""")
                    val match = regex.find(line)
                    if (match != null) {
                        val index = match.groupValues[1].toInt()
                        val name = match.groupValues[2].trim()
                        val desc = nextLine.ifEmpty { match.groupValues[4].trim() }
                        allCards.add(SoundCardInfo(index, name, desc))
                    }

                    i += 2
                }

                val usbCards = allCards.filter {
                    it.name.contains("usb", true) ||
                    it.description.contains("usb", true) ||
                    it.description.contains("mic", true)
                }

                listener.onReadCompleted(allCards, usbCards)
            } catch (e: Exception) {
                listener.onReadFailed(e.message ?: "Unknown error")
            }
        }.start()
    }
}

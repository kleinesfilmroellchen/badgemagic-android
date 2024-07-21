package org.fossasia.badgemagic.device

import com.soywiz.klock.DateTime
import org.fossasia.badgemagic.data.DataToSend
import kotlin.experimental.or
import org.fossasia.badgemagic.utils.ByteArrayUtils

object DataToByteArrayConverter {

    private const val MAX_MESSAGES = 8
    private const val PACKET_START = "77616E670000"
    private const val PACKET_BYTE_SIZE = 16

    val CHAR_CODES = mapOf(
            '0' to "007CC6CEDEF6E6C6C67C00",
            '1' to "0018387818181818187E00",
            '2' to "007CC6060C183060C6FE00",
            '3' to "007CC606063C0606C67C00",
            '4' to "000C1C3C6CCCFE0C0C1E00",
            '5' to "00FEC0C0FC060606C67C00",
            '6' to "007CC6C0C0FCC6C6C67C00",
            '7' to "00FEC6060C183030303000",
            '8' to "007CC6C6C67CC6C6C67C00",
            '9' to "007CC6C6C67E0606C67C00",
            '#' to "006C6CFE6C6CFE6C6C0000",
            '&' to "00386C6C3876DCCCCC7600",
            '_' to "00000000000000000000FF",
            '-' to "0000000000FE0000000000",
            '?' to "007CC6C60C181800181800",
            '@' to "00003C429DA5ADB6403C00",
            '(' to "000C183030303030180C00",
            ')' to "0030180C0C0C0C0C183000",
            '=' to "0000007E00007E00000000",
            '+' to "00000018187E1818000000",
            '!' to "00183C3C3C181800181800",
            '\'' to "1818081000000000000000",
            ':' to "0000001818000018180000",
            '%' to "006092966C106CD2920C00",
            '/' to "000002060C183060C08000",
            '"' to "6666222200000000000000",
            ' ' to "0000000000000000000000",
            '*' to "000000663CFF3C66000000",
            ',' to "0000000000000030301020",
            '.' to "0000000000000000303000",
            '$' to "107CD6D6701CD6D67C1010",
            '~' to "0076DC0000000000000000",
            '[' to "003C303030303030303C00",
            ']' to "003C0C0C0C0C0C0C0C3C00",
            '{' to "000E181818701818180E00",
            '}' to "00701818180E1818187000",
            '<' to "00060C18306030180C0600",
            '>' to "006030180C060C18306000",
            '^' to "386CC60000000000000000",
            '`' to "1818100800000000000000",
            ';' to "0000001818000018180810",
            '\\' to "0080C06030180C06020000",
            '|' to "0018181818001818181800",
            'a' to "00000000780C7CCCCC7600",
            'b' to "00E060607C666666667C00",
            'c' to "000000007CC6C0C0C67C00",
            'd' to "001C0C0C7CCCCCCCCC7600",
            'e' to "000000007CC6FEC0C67C00",
            'f' to "001C363078303030307800",
            'g' to "00000076CCCCCC7C0CCC78",
            'h' to "00E060606C76666666E600",
            'i' to "0018180038181818183C00",
            'j' to "0C0C001C0C0C0C0CCCCC78",
            'k' to "00E06060666C78786CE600",
            'l' to "0038181818181818183C00",
            'm' to "00000000ECFED6D6D6C600",
            'n' to "00000000DC666666666600",
            'o' to "000000007CC6C6C6C67C00",
            'p' to "000000DC6666667C6060F0",
            'q' to "0000007CCCCCCC7C0C0C1E",
            'r' to "00000000DE76606060F000",
            's' to "000000007CC6701CC67C00",
            't' to "00103030FC303030341800",
            'u' to "00000000CCCCCCCCCC7600",
            'v' to "00000000C6C6C66C381000",
            'w' to "00000000C6D6D6D6FE6C00",
            'x' to "00000000C66C38386CC600",
            'y' to "000000C6C6C6C67E060CF8",
            'z' to "00000000FE8C183062FE00",
            'A' to "00386CC6C6FEC6C6C6C600",
            'B' to "00FC6666667C666666FC00",
            'C' to "007CC6C6C0C0C0C6C67C00",
            'D' to "00FC66666666666666FC00",
            'E' to "00FE66626878686266FE00",
            'F' to "00FE66626878686060F000",
            'G' to "007CC6C6C0C0CEC6C67E00",
            'H' to "00C6C6C6C6FEC6C6C6C600",
            'I' to "003C181818181818183C00",
            'J' to "001E0C0C0C0C0CCCCC7800",
            'K' to "00E6666C6C786C6C66E600",
            'L' to "00F060606060606266FE00",
            'M' to "0082C6EEFED6C6C6C6C600",
            'N' to "0086C6E6F6DECEC6C6C600",
            'O' to "007CC6C6C6C6C6C6C67C00",
            'P' to "00FC6666667C606060F000",
            'Q' to "007CC6C6C6C6C6D6DE7C06",
            'R' to "00FC6666667C6C6666E600",
            'S' to "007CC6C660380CC6C67C00",
            'T' to "007E7E5A18181818183C00",
            'U' to "00C6C6C6C6C6C6C6C67C00",
            'V' to "00C6C6C6C6C6C66C381000",
            'W' to "00C6C6C6C6D6FEEEC68200",
            'X' to "00C6C66C7C387C6CC6C600",
            'Y' to "00666666663C1818183C00",
            'Z' to "00FEC6860C183062C6FE00",
            '¶' to "003E7A7A7A3A1A0A0A0A00",
            '£' to "001C222220782020207E00",
            '∆' to "001010282844444482FE00",
            '°' to "0038283800000000000000",
            '€' to "000E10207E207E20100E00",
            '¢' to "00081C20404040201C0800",
            '¥' to "0082444428103810381000",
            '$' to "00287EA8A87C2A2AFC2800",
            'π' to "000000007E242424640000",
            '₹' to "007C087C08702010080400",
            '•' to "0000000000001818000000",
            '×' to "0000006C7C387C6C000000",
            '÷' to "00000010007C0010000000",
            '√' to "0004040C08482828181000",
            '₱' to "003CFF22FF3C2020202000"
    )

    fun convert(data: DataToSend): List<ByteArray> {
        println(data)
        check(data.messages.size <= MAX_MESSAGES) { "Max messages=$MAX_MESSAGES" }

        return StringBuilder()
                .apply {
                    append(PACKET_START)
                    append(getFlash(data))
                    append(getMarquee(data))
                    append(getOptions(data))
                    append(getSizes(data))
                    append("000000000000")
                    append(getTimestamp())
                    append("00000000")
                    append("00000000000000000000000000000000")
                    append(getMessages(data))
                    append(fillWithZeros(length))
                }
                .toString()
                .chunked(PACKET_BYTE_SIZE * 2)
                .map { ByteArrayUtils.hexStringToByteArray(it) }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun getFlash(data: DataToSend): String {
        val flashByte = ByteArray(1)

        data.messages.forEachIndexed { index, message ->
            val flashFlag = if (message.flash) 1 else 0
            flashByte[0] = flashByte[0] or (flashFlag shl index).toByte()
        }
        println("${flashByte[0].toHexString()}")

        return ByteArrayUtils.byteArrayToHexString(flashByte)
    }

    private fun getMarquee(data: DataToSend): String {
        val marqueeByte = ByteArray(1)

        data.messages.forEachIndexed { index, message ->
            val marqueeFlag = if (message.marquee) 1 else 0
            marqueeByte[0] = marqueeByte[0] or (marqueeFlag shl index).toByte()
        }

        return ByteArrayUtils.byteArrayToHexString(marqueeByte)
    }

    private fun getOptions(data: DataToSend): String {
        val nbMessages = data.messages.size
        return data.messages
                .map { it.speed.hexValue or it.mode.hexValue }
                .map { ByteArray(1).apply { set(0, it) } }.joinToString(separator = "", postfix = "00".repeat(MAX_MESSAGES - nbMessages)) { ByteArrayUtils.byteArrayToHexString(it) }
    }

    private fun getSizes(data: DataToSend): String {
        val nbMessages = data.messages.size

        return data.messages
                .map { it.hexStrings.size }
                .map {
                    ByteArray(2).apply {
                        set(1, (it and 0xFF).toByte())
                        set(0, (it shr 8 and 0xFF).toByte())
                    }
                }.joinToString(separator = "", postfix = "0000".repeat(MAX_MESSAGES - nbMessages)) { ByteArrayUtils.byteArrayToHexString(it) }
    }

    private fun getTimestamp(): String {
        val currentTime: DateTime = DateTime.now()
        return ByteArrayUtils.byteArrayToHexString(ByteArray(6).apply {
            set(0, (currentTime.yearInt and 0xFF).toByte())
            set(1, (currentTime.month1 and 0xFF).toByte())
            set(2, (currentTime.dayOfMonth and 0xFF).toByte())
            set(3, (currentTime.hours and 0xFF).toByte())
            set(4, (currentTime.minutes and 0xFF).toByte())
            set(5, (currentTime.seconds and 0xFF).toByte())
        })
    }

    private fun getMessages(data: DataToSend): String {
        return data.messages
                .joinToString(separator = "") {
                    it.hexStrings.joinToString(separator = "")
                }
    }

    private fun fillWithZeros(length: Int): String {
        return "0".repeat((length / (PACKET_BYTE_SIZE * 2) + 1) * PACKET_BYTE_SIZE * 2 - length)
    }
}

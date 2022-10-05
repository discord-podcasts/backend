import com.github.discordPodcasts.globalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File

private val file = File("accounts.json")
private val json = Json
val accounts = mutableMapOf<String, String>()

fun loadAccountManager() {
    loadAccounts()
    listenForCommands()
}

private fun loadAccounts() {
    if (!file.exists()) file.printWriter().use { it.write("[]") }

    val text = file.bufferedReader().readText()
    val array = json.decodeFromString<JsonArray>(text)
    array.forEach {
        val account = it.jsonObject
        val id = account["id"]!!.jsonPrimitive.content
        val secret = account["secret"]!!.jsonPrimitive.content
        accounts[id] = secret
    }
    println(accounts)
}

private fun listenForCommands() {
    globalScope.launch {
        while (true) {
            val input = readln()
            if (input == "create_account") {
                val id = generateId()
                val secret = generateSecret()
                accounts[id] = secret
                file.writer().use {
                    val accounts = accounts.map { acc ->
                        """
                            |{
                            |   "id": "${acc.key}",
                            |   "secret": "${acc.value}"
                            |}
                        """.trimMargin()
                    }.joinToString(",\n")
                    it.append("[\n$accounts\n]")
                }
                println("Created account\nid=$id\nsecret=$secret")
            } else {
                println("Create account = create_account")
            }
        }
    }
}

private fun generateId(): String {
    val chars = "1234567890"
    val id = (0..15).map { chars.random() }.joinToString("")
    return if (accounts.keys.contains(id)) generateId()
    else id
}

private fun generateSecret(): String {
    val chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890-_"
    val secret = (0..25).map { chars.random() }.joinToString("")
    return if (accounts.values.contains(secret)) generateSecret()
    else secret
}
package tech.jaya.jhony.octoevents

import io.javalin.Javalin
import io.javalin.core.validation.Validator
import io.javalin.http.BadRequestResponse
import io.javalin.plugin.json.JavalinJson
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.KoinComponent
import org.koin.core.inject
import tech.jaya.jhony.octoevents.event.EventDto
import tech.jaya.jhony.octoevents.event.EventService
import tech.jaya.jhony.octoevents.event.Events
import tech.jaya.jhony.octoevents.issue.Issues
import tech.jaya.jhony.octoevents.repository.Repositories
import tech.jaya.jhony.octoevents.user.Users
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import javax.management.openmbean.InvalidKeyException
import org.apache.commons.codec.DecoderException
import org.apache.commons.codec.binary.Hex


class ApplicationServerComponent : KoinComponent {

    private val SIGNATURE_PREFIX = "sha1="
    private val HMAC_SHA1_ALGORITHM = "HmacSHA1"

    private val eventService by inject<EventService<EventDto>>()
    private var app: Javalin = Javalin.create()
    private var webhookSecret = byteArrayOf()
    private var port = 0

    private fun init() {
        val props = loadProperties()
        port = props.getProperty("application.port").toInt()
        val secret = props.getProperty("application.secret")
        if (secret.isNotBlank()) {
            webhookSecret = secret.toByteArray(StandardCharsets.UTF_8)
        }
        configureRoutes()
        configureDatabase(props)
    }

    fun start() {
        if (port == 0) {
            init()
        }
        app.start(port)
    }

    fun stop() {
        app.stop()
    }

    private fun loadProperties() : Properties {
        return javaClass.classLoader.getResourceAsStream("application.properties").use { `in` ->
            val props = Properties()
            props.load(`in`)
            props
        }
    }

    private fun configureRoutes() {
        app.routes {
            app.get("/issues/:number/events") { ctx ->
                val issueNumber = ctx.pathParam("number").toLong()
                ctx.json(eventService.findByIssueNumber(issueNumber))
            }

            app.post("/events") { ctx ->
                val signatureHeader = ctx.req.getHeader("X-Hub-Signature") ?: ""
                val body = ctx.body()
                if (validateSecret(body, signatureHeader)) {
                    val event = toJson<EventDto>(body)
                    eventService.create(event)
                } else {
                    ctx.res.sendError(403, "Invalid signature: $signatureHeader")
                }
            }
        }
    }

    private fun configureDatabase(props: Properties) {
        Database.connect(
            props.getProperty("database.url"),
            driver = props.getProperty("database.driver"),
            user = props.getProperty("database.user"),
            password = props.getProperty("database.password"))

        if (props.getProperty("database.create-on-startup") == "true") {
            transaction {
                SchemaUtils.create(Users, Issues, Repositories, Events)
            }
        }
    }

    private inline fun <reified T : Any> toJson(body: String): T = toJson(body, T::class.java)

    private fun <T> toJson(body: String, clazz: Class<T>) : T {
        try {
            return Validator(JavalinJson.fromJson(body, clazz), "Request body as ${clazz.simpleName}").get()
        } catch (e: Exception) {
            Javalin.log?.debug("Couldn't deserialize body to ${clazz.simpleName}", e)
            throw BadRequestResponse("Couldn't deserialize body to ${clazz.simpleName}")
        }
    }

    private fun validateSecret(bodyContent: String, signatureHeader: String) : Boolean {
        if (webhookSecret.isEmpty()) {
            return true
        }

        val payload: ByteArray = bodyContent.toByteArray(StandardCharsets.UTF_8)
        val signature = try {
            Hex.decodeHex(signatureHeader.substring(SIGNATURE_PREFIX.length).toCharArray())
        } catch (exception: DecoderException) {
            return false
        }
        return MessageDigest.isEqual(signature, getExpectedSignature(payload))
    }

    private fun getExpectedSignature(payload: ByteArray): ByteArray? {
        val key = SecretKeySpec(webhookSecret, HMAC_SHA1_ALGORITHM)
        val hmac: Mac
        try {
            hmac = Mac.getInstance(HMAC_SHA1_ALGORITHM)
            hmac.init(key)
        } catch (e: NoSuchAlgorithmException) {
            throw IllegalStateException("Hmac SHA1 must be supported", e)
        } catch (e: InvalidKeyException) {
            throw IllegalStateException("Hmac SHA1 must be compatible to Hmac SHA1 Secret Key", e)
        }
        return hmac.doFinal(payload)
    }
}
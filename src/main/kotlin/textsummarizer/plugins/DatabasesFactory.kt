package textsummarizer.plugins

import com.typesafe.config.ConfigFactory
import io.ktor.server.config.*
import org.apache.commons.dbcp2.BasicDataSource
import org.flywaydb.core.Flyway
import org.ktorm.database.Database
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("DatabaseFactory")

/**
 * Database connection object.
 *
 * Sets up database connection pooling.
 */
object DatabaseFactory {

    private val config = HoconApplicationConfig(ConfigFactory.load())

    val db: Database

    init {
        logger.info("Initializing Database")
        val dataSource = setupDataSource()
        Flyway.configure().dataSource(dataSource).load().migrate()
        db = Database.connect(dataSource)
        logger.info("Database connected")
    }

    private fun setupDataSource(): BasicDataSource {
        return BasicDataSource().apply {
            driverClassName = config.property("db.driver").getString()
            url = config.property("db.url").getString()
            username = config.property("db.user").getString()
            password = config.property("db.password").getString()
            minIdle = 5
            maxTotal = 30
            maxWaitMillis = 5_000
            validationQuery = "SELECT 1"
            testOnBorrow = true
        }
    }

}
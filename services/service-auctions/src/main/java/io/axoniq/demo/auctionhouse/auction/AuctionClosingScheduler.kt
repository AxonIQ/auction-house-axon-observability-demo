package io.axoniq.demo.auctionhouse.auction

import org.axonframework.messaging.commandhandling.gateway.CommandGateway
import org.axonframework.messaging.eventhandling.annotation.EventHandler
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

@Service
class AuctionClosingScheduler(
    private val commandGateway: CommandGateway,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(2)

    @EventHandler
    fun on(event: AuctionCreated) {
        val delay = Duration.between(Instant.now(), event.endTime).toMillis().coerceAtLeast(0)
        logger.info("Scheduling close for auction {} in {}ms", event.auctionId, delay)
        scheduler.schedule({
            commandGateway.send(CloseAuction(event.auctionId))
        }, delay, TimeUnit.MILLISECONDS)
    }
}

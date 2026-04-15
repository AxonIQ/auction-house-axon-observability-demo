/*
 * Copyright (c) 2023-2023. AxonIQ
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.axoniq.demo.auctionhouse.auction

import org.axonframework.common.IdentifierFactory
import org.axonframework.eventsourcing.annotation.EventSourcingHandler
import org.axonframework.eventsourcing.annotation.reflection.EntityCreator
import org.axonframework.extension.spring.stereotype.EventSourced
import org.axonframework.messaging.commandhandling.annotation.CommandHandler
import org.axonframework.messaging.eventhandling.gateway.EventAppender
import java.time.Instant

@EventSourced(tagKey = "Auction")
class Auction {
    private lateinit var id: String
    private lateinit var objectId: String
    private lateinit var owner: String
    private lateinit var endTime: Instant
    private var minimalBid: Long = 0
    private var state = State.CREATED
    private val bids = mutableListOf<Bid>()
    private val winningBid: Bid?
        get() = bids.maxByOrNull { it.amount }

    enum class State {
        CREATED, STARTED, ENDED, REVERTED
    }

    @EntityCreator
    constructor()

    companion object {
        @JvmStatic
        @CommandHandler
        fun create(command: CreateAuction, eventAppender: EventAppender): String? {
            val endTime = Instant.now().plusSeconds(10)
            val auctionId = IdentifierFactory.getInstance().generateIdentifier()
            eventAppender.append(
                AuctionCreated(
                    auctionId,
                    command.auctionHouseId,
                    command.objectId,
                    command.owner,
                    command.minimumPrice,
                    endTime
                )
            )
            return auctionId
        }
    }

    @CommandHandler
    fun on(command: PlaceBidOnAuction, eventAppender: EventAppender): Boolean {
        if (state != State.STARTED) {
            return false
        }
        if (Instant.now().isAfter(endTime)) {
            return false;
        }
        val highest = winningBid?.amount ?: minimalBid
        if (highest >= command.price) {
            return false
        }
        eventAppender.append(BidPlacedOnAuction(id, command.participantId, command.price))
        return true
    }

    @CommandHandler
    fun on(command: RevertAuction, eventAppender: EventAppender) {
        eventAppender.append(AuctionReverted(id, objectId, owner, command.reason))
    }

    @CommandHandler
    fun on(command: CloseAuction, eventAppender: EventAppender) {
        if (state != State.STARTED) {
            return
        }
        val winningBid = bids.maxByOrNull { it.amount }
        eventAppender.append(
            AuctionEnded(
                auctionId = id,
                winningParticipant = winningBid?.participantId,
                winningPrice = winningBid?.amount,
                endTime = Instant.now(),
                seller = owner,
                objectId = objectId
            )
        )
    }

    @EventSourcingHandler
    fun on(event: AuctionCreated) {
        this.id = event.auctionId
        this.objectId = event.objectId
        this.state = State.STARTED
        this.owner = event.owner
        this.minimalBid = event.minimumPrice
        this.endTime = event.endTime
    }

    @EventSourcingHandler
    fun on(event: AuctionEnded) {
        this.state = State.ENDED
    }

    @EventSourcingHandler
    fun on(event: AuctionReverted) {
        this.state = State.REVERTED
    }

    @EventSourcingHandler
    fun on(event: BidPlacedOnAuction) {
        bids.add(Bid(event.participant, event.price))
    }
}

data class Bid(
    val participantId: String, val amount: Long
)

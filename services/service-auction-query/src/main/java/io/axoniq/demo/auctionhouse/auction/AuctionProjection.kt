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

import io.axoniq.demo.auctionhouse.auction.*
import org.axonframework.messaging.eventhandling.annotation.EventHandler
import org.axonframework.messaging.queryhandling.QueryUpdateEmitter
import org.axonframework.messaging.queryhandling.annotation.QueryHandler
import org.springframework.stereotype.Service

@Service
class AuctionProjection(
    private val repository: AuctionInfoRepository
) {
    @EventHandler
    fun on(event: AuctionCreated, queryUpdateEmitter: QueryUpdateEmitter) {
        val auction = repository.save(
            AuctionInformation(
                event.auctionId,
                event.auctionHouseId,
                event.objectId,
                ActiveAuctionState.STARTED,
                event.minimumPrice,
                0,
                null,
                event.endTime
            )
        )
        auction.emit(queryUpdateEmitter)
    }

    @EventHandler
    fun on(event: AuctionEnded, queryUpdateEmitter: QueryUpdateEmitter) {
        val auction = repository.findById(event.auctionId).orElseThrow()
        auction.state = ActiveAuctionState.ENDED
        auction.emit(queryUpdateEmitter)
    }

    @EventHandler
    fun on(event: AuctionReverted, queryUpdateEmitter: QueryUpdateEmitter) {
        val auction = repository.findById(event.auctionId).orElseThrow()
        auction.state = ActiveAuctionState.REVERTED
        auction.emit(queryUpdateEmitter)
    }

    @EventHandler
    fun on(event: BidPlacedOnAuction, queryUpdateEmitter: QueryUpdateEmitter) {
        val auction = repository.findById(event.auctionId).orElseThrow()
        auction.currentBid = event.price
        auction.currentBidder = event.participant
        auction.emit(queryUpdateEmitter)
    }

    private fun AuctionInformation.emit(queryUpdateEmitter: QueryUpdateEmitter) {
        queryUpdateEmitter.emit(GetActiveAuctions::class.java, { it.auctionHouseId == this.auctionHouseId }, this.toDto())
    }

    @QueryHandler
    fun on(query: GetActiveAuctions): List<ActiveAuctionItem> {
        return repository.findAllByStateAndAuctionHouseId(ActiveAuctionState.STARTED, query.auctionHouseId).map { it.toDto() }
    }

    fun AuctionInformation.toDto() =
        ActiveAuctionItem(identifier, objectId, state, minimumBid, currentBid, currentBidder, endTime)
}

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

package io.axoniq.demo.auctionhouse.interfaces

import io.axoniq.demo.auctionhouse.auction.ActiveAuctionItem
import io.axoniq.demo.auctionhouse.auction.GetActiveAuctions
import io.axoniq.demo.auctionhouse.interfaces.simulation.GetSimulatedParticipants
import io.axoniq.demo.auctionhouse.interfaces.simulation.SimulatedParticipantDtoResponse
import io.axoniq.demo.auctionhouse.interfaces.simulation.SimulatedParticipantOrchestrator
import io.axoniq.demo.auctionhouse.objectsregistry.AuctionOwnershipInfoItem
import io.axoniq.demo.auctionhouse.objectsregistry.GetAuctionObjects
import org.axonframework.messaging.queryhandling.gateway.QueryGateway
import org.springframework.http.codec.ServerSentEvent
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers
import java.time.Duration

@RestController
@RequestMapping("api")
class FrontendController(
    private val orchestrator: SimulatedParticipantOrchestrator,
    private val queryGateway: QueryGateway,
) {
    @GetMapping("participants/count")
    fun getPartcipantCount() = orchestrator.desiredCount


    @PutMapping("participants/count/{amount}")
    fun setPartcipantCount(@PathVariable amount: Int) {
        orchestrator.desiredCount = amount
    }

    @GetMapping("auctions")
    fun auctions(): Flux<ServerSentEvent<*>> {
        return Flux.from(queryGateway.subscriptionQuery(
                GetActiveAuctions(orchestrator.auctionHouseId),
                ActiveAuctionItem::class.java
            ))
            .buffer(Duration.ofMillis(50))
            .asServerSentEvents()
            .withHeartbeat()
    }

    @GetMapping("participants")
    fun participants(): Flux<ServerSentEvent<*>> {
        return Flux.from(queryGateway.subscriptionQuery(
                GetSimulatedParticipants(),
                SimulatedParticipantDtoResponse::class.java
            ))
            .publishOn(Schedulers.boundedElastic())
            .map { it.participants }
            .asServerSentEvents()
            .withHeartbeat()
    }

    @GetMapping("ownership")
    fun ownership(): Flux<ServerSentEvent<*>> {
        return Flux.from(queryGateway.subscriptionQuery(
                GetAuctionObjects(orchestrator.auctionHouseId),
                AuctionOwnershipInfoItem::class.java
            ))
            .buffer(Duration.ofMillis(50))
            .asServerSentEvents()
            .withHeartbeat()
    }
}


fun <T : Any> Flux<T>.asServerSentEvents() = this.map { update ->
    ServerSentEvent.builder<T>()
        .event("message")
        .data(update)
        .build()
}

fun <T : Any> Flux<ServerSentEvent<T>>.withHeartbeat(): Flux<ServerSentEvent<*>> {
    return Flux.merge(this, Flux.interval(Duration.ofSeconds(2))
        .publishOn(Schedulers.boundedElastic())
        .map { _ ->
            ServerSentEvent.builder<String>()
                .event("ping")
                .build()
        })
}

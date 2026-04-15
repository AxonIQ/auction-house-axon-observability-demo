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

package io.axoniq.demo.auctionhouse.objectregistry

import io.axoniq.demo.auctionhouse.objectsregistry.AuctionObjectOwnerTransferred
import io.axoniq.demo.auctionhouse.objectsregistry.AuctionObjectSubmitted
import io.axoniq.demo.auctionhouse.objectsregistry.SubmitAuctionObject
import io.axoniq.demo.auctionhouse.objectsregistry.TransferAuctionObject
import org.axonframework.common.IdentifierFactory
import org.axonframework.eventsourcing.annotation.EventSourcingHandler
import org.axonframework.eventsourcing.annotation.reflection.EntityCreator
import org.axonframework.extension.spring.stereotype.EventSourced
import org.axonframework.messaging.commandhandling.annotation.CommandHandler
import org.axonframework.messaging.eventhandling.gateway.EventAppender

@EventSourced(tagKey = "Object")
class AuctionObject {
    private lateinit var id: String
    private lateinit var owner: String

    @EntityCreator
    constructor()

    companion object {
        @JvmStatic
        @CommandHandler
        fun create(command: SubmitAuctionObject, eventAppender: EventAppender): String? {
            val id = IdentifierFactory.getInstance().generateIdentifier()
            eventAppender.append(
                AuctionObjectSubmitted(
                    id,
                    command.name,
                    command.initialOwner,
                    command.auctionHouseId,
                )
            )
            return id
        }
    }

    @CommandHandler
    fun on(command: TransferAuctionObject, eventAppender: EventAppender) {
        eventAppender.append(AuctionObjectOwnerTransferred(id, owner, command.owner))
    }

    @EventSourcingHandler
    fun on(event: AuctionObjectSubmitted) {
        this.id = event.id
        this.owner = event.owner
    }

    @EventSourcingHandler
    fun on(event: AuctionObjectOwnerTransferred) {
        this.owner = event.newOwner
    }
}

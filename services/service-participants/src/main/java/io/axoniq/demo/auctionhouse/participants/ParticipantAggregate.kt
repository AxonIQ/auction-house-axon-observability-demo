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

package io.axoniq.demo.auctionhouse.participants

import org.axonframework.common.IdentifierFactory
import org.axonframework.eventsourcing.annotation.EventSourcingHandler
import org.axonframework.eventsourcing.annotation.reflection.EntityCreator
import org.axonframework.extension.spring.stereotype.EventSourced
import org.axonframework.messaging.commandhandling.annotation.CommandHandler
import org.axonframework.messaging.eventhandling.gateway.EventAppender

@EventSourced(tagKey = "Participant")
class ParticipantAggregate {
    private lateinit var id: String
    private var email: String? = null
    private var balance = 0L

    @EntityCreator
    constructor()

    companion object {
        @JvmStatic
        @CommandHandler
        fun create(command: RegisterParticipant, eventAppender: EventAppender): String? {
            val id = IdentifierFactory.getInstance().generateIdentifier()
            eventAppender.append(ParticipantEmailConfirmed(id, command.requestedEmail))
            eventAppender.append(BalanceAddedToParticipant(id, "0", 5000, 5000, "New account creation"))
            return id
        }
    }

    @CommandHandler
    fun on(command: ChangeParticipantEmail, eventAppender: EventAppender) {
        eventAppender.append(ParticipantEmailConfirmed(command.id, command.requestedEmail))
    }

    @CommandHandler
    fun on(command: DeductBalance, eventAppender: EventAppender) {
        eventAppender.append(BalanceDeducted(id, command.reference, command.amount, balance - command.amount))
    }

    @CommandHandler
    fun on(command: IncreaseBalance, eventAppender: EventAppender) {
        eventAppender.append(BalanceAddedToParticipant(id, command.reference, command.amount, balance + command.amount, ""))
    }

    @EventSourcingHandler
    fun on(event: ParticipantEmailConfirmed) {
        this.email = event.email
    }

    @EventSourcingHandler
    fun on(event: BalanceDeducted) {
        this.balance = event.newBalance
    }

    @EventSourcingHandler
    fun on(event: BalanceAddedToParticipant) {
        this.balance = event.newBalance
    }
}

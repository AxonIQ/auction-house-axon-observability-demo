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

import io.axoniq.demo.auctionhouse.auction.AuctionEnded
import io.axoniq.demo.auctionhouse.auction.RevertAuction
import io.axoniq.demo.auctionhouse.objectsregistry.TransferAuctionObject
import io.axoniq.demo.auctionhouse.participants.DeductBalance
import io.axoniq.demo.auctionhouse.participants.IncreaseBalance
import io.axoniq.workflow.dsl.kotlin.Kontext
import io.axoniq.workflow.runtime.api.annotation.Workflow
import io.axoniq.workflow.runtime.api.execution.state.WorkflowStepResult
import org.axonframework.messaging.commandhandling.gateway.CommandGateway
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.time.Duration.Companion.seconds

@Component
class TransferOfOwnershipSaga(
    private val commandGateway: CommandGateway
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Workflow(idProperty = "auctionId", startOnEvent = "io.axoniq.demo.auctionhouse.auction.AuctionEnded")
    fun Kontext.onExecute() {
        logger.info("Executing TransferOfOwnershipSaga")
        if (payload["winningParticipant"] == null) {
            logger.info("No winner found, skipping")
            return
        }


        try {
            awaitExecute("deductBalance", { ctx ->
                commandGateway.sendAndWait(
                    DeductBalance(
                        payload["buyer"] as String,
                        payload["auctionId"] as String,
                        payload["amount"] as Long
                    )
                )
            })
        } catch (e: Exception) {
            commandGateway.sendAndWait(
                RevertAuction(
                    payload["auctionId"] as String,
                    "Something went wrong during the transfer of ownership!"
                )
            )
            return
        }

        logger.info("Deducted balance from ${payload["buyer"]} to ${payload["auctionId"]}")

        val increaseBalance = execute(
            stepName = "increaseBalanceOfSeller",
            timeout = 30.seconds,
            action = { ctx, p ->
                commandGateway.sendAndWait(
                    IncreaseBalance(
                        payload["seller"] as String,
                        payload["auctionId"] as String,
                        payload["amount"] as Long
                    )
                )
                emptyMap()
            })

        val transferObject = execute(
            stepName = "transferObject",
            timeout = 30.seconds,
            action = { ctx, p ->
                commandGateway.sendAndWait(
                    TransferAuctionObject(
                        payload["objectId"] as String,
                        payload["buyer"] as String
                    )
                )
                emptyMap()
            })

        // Now we can do two actions at the same time
        allMatch(
            predicate = WorkflowStepResult::success,
            transferObject,
            increaseBalance
        )
    }
}

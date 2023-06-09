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

import java.time.Instant

data class ActiveAuctionItem(
    val identifier: String,
    val objectId: String,
    val state: ActiveAuctionState,
    var minimumBid: Long?,
    var currentBid: Long?,
    var currentBidder: String?,
    var endTime: Instant?,
)

data class ActiveAuctionsResponse(
    val auctions: List<ActiveAuctionItem>
)

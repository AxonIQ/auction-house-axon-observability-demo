package io.axoniq.demo.auctionhouse.auction

import org.axonframework.modelling.annotation.TargetEntityId

data class CloseAuction(
    @TargetEntityId
    val auctionId: String,
)

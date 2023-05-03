package io.axoniq.demo.auctionhouse.objectregistry

import jakarta.persistence.Entity
import jakarta.persistence.Id


@Entity
data class AuctionObjectInformation(
    @Id
    val identifier: String,
    var name: String,
    var owner: String,
    val auctionHouseId: String,
)

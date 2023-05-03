package io.axoniq.demo.auctionhouse.objectsregistry


data class AuctionOwnershipInfoItem(
    val identifier: String,
    val name: String,
    val owner: String,
)

data class AuctionOwnershipResponse(
    val items: List<AuctionOwnershipInfoItem>,
)

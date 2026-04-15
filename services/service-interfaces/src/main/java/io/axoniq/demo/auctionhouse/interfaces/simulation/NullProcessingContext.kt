package io.axoniq.demo.auctionhouse.interfaces.simulation

import org.axonframework.messaging.core.Context
import org.axonframework.messaging.core.unitofwork.ProcessingContext
import org.axonframework.messaging.core.unitofwork.ProcessingLifecycle
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Supplier
import java.util.function.UnaryOperator

class NullProcessingContext : ProcessingContext {
    override fun <T : Any> putResource(key: Context.ResourceKey<T>, resource: T): T = resource

    override fun <T : Any> updateResource(key: Context.ResourceKey<T>, resourceUpdater: UnaryOperator<T>): T =
        throw UnsupportedOperationException()

    override fun <T : Any> putResourceIfAbsent(key: Context.ResourceKey<T>, resource: T): T = resource

    override fun <T : Any> computeResourceIfAbsent(key: Context.ResourceKey<T>, resourceSupplier: Supplier<T>): T =
        resourceSupplier.get()

    override fun <T : Any> removeResource(key: Context.ResourceKey<T>): T =
        throw UnsupportedOperationException()

    override fun <T : Any> removeResource(key: Context.ResourceKey<T>, expectedResource: T): Boolean = false

    override fun isStarted(): Boolean = false
    override fun isError(): Boolean = false
    override fun isCommitted(): Boolean = false
    override fun isCompleted(): Boolean = false

    override fun on(
        phase: ProcessingLifecycle.Phase,
        action: Function<ProcessingContext, CompletableFuture<*>>
    ): ProcessingLifecycle = this

    override fun onError(action: ProcessingLifecycle.ErrorHandler): ProcessingLifecycle = this

    override fun whenComplete(action: Consumer<ProcessingContext>): ProcessingLifecycle = this

    override fun <C : Any> component(type: Class<C>, name: String?): C =
        throw UnsupportedOperationException()

    override fun containsResource(key: Context.ResourceKey<*>): Boolean = false

    override fun <T : Any> getResource(key: Context.ResourceKey<T>): T? = null

    override fun resources(): Map<Context.ResourceKey<*>, Any> = emptyMap()
}

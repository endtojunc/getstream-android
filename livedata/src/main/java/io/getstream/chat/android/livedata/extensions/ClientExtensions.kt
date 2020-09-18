package io.getstream.chat.android.livedata.extensions

import io.getstream.chat.android.client.api.models.QuerySort
import io.getstream.chat.android.client.errors.ChatError
import io.getstream.chat.android.client.errors.ChatNetworkError
import io.getstream.chat.android.client.models.Channel
import io.getstream.chat.android.client.models.ChannelUserRead
import io.getstream.chat.android.client.models.Member
import io.getstream.chat.android.client.models.Message
import io.getstream.chat.android.client.models.Reaction
import io.getstream.chat.android.livedata.entity.ChannelEntityPair
import io.getstream.chat.android.livedata.request.AnyChannelPaginationRequest
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties

private const val EQUAL_ON_COMPARISON = 0

/**
 * cid is sometimes devent as message.cid other times as message.channel.cid
 */
internal fun Message.getCid(): String =
    if (this.cid.isEmpty()) {
        this.channel.cid
    } else {
        this.cid
    }

internal fun Message.users() = latestReactions.mapNotNull(Reaction::user) + user

internal fun Channel.users() = members.map(Member::user) + read.map(ChannelUserRead::user) + createdBy

internal fun Message.addReaction(reaction: Reaction, isMine: Boolean) {

    // add to own reactions
    if (isMine) {
        this.ownReactions = this.ownReactions.toMutableList()
        this.ownReactions.add(reaction)
    }

    // add to latest reactions
    this.latestReactions = this.latestReactions.toMutableList()
    this.latestReactions.add(reaction)

    // update the count
    val currentCount = this.reactionCounts.getOrElse(reaction.type) { 0 }
    // copy the object so livedata's diffutils can notice a change
    this.reactionCounts = this.reactionCounts.toMutableMap()
    this.reactionCounts[reaction.type] = currentCount + 1
    // update the score
    val currentScore = this.reactionScores.getOrElse(reaction.type) { 0 }
    this.reactionScores = this.reactionScores.toMutableMap()
    this.reactionScores[reaction.type] = currentScore + reaction.score
}

internal fun Message.removeReaction(reaction: Reaction, updateCounts: Boolean) {

    val countBeforeFilter = ownReactions.size + latestReactions.size
    ownReactions = ownReactions.filterNot { it.type == reaction.type && it.userId == reaction.userId }.toMutableList()
    latestReactions =
        latestReactions.filterNot { it.type == reaction.type && it.userId == reaction.userId }.toMutableList()
    val countAfterFilter = ownReactions.size + latestReactions.size

    if (updateCounts) {
        val shouldDecrement = countBeforeFilter > countAfterFilter || this.latestReactions.size >= 15
        if (shouldDecrement) {
            this.reactionCounts = this.reactionCounts.toMutableMap()
            val currentCount = this.reactionCounts.getOrElse(reaction.type) { 1 }
            val newCount = currentCount - 1
            this.reactionCounts[reaction.type] = newCount
            if (newCount <= 0) {
                reactionCounts.remove(reaction.type)
            }
            this.reactionScores = this.reactionScores.toMutableMap()
            val currentScore = this.reactionScores.getOrElse(reaction.type) { 1 }
            val newScore = currentScore - reaction.score
            this.reactionScores[reaction.type] = newScore
            if (newScore <= 0) {
                reactionScores.remove(reaction.type)
            }
        }
    }
}

/**
 * Returns true if an error is a permanent failure instead of a temporary one (broken network, 500, rate limit etc.)
 */
fun ChatError.isPermanent(): Boolean {
    // errors without a networkError.streamCode should always be considered temporary
    // errors with networkError.statusCode 429 should be considered temporary
    // everything else is a permanent error
    var isPermanent = true
    if (this is ChatNetworkError) {
        val networkError: ChatNetworkError = this
        if (networkError.statusCode == 429) {
            isPermanent = false
        } else if (networkError.streamCode == 0) {
            isPermanent = false
        }
    } else {
        isPermanent = false
    }
    return isPermanent
}

internal fun Collection<ChannelEntityPair>.applyPagination(pagination: AnyChannelPaginationRequest): List<ChannelEntityPair> =
    sortedWith(pagination.sort.comparator).drop(pagination.channelOffset).take(pagination.channelLimit)

internal val QuerySort.comparator: Comparator<in ChannelEntityPair>
    get() =
        CompositeComparator(data.mapNotNull { it.comparator as? Comparator<ChannelEntityPair> })

internal val Map<String, Any>.comparator: Comparator<in ChannelEntityPair>?
    get() =
        (this["field"] as? String)?.let { fieldName ->
            (this["direction"] as? Int)?.let { sortDirection ->
                Channel::class.declaredMemberProperties
                    .find { it.name == fieldName }
                    ?.comparator(sortDirection)
            }
        }

internal fun KProperty1<Channel, *>?.comparator(sortDirection: Int): Comparator<ChannelEntityPair>? =
    this?.let { compareProperty ->
        Comparator { c0, c1 ->
            (compareProperty.getter.call(c0.channel) as? Comparable<Any>)?.let { a ->
                (compareProperty.getter.call(c1.channel) as? Comparable<Any>)?.let { b ->
                    a.compareTo(b) * sortDirection
                }
            } ?: EQUAL_ON_COMPARISON
        }
    }

internal class CompositeComparator<T>(private val comparators: List<Comparator<T>>) : Comparator<T> {
    override fun compare(o1: T, o2: T): Int =
        comparators.fold(EQUAL_ON_COMPARISON) { currentComparisonValue, comparator ->
            when (currentComparisonValue) {
                EQUAL_ON_COMPARISON -> comparator.compare(o1, o2)
                else -> currentComparisonValue
            }
        }
}

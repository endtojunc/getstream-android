package io.getstream.chat.android.livedata.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import io.getstream.chat.android.client.api.models.Pagination
import io.getstream.chat.android.client.utils.SyncStatus
import io.getstream.chat.android.livedata.BaseDomainTest
import io.getstream.chat.android.livedata.request.AnyChannelPaginationRequest
import io.getstream.chat.android.livedata.utils.calendar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class ChannelRepositoryTest: BaseDomainTest() {
    val repo by lazy { chatDomain.repos.channels }

    @Before
    fun setup() {
        client = createDisconnectedMockClient()
        setupChatDomain(client, false)
    }

    @After
    fun tearDown() {
        chatDomain.disconnect()
        db.close()
    }

    @Test
    fun testInsertAndRead() = runBlocking(Dispatchers.IO) {
        repo.insertChannel(data.channel1)
        val entity = repo.select(data.channel1.cid)
        val channel = entity!!.toChannel(data.userMap)
        Truth.assertThat(channel).isEqualTo(data.channel1)
    }

    @Test
    fun testUpdate() = runBlocking(Dispatchers.IO) {
        repo.insertChannel(data.channel1)
        repo.insertChannel(data.channel1Updated)
        val entity = repo.select(data.channel1.cid)
        val channel = entity!!.toChannel(data.userMap)
        Truth.assertThat(channel).isEqualTo(data.channel1Updated)
    }

    @Test
    fun testSyncNeeded() = runBlocking(Dispatchers.IO) {
        data.channel1.syncStatus = SyncStatus.SYNC_NEEDED
        data.channel2.syncStatus = SyncStatus.SYNCED

        repo.insertChannel(listOf(data.channel1, data.channel2))

        var channels = repo.selectSyncNeeded()
        Truth.assertThat(channels.size).isEqualTo(1)
        Truth.assertThat(channels.first().syncStatus).isEqualTo(SyncStatus.SYNC_NEEDED)

        channels = repo.retryChannels()
        Truth.assertThat(channels.size).isEqualTo(1)
        Truth.assertThat(channels.first().syncStatus).isEqualTo(SyncStatus.SYNCED)

        channels = repo.selectSyncNeeded()
        Truth.assertThat(channels.size).isEqualTo(0)
    }

}
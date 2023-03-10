/*
 * Copyright (c) 2014-2022 Stream.io Inc. All rights reserved.
 *
 * Licensed under the Stream License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://github.com/GetStream/stream-chat-android/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.getstream.chat.android.ui.search.internal

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.models.Message
import io.getstream.chat.android.client.setup.state.ClientState
import io.getstream.chat.android.ui.common.extensions.internal.asMention
import io.getstream.chat.android.ui.common.extensions.internal.context
import io.getstream.chat.android.ui.common.extensions.internal.streamThemeInflater
import io.getstream.chat.android.ui.databinding.StreamUiItemMentionListBinding
import io.getstream.chat.android.ui.message.preview.MessagePreviewStyle
import io.getstream.chat.android.ui.search.internal.SearchResultListAdapter.MessagePreviewViewHolder
import io.getstream.chat.android.ui.search.list.SearchResultListView.SearchResultSelectedListener

internal class SearchResultListAdapter(
    private val clientState: ClientState = ChatClient.instance().clientState,
) : ListAdapter<Message, MessagePreviewViewHolder>(MessageDiffCallback) {

    private var searchResultSelectedListener: SearchResultSelectedListener? = null

    var messagePreviewStyle: MessagePreviewStyle? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagePreviewViewHolder {
        return StreamUiItemMentionListBinding
            .inflate(parent.streamThemeInflater, parent, false)
            .let { binding ->
                messagePreviewStyle?.let(binding.root::styleView)
                MessagePreviewViewHolder(binding)
            }
    }

    override fun onBindViewHolder(holder: MessagePreviewViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun setSearchResultSelectedListener(searchResultSelectedListener: SearchResultSelectedListener?) {
        this.searchResultSelectedListener = searchResultSelectedListener
    }

    inner class MessagePreviewViewHolder(
        private val binding: StreamUiItemMentionListBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var message: Message

        init {
            binding.root.setOnClickListener {
                searchResultSelectedListener?.onSearchResultSelected(message)
            }
        }

        internal fun bind(message: Message) {
            this.message = message
            binding.root.setMessage(message, clientState.user.value?.asMention(context))
        }
    }

    private object MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            // Comparing only properties used by the ViewHolder
            return oldItem.id == newItem.id &&
                oldItem.createdAt == newItem.createdAt &&
                oldItem.createdLocallyAt == newItem.createdLocallyAt &&
                oldItem.text == newItem.text &&
                oldItem.user == newItem.user
        }
    }
}

package io.getstream.chat.docs.kotlin.ui.utility

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import io.getstream.chat.android.ui.mention.list.MentionListView
import io.getstream.chat.android.ui.mention.list.viewmodel.MentionListViewModel
import io.getstream.chat.android.ui.mention.list.viewmodel.bindView

/**
 * [Mention List View](https://getstream.io/chat/docs/sdk/android/ui/utility-components/mention-list-view/)
 */
class MentionListView : Fragment() {

    private lateinit var mentionListView: MentionListView

    fun usage() {
        val viewModel: MentionListViewModel by viewModels()
        viewModel.bindView(mentionListView, viewLifecycleOwner)
    }

    fun handlingActions() {
        mentionListView.setMentionSelectedListener { message ->
            // Handle a mention item being clicked
        }
    }
}

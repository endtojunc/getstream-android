package io.getstream.chat.ui.sample.feature.channel.list.search

import android.content.Context
import android.text.Html
import android.util.AttributeSet
import android.widget.ViewFlipper
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import io.getstream.chat.android.ui.R
import io.getstream.chat.android.ui.common.extensions.internal.createStreamThemeWrapper
import io.getstream.chat.android.ui.pinned.list.PinnedMessageListViewStyle

class SearchChannelAndMessageResultListView: ViewFlipper {
    public constructor(context: Context) : super(context.createStreamThemeWrapper()) {
        init(null)
    }

    public constructor(context: Context, attrs: AttributeSet?) : super(context.createStreamThemeWrapper(), attrs) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {

    }
}
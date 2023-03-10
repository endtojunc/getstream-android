// ktlint-disable filename

package io.getstream.chat.docs.kotlin.compose.messages

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.getstream.chat.android.compose.ui.components.avatar.ChannelAvatar
import io.getstream.chat.android.compose.ui.messages.header.MessageListHeader
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.compose.viewmodel.messages.MessageListViewModel
import io.getstream.chat.android.compose.viewmodel.messages.MessagesViewModelFactory
import io.getstream.chat.docs.R

/**
 * [Usage](https://getstream.io/chat/docs/sdk/android/compose/message-components/message-list-header/#usage)
 */
private object MessageListHeaderUsageSnippet {

    class MyActivity : AppCompatActivity() {
        val factory by lazy {
            MessagesViewModelFactory(
                context = this,
                channelId = "messaging:123",
            )
        }

        val listViewModel by viewModels<MessageListViewModel>(factoryProducer = { factory })

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            setContent {
                ChatTheme {
                    val channel = listViewModel.channel
                    val currentUser by listViewModel.user.collectAsState()
                    val connectionState by listViewModel.connectionState.collectAsState()
                    val messageMode = listViewModel.messageMode

                    Column(Modifier.fillMaxSize()) {
                        MessageListHeader(
                            modifier = Modifier.wrapContentHeight(),
                            channel = channel,
                            currentUser = currentUser,
                            connectionState = connectionState,
                            messageMode = messageMode,
                            onBackPressed = { },
                            onHeaderActionClick = { },
                        )

                        // Rest of your UI
                    }
                }
            }
        }
    }
}

/**
 * [Handling Actions](https://getstream.io/chat/docs/sdk/android/compose/message-components/message-list-header/#handling-actions)
 */
private object MessageListHeaderHandlingActionsSnippet {

    class MyActivity : AppCompatActivity() {
        val factory by lazy {
            MessagesViewModelFactory(
                context = this,
                channelId = "messaging:123",
            )
        }

        val listViewModel by viewModels<MessageListViewModel>(factoryProducer = { factory })

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            setContent {
                ChatTheme {
                    val channel = listViewModel.channel
                    val currentUser by listViewModel.user.collectAsState()

                    MessageListHeader(
                        channel = channel,
                        currentUser = currentUser,
                        onBackPressed = { finish() },
                        onHeaderActionClick = {
                            // Show your custom UI
                        },
                        // Content
                    )
                }
            }
        }
    }
}

/**
 * [Customization](https://getstream.io/chat/docs/sdk/android/compose/message-components/message-list-header/#customization)
 */
private object MessageListHeaderCustomizationSnippet {

    class MyActivity : AppCompatActivity() {
        val factory by lazy {
            MessagesViewModelFactory(
                context = this,
                channelId = "messaging:123",
            )
        }

        val listViewModel by viewModels<MessageListViewModel>(factoryProducer = { factory })

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            setContent {
                ChatTheme {
                    val channel = listViewModel.channel
                    val currentUser by listViewModel.user.collectAsState()

                    MessageListHeader(
                        channel = channel,
                        currentUser = currentUser,
                        leadingContent = {
                            ChannelAvatar(
                                modifier = Modifier.size(40.dp),
                                channel = channel,
                                currentUser = currentUser,
                            )
                        },
                        trailingContent = { InfoButton() }
                    )
                }
            }
        }

        @Composable
        fun InfoButton() {
            IconButton(
                onClick = {
                    // Handle on click
                }
            ) {
                Icon(
                    modifier = Modifier.height(40.dp),
                    painter = painterResource(id = R.drawable.ic_info),
                    contentDescription = "info button",
                    tint = Color.Black
                )
            }
        }
    }
}

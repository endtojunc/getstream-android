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

package io.getstream.chat.android.uitests.snapshot.uicomponents

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.getstream.sdk.chat.coil.StreamCoil
import com.karumi.shot.ScreenshotTest
import io.getstream.chat.android.core.internal.InternalStreamChatApi
import io.getstream.chat.android.uitests.util.FakeImageLoader
import org.junit.Before

abstract class UiComponentsScreenshotTest : ScreenshotTest {

    protected val context: Context get() = InstrumentationRegistry.getInstrumentation().targetContext

    @OptIn(InternalStreamChatApi::class)
    @Before
    fun setup() {
        StreamCoil.setImageLoader { FakeImageLoader(context) }
    }
}
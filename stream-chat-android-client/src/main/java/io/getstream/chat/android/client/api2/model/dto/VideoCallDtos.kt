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

package io.getstream.chat.android.client.api2.model.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class HMSDto(
    @field:Json(name = "room_id") val roomId: String,
    @field:Json(name = "room_name") val roomName: String,
) : VideoCallDto

@JsonClass(generateAdapter = true)
internal data class AgoraDto(
    val channel: String,
) : VideoCallDto

@JsonClass(generateAdapter = true)
internal data class VideoCallInfoDto(
    val id: String,
    val provider: String,
    val type: String,
    val agora: AgoraDto,
    val hms: HMSDto,
)

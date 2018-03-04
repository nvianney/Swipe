package com.paperatus.swipe.objects

import COMPONENT_MESSAGE_BLOCKADE_COLLISION
import com.paperatus.swipe.core.ComponentMessage
import com.paperatus.swipe.core.GameObject
import com.paperatus.swipe.core.PhysicsComponent

// Similar to an extension function of the player GameObject
class PlayerCollisionResponse(private val player: GameObject) : PhysicsComponent.ContactListener {
    override fun onContactBegin(other: GameObject) {
        val message: ComponentMessage = when(other) {
            is Blockade -> COMPONENT_MESSAGE_BLOCKADE_COLLISION
            else -> return
        }

        player.messageComponent(message)

    }

    override fun onContactEnd(other: GameObject) {
    }
}

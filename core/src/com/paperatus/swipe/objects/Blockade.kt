package com.paperatus.swipe.objects

import com.badlogic.gdx.physics.box2d.Body
import com.paperatus.swipe.components.BlockadePhysicsComponent
import com.paperatus.swipe.core.GameObject
import com.paperatus.swipe.core.PhysicsComponent

private const val DAMAGE_PER_VELOCITY = 2.0f
class Blockade : GameObject("blockade.png"), PhysicsComponent.ContactListener {

    var health = 100.0f
    var maxHealth = 100.0f

    init {
        attachComponent<PhysicsComponent>(BlockadePhysicsComponent().apply {
            addContactListener(this@Blockade)
        })
    }

    override fun onContactBegin(other: Body) {
        val velocity = other.linearVelocity.len()
        health -= DAMAGE_PER_VELOCITY * velocity
    }

    override fun onContactEnd(other: Body) {
    }
}

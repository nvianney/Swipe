package com.paperatus.swipe.objects

import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.paperatus.swipe.components.StaticPhysicsComponent
import com.paperatus.swipe.core.GameObject
import com.paperatus.swipe.core.PhysicsComponent

const val DAMAGE_PER_VELOCITY = 2.0f
class Destructible : GameObject("blockade.png"), PhysicsComponent.ContactListener {

    var health = 100.0f

    init {
        val shape = PolygonShape()
        shape.setAsBox(1.5f, 1.5f)
        attachComponent<PhysicsComponent>(StaticPhysicsComponent(shape).apply {
            addContactListener(this@Destructible)
        })
    }

    override fun onContactBegin(other: Body) {
        val velocity = other.linearVelocity.len()
        health -= DAMAGE_PER_VELOCITY * velocity
    }

    override fun onContactEnd(other: Body) {
    }
}

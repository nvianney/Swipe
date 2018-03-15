package com.paperatus.swipe.objects

import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.paperatus.swipe.components.StaticPhysicsComponent
import com.paperatus.swipe.core.AnimationRenderComponent
import com.paperatus.swipe.core.GameObject
import com.paperatus.swipe.core.PhysicsComponent
import com.paperatus.swipe.core.RenderComponent
import com.paperatus.swipe.data.ExplosionParticleBlending

private val explosionAnimation = Array(127) {
    "destructible/explosion$it"
}

class Destructible : GameObject(), PhysicsComponent.ContactListener {

    init {
        val shape = PolygonShape()
        shape.setAsBox(1.5f, 1.5f)
        attachComponent<PhysicsComponent>(StaticPhysicsComponent(shape).apply {
            onInit = fun(body: Body) {
                val fixture = body.fixtureList[0]
                fixture.isSensor = true
            }
            addContactListener(this@Destructible)
            positioning = PhysicsComponent.Positioning.BODY_TO_OBJECT
        })
        transform.anchor.set(0.5f, 0.5f)
    }

    override fun onContactBegin(other: GameObject) {
        transform.worldSize.set(3.0f, 2.789f)
        detachComponent<RenderComponent>()

        val animation = AnimationRenderComponent(
                1.0f / 60.0f,
                false,
                *explosionAnimation)
        animation.apply {
            onFinish = fun () {
                requestRemove()
            }
            addRenderParams(ExplosionParticleBlending())
            start()
        }
        attachComponent<RenderComponent>(animation)
    }

    override fun onContactEnd(other: GameObject) {
    }
}

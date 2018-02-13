package com.paperatus.swipe.core

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import com.paperatus.swipe.Game

abstract class PhysicsScene(game: Game,
                            gravity: Vector2,
                            doSleep: Boolean = true) : ObjectScene(game) {
    private val physicsWorld = World(gravity, doSleep)
    private val debugRenderer = Box2DDebugRenderer()

    override fun update(delta: Float) {
        super.update(delta)

        physicsWorld.step(delta, 6, 2)
    }

    protected fun debugRender(camera: Camera) {
        debugRenderer.render(physicsWorld, camera.combined)
    }

    override fun addObject(gameObject: GameObject) {
        super.addObject(gameObject)

        if (gameObject.components.containsKey(PhysicsComponent::class)) {
            (gameObject.components[PhysicsComponent::class] as PhysicsComponent)
                    .init(physicsWorld)
        }
    }


}
package com.paperatus.swipe.core

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.paperatus.swipe.Game
import kotlin.reflect.KClass

private val COMPONENT_ORDER: Array<KClass<out Component>> = arrayOf(
        InputComponent::class,
        Component::class, // Used to update the GameObject itself
        PhysicsComponent::class,
        TransformComponent::class
)

class NodeUpdater : NodeTraversal.Callback {
    override fun onTraverse(gameObject: GameObject, data: Any) {
        val delta = data as Float

        for (i in 0 until COMPONENT_ORDER.size) {
            val order = COMPONENT_ORDER[i]
            if (order == Component::class) {
                gameObject.update(delta)
                continue
            }

            gameObject.getComponent(order)?.update(delta, gameObject)
        }
    }
}

class NodeRenderer(val game: Game) : NodeTraversal.Callback {

    override fun onTraverse(gameObject: GameObject, data: Any) {
        val batch = data as SpriteBatch

        gameObject.getComponent<RenderComponent>()?.let {
            if (it.renderMode == RenderComponent.Mode.CUSTOM) return
            val spriteName = it.sprite
                    ?: throw RenderException("Sprite name is null")

            if (spriteName == "") throw RenderException("Sprite name cannot be empty")
            if (!game.assets.isLoaded(spriteName)) throw AssetNotLoadedException(
                    "Sprite $spriteName isn't loaded!"
            )

            // Apply custom rendering params if requested
            it.customParams.forEach { it.applyParams(batch) }
            renderGameObject(batch, gameObject, spriteName)
            it.customParams.forEach { it.resetParams(batch) }
        }
    }

    private val tempPosition = Vector2()
    private fun renderGameObject(batch: SpriteBatch,
                                 gameObject: GameObject,
                                 spriteName: String) {
        tempPosition.set(gameObject.transform.position)
        tempPosition.mul(gameObject.parent!!.transform.transformMatrix)

        val image: Any = game.assets[spriteName]

        gameObject.apply {
            val width = transform.worldSize.width * transform.scale.x
            val height = transform.worldSize.height * transform.scale.y

            when (image) {
                is Texture -> batch.draw(image,
                        tempPosition.x - width * transform.anchor.x,
                        tempPosition.y - height * transform.anchor.y,
                        transform.anchor.x * width,
                        transform.anchor.y * height,
                        width, height,
                        1.0f, 1.0f,
                        transform.rotation,
                        0, 0, image.width, image.height,
                        false, false
                )
                is TextureRegion -> batch.draw(image,
                        tempPosition.x - width * transform.anchor.x,
                        tempPosition.y - height * transform.anchor.y,
                        transform.anchor.x * width,
                        transform.anchor.y * height,
                        width, height,
                        1.0f, 1.0f,
                        transform.rotation
                )
                else -> ktx.log.error(RuntimeException()) {
                    "Asset '$spriteName' is of type ${image::class}!"
                }
            }
        }
    }
}

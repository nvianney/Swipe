package com.paperatus.swipe.objects

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.paperatus.swipe.core.components.PhysicsComponent
import com.paperatus.swipe.core.scene.GameObject
import com.paperatus.swipe.data.Solver

private const val DISTANCE_X_MIN = 1.0f
private const val DISTANCE_X_MAX = 15.0f
private const val DISTANCE_Y_MIN = 2.0f
private const val DISTANCE_Y_MAX = 30.0f
private const val VELOCITY_MIN = 7.0f
private const val VELOCITY_MAX = 18.0f
private const val ZOOM_MIN = 0.35f
private const val ZOOM_MAX = 1.4f

private const val POSITION_X_OFFSET = 0.0f
private const val POSITION_Y_OFFSET = -3.0f

private const val POSITION_MAX_CHANGE_PER_SECOND = 50.0f
private const val ZOOM_MAX_CHANGE_PER_SECOND = 0.30f

class GameCamera(width: Float, height: Float) :
        OrthographicCamera(width, height) {

    private var positionInterpolation: Interpolation = Interpolation.pow3Out
    private var velocityInterpolation: Interpolation = Interpolation.pow2Out

    private val tempPosition = Vector3()

    fun update(delta: Float, player: GameObject) {
        updatePosition(delta, player)
        updateZoom(delta, player)

        super.update()
    }

    private fun updatePosition(delta: Float, player: GameObject) {
        val deltaX = player.transform.position.x - (position.x + POSITION_X_OFFSET)
        val deltaY = player.transform.position.y - (position.y + POSITION_Y_OFFSET)

        // Position interpolation
        tempPosition.apply {
            // Calculate the amount to change per second relative to the
            // difference between the player's position and the current position

            x = Solver.inverseLerpClamped(Math.abs(deltaX), DISTANCE_X_MIN, DISTANCE_X_MAX)
            y = Solver.inverseLerpClamped(Math.abs(deltaY), DISTANCE_Y_MIN, DISTANCE_Y_MAX)

            x = positionInterpolation
                    // Interpolate
                    .apply(x) *

                    // Direction (|x| removes the negative direction)
                    Math.signum(deltaX) *

                    // Speed
                    POSITION_MAX_CHANGE_PER_SECOND * delta

            y = positionInterpolation.apply(y) *
                    Math.signum(deltaY) *
                    POSITION_MAX_CHANGE_PER_SECOND * delta
        }
        position.add(tempPosition)
    }

    private fun updateZoom(delta: Float, player: GameObject) {
        val physicsComponent = player.getComponent<PhysicsComponent>()!!

        val velocity = MathUtils.clamp(physicsComponent.physicsBody.linearVelocity.len(),
                VELOCITY_MIN, VELOCITY_MAX)

        // Project the value from the range [VELOCITY_MIN, VELOCITY_MAX] to
        // [ZOOM_MIN, ZOOM_MAX] to calculate the expected zoom
        val velocityAlpha = Solver.inverseLerpClamped(velocity, VELOCITY_MIN, VELOCITY_MAX)
        val targetZoom = MathUtils.lerp(ZOOM_MIN, ZOOM_MAX, velocityAlpha)

        // Determine how much change is needed to reach the target zoom
        val zoomDelta = Math.abs(targetZoom - zoom)

        // Interpolate to smooth out the changes in the camera zoom
        val interpolatedZoom = velocityInterpolation.apply(zoomDelta) * Math.signum(targetZoom - zoom) *
                ZOOM_MAX_CHANGE_PER_SECOND

        zoom += interpolatedZoom * delta
    }
}

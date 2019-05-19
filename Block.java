/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 * The Primary block class.
 *
 * @author karanehra
 */
public class Block {

    private final Float SCALE_FACTOR = 6f;
    private Spatial block_spatial;
    private Vector3f block_location = new Vector3f();
    public String block_id;
    public Node rootNode;
    RigidBodyControl control = new RigidBodyControl(0);
    private BulletAppState bulletAppState;

    public Block() {
    }

    public Block(Spatial sp, BulletAppState bas, Node rn, Vector3f pos) {
        block_spatial = sp;
        block_location = pos;
        translateBlock();
        bulletAppState = bas;
        rootNode = rn;
        block_id = Float.toString(pos.x) + "-" + Float.toString(pos.z);
        block_spatial.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        block_spatial.scale(SCALE_FACTOR);
        block_spatial.addControl(control);
        control.getCollisionShape().setMargin(0.4f*SCALE_FACTOR);
//        control.setCcdMotionThreshold(1e-9f);
        control.setRestitution(0.1f);
        control.setFriction(0.4f);
        bulletAppState.getPhysicsSpace().add(block_spatial);
        rn.attachChild(block_spatial);
    }   
    
    /** 
     * The universal block translation formula. Can be used to move around the map
     */
    private void translateBlock() {
        block_spatial.setLocalTranslation(
                block_location.x * SCALE_FACTOR - 4,
                block_location.y * SCALE_FACTOR - 7,
                block_location.z * SCALE_FACTOR
        );
    }

    public Spatial getSpatial() {
        return block_spatial;
    }

    public void rotateAlongVertical(Float angle) {
        block_spatial.rotate(0f, angle, 0f);
    }

    public String getId() {
        return block_id;
    }

    public RigidBodyControl getRBC() {
        return control;
    }

    public void detach() {
        bulletAppState.getPhysicsSpace().remove(block_spatial);
        rootNode.detachChild(block_spatial);
    }
}

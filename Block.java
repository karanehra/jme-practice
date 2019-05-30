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

    public Float SCALE_FACTOR;
    public Spatial block_spatial;
    public Vector3f block_location = new Vector3f();
    public String block_id;
    public Node rootNode;

    public BulletAppState bulletAppState;

    Node block_node = new Node("block-node");

    public Block() {
    }

    public Block(Spatial sp, GameManager gm, Vector3f pos) {
        block_spatial = sp;
        block_location = pos;

        
        setupClassFields(gm);
        translateBlock();
        setupBlock();
        assignBlockID();

    }
    
    public final void setupClassFields(GameManager gm){
        SCALE_FACTOR = gm.getSCALE_FACTOR();
        bulletAppState = gm.getBulletAppState();
        rootNode = gm.getRootNode();
    }

    /**
     * The universal block translation formula. Can be used to move around the
     * map
     */
    public final void translateBlock() {
        block_spatial.setLocalTranslation(
                block_location.x * SCALE_FACTOR - 4,
                block_location.y * SCALE_FACTOR - 7,
                block_location.z * SCALE_FACTOR
        );
    }

    public final void setupBlock() {
        block_spatial.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        block_spatial.scale(SCALE_FACTOR);
        RigidBodyControl control = new RigidBodyControl(0);
        block_spatial.addControl(control);
        control.getCollisionShape().setMargin(0.4f * SCALE_FACTOR);
        control.setRestitution(0.1f);
        control.setFriction(0.4f);
        block_node.attachChild(block_spatial);
        bulletAppState.getPhysicsSpace().add(block_spatial);
        rootNode.attachChild(block_node);
    }

    public void rotateAlongVertical(Float angle) {
        block_node.rotate(0f, angle, 0f);
    }

    public String getId() {
        return block_id;
    }

    public void detach() {
        bulletAppState.getPhysicsSpace().remove(block_spatial);
        rootNode.detachChild(block_node);
    }

    public final void assignBlockID() {
        block_id = Float.toString(block_location.x) + "-" + Float.toString(block_location.z);
    }
}

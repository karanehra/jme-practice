/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author karanehra
 */
public class Block {
    
    private Float SCALE_FACTOR = 5f;
    private Spatial block_spatial;
    private Vector3f block_location = new Vector3f();
    
    public Block(Spatial sp,BulletAppState bas, Node rn, Vector3f pos){
        block_spatial = sp;
        block_location = pos;
        translateBlock();
        block_spatial.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        block_spatial.scale(SCALE_FACTOR);
        RigidBodyControl control = new RigidBodyControl(0);
        block_spatial.addControl(control);
        control.getCollisionShape().setMargin(0.4f);
        control.setRestitution(0.1f);
        control.setFriction(0.4f);
    }   
    
    private void translateBlock(){
        block_spatial.setLocalTranslation(block_location.x* SCALE_FACTOR, block_location.y * SCALE_FACTOR - 5, block_location.z * SCALE_FACTOR);
    }
    public Spatial getSpatial(){
        return block_spatial;
    }
    
    public void rotateAlongVertical(Float angle){
        block_spatial.rotate(0f, angle, 0f);
    }
}

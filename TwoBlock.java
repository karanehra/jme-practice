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
import java.util.ArrayList;

/**
 *
 * @author karanehra
 */
public class TwoBlock extends Block{

    private ArrayList<Spatial> spatials = new ArrayList<>();
    private ArrayList<Vector3f> positions = new ArrayList<>();
//    private final float SCALE_FACTOR;
//    private final BulletAppState bulletAppState;
//    private final Node rootNode;
//    private final String block_id;
    
//    private Node block_node;
    

    public TwoBlock(
            Spatial sp1,
            Spatial sp2,
            Spatial sp3,
            Spatial sp4,
            GameManager gm,
            Vector3f pos
    ) {
        spatials.add(sp1);
        positions.add(pos);
        spatials.add(sp2);
        positions.add(new Vector3f(pos.x + 1, pos.y, pos.z));
        spatials.add(sp3);
        positions.add(new Vector3f(pos.x, pos.y, pos.z + 1));
        spatials.add(sp4);
        positions.add(new Vector3f(pos.x + 1, pos.y, pos.z + 1));

        SCALE_FACTOR = gm.getSCALE_FACTOR();
        bulletAppState = gm.getBulletAppState();
        rootNode = gm.getRootNode();

        block_id = Float.toString(pos.x) + "-" + Float.toString(pos.z);

        setupBlocks();
        
        rootNode.attachChild(block_node);
    }

    private void translateBlock(Spatial s, Vector3f pos) {
        s.setLocalTranslation(
                pos.x * SCALE_FACTOR - 4,
                pos.y * SCALE_FACTOR - 7,
                pos.z * SCALE_FACTOR
        );

    }

    private void setupBlocks() {
        for (int i = 0; i < spatials.size(); i++) {
            Spatial temp_spatial = spatials.get(i);
            translateBlock(temp_spatial, positions.get(i));
            temp_spatial.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
            temp_spatial.scale(SCALE_FACTOR);

            RigidBodyControl control = new RigidBodyControl(0);
            temp_spatial.addControl(control);
            control.getCollisionShape().setMargin(0.4f * SCALE_FACTOR);
            control.setRestitution(0.1f);
            control.setFriction(0.4f);
            bulletAppState.getPhysicsSpace().add(temp_spatial);
            block_node.attachChild(temp_spatial);
        }
    }
    
    @Override
    public void detach() {
        for (int i = 0; i < spatials.size(); i++) {
            Spatial temp_spatial = spatials.get(i);
            bulletAppState.getPhysicsSpace().remove(temp_spatial);
        }
        rootNode.detachChild(block_node);
    }
}

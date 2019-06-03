/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.models;

import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import mygame.Block;
import mygame.GameManager;
import mygame.ModelLoader;

/**
 *
 * @author karanehra
 */
public class StraightRoad extends Block {
    private final ModelLoader modelLoader;
    
    public StraightRoad(GameManager gm, Vector3f pos){
        modelLoader = gm.getModelLoader();
        block_spatial = modelLoader.getStraightRoadSpatial();
        block_location = pos;
        
        setupClassFields(gm);
        translateBlock();
        setupBlock();
        assignBlockID();
    }
    
    public StraightRoad(GameManager gm, Vector3f pos,Float rot) {
        modelLoader = gm.getModelLoader();
        block_spatial = modelLoader.getStraightRoadSpatial();
        block_location = pos;
        block_spatial.rotate(0, rot, 0);

        
        setupClassFields(gm);
        translateBlock();
        setupBlock();
        assignBlockID();

    }
}

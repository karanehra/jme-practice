/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.models;

import com.jme3.math.Vector3f;
import mygame.Block;
import mygame.GameManager;
import mygame.ModelLoader;

/**
 *
 * @author karanehra
 */
public class Grass extends Block{
    private final ModelLoader modelLoader;
    
    public Grass(GameManager gm, Vector3f pos){
        modelLoader = gm.getModelLoader();
        block_spatial = modelLoader.getGrassSpatial();
        block_location = pos;
        
        setupClassFields(gm);
        translateBlock();
        setupBlock();
        assignBlockID();
    }
}

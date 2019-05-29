/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;

/**
 *
 * @author karanehra
 */
public class GameManager {

    public GameManager() {

    }

    private AssetManager assetManager;

    /**
     * Get the value of assetManager
     *
     * @return the value of assetManager
     */
    public AssetManager getAssetManager() {
        return assetManager;
    }

    /**
     * Set the value of assetManager
     *
     * @param assetManager new value of assetManager
     */
    public void setAssetManager(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    private BulletAppState bulletAppState;

    /**
     * Get the value of bulletAppState
     *
     * @return the value of bulletAppState
     */
    public BulletAppState getBulletAppState() {
        return bulletAppState;
    }

    /**
     * Set the value of bulletAppState
     *
     * @param bulletAppState new value of bulletAppState
     */
    public void setBulletAppState(BulletAppState bulletAppState) {
        this.bulletAppState = bulletAppState;
    }
    private Node rootNode;

    /**
     * Get the value of rootNode
     *
     * @return the value of rootNode
     */
    public Node getRootNode() {
        return rootNode;
    }

    /**
     * Set the value of rootNode
     *
     * @param rootNode new value of rootNode
     */
    public void setRootNode(Node rootNode) {
        this.rootNode = rootNode;
    }
    
        private ViewPort viewPort;

    /**
     * Get the value of viewPort
     *
     * @return the value of viewPort
     */
    public ViewPort getViewPort() {
        return viewPort;
    }

    /**
     * Set the value of viewPort
     *
     * @param viewPort new value of viewPort
     */
    public void setViewPort(ViewPort viewPort) {
        this.viewPort = viewPort;
    }

    private float SCALE_FACTOR;

    /**
     * Get the value of SCALE_FACTOR
     *
     * @return the value of SCALE_FACTOR
     */
    public float getSCALE_FACTOR() {
        return SCALE_FACTOR;
    }

    /**
     * Set the value of SCALE_FACTOR
     *
     * @param SCALE_FACTOR new value of SCALE_FACTOR
     */
    public void setSCALE_FACTOR(float SCALE_FACTOR) {
        this.SCALE_FACTOR = SCALE_FACTOR;
    }
    
        private ModelLoader modelLoader;

    /**
     * Get the value of modelLoader
     *
     * @return the value of modelLoader
     */
    public ModelLoader getModelLoader() {
        return modelLoader;
    }

    /**
     * Set the value of modelLoader
     *
     * @param modelLoader new value of modelLoader
     */
    public void setModelLoader(ModelLoader modelLoader) {
        this.modelLoader = modelLoader;
    }



}

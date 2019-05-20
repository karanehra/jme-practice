/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 *
 * @author karanehra
 */
public class ModelLoader {

    private final AssetManager assetManager;
    private Spatial house;
    private Spatial grass;
    private Spatial road;
    private Spatial turn;
    private Spatial intersection;
    private Spatial trisection;
    private Spatial billboard;
    private final BulletAppState bulletAppState;
    private final Node rootNode;
    
    public ModelLoader(AssetManager am, BulletAppState bs, Node rn){
        assetManager = am;
        bulletAppState = bs;
        rootNode = rn;
    }
    
    /**
     * Used to load all assets ONCE on game start.
     *  These assets are the cloned whenever needed by the block creators.
     */
    public void loadAssets() {
        house = assetManager.loadModel("Models/h2/h2.j3o");
        grass = assetManager.loadModel("Models/ground/ground.j3o");
        road = assetManager.loadModel("Models/road-st-1x/road-st-1x.j3o");
        turn = assetManager.loadModel("Models/road-turn-1x/road-turn-1x.j3o");
        intersection = assetManager.loadModel("Models/intersection/imtersection.j3o");
        trisection = assetManager.loadModel("Models/trisection-1x/trisection-1x.j3o");
        billboard = assetManager.loadModel("Models/billboard/billboard.j3o");
    }
    
    public Block createHouse(int i, int j) {
        Spatial x = house.clone();
        Block blc = new Block(x, bulletAppState, rootNode, new Vector3f(i, 0.5f, j));
        return blc;
    }

    public Block createStraight1xRoad(int i, int j, boolean rotate) {
        Spatial x = road.clone();
        if (rotate) {
            x.rotate(0, FastMath.HALF_PI, 0);
        }
        Block blc = new Block(x, bulletAppState, rootNode, new Vector3f(i, 0, j));
        return blc;
    }

    public Block createTurn1xRoad(int i, int j, int rotate_count) {
        Spatial x = turn.clone();
        x.rotate(0, rotate_count * FastMath.HALF_PI, 0);
        Block blc = new Block(x, bulletAppState, rootNode, new Vector3f(i, 0, j));
        return blc;
    }

    public Block createGrass(int i, int j) {
        Spatial x = grass.clone();
        Block blc = new Block(x, bulletAppState, rootNode, new Vector3f(i, 0.05f, j));
        return blc;

    }

    public Block createIntersection(int i, int j) {
        Spatial x = intersection.clone();
        Block blc = new Block(x, bulletAppState, rootNode, new Vector3f(i, 0, j));
        return blc;
    }

    public Block createTrisection1xRoad(int i, int j, int rotation_count) {
        Spatial x = trisection.clone();
        x.rotate(0, FastMath.HALF_PI * rotation_count, 0);
        Block blc = new Block(x, bulletAppState, rootNode, new Vector3f(i, 0, j));
        return blc;
    }

    public Block createBillboard(int i, int j) {
        Spatial x = billboard.clone();
        Block blc = new Block(x, bulletAppState, rootNode, new Vector3f(i, 0, j));
        return blc;
    }

    public Block getIntersectionType(int i, int j, ArrayList<String[]> map) {
        String neighbour_code = map.get(i - 1)[j] + map.get(i)[j + 1] + map.get(i + 1)[j] + map.get(i)[j - 1];
        Block tempBlock;
        if (Pattern.matches("[^1]111$", neighbour_code)) {
            tempBlock = createTrisection1xRoad(i, j, 1);
        } else if (Pattern.matches("^1[^1]11$", neighbour_code)) {
            tempBlock = createTrisection1xRoad(i, j, 2);
        } else if (Pattern.matches("^11[^1]1$", neighbour_code)) {
            tempBlock = createTrisection1xRoad(i, j, 3);
        } else if (Pattern.matches("^111[^1]$", neighbour_code)) {
            tempBlock = createTrisection1xRoad(i, j, 0);
        } else if (Pattern.matches("^11[^1]*$", neighbour_code)) {
            tempBlock = createTurn1xRoad(i, j, 0);
        } else if (Pattern.matches("^[^1]11[^1]$", neighbour_code)) {
            tempBlock = createTurn1xRoad(i, j, 1);
        } else if (Pattern.matches("^[^1]*11$", neighbour_code)) {
            tempBlock = createTurn1xRoad(i, j, 2);
        } else if (Pattern.matches("^1[^1]*1$", neighbour_code)) {
            tempBlock = createTurn1xRoad(i, j, 3);
        } else if (Pattern.matches("^1[^1]1[^1]$", neighbour_code)) {
            tempBlock = createStraight1xRoad(i, j, false);
        } else if (Pattern.matches("^[^1]1[^1]1$", neighbour_code)) {
            tempBlock = createStraight1xRoad(i, j, true);
        } else if (Pattern.matches("^1[^1]*$", neighbour_code)) {
            tempBlock = createStraight1xRoad(i, j, false);
        } else if (Pattern.matches("^[^1]1[^1]*$", neighbour_code)) {
            tempBlock = createStraight1xRoad(i, j, false);
        } else if (Pattern.matches("^[^1]*1[^1]$", neighbour_code)) {
            tempBlock = createStraight1xRoad(i, j, true);
        } else if (Pattern.matches("^[^1]*1$", neighbour_code)) {
            tempBlock = createStraight1xRoad(i, j, true);
        } else {
            tempBlock = createIntersection(i, j);
        }
        
        return tempBlock;
    }
}

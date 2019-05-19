/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;

import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Pattern;

/**
 *
 * @author karanehra
 */
public class MapLoader {

    private String directory = System.getProperty("user.home") + File.separator + "sample.txt";

    private static Node rootNode;
    private static AssetManager assetManager;
    private static ViewPort viewPort;
    private int map_size = 64;
    private float SCALE_FACTOR = 5f;

    public Geometry player;

    private ArrayList<String[]> map_array = new ArrayList<String[]>();
    private ArrayList<Integer[]> render_state = new ArrayList<Integer[]>();
    private ArrayList<Block> block_state = new ArrayList<Block>();
    private ArrayList<String> ids_state = new ArrayList<String>();
    

    private Spatial house;
    private Spatial road;
    private Spatial ground;
    private Spatial turn;
    private Spatial grass;
    private Spatial intersection;
    private Spatial trisection;
    private Spatial billboard;
    private static BulletAppState bulletAppState;

    private Vector3f player_pos_tracker = new Vector3f();
    private final ModelLoader modelLoader;

    /**
     * Used to initialize the mapLoader. The params needed to prevent an
     * unnecessary extension from the SimpleGame class
     *
     * @param rn the root node
     * @param am the asset manager
     * @param vp the viewport
     * @param ba the bulletApp
     *
     *
     */
    public MapLoader(Node rn, AssetManager am, ViewPort vp, BulletAppState ba) {
        rootNode = rn;
        assetManager = am;
        viewPort = vp;
        bulletAppState = ba;
        modelLoader = new ModelLoader(assetManager,bulletAppState,rootNode);
        modelLoader.loadAssets();

    }

    /**
     *
     */
    public void initMap() {

        try (BufferedReader fileReader = new BufferedReader(new FileReader(directory))) {
            String line = fileReader.readLine();
            while (line != null) {
                map_array.add(line.split(""));
                Integer[] temp = new Integer[64];
                Arrays.fill(temp, 0);
                render_state.add(temp);
                line = fileReader.readLine();
            }
        } catch (FileNotFoundException e) {
            // exception handling
        } catch (IOException e) {
            // exception handling
        }

        createLightsAndShadows();

    }

    public void renderMap(Vector3f player_pos) {
        if (!player_pos_tracker.equals(player_pos)) {
            player_pos_tracker = player_pos;
            int x_lower = Integer.max((int) player_pos.x - 3, 0);
            int x_upper = Integer.min((int) player_pos.x + 3, 8);

            int y_lower = Integer.max((int) player_pos.z - 3, 0);
            int y_upper = Integer.min((int) player_pos.z + 3, 64);

            for (int i = x_lower; i < x_upper; i++) {
                for (int j = y_lower; j < y_upper; j++) {
                    Block tempBlock = new Block();
                    String tempId;
                    if ("0".equals(map_array.get(i)[j])) {
                        if (render_state.get(i)[j] == 0) {
                            tempBlock = modelLoader.createGrass(i, j);
                            render_state.get(i)[j] = 1;
                        }
                    } else if ("1".equals(map_array.get(i)[j])) {
                        if (render_state.get(i)[j] == 0) {
                            tempBlock = modelLoader.getIntersectionType(i, j, map_array);
                            render_state.get(i)[j] = 1;
                        }
                    } else if ("2".equals(map_array.get(i)[j])) {
                        if (render_state.get(i)[j] == 0) {
                            tempBlock = modelLoader.createHouse(i, j);
                            render_state.get(i)[j] = 1;
                        }

                    } else if ("b".equals(map_array.get(i)[j])) {
                        if (render_state.get(i)[j] == 0) {
                            tempBlock = modelLoader.createBillboard(i, j);
                            render_state.get(i)[j] = 1;
                        }

                    }
                    tempId = tempBlock.getId();
                    if(!ids_state.contains(tempId)){
                        ids_state.add(tempId);
                        block_state.add(tempBlock);
//                        bulletAppState.getPhysicsSpace().add(tempBlock.getRBC());
//                        rootNode.attachChild(tempBlock.getSpatial());
                    } else {
                        Block temp = block_state.get(ids_state.indexOf(tempId));
//                        temp.detach();
                    }
                }
            }
        }

    }

    private void createLightsAndShadows() {

        DirectionalLight sun = new DirectionalLight();
        sun.setColor(ColorRGBA.White);
        sun.setDirection(new Vector3f(10, -10, -10).normalizeLocal());
        rootNode.addLight(sun);

        final int SHADOWMAP_SIZE = 1024;
        DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, SHADOWMAP_SIZE, 4);
        dlsr.setLight(sun);
        viewPort.addProcessor(dlsr);

        DirectionalLightShadowFilter dlsf = new DirectionalLightShadowFilter(assetManager, SHADOWMAP_SIZE, 4);
        dlsf.setLight(sun);
        dlsf.setEnabled(true);
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        fpp.addFilter(dlsf);
        viewPort.addProcessor(fpp);

    }

}

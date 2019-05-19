/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;

import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

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
        modelLoader = new ModelLoader(assetManager, bulletAppState, rootNode);
        modelLoader.loadAssets();

    }

    /**
     *
     */
    public void initMap(Vector3f player_pos) {

        player_pos_tracker = player_pos;

        int x_lower = Integer.max((int) player_pos_tracker.x - 3, 0);
        int x_upper = Integer.min((int) player_pos_tracker.x + 3, 8);

        int z_lower = Integer.max((int) player_pos_tracker.z - 3, 0);
        int z_upper = Integer.min((int) player_pos_tracker.z + 3, 64);

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
        if (ids_state.isEmpty()) {
            player_pos_tracker = player_pos;
            int x_lower = Integer.max((int) player_pos.x - 3, 0);
            int x_upper = Integer.min((int) player_pos.x + 3, 8);

            int z_lower = Integer.max((int) player_pos.z - 3, 0);
            int z_upper = Integer.min((int) player_pos.z + 3, 64);

            for (int i = x_lower; i < x_upper; i++) {
                for (int j = z_lower; j < z_upper; j++) {
                    Block tempBlock = new Block();
                    if ("0".equals(map_array.get(i)[j])) {
                        tempBlock = modelLoader.createGrass(i, j);
                    } else if ("1".equals(map_array.get(i)[j])) {
                        tempBlock = modelLoader.getIntersectionType(i, j, map_array);
                    } else if ("2".equals(map_array.get(i)[j])) {
                        tempBlock = modelLoader.createHouse(i, j);

                    } else if ("b".equals(map_array.get(i)[j])) {
                        tempBlock = modelLoader.createBillboard(i, j);
                    }
                    ids_state.add(tempBlock.getId());
                    block_state.add(tempBlock);
                }
            }
        } else {
            if (!player_pos_tracker.equals(player_pos)) {
                player_pos_tracker = player_pos;
                int x_lower = Integer.max((int) player_pos.x - 3, 0);
                int x_upper = Integer.min((int) player_pos.x + 3, 8);

                int z_lower = Integer.max((int) player_pos.z - 3, 0);
                int z_upper = Integer.min((int) player_pos.z + 3, 64);

                ArrayList<String> new_ids = new ArrayList<String>();

                for (int i = x_lower; i < x_upper; i++) {
                    for (int j = z_lower; j < z_upper; j++) {
                        String id = Integer.toString(i) +"-" + Integer.toString(j);
                        new_ids.add(id);
                    }
                }

                for (int c = 0; c < block_state.size(); c++) {
                    Block temp = block_state.get(c);
                    if(!new_ids.contains(temp.getId())){
                        temp.detach();
                    }
                }

                for (int counter = 0; counter < new_ids.size(); counter++) {
                    String element = new_ids.get(counter);
                    if (ids_state.contains(element)) {

                    } else {
                        Block tempBlock = new Block();
                        int i = Integer.parseInt(element.split("-")[0]);
                        int j = Integer.parseInt(element.split("-")[1]);
                        if ("0".equals(map_array.get(i)[j])) {
                            tempBlock = modelLoader.createGrass(i, j);
                        } else if ("1".equals(map_array.get(i)[j])) {
                            tempBlock = modelLoader.getIntersectionType(i, j, map_array);
                        } else if ("2".equals(map_array.get(i)[j])) {
                            tempBlock = modelLoader.createHouse(i, j);

                        } else if ("b".equals(map_array.get(i)[j])) {
                            tempBlock = modelLoader.createBillboard(i, j);
                        }
                        ids_state.add(tempBlock.getId());
                        block_state.add(tempBlock);
                    }
                }
            }
        }
    }

    public void renderMapnew(Vector3f player_pos) {
        if (!player_pos_tracker.equals(player_pos)) {
            player_pos_tracker = player_pos;
            int x_lower = Integer.max((int) player_pos.x - 3, 0);
            int x_upper = Integer.min((int) player_pos.x + 3, 8);

            int z_lower = Integer.max((int) player_pos.z - 3, 0);
            int z_upper = Integer.min((int) player_pos.z + 3, 64);

            ArrayList<String> new_ids = new ArrayList<String>();

            for (int i = x_lower; i < x_upper; i++) {
                for (int j = z_lower; j < z_upper; j++) {
                    String id = Integer.toString(i) + Integer.toString(j);
                    new_ids.add(id);
                }
            }

            if (ids_state.size() > 0) {
                for (int c = 0; c < ids_state.size(); c++) {
                    String element = ids_state.get(c);
                    if (new_ids.contains(element)) {

                    } else {

                    }
                }
            }

//            for (int i = x_lower; i < x_upper; i++) {
//                for (int j = y_lower; j < y_upper; j++) {
//                    Block tempBlock = new Block();
//                    String tempId;
//                    if ("0".equals(map_array.get(i)[j])) {
//                        if (render_state.get(i)[j] == 0) {
//                            tempBlock = modelLoader.createGrass(i, j);
//                            render_state.get(i)[j] = 1;
//                        }
//                    } else if ("1".equals(map_array.get(i)[j])) {
//                        if (render_state.get(i)[j] == 0) {
//                            tempBlock = modelLoader.getIntersectionType(i, j, map_array);
//                            render_state.get(i)[j] = 1;
//                        }
//                    } else if ("2".equals(map_array.get(i)[j])) {
//                        if (render_state.get(i)[j] == 0) {
//                            tempBlock = modelLoader.createHouse(i, j);
//                            render_state.get(i)[j] = 1;
//                        }
//
//                    } else if ("b".equals(map_array.get(i)[j])) {
//                        if (render_state.get(i)[j] == 0) {
//                            tempBlock = modelLoader.createBillboard(i, j);
//                            render_state.get(i)[j] = 1;
//                        }
//
//                    }
//                    tempId = tempBlock.getId();
//                    if (!ids_state.contains(tempId)) {
//                        ids_state.add(tempId);
//                        block_state.add(tempBlock);
////                        bulletAppState.getPhysicsSpace().add(tempBlock.getRBC());
////                        rootNode.attachChild(tempBlock.getSpatial());
//                    } else {
//                        Block temp = block_state.get(ids_state.indexOf(tempId));
////                        temp.detach();
//                    }
//                }
//            }
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

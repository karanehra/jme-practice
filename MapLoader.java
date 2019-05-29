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
import mygame.models.House;

/**
 *
 * @author karanehra
 */
public class MapLoader {

    private final String directory = System.getProperty("user.home") + File.separator + "sample.txt";

    private static Node rootNode;
    private static AssetManager assetManager;
    private static ViewPort viewPort;
    private final int map_size = 64;
    private final float SCALE_FACTOR = 5f;

    private ArrayList<String[]> map_array = new ArrayList<String[]>();
    private ArrayList<Block> block_state = new ArrayList<Block>();
    private ArrayList<String> ids_state = new ArrayList<String>();

    private static BulletAppState bulletAppState;

    private Vector3f player_pos_tracker = new Vector3f();
    private ModelLoader modelLoader;
    private final GameManager gameManager;

    /**
     * Used to initialize the mapLoader. The params needed to prevent an
     * unnecessary extension from the SimpleGame class
     *
     * @param gm The GameManager object
     */
    public MapLoader(GameManager gm) {
        rootNode = gm.getRootNode();
        assetManager = gm.getAssetManager();
        viewPort = gm.getViewPort();
        bulletAppState = gm.getBulletAppState();
        modelLoader = new ModelLoader(gm);
        gm.setModelLoader(modelLoader);
        
        gameManager = gm;
    }

    /**
     * Initializes the player position and the model loads.
     *
     * @param player_pos the player position
     */
    public void initMap(Vector3f player_pos) {

        player_pos_tracker = player_pos;

        modelLoader.loadAssets();

        try (BufferedReader fileReader = new BufferedReader(new FileReader(directory))) {
            String line = fileReader.readLine();
            while (line != null) {
                map_array.add(line.split(""));
                Integer[] temp = new Integer[64];
                Arrays.fill(temp, 0);
                line = fileReader.readLine();
            }
        } catch (FileNotFoundException e) {
            // exception handling
        } catch (IOException e) {
            // exception handling
        }

        createLightsAndShadows();

    }

    /**
     * The render function called per frame. Used to load/unload blocks from the
     * scene as per player position.
     *
     * @param player_pos the player position
     * @see neeeds optimization
     */
    public void renderMap(Vector3f player_pos) {
        if (ids_state.isEmpty()) {
            player_pos_tracker = player_pos;
            int x_lower = Integer.max((int) player_pos.x - 3, 0);
            int x_upper = Integer.min((int) player_pos.x + 3, 64);

            int z_lower = Integer.max((int) player_pos.z - 3, 0);
            int z_upper = Integer.min((int) player_pos.z + 3, 64);

            for (int i = x_lower; i < x_upper; i++) {
                for (int j = z_lower; j < z_upper; j++) {
                    if (!"x".equals(map_array.get(i)[j])) {
                        Block tempBlock = new Block();
                        if ("0".equals(map_array.get(i)[j])) {
                            tempBlock = modelLoader.createGrass(i, j);
                        } else if ("1".equals(map_array.get(i)[j])) {
                            tempBlock = modelLoader.getIntersectionType(i, j, map_array);
                        } else if ("2".equals(map_array.get(i)[j])) {
                            tempBlock = new House(gameManager,new Vector3f(i,0,j));

                        } else if ("b".equals(map_array.get(i)[j])) {
                            tempBlock = modelLoader.createBillboard(i, j);
                        }
                        ids_state.add(tempBlock.getId());
                        block_state.add(tempBlock);
                    }
                }
            }
        } else {
            if (!player_pos_tracker.equals(player_pos)) {
                player_pos_tracker = player_pos;
                int x_lower = Integer.max((int) player_pos.x - 3, 0);
                int x_upper = Integer.min((int) player_pos.x + 3, 64);

                int z_lower = Integer.max((int) player_pos.z - 3, 0);
                int z_upper = Integer.min((int) player_pos.z + 3, 64);

                ArrayList<String> new_ids = new ArrayList<>();

                for (int i = x_lower; i < x_upper; i++) {
                    for (int j = z_lower; j < z_upper; j++) {
                        if (!"x".equals(map_array.get(i)[j])) {
                            String id = Integer.toString(i) + "-" + Integer.toString(j);
                            new_ids.add(id);
                        }
                    }
                }

                for (int c = 0; c < block_state.size(); c++) {
                    Block temp = block_state.get(c);
                    if (!new_ids.contains(temp.getId())) {
                        temp.detach();
                    }
                }

                for (int counter = 0; counter < new_ids.size(); counter++) {
                    String id_of_block = new_ids.get(counter);
                    if (ids_state.contains(id_of_block)) {

                    } else {
                        int i = Integer.parseInt(id_of_block.split("-")[0]);
                        int j = Integer.parseInt(id_of_block.split("-")[1]);
                        if (!"x".equals(equals(map_array.get(i)[j]))) {
                            Block tempBlock = new Block();

                            if ("0".equals(map_array.get(i)[j])) {
                                tempBlock = modelLoader.createGrass(i, j);
                            } else if ("1".equals(map_array.get(i)[j])) {
                                tempBlock = modelLoader.getIntersectionType(i, j, map_array);
                            } else if ("2".equals(map_array.get(i)[j])) {
                                tempBlock = modelLoader.createHouse(i, j);

                            } else if ("b".equals(map_array.get(i)[j])) {
                                tempBlock = modelLoader.createBillboard(i, j);
                            } else if ("3".equals(map_array.get(i)[j])) {
                                tempBlock = modelLoader.create2xBlock(i, j);
                            }
                            ids_state.add(tempBlock.getId());
                            block_state.add(tempBlock);
                        }
                    }
                }
            }
        }
    }

    /**
     * Lights up the scene.
     */
    private void createLightsAndShadows() {

        DirectionalLight sun = new DirectionalLight();
        sun.setColor(ColorRGBA.White.mult(2f));
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

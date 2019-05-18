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

    /**
     * Used to initialize the mapLoader. The params needed to prevent an
     * unnecessary extension from the SimpleGame class
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
        loadAssets();

    }

    private void loadAssets() {
        house = assetManager.loadModel("Models/h2/h2.j3o");
        grass = assetManager.loadModel("Models/ground/ground.j3o");
        road = assetManager.loadModel("Models/road-st-1x/road-st-1x.j3o");
        turn = assetManager.loadModel("Models/road-turn-1x/road-turn-1x.j3o");
        intersection = assetManager.loadModel("Models/intersection/imtersection.j3o");
        trisection = assetManager.loadModel("Models/trisection-1x/trisection-1x.j3o");
        billboard = assetManager.loadModel("Models/billboard/billboard.j3o");
    }

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
                    if ("0".equals(map_array.get(i)[j])) {
                        if (render_state.get(i)[j] == 0) {
                            createGrass(i, j);
                            render_state.get(i)[j] = 1;
                        }
                    } else if ("1".equals(map_array.get(i)[j])) {
                        if (render_state.get(i)[j] == 0) {
                            getIntersectionType(i, j, map_array);
                            render_state.get(i)[j] = 1;
                        }
                    } else if ("2".equals(map_array.get(i)[j])) {
                        if (render_state.get(i)[j] == 0) {
                            createHouse(i, j);
                            render_state.get(i)[j] = 1;
                        }

                    } else if ("b".equals(map_array.get(i)[j])) {
                        if (render_state.get(i)[j] == 0) {
                            createBillboard(i, j);
                            render_state.get(i)[j] = 1;
                        }

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

    private void createHouse(int i, int j) {
        Spatial x = house.clone();
        Block blc = new Block(x, bulletAppState, rootNode, new Vector3f(i, 0.5f, j));
        bulletAppState.getPhysicsSpace().add(blc.getSpatial());
        rootNode.attachChild(blc.getSpatial());
    }

    private void createStraight1xRoad(int i, int j, boolean rotate) {
        Spatial x = road.clone();
        if (rotate) {
            x.rotate(0,FastMath.HALF_PI,0);
        }
        Block blc = new Block(x, bulletAppState, rootNode, new Vector3f(i, 0, j));
        bulletAppState.getPhysicsSpace().add(blc.getSpatial());
        rootNode.attachChild(blc.getSpatial());
    }

    private void createTurn1xRoad(int i, int j, int rotate_count) {
        Spatial x = turn.clone();
        x.rotate(0,rotate_count * FastMath.HALF_PI,0);
        Block blc = new Block(x, bulletAppState, rootNode, new Vector3f(i, 0, j));
        bulletAppState.getPhysicsSpace().add(blc.getSpatial());
        rootNode.attachChild(blc.getSpatial());
    }

    private void createGrass(int i, int j) {
        Spatial x = grass.clone();
        Block blc = new Block(x, bulletAppState, rootNode, new Vector3f(i, 0.05f, j));
        bulletAppState.getPhysicsSpace().add(blc.getSpatial());
        rootNode.attachChild(blc.getSpatial());

    }
    
    private void createIntersection(int i, int j) {
        Spatial x = intersection.clone();
        Block blc = new Block(x, bulletAppState, rootNode, new Vector3f(i, 0, j));
        bulletAppState.getPhysicsSpace().add(blc.getSpatial());
        rootNode.attachChild(blc.getSpatial());
    }

    private void createTrisection1xRoad(int i, int j, int rotation_count) {
        Spatial x = trisection.clone();
        x.rotate(0,FastMath.HALF_PI * rotation_count,0);
        Block blc = new Block(x, bulletAppState, rootNode, new Vector3f(i, 0, j));
        bulletAppState.getPhysicsSpace().add(blc.getSpatial());
        rootNode.attachChild(blc.getSpatial());
    }

    private void createBillboard(int i, int j) {
        Spatial x = billboard.clone();
        Block blc = new Block(x, bulletAppState, rootNode, new Vector3f(i, 0, j));
        bulletAppState.getPhysicsSpace().add(blc.getSpatial());
        rootNode.attachChild(blc.getSpatial());
    }

    public void getIntersectionType(int i, int j, ArrayList<String[]> map) {
        String neighbour_code = map.get(i - 1)[j] + map.get(i)[j + 1] + map.get(i + 1)[j] + map.get(i)[j - 1];

        if (Pattern.matches("[^1]111$", neighbour_code)) {
            createTrisection1xRoad(i, j, 1);
        } else if (Pattern.matches("^1[^1]11$", neighbour_code)) {
            createTrisection1xRoad(i, j, 2);
        } else if (Pattern.matches("^11[^1]1$", neighbour_code)) {
            createTrisection1xRoad(i, j, 3);
        } else if (Pattern.matches("^111[^1]$", neighbour_code)) {
            createTrisection1xRoad(i, j, 0);
        } else if (Pattern.matches("^11[^1]*$", neighbour_code)) {
            createTurn1xRoad(i, j, 0);
        } else if (Pattern.matches("^[^1]11[^1]$", neighbour_code)) {
            createTurn1xRoad(i, j, 1);
        } else if (Pattern.matches("^[^1]*11$", neighbour_code)) {
            createTurn1xRoad(i, j, 2);
        } else if (Pattern.matches("^1[^1]*1$", neighbour_code)) {
            createTurn1xRoad(i, j, 3);
        } else if (Pattern.matches("^1[^1]1[^1]$", neighbour_code)) {
            createStraight1xRoad(i, j, false);
        } else if (Pattern.matches("^[^1]1[^1]1$", neighbour_code)) {
            createStraight1xRoad(i, j, true);
        } else if (Pattern.matches("^1[^1]*$", neighbour_code)) {
            createStraight1xRoad(i, j, false);
        } else if (Pattern.matches("^[^1]1[^1]*$", neighbour_code)) {
            createStraight1xRoad(i, j, false);
        } else if (Pattern.matches("^[^1]*1[^1]$", neighbour_code)) {
            createStraight1xRoad(i, j, true);
        } else if (Pattern.matches("^[^1]*1$", neighbour_code)) {
            createStraight1xRoad(i, j, true);
        } else {
            createIntersection(i, j);
        }
    }
}

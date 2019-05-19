package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.input.ChaseCamera;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * 
 * PLEASE READ THIS 
 * 
 * X, Z ARE HORIZONTAL COORDINATES. Y IS THE VERTICAL COORDINATE
 *
 * @author karanehra
 */
public class Main extends SimpleApplication implements AnalogListener, ActionListener {

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    private Geometry geom;
    private MapLoader mapLoader;
    private BulletAppState bulletAppState;
    private SimplePlayer player;
    private float steeringValue = 0;
    private final float accelerationForce = 1000.0f;
    private final float brakeForce = 100.0f;
    private float accelerationValue = 0;

    public Vector3f player_position = new Vector3f();

    public PhysicsSpace getPhysicsSpace() {
        return bulletAppState.getPhysicsSpace();
    }

    @Override
    public void simpleInitApp() {
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0, -9.8f, 0));
        bulletAppState.setDebugEnabled(true);
        mapLoader = new MapLoader(rootNode, assetManager, viewPort, bulletAppState);
        player = new SimplePlayer(assetManager, rootNode, bulletAppState);
        player_position = player.getGeo().getLocalTranslation();
        mapLoader.initMap(player_position);
        flyCam.setEnabled(false);

        

        ChaseCamera chaseCam = new ChaseCamera(cam, player.getGeo(), inputManager);
        chaseCam.setDefaultDistance(5);
        registerInput();

    }

    @Override
    public void simpleUpdate(float tpf) {
        player_position = player.getGeo().getLocalTranslation();
        mapLoader.renderMap(
                new Vector3f(
                        FastMath.floor(player_position.x) / 5,
                        FastMath.floor(player_position.y) / 5,
                        FastMath.floor(player_position.z) / 5
                )
        );
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    private void registerInput() {
        inputManager.addMapping("moveForward", new KeyTrigger(keyInput.KEY_UP), new KeyTrigger(keyInput.KEY_W));
        inputManager.addMapping("moveBackward", new KeyTrigger(keyInput.KEY_DOWN), new KeyTrigger(keyInput.KEY_S));
        inputManager.addMapping("moveRight", new KeyTrigger(keyInput.KEY_RIGHT), new KeyTrigger(keyInput.KEY_D));
        inputManager.addMapping("moveLeft", new KeyTrigger(keyInput.KEY_LEFT), new KeyTrigger(keyInput.KEY_A));
        inputManager.addMapping("jump", new KeyTrigger(keyInput.KEY_P));
        inputManager.addListener(this, "moveForward", "moveBackward", "moveRight", "moveLeft", "jump");
    }

    @Override
    public void onAnalog(String name, float value, float tpf) {

    }

    @Override
    public void onAction(String name, boolean keyPressed, float tpf) {
        if (name.equals("moveLeft")) {
            if (keyPressed) {
                steeringValue += .5f;
            } else {
                steeringValue += -.5f;
            }
            player.getVehicle().steer(steeringValue);
        } else if (name.equals("moveRight")) {
            if (keyPressed) {
                steeringValue += -.5f;
            } else {
                steeringValue += .5f;
            }
            player.getVehicle().steer(steeringValue);
        } else if (name.equals("moveForward")) {
            if (keyPressed) {
                accelerationValue += accelerationForce;
            } else {
                accelerationValue -= accelerationForce;
            }
            player.getVehicle().accelerate(accelerationValue);
        } else if (name.equals("moveBackward")) {
            if (keyPressed) {
                player.getVehicle().brake(brakeForce);
            } else {
                player.getVehicle().brake(0f);
            }
        } else if (name.equals("jump")) {
            player.jump();
        }
    }
}

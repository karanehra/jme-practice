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
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;

/**
 *
 * @author karanehra
 */
public class SimplePlayer {

    private AssetManager assetManager;
    private Node rootNode;
    private BulletAppState bulletAppState;
    private Node geo_cam;
    private VehicleControl vehicle;

    private float SCALE_FACTOR = 0.8f;
    private float chassis_x = 0.6f*SCALE_FACTOR, chassis_y = 0.2f*SCALE_FACTOR, chassis_z = 1.2f*SCALE_FACTOR;

    public SimplePlayer(AssetManager am, Node rn, BulletAppState ba) {
        assetManager = am;
        rootNode = rn;
        bulletAppState = ba;
        initPlayer();
    }

    private void initPlayer() {
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Red);

        Material mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat2.setColor("Color", ColorRGBA.Gray);
        
        Box box = new Box(chassis_x,chassis_y,chassis_z);
        Geometry chasis = new Geometry("box", box);
        chasis.setMaterial(mat2);
        CollisionShape carHull = CollisionShapeFactory.createDynamicMeshShape(chasis);

        Node vehicleNode = new Node("vehicleNode");
        vehicleNode.attachChild(chasis);
        geo_cam = vehicleNode;

        vehicle = new VehicleControl(carHull, 1000);
        vehicleNode.addControl(vehicle);

        float stiffness = 60.0f;//200=f1 car
        float compValue = .3f; //(should be lower than damp)
        float dampValue = .4f;
        vehicle.setSuspensionCompression(compValue * 2.0f * FastMath.sqrt(stiffness));
        vehicle.setSuspensionDamping(dampValue * 2.0f * FastMath.sqrt(stiffness));
        vehicle.setSuspensionStiffness(stiffness);
        vehicle.setMaxSuspensionForce(10000.0f);
//        vehicle.setSpatial(chasis);

        Vector3f wheelDirection = new Vector3f(0, -1, 0);
        Vector3f wheelAxle = new Vector3f(-1, 0, 0);
        float radius = 0.5f *SCALE_FACTOR;
        float restLength = 0.2f *SCALE_FACTOR;
        float yOff = -0.4f*SCALE_FACTOR;
        float xOff = 1f*SCALE_FACTOR;
        float zOff = 1f*SCALE_FACTOR;

        Cylinder wheelMesh = new Cylinder(16, 16, radius, radius * 0.6f, true);

        Node node1 = new Node("wheel 1 node");
        Geometry wheels1 = new Geometry("wheel 1", wheelMesh);
        node1.attachChild(wheels1);
        wheels1.rotate(0, FastMath.HALF_PI, 0);
        wheels1.setMaterial(mat);
        vehicle.addWheel(node1, new Vector3f(-xOff, yOff, zOff),
                wheelDirection, wheelAxle, restLength, radius, true);

        Node node2 = new Node("wheel 2 node");
        Geometry wheels2 = new Geometry("wheel 2", wheelMesh);
        node2.attachChild(wheels2);
        wheels2.rotate(0, FastMath.HALF_PI, 0);
        wheels2.setMaterial(mat);
        vehicle.addWheel(node2, new Vector3f(xOff, yOff, zOff),
                wheelDirection, wheelAxle, restLength, radius, true);

        Node node3 = new Node("wheel 3 node");
        Geometry wheels3 = new Geometry("wheel 3", wheelMesh);
        node3.attachChild(wheels3);
        wheels3.rotate(0, FastMath.HALF_PI, 0);
        wheels3.setMaterial(mat);
        vehicle.addWheel(node3, new Vector3f(-xOff, yOff, -zOff),
                wheelDirection, wheelAxle, restLength, radius, false);

        Node node4 = new Node("wheel 4 node");
        Geometry wheels4 = new Geometry("wheel 4", wheelMesh);
        node4.attachChild(wheels4);
        wheels4.rotate(0, FastMath.HALF_PI, 0);
        wheels4.setMaterial(mat);
        vehicle.addWheel(node4, new Vector3f(xOff, yOff, -zOff),
                wheelDirection, wheelAxle, restLength, radius, false);

        vehicleNode.attachChild(node1);
        vehicleNode.attachChild(node2);
        vehicleNode.attachChild(node3);
        vehicleNode.attachChild(node4);

        vehicleNode.rotate(0, FastMath.PI / 2, 0);
        rootNode.attachChild(vehicleNode);
        vehicleNode.setLocalTranslation(new Vector3f(10, 15, 10));

        bulletAppState.getPhysicsSpace().add(vehicle);

//        Box box2 = new Box(100, 2, 100);
//        Geometry geom = new Geometry("floor", box2);
//        geom.setMaterial(mat2);
//        geom.setLocalTranslation(new Vector3f(0, -10, 0));
//        RigidBodyControl rgc = new RigidBodyControl(0f);
//        geom.addControl(rgc);
//        rgc.getCollisionShape().setMargin(0.5f);
//        bulletAppState.getPhysicsSpace().add(geom);
//        rootNode.attachChild(geom);

    }

    public Node getGeo() {
        return geo_cam;
    }

    public VehicleControl getVehicle() {
        return vehicle;
    }
    
    public void jump(){
        vehicle.applyImpulse(new Vector3f(0f,100f,0), Vector3f.ZERO);
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.collision.shapes.CylinderCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.joints.HingeJoint;
import com.jme3.bullet.joints.SixDofSpringJoint;
import com.jme3.bullet.joints.SliderJoint;
import com.jme3.bullet.joints.motors.RotationalLimitMotor;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;

/**
 *
 * @author karanehra
 */
public class Player {

    private final AssetManager assetManager;
    private final Node rootNode;
    private final BulletAppState bulletAppState;

    private Geometry body_geo;

    private RigidBodyControl chassis_control;
    private RigidBodyControl suspension_control_1, suspension_control_2, suspension_control_3, suspension_control_4;

    private Material chassis_material, suspension_material, wheel_material;
    
    private float SCALE_FACTOR = 1f;
    private float CHASSIS_WEIGHT = 50f;
    private float SUSPENSION_WEIGHT = 5f;

    private final float chassis_x = 0.4f*SCALE_FACTOR, chassis_y = 0.1f*SCALE_FACTOR, chassis_z = 0.4f*SCALE_FACTOR;
    private final float suspension_x = 0.01f, suspension_y = chassis_y, suspension_z = 0.01f;
    private final float chassis_piv_x = chassis_x + 0.05f, chassis_piv_y = 0, chassis_piv_z = chassis_z + 0.05f;
    private final float sus_piv_x = 0, sus_piv_y = chassis_y, sus_piv_z = 0;
    private final float sus_piv_2_x = 0f, sus_piv_2_y = -chassis_y, sus_piv_2_z = 0.1f;
    private final float tyre_radius = 0.12f, tyre_height = 0.05f;

    private float tyre_speed = 0f;

    private HingeJoint wheel_joint_1, wheel_joint_2, wheel_joint_3, wheel_joint_4;
    private SixDofSpringJoint suspension_joint_1, suspension_joint_2, suspension_joint_3, suspension_joint_4;
    private SixDofSpringJoint suspensions[] = {suspension_joint_1, suspension_joint_2, suspension_joint_3, suspension_joint_4};
    private HingeJoint wheels[] = {wheel_joint_1, wheel_joint_2, wheel_joint_3, wheel_joint_4};

    public Player(AssetManager am, Node rn, BulletAppState ba) {
        assetManager = am;
        rootNode = rn;
        bulletAppState = ba;
        initPlayer();
    }

    private void initPlayer() {
        chassis_material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        chassis_material.setColor("Color", ColorRGBA.White);
        suspension_material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        suspension_material.setColor("Color", ColorRGBA.Gray);
        wheel_material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        wheel_material.setColor("Color", ColorRGBA.Black);
        createChassis();
        createSuspensions();
        createWheels();
    }

    private void createChassis() {
        Box body = new Box(chassis_x, chassis_y, chassis_z);
        body_geo = new Geometry("Chassis", body);
        body_geo.setMaterial(chassis_material);
        body_geo.setLocalTranslation(0, 5, 0);
        chassis_control = new RigidBodyControl(CHASSIS_WEIGHT);
        body_geo.addControl(chassis_control);
        chassis_control.setRestitution(0.4f);
        chassis_control.getCollisionShape().setMargin(0.2f);
        bulletAppState.getPhysicsSpace().add(body_geo);
        rootNode.attachChild(body_geo);
    }

    private void createSuspensions() {
        Box child = new Box(suspension_x, suspension_y, suspension_z);

        Geometry suspension_1 = new Geometry("child", child);
        suspension_1.setMaterial(suspension_material);
        suspension_control_1 = new RigidBodyControl(SUSPENSION_WEIGHT);
        suspension_1.addControl(suspension_control_1);
        suspension_control_1.getCollisionShape().setMargin(0.1f);
        suspension_control_1.setCcdMotionThreshold(1e-6f);
        bulletAppState.getPhysicsSpace().add(suspension_1);
        rootNode.attachChild(suspension_1);

        Quaternion q1 = new Quaternion();
        Quaternion q2 = new Quaternion();
        Matrix3f rot1 = q1.toRotationMatrix();
        Matrix3f rot2 = q2.toRotationMatrix();

        suspension_joint_1 = new SixDofSpringJoint(
                chassis_control,
                suspension_control_1,
                new Vector3f(chassis_piv_x, chassis_piv_y, chassis_piv_z),
                new Vector3f(sus_piv_x, sus_piv_y, sus_piv_z),
                rot1, rot2, false);

        Geometry suspension_2 = new Geometry("child", child);
        suspension_control_2 = new RigidBodyControl(SUSPENSION_WEIGHT);
        suspension_2.addControl(suspension_control_2);
        suspension_2.setMaterial(suspension_material);
        suspension_control_2.getCollisionShape().setMargin(0.1f);
        suspension_control_2.setCcdMotionThreshold(1e-6f);
        bulletAppState.getPhysicsSpace().add(suspension_2);
        rootNode.attachChild(suspension_2);

        suspension_joint_2 = new SixDofSpringJoint(
                chassis_control,
                suspension_control_2,
                new Vector3f(-chassis_piv_x, chassis_piv_y, chassis_piv_z),
                new Vector3f(sus_piv_x, sus_piv_y, sus_piv_z),
                rot1, rot2, false);

        Geometry suspension_3 = new Geometry("child", child);
        suspension_3.setMaterial(suspension_material);
        suspension_control_3 = new RigidBodyControl(SUSPENSION_WEIGHT);
        suspension_3.addControl(suspension_control_3);
        suspension_control_3.getCollisionShape().setMargin(0.1f);
        suspension_control_3.setCcdMotionThreshold(1e-6f);
        bulletAppState.getPhysicsSpace().add(suspension_3);
        
        rootNode.attachChild(suspension_3);

        suspension_joint_3 = new SixDofSpringJoint(
                chassis_control,
                suspension_control_3,
                new Vector3f(-chassis_piv_x, chassis_piv_y, -chassis_piv_z),
                new Vector3f(sus_piv_x, sus_piv_y, sus_piv_z),
                rot1, rot2, false);

        Geometry suspension_4 = new Geometry("child", child);
        suspension_4.setMaterial(suspension_material);
        suspension_control_4 = new RigidBodyControl(SUSPENSION_WEIGHT);
        suspension_4.addControl(suspension_control_4);
        suspension_control_4.getCollisionShape().setMargin(0.1f);
        suspension_control_4.setCcdMotionThreshold(1e-6f);
        bulletAppState.getPhysicsSpace().add(suspension_4);
        rootNode.attachChild(suspension_4);

        suspension_joint_4 = new SixDofSpringJoint(chassis_control,
                suspension_control_4,
                new Vector3f(chassis_piv_x, chassis_piv_y, -chassis_piv_z),
                new Vector3f(sus_piv_x, sus_piv_y, sus_piv_z),
                rot1, rot2, false);

        SixDofSpringJoint join_arr[] = {suspension_joint_1, suspension_joint_2, suspension_joint_3, suspension_joint_4};

        for (SixDofSpringJoint joint : join_arr) {
            joint.setAngularLowerLimit(new Vector3f(0, 0, 0));
            joint.setAngularUpperLimit(new Vector3f(0, 0, 0));
            joint.setStiffness(0, 0.3f);
            bulletAppState.getPhysicsSpace().add(joint);
        }

    }

    private void createWheels() {
        Cylinder mesh = new Cylinder(3, 16, tyre_radius, tyre_height, true, true);

        Geometry wheel_1 = new Geometry("Wheel_1", mesh);
        wheel_1.setMaterial(wheel_material);
        RigidBodyControl wheel_control = new RigidBodyControl(5f);
        wheel_1.addControl(wheel_control);
        wheel_control.setCcdMotionThreshold(1e-6f);
        wheel_1.rotate(FastMath.HALF_PI, 0, 0);
        bulletAppState.getPhysicsSpace().add(wheel_1);

        wheel_joint_1 = new HingeJoint(
                suspension_control_1,
                wheel_control,
                new Vector3f(sus_piv_2_x, sus_piv_2_y, sus_piv_2_z),
                new Vector3f(0, 0, -0.04f),
                Vector3f.UNIT_Z,
                Vector3f.UNIT_Z
        );
        bulletAppState.getPhysicsSpace().add(wheel_joint_1);
        rootNode.attachChild(wheel_1);

        Geometry wheel_2 = new Geometry("Wheel_2", mesh);
        wheel_2.setMaterial(wheel_material);
        RigidBodyControl wheel_control_2 = new RigidBodyControl(5f);
        wheel_2.addControl(wheel_control_2);
        wheel_control_2.setCcdMotionThreshold(1e-6f);
        wheel_2.rotate(FastMath.HALF_PI, 0, 0);
        bulletAppState.getPhysicsSpace().add(wheel_2);

        wheel_joint_2 = new HingeJoint(
                suspension_control_2,
                wheel_control_2,
                new Vector3f(sus_piv_2_x, sus_piv_2_y, sus_piv_2_z),
                new Vector3f(0, 0, -0.04f),
                Vector3f.UNIT_Z,
                Vector3f.UNIT_Z
        );
        bulletAppState.getPhysicsSpace().add(wheel_joint_2);
        rootNode.attachChild(wheel_2);

        Geometry wheel_3 = new Geometry("Wheel_3", mesh);
        wheel_3.setMaterial(wheel_material);
        RigidBodyControl wheel_control_3 = new RigidBodyControl(5f);
        wheel_3.addControl(wheel_control_3);
        wheel_control_3.setCcdMotionThreshold(1e-6f);
        wheel_3.rotate(FastMath.HALF_PI, 0, 0);
        bulletAppState.getPhysicsSpace().add(wheel_3);

        wheel_joint_3 = new HingeJoint(
                suspension_control_3,
                wheel_control_3,
                new Vector3f(sus_piv_2_x, sus_piv_2_y, -sus_piv_2_z),
                new Vector3f(0, 0, 0.04f),
                Vector3f.UNIT_Z,
                Vector3f.UNIT_Z
        );
        bulletAppState.getPhysicsSpace().add(wheel_joint_3);
        rootNode.attachChild(wheel_3);

        Geometry wheel_4 = new Geometry("Wheel_4", mesh);
        wheel_4.setMaterial(wheel_material);
        RigidBodyControl wheel_control_4 = new RigidBodyControl(5f);
        wheel_4.addControl(wheel_control_4);
        wheel_4.rotate(FastMath.HALF_PI, 0, 0);
        wheel_control_3.setCcdMotionThreshold(1e-6f);
        bulletAppState.getPhysicsSpace().add(wheel_4);
        wheel_control_4.setFriction(0.4f);

        wheel_joint_4 = new HingeJoint(
                suspension_control_4,
                wheel_control_4,
                new Vector3f(sus_piv_2_x, sus_piv_2_y, -sus_piv_2_z),
                new Vector3f(0, 0, 0.04f),
                Vector3f.UNIT_Z,
                Vector3f.UNIT_Z
        );
        bulletAppState.getPhysicsSpace().add(wheel_joint_4);
        rootNode.attachChild(wheel_4);

    }

    public Geometry getGeo() {
        return body_geo;
    }

    public void applyForce() {
        chassis_control.applyImpulse(new Vector3f(0, 70f, 0), Vector3f.ZERO);
    }

    public void resetCar() {
        body_geo.setLocalTranslation(0, 10, 0);
    }

    public void accelerate(float value) {
        if (tyre_speed + value > 0) {
            tyre_speed += value;
        }
        wheel_joint_1.enableMotor(true, -tyre_speed, 1000f);
//        wheel_joint_1.
//        wheel_joint_2.enableMotor(true, tyre_speed, 1000f);
//        wheel_joint_3.enableMotor(true, tyre_speed, 1000f);
//        wheel_joint_4.enableMotor(true, -tyre_speed, 1000f);
//        bulletAppState.getPhysicsSpace().update(30f);

    }

}

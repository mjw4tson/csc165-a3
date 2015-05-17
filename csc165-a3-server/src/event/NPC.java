package event;

import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;

import java.util.Random;

import sage.scene.shape.Sphere;

public class NPC extends Sphere {
    private static int     NEXT_ID          = 0;
    private static Point3D DEFAULT_LOCATION = new Point3D(50, 14.0f, 75);

    private boolean        isSmall          = false;

    private int            id;

    public NPC() {
        id = ++NEXT_ID;

        Random rand = new Random();

        Matrix3D trans = new Matrix3D();
        trans.translate(DEFAULT_LOCATION.getX() + (rand.nextInt(10) - 20), DEFAULT_LOCATION.getY(), DEFAULT_LOCATION.getZ() + (rand.nextInt(10) - 20));
        setLocalTranslation(trans);
    }

    public int getId() {
        return id;
    }

    public double getX() {
        return getWorldTransform().getCol(3).getX();
    }

    public double getY() {
        return getWorldTransform().getCol(3).getY();
    }

    public double getZ() {
        return getWorldTransform().getCol(3).getZ();
    }

    public void updateLocation() {
        // TODO
    }

    public void getSmall() {
        if (isSmall) {

        } else {
            this.scale(.5f, .5f, .5f);
            isSmall = !isSmall;
        }
    }

    public void getBig() {
        if (!isSmall) {

        } else {
            this.scale(2f, 2f, 2f);
            isSmall = !isSmall;
        }
    }
}

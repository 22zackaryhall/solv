package math;

import com.badlogic.gdx.math.Rectangle;

public class AABB {


    public static boolean collides(Rectangle a, Rectangle b) {
        float leftA, leftB;
        float rightA, rightB;
        float topA, topB;
        float bottomA, bottomB;

        leftA = a.x;
        rightA = a.x + a.width;
        topA = a.y;
        bottomA = a.y + a.height;

        leftB = b.x;
        rightB = b.x + b.width;
        topB = b.y;
        bottomB = b.y + b.height;

        //posotive width and height collision
        if (b.width > 0) {
            if (rightA <= leftB)
                return false;
            if (leftA >= rightB)
                return false;
        }

        if (b.height > 0) {
            if (bottomA <= topB)
                return false;
            if (topA >= bottomB)
                return false;
        }


        //negative width and height collision
        if (b.height < 0) {
            if (topA >= topB)
                return false;
            if (bottomA <= bottomB)
                return false;
        }

        if (b.width < 0) {
            if (rightA <= rightB)
                return false;
            if (leftA >= leftB)
                return false;
        }

        return true;
    }
}
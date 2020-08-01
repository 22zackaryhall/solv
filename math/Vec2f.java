package math;

public class Vec2f {

    public float x, y;

    public Vec2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vec2f() {
    }

    public Vec2f add(Vec2f a, Vec2f b) {
        return new Vec2f(a.x + b.x, a.y + b.y);
    }

    public Vec2f sub(Vec2f a, Vec2f b) {
        return new Vec2f(a.x - b.x, a.y - b.y);
    }

    public Vec2f mul(Vec2f a, Vec2f b) {
        return new Vec2f(a.x * b.x, a.y * b.y);
    }

    public Vec2f div(Vec2f a, Vec2f b) {
        return new Vec2f(a.x / b.x, a.y / b.y);
    }

    public void norm() {
        float magnitude = (float) Math.sqrt(this.x * this.x + this.y * this.y);
        this.x /= magnitude;
        this.y /= magnitude;
    }

    public float getNorm(Vec2f v) {
        return (float) Math.sqrt(v.x * v.x + v.y * v.y);
    }

    public float getNorm() {
        return (float) Math.sqrt(this.x * this.x + this.y * this.y);
    }
}
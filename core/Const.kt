package core

import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector2
import java.util.*

object Const {

    var CURRENT_FILE = "cave_0";
    lateinit var defaultMatrix: Matrix4

    val TILESIZE = 64;
    var ZOOM = 1f;
    var MOVING_CAMERA_X = true;
    var MOVING_CAMERA_Y = true;
    var SPRITE_LERP = .2f;
    var HAS_MIN_X = false;
    var HAS_MIN_Y = false;
    var HAS_MAX_X = false;
    var HAS_MAX_Y = false;
    var MIN_X: Float = 0f;
    var MIN_Y: Float = 0f;
    var MAX_X: Float = 0f;
    var MAX_Y: Float = 0f;

    // space_2 things
    var COMPLETEDSPACE = false // finished space world?

    var BOOKS = ArrayList<Int>();
    var LAST_ROCKBLOCKER_POS: Vector2? = null;

    var QUOTES = ArrayList<String>();
    var QUOTE_CURRENT = "POOPOO PEEPEE";

    // time it takes to start a step
    var MOVEMENT_DELAY = 170L;
    var PLAYER_MOVEMENT_DELAY = 90L;

    // Duration spawn text is visible
    var debugging = false;
    var MUTED = false;

    // NOTE(Jervac): false so when you enter a level, doors activated due to solution tile on save don't make their sound
    // This becomes true after the duration of the door sound is over
    var MUTE_SAFE = false;
    var FIRST_TIME_PLAYING = false; // TODO:(Jervac): SET TO TRUE ON DEPLOY so it Saves your last location
    var SHADOW_HERO_LEVEL = false;
    var CREDITS = false;

    // TILES
    val EMPTY_ID = 0;

    val GRASS_0_ID = 1;
    val WATER_ID = 2;
    val FLOOR_1_ID = 3;
    val FLOOR_2_ID = 4;
    val FLOOR_3_ID = 5;
    val FLOOR_4_ID = 6;
    val FLOOR_5_ID = 7;
    val FLOOR_6_ID = 8;
    val FLOOR_7_ID = 9;
    val FLOOR_8_ID = 10;
    val FLOOR_9_ID = 11;
    val FLOOR_10_ID = 12;
    val FLOOR_11_ID = 13;
    val FLOOR_12_ID = 14;
    val FLOOR_13_ID = 15;
    val FLOOR_14_ID = 16;
    val DOCK_0 = 17;
    val SAND_0 = 18;
    val GRASS_1_ID = 19;
    val GRASS_2_ID = 20;
    val TOWER_BRICK = 21;
    val TOWER_BRICK_DARK = 22;

    val SPACE_ENTRANCE = 23;

    // OBJECTS
    val WALL_ID = 1;
    val ROCK_ID = 2;
    val LASER_UP_ID = 3;
    val LASER_DOWN_ID = 4;
    val LASER_LEFT_ID = 5;
    val LASER_RIGHT_ID = 6;
    val REFLECTOR_DOWNRIGHT_ID = 7;
    val REFLECTOR_DOWNLEFT_ID = 8;
    val REFLECTOR_UPLEFT_ID = 9;
    val REFLECTOR_UPRIGHT_ID = 10;

    // moves left/right faces up or down
    val MOVING_LASER_LR_DOWN_ID = 11;
    val MOVING_LASER_LR_UP_ID = 12;

    // moves up/down faces left or right
    val MOVING_LASER_UD_RIGHT_ID = 13;
    val MOVING_LASER_UD_LEFT_ID = 14;

    // walls
    val WALL_DETAIL = 19;
    val WALL_UP = 20;
    val WALL_DOWN = 21;
    val WALL_LEFT = 22;
    val WALL_RIGHT = 23;
    val WALL_UPLEFT = 24;
    val WALL_UPRIGHT = 25;
    val WALL_DOWNLEFT = 26;
    val WALL_DOWNRIGHT = 27;
    val WALL_UPDOWN = 28;
    val WALL_LEFTRIGHT = 29;
    val WALL_WATER_DOWNRIGHT2_ID = 30;
    val WALL_WATER_DOWNLEFT2_ID = 31
    val WALL_2_ID = 32
    val WALL_2_UP_ID = 33;
    val WALL_2_DOWN_ID = 34;
    val WALL_2_LEFT_ID = 35;
    val WALL_2_RIGHT_ID = 36;
    val WALL_2_UPRIGHT_ID = 37;
    val WALL_2_UPLEFT_ID = 38;
    val WALL_2_DOWNRIGHT_ID = 39;
    val WALL_2_DOWNLEFT_ID = 40;
    val WALL_2_UPRIGHT2_ID = 41;
    val WALL_2_UPLEFT2_ID = 42;
    val WALL_2_DOWNRIGHT2_ID = 43;
    val WALL_2_DOWNLEFT2_ID = 44;
    val BONE_ID = 46;
    val WALL_INVISIBLE = 47;
    val WALL_2_UPDOWN = 48;
    val WALL_2_LEFTRIGHT = 49;
    val WALL_2_UPDOWNLEFT = 50;
    val WALL_2_UPDOWNRIGHT = 51;
    val WALL_2_LEFTRIGHTUP = 52;
    val WALL_2_LEFTRIGHTDOWN = 53;
}

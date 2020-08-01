package core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import entities.*;
import util.Gfx;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class world {

    public Vector2 spawnPos = new Vector2();
    public Vector2 shadowspawnPos = new Vector2();

    public ArrayList<TileFloor> tfloors = new ArrayList<TileFloor>();
    public ArrayList<EntityFloor> efloors = new ArrayList<EntityFloor>();
    public ArrayList<Triggerable> trigerables = new ArrayList<Triggerable>();

    public int currentFloorId = 0;
    public String path;
    private BasicGame game;

    public world(BasicGame game) {
        this.game = game;
    }

    public void loadTriggerables(String p) {
        path = "maps/" + p;

        FileHandle handle = Gdx.files.internal(path);
        String text = handle.readString();
        String lines[] = text.split("\n");

        Const.INSTANCE.setSHADOW_HERO_LEVEL(false);

        boolean containsSpawnText = false;

        // clear min / max camera settings from previous level
        Const.INSTANCE.setHAS_MAX_X(false);
        Const.INSTANCE.setHAS_MAX_Y(false);
        Const.INSTANCE.setHAS_MIN_X(false);
        Const.INSTANCE.setHAS_MIN_Y(false);

        for (String line : lines) {
            if (line.startsWith("#"))
                continue;

            String items[] = line.split(":");

            if (items[0].equals("cam")) {
                Gfx.cam.position.x = Float.parseFloat(items[1]);
                Gfx.cam.position.y = Float.parseFloat(items[2]);
                Const.INSTANCE.setMOVING_CAMERA_X(false);
                Const.INSTANCE.setMOVING_CAMERA_Y(false);
            }

            if (items[0].equals("cam_follow")) {
                Const.INSTANCE.setMOVING_CAMERA_X(Boolean.parseBoolean(items[1]));
                Const.INSTANCE.setMOVING_CAMERA_Y(Boolean.parseBoolean(items[2]));
            }

            if (items[0].equals("cam_max_y")) {
                Const.INSTANCE.setMOVING_CAMERA_Y(true);
                Const.INSTANCE.setHAS_MAX_Y(true);
                Const.INSTANCE.setMAX_Y(Float.parseFloat(items[1]));
            }
            if (items[0].equals("cam_min_y")) {
                Const.INSTANCE.setMOVING_CAMERA_Y(true);
                Const.INSTANCE.setHAS_MIN_Y(true);
                Const.INSTANCE.setMIN_Y(Float.parseFloat(items[1]));
            }
            if (items[0].equals("cam_max_x")) {
                Const.INSTANCE.setMOVING_CAMERA_X(true);
                Const.INSTANCE.setHAS_MAX_X(true);
                Const.INSTANCE.setMAX_X(Float.parseFloat(items[1]));
            }
            if (items[0].equals("cam_min_x")) {
                Const.INSTANCE.setMOVING_CAMERA_X(true);
                Const.INSTANCE.setHAS_MIN_X(true);
                Const.INSTANCE.setMIN_X(Float.parseFloat(items[1]));
            }

            if (items[0].equals("zoom"))
                Const.INSTANCE.setZOOM(Float.parseFloat(items[1]));

            if (items[0].equals("book")) {
                if (!Const.INSTANCE.getBOOKS().contains(Integer.parseInt(items[3]))) {
                    trigerables.add(new BookPage(
                            new Vector2(Const.INSTANCE.getTILESIZE() * Integer.parseInt(items[1]), Const.INSTANCE.getTILESIZE() * Integer.parseInt(items[2])),
                            game,
                            Integer.parseInt(items[3])
                    ));
                }
            }

            if (items[0].equals("laserbutton"))
                trigerables.add(new LaserButton(
                        new Vector2(Const.INSTANCE.getTILESIZE() * Integer.parseInt(items[1]), Const.INSTANCE.getTILESIZE() * Integer.parseInt(items[2])),
                        Integer.parseInt(items[3]),
                        Boolean.parseBoolean(items[4]),
                        game
                ));

            if (items[0].equals("solutiontile")) {
                trigerables.add(new SolutionTile(
                        new Vector2(
                                Const.INSTANCE.getTILESIZE() * Integer.parseInt(items[1]),
                                Const.INSTANCE.getTILESIZE() * Integer.parseInt(items[2])),
                        Integer.parseInt(items[3]),
                        game,
                        Boolean.parseBoolean(items[4]),
                        false
                ));
            }


            if (items[0].equals("solutiontile2")) {
                trigerables.add(new SolutionTile(
                        new Vector2(
                                Const.INSTANCE.getTILESIZE() * Integer.parseInt(items[1]),
                                Const.INSTANCE.getTILESIZE() * Integer.parseInt(items[2])),
                        Integer.parseInt(items[3]),
                        game,
                        Boolean.parseBoolean(items[4]),
                        true
                ));
            }

            if (items[0].equals("solutiontile3")) {
                SolutionTile st = new SolutionTile(
                        new Vector2(
                                Const.INSTANCE.getTILESIZE() * Integer.parseInt(items[1]),
                                Const.INSTANCE.getTILESIZE() * Integer.parseInt(items[2])),
                        Integer.parseInt(items[3]),
                        game,
                        Boolean.parseBoolean(items[4]),
                        true
                );
                st.setSolutiontile3(true);
                trigerables.add(st);
            }
            if (items[0].equals("areadoor")) {
                ArrayList<Integer> needed = new ArrayList<Integer>();
                int i = 5;
                while (true) {
                    try {
                        needed.add(Integer.parseInt(items[i]));
                        i++;
                    } catch (Exception e) {
                        break;
                    }
                }
                trigerables.add(new AreaDoor(
                        new Vector2(Const.INSTANCE.getTILESIZE() * Integer.parseInt(items[1]), Const.INSTANCE.getTILESIZE() * Integer.parseInt(items[2])),
                        game,
                        new Vector2(Const.INSTANCE.getTILESIZE() * Integer.parseInt(items[3]), Const.INSTANCE.getTILESIZE() * Integer.parseInt(items[4])),
                        needed
                ));
            }

            if (items[0].equals("door")) {
                trigerables.add(new Door(
                        new Vector2(Const.INSTANCE.getTILESIZE() * Integer.parseInt(items[1]), Const.INSTANCE.getTILESIZE() * Integer.parseInt(items[2])),
                        Integer.parseInt(items[3]),
                        Boolean.parseBoolean(items[4]),
                        game
                ));
                Door d = (Door) trigerables.get(trigerables.size() - 1);
                d.resize('u', 1);
            }

            if (items[0].equals("door2")) {
                trigerables.add(new Door(
                        new Vector2(Const.INSTANCE.getTILESIZE() * Integer.parseInt(items[1]), Const.INSTANCE.getTILESIZE() * Integer.parseInt(items[2])),
                        Integer.parseInt(items[3]),
                        Boolean.parseBoolean(items[4]),
                        game
                ));
                Door d = (Door) trigerables.get(trigerables.size() - 1);
                d.resize('r', 2);
            }

            if (items[0].equals("door3")) {
                trigerables.add(new Door(
                        new Vector2(Const.INSTANCE.getTILESIZE() * Integer.parseInt(items[1]), Const.INSTANCE.getTILESIZE() * Integer.parseInt(items[2])),
                        Integer.parseInt(items[3]),
                        Boolean.parseBoolean(items[4]),
                        game
                ));
                Door d = (Door) trigerables.get(trigerables.size() - 1);
                d.resize('r', 1);
            }

            if (items[0].equals("door4")) {
                trigerables.add(new Door(
                        new Vector2(Const.INSTANCE.getTILESIZE() * Integer.parseInt(items[1]), Const.INSTANCE.getTILESIZE() * Integer.parseInt(items[2])),
                        Integer.parseInt(items[3]),
                        Boolean.parseBoolean(items[4]),
                        game
                ));
                Door d = (Door) trigerables.get(trigerables.size() - 1);
                d.resize('d', 2);
            }

            if (items[0].equals("door5")) {
                trigerables.add(new Door(
                        new Vector2(Const.INSTANCE.getTILESIZE() * Integer.parseInt(items[1]), Const.INSTANCE.getTILESIZE() * Integer.parseInt(items[2])),
                        Integer.parseInt(items[3]),
                        Boolean.parseBoolean(items[4]),
                        game
                ));
                Door d = (Door) trigerables.get(trigerables.size() - 1);
                d.resize('d', 1);
            }

            if (items[0].equals("button"))
                trigerables.add(new Button(
                        new Vector2(Const.INSTANCE.getTILESIZE() * Integer.parseInt(items[1]), Const.INSTANCE.getTILESIZE() * Integer.parseInt(items[2])),
                        Integer.parseInt(items[3]),
                        Boolean.parseBoolean(items[4]),
                        game
                ));

            if (items[0].equals("levelswitcher")) {
                trigerables.add(new LevelSwitcher(
                        new Vector2(Const.INSTANCE.getTILESIZE() * Integer.parseInt(items[1]), Const.INSTANCE.getTILESIZE() * Integer.parseInt(items[2])),
                        game,
                        items[3],
                        false,
                        new Vector2()
                ));
            }
            if (items[0].equals("levelswitcher2")) {
                trigerables.add(new LevelSwitcher(
                        new Vector2(Const.INSTANCE.getTILESIZE() * Integer.parseInt(items[1]), Const.INSTANCE.getTILESIZE() * Integer.parseInt(items[2])),
                        game,
                        items[3],
                        true,
                        new Vector2(Const.INSTANCE.getTILESIZE() * Integer.parseInt(items[4]), Const.INSTANCE.getTILESIZE() * Integer.parseInt(items[5]))
                ));
            }

            if (items[0].equals("spawn"))
                spawnPos = new Vector2(Const.INSTANCE.getTILESIZE() * Integer.parseInt(items[1]), Const.INSTANCE.getTILESIZE() * Integer.parseInt(items[2]));

            if (items[0].equals("shadowspawn")) {
                shadowspawnPos = new Vector2(Const.INSTANCE.getTILESIZE() * Integer.parseInt(items[1]), Const.INSTANCE.getTILESIZE() * Integer.parseInt(items[2]));
                Const.INSTANCE.setSHADOW_HERO_LEVEL(true);
            }

            if (items[0].equals("walker")) {
                trigerables.add(new Walker(
                        new Vector2(Const.INSTANCE.getTILESIZE() * Integer.parseInt(items[2]), Const.INSTANCE.getTILESIZE() * Integer.parseInt(items[3])),
                        69, // 69 cuz this shouldn't be activated by any buttons or anything
                        Boolean.parseBoolean(items[4]),
                        game,
                        items[1]
                ));
            }

            if (items[0].equals("spawntext")) {
                game.spawnText(items[1]);
                containsSpawnText = true;
            }

            if (items[0].equals("spawnmusic")) {
                game.theme.stop();
                game.theme = Gdx.audio.newMusic(Gdx.files.internal("music/" + items[1] + ".mp3"));
                if (items[2] != null)
                    game.theme.setVolume(Float.parseFloat(items[2]));
                game.theme.play();
            }

            if (items[0].equals("rockblocker")) {
                ArrayList<Integer> needed = new ArrayList<Integer>();
                int i = 3;
                while (true) {
                    try {
                        needed.add(Integer.parseInt(items[i]));
                        i++;
                    } catch (Exception e) {
                        break;
                    }
                }
                RockBlocker rb = new RockBlocker(
                        new Vector2(
                                Const.INSTANCE.getTILESIZE() * Integer.parseInt(items[1]),
                                Const.INSTANCE.getTILESIZE() * Integer.parseInt(items[2])),
                        Integer.parseInt(items[3]),
                        game,
                        needed
                );
                // use up and down texture if needed
                try {
                    if (items[4].equals("ud")) {
                        rb.getSprite().setTexture(new Texture("tiles/rockblocker_ud.png"));
                    }
                } catch (Exception e) {
                }
                trigerables.add(rb);
            }

            // normal rockblocker that blocks rocks only
            if (items[0].equals("rockblocker2")) {
                // empty
                ArrayList<Integer> needed = new ArrayList<Integer>();
                RockBlocker rb = new RockBlocker(
                        new Vector2(
                                Const.INSTANCE.getTILESIZE() * Integer.parseInt(items[1]),
                                Const.INSTANCE.getTILESIZE() * Integer.parseInt(items[2])),
                        Integer.parseInt(items[3]),
                        game,
                        needed
                );

                // use up and down texture if needed
                try {
                    if (items[4].equals("ud")) {
                        rb.getSprite().setTexture(new Texture("tiles/rockblocker_ud.png"));
                    }
                } catch (Exception e) {
                }
                trigerables.add(rb);
            }

            if (items[0].equals("rock")) {
                game.map.getCurrentEntityFloor().entities.add(
                        new StaticEntity(
                                game,
                                Const.INSTANCE.getTILESIZE() * Integer.parseInt(items[1]),
                                Const.INSTANCE.getTILESIZE() * Integer.parseInt(items[2]),
                                Const.INSTANCE.getROCK_ID())
                );
            }

            if (items[0].equals("reflector")) {
                game.map.getCurrentEntityFloor().entities.add(
                        new StaticEntity(
                                game,
                                Const.INSTANCE.getTILESIZE() * Integer.parseInt(items[1]),
                                Const.INSTANCE.getTILESIZE() * Integer.parseInt(items[2]),
                                Integer.parseInt(items[3]))
                );
            }

        }

        if (!containsSpawnText)
            game.setSpawnTextContents("");
    }

    public TileFloor getCurrentTileFloor() {
        return getTileFloor(currentFloorId);
    }

    public EntityFloor getCurrentEntityFloor() {
        return getEntityFloor(currentFloorId);
    }

    public void setCurrentFloorId(int id) {
        currentFloorId = id;
    }

    public void addTileFloor(TileFloor layer) {
        tfloors.add(layer);
    }

    public void addEntityFloor(EntityFloor layer) {
        efloors.add(layer);
    }

    public TileFloor getTileFloor(int id) {
        for (TileFloor t : tfloors) {
            if (t.id == id) {
                return t;
            }
        }
        return null;
    }

    public EntityFloor getEntityFloor(int id) {
        for (EntityFloor t : efloors) {
            if (t.id == id) {
                return t;
            }
        }
        return null;
    }

    public void updateTriggerables() {
        for (Triggerable t : trigerables)
            if (t.alive)
                t.update();
    }

    public void renderTriggerables() {
        for (Triggerable t : trigerables)
            if (t.getClass() != Door.class && t.getClass() != SolutionTile.class)
                t.render();
    }

    public void renderTriggerableDoors() {
        for (Triggerable t : trigerables)
            if (t.getClass() == Door.class)
                t.render();
    }

    public void renderTriggerableSolutionTiles() {
        for (Triggerable t : trigerables) {
            if (t.getClass() == SolutionTile.class)
                t.render();
        }
    }

    public void save() {
        for (TileFloor t : tfloors)
            t.save();
        for (EntityFloor f : efloors)
            f.save();
    }

    // Load save data for things like player last location
    // and books collected
    public void loadSaveData() {
        try {
            File inputFile = new File("save_data");
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                final String[] tokens = currentLine.split(Pattern.quote(":"));

                // Books
                if (tokens[0].equals("book")) {
                    if (!Const.INSTANCE.getBOOKS().contains(Integer.parseInt(tokens[1]))) {
                        Const.INSTANCE.getBOOKS().add(Integer.parseInt(tokens[1]));
                    }
                }
                // Space world complete
                else if (tokens[0].equals("space_complete")) {
                    Const.INSTANCE.setCOMPLETEDSPACE(true);
                }
                // Load last world player was in
                else if (tokens[0].equals("last_world")) {
                    System.out.println("Setting level to " + tokens[1]);
                    Const.INSTANCE.setCURRENT_FILE(tokens[1]);
                }
            }
            System.out.println("Books total: " + Const.INSTANCE.getBOOKS());
            reader.close();
            System.out.println("Loaded save_data!");
        } catch (Exception e) {
            System.out.println("Couldn't load save data");
        }
    }

    // Clear save data and input current books
    public void saveSaveData() {
        System.out.println("Saving save data");
        try {
            PrintWriter writer = new PrintWriter("save_data");

            // Books
            for (int i = 0; i < Const.INSTANCE.getBOOKS().size(); i++) {
                writer.print("book:" + Const.INSTANCE.getBOOKS().get(i) + "\n");
            }

            // Space world completed
            if (Const.INSTANCE.getCOMPLETEDSPACE()) {
                writer.print("space_complete\n");
            }

            // Save last world player was in before game was exited
            System.out.println(Const.INSTANCE.getCURRENT_FILE());
            writer.print("last_world:" + Const.INSTANCE.getCURRENT_FILE());

            writer.close();
            System.out.println("Saved stuff to save_data");
        } catch (Exception e) {
            System.out.println("Couldn't save data");
        }
    }

    public void dispose() {
        getCurrentTileFloor().dispose();
        getCurrentEntityFloor().dispose();
        for (Triggerable t : trigerables)
            t.dispose();
        // NOTE: have the triggerable that makes this get called avoid concurrent mod. Otherwise,
        // NOTE: triggerables are a memory leak
//                trigerables.clear();    // Concurrent modifications happen if I clear this
    }

}

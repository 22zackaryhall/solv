package core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import entities.SolutionTile;
import entities.StaticEntity;
import entities.Triggerable;
import util.Logger;

import java.io.*;
import java.util.ArrayList;

public class EntityFloor {

    public int id;
    public float size;
    public ArrayList<StaticEntity> entities = new ArrayList<StaticEntity>();
    public int[][] map;
    BasicGame game;
    private int width, height;
    private FileHandle handle;
    private String entityData = "";

    public EntityFloor(int id, float size, FileHandle fileHandle, BasicGame game) {
        this.size = size;
        this.id = id;
        this.handle = fileHandle;
        this.game = game;

        try {
            BufferedReader br = Gdx.files.internal(fileHandle + "").reader(1024);
            if (!fileHandle.exists())
                Logger.error("Couldn't load map: " + fileHandle.toString(), true);
            width = Integer.parseInt(br.readLine());
            height = Integer.parseInt(br.readLine());
            map = new int[width][height];

            for (int row = 0; row < width; row++) {
                String line = br.readLine();
                if (line == null || line.isEmpty()) {
                    System.out.println("Line is empty or null");

                } else if (!line.contains("-")) {
                    break;
                } else {
                    String[] tileValues = line.split("-");
                    for (int col = 0; col < height; col++) {
                        map[row][col] = Integer.parseInt(tileValues[col]);
                    }
                }
            }
            ArrayList<String> textures = new ArrayList<String>();
            while (true) {
                String sprite = br.readLine();
                if (sprite == null)
                    break;
                else {
                    textures.add(sprite);
//                    game.setEntityIds(textures);
                }
                entityData += sprite + "\n";
            }
            // Adds entities to the list
            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[i].length; j++) {
                    if (Const.INSTANCE.getDebugging()) {
                        entities.add(new StaticEntity(game, i * size, j * size, map[j][i]));
                    } else if (map[j][i] != 0) {
                        entities.add(new StaticEntity(game, i * size, j * size, map[j][i]));
                    }
                }
            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void render() {
        for (StaticEntity t : entities)
            if (t.getId() != Const.INSTANCE.getEMPTY_ID())
                t.render();
    }

    public void update() {
        for (StaticEntity t : entities)
            t.update();
    }

    public void dispose() {

        for (StaticEntity e : entities) {
            e.dispose();
        }
    }

    public void save() {
        System.out.println("saving entities except rock and reflector on map. They saved on to trig");

        FileHandle tmpe;
        tmpe = Gdx.files.local(handle.path());
        tmpe.delete();
        tmpe.writeString(width + "\n" + height + "\n", true);
        String f = "";
        for (int row = 0; row < width; row++) {
            for (int col = 0; col < height; col++) {
                for (StaticEntity e : entities) {
                    if (e.getX() / Const.INSTANCE.getTILESIZE() == col && e.getY() / Const.INSTANCE.getTILESIZE() == row) {
                        // save rocks and reflectors to .trig
                        if (e.getId() == Const.INSTANCE.getROCK_ID() || e.getId() == Const.INSTANCE.getREFLECTOR_UPLEFT_ID() ||
                                e.getId() == Const.INSTANCE.getREFLECTOR_UPRIGHT_ID() || e.getId() == Const.INSTANCE.getREFLECTOR_DOWNLEFT_ID() ||
                                e.getId() == Const.INSTANCE.getREFLECTOR_DOWNRIGHT_ID()) {
                            map[row][col] = 0;

                        } else {
                            map[row][col] = e.getId();
                        }
                    }
                }
                int a = map[row][col];
                f += a;
                if (col < height - 1) {
                    f += "-";
                }
            }
            if (row != width - 1)
                f += "\n";
        }
        tmpe.writeString(f + "\n" + entityData, true);


        // remove all lines with rock
        try {
            File inputFile = new File("maps/" + Const.INSTANCE.getCURRENT_FILE() + ".trig");
            // file it becomes
            File tempFile = new File("maps/" + Const.INSTANCE.getCURRENT_FILE() + ".trig_temp");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String lineToRemove = "rock:";
            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                // trim newline when comparing with lineToRemove
                String trimmedLine = currentLine.trim();
                if (trimmedLine.contains(lineToRemove)) continue;
                writer.write(currentLine + System.getProperty("line.separator"));
            }
            writer.close();
            reader.close();
            boolean success = tempFile.renameTo(inputFile);
        } catch (Exception exc) {
            System.out.println("failed to save pushpullable objects rocks");
        }

        // remove all lines with reflector
        try {
            File inputFile = new File("maps/" + Const.INSTANCE.getCURRENT_FILE() + ".trig");
            // file it becomes
            File tempFile = new File("maps/" + Const.INSTANCE.getCURRENT_FILE() + ".trig_temp");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String lineToRemove = "reflector:";
            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                // trim newline when comparing with lineToRemove
                String trimmedLine = currentLine.trim();
                if (trimmedLine.contains(lineToRemove)) continue;
                writer.write(currentLine + System.getProperty("line.separator"));
            }
            writer.close();
            reader.close();
            boolean success = tempFile.renameTo(inputFile);
        } catch (Exception exc) {
            System.out.println("failed to save pushpullable objects rocks");
        }

        // remove all lines with solutiontile
        try {
            File inputFile = new File("maps/" + Const.INSTANCE.getCURRENT_FILE() + ".trig");
            // file it becomes
            File tempFile = new File("maps/" + Const.INSTANCE.getCURRENT_FILE() + ".trig_temp");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String lineToRemove = "solutiontile:";
            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                // trim newline when comparing with lineToRemove
                String trimmedLine = currentLine.trim();
                if (trimmedLine.contains(lineToRemove)) continue;
                writer.write(currentLine + System.getProperty("line.separator"));
            }
            writer.close();
            reader.close();
            boolean success = tempFile.renameTo(inputFile);
        } catch (Exception exc) {
            System.out.println("failed to save pushpullable objects solutiontiles");
        }

        // remove all lines with solutiontile2
        try {
            File inputFile = new File("maps/" + Const.INSTANCE.getCURRENT_FILE() + ".trig");
            // file it becomes
            File tempFile = new File("maps/" + Const.INSTANCE.getCURRENT_FILE() + ".trig_temp");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String lineToRemove = "solutiontile2:";
            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                // trim newline when comparing with lineToRemove
                String trimmedLine = currentLine.trim();
                if (trimmedLine.contains(lineToRemove)) continue;
                writer.write(currentLine + System.getProperty("line.separator"));
            }
            writer.close();
            reader.close();
            boolean success = tempFile.renameTo(inputFile);
        } catch (Exception exc) {
            System.out.println("failed to save pushpullable objects solutiontiles");
        }

        // remove all lines with solutiontile3
        try {
            File inputFile = new File("maps/" + Const.INSTANCE.getCURRENT_FILE() + ".trig");
            // file it becomes
            File tempFile = new File("maps/" + Const.INSTANCE.getCURRENT_FILE() + ".trig_temp");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String lineToRemove = "solutiontile3:";
            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                // trim newline when comparing with lineToRemove
                String trimmedLine = currentLine.trim();
                if (trimmedLine.contains(lineToRemove)) continue;
                writer.write(currentLine + System.getProperty("line.separator"));
            }
            writer.close();
            reader.close();
            boolean success = tempFile.renameTo(inputFile);
        } catch (Exception exc) {
            System.out.println("failed to save pushpullable objects solutiontiles");
        }

        // add solutiontile to the file
        try (FileWriter fw = new FileWriter("maps/" + Const.INSTANCE.getCURRENT_FILE() + ".trig", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {

            // Squeeze in spawnPos
            out.println("spawn:" + (int) Math.floor(game.map.spawnPos.x / Const.INSTANCE.getTILESIZE()) + ":" + (int) Math.floor(game.map.spawnPos.y / Const.INSTANCE.getTILESIZE()));

            // Solution tiles
            for (Triggerable e : game.map.trigerables) {
                if (e.getClass() == SolutionTile.class) {
                    SolutionTile st = (SolutionTile) e;
                    if (st.getInvisible() && !st.getSolutiontile3()) {
                        out.println("solutiontile2:" +
                                (int) (st.pos.x / Const.INSTANCE.getTILESIZE()) + ":" +
                                (int) (st.pos.y / Const.INSTANCE.getTILESIZE()) + ":" +
                                st.id + ":" +
                                st.getOn());
                    } else if (!st.getSolutiontile3()) {
                        out.println("solutiontile:" +
                                (int) (st.pos.x / Const.INSTANCE.getTILESIZE()) + ":" +
                                (int) (st.pos.y / Const.INSTANCE.getTILESIZE()) + ":" +
                                st.id + ":" +
                                st.getOn());
                    } else if (st.getSolutiontile3()) {
                        out.println("solutiontile3:" +
                                (int) (st.pos.x / Const.INSTANCE.getTILESIZE()) + ":" +
                                (int) (st.pos.y / Const.INSTANCE.getTILESIZE()) + ":" +
                                st.id + ":" +
                                st.getOn());
                    }
                }
            }
        } catch (IOException e) {
        }

        // add rocks and reflectors to the file
        try (FileWriter fw = new FileWriter("maps/" + Const.INSTANCE.getCURRENT_FILE() + ".trig", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            for (StaticEntity e : entities) {
                if (e.getId() == Const.INSTANCE.getROCK_ID()) {
                    out.println("rock:" + (int) (e.getX() / Const.INSTANCE.getTILESIZE()) + ":" + (int) (e.getY() / Const.INSTANCE.getTILESIZE()));
                } else if (e.getId() == Const.INSTANCE.getREFLECTOR_UPLEFT_ID() || e.getId() == Const.INSTANCE.getREFLECTOR_UPRIGHT_ID() ||
                        e.getId() == Const.INSTANCE.getREFLECTOR_DOWNLEFT_ID() || e.getId() == Const.INSTANCE.getREFLECTOR_DOWNRIGHT_ID()) {
                    out.println("reflector:" + (int) (e.getX() / Const.INSTANCE.getTILESIZE()) + ":" + (int) (e.getY() / Const.INSTANCE.getTILESIZE()) + ":" + e.getId());
                }
            }
        } catch (IOException e) {
        }
    }

}

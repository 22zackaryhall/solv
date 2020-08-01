package core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import entities.Tile;
import util.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class TileFloor {

    public float size;
    public ArrayList<Tile> tiles = new ArrayList<Tile>();
    int id;
    private int[][] map;
    private int width, height;
    private FileHandle handle;

    private boolean saveCurrent = false;

    //tile info at bottom of map files
    private String tileData = "";

    public TileFloor(int id, float size, FileHandle fileHandle, BasicGame game) {
        this.size = size;
        this.id = id;
        this.handle = fileHandle;
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
                } else if (!line.contains(":")) {
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
                    game.setTileIds(textures);
                }
                tileData += sprite + "\n";
            }
            // Adds tiles to the list
            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[i].length; j++) {
                    if (Const.INSTANCE.getDebugging()) {
                        tiles.add(new Tile(i * size, j * size, size, map[j][i], textures, game));
                    } else if (map[j][i] != 0) {
                        tiles.add(new Tile(i * size, j * size, size, map[j][i], textures, game));
                    }
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void render() {
        for (Tile t : tiles)
            if (t.getId() != Const.INSTANCE.getEMPTY_ID())
                t.render();
    }

    public void update() {
        for (Tile t : tiles)
            t.update();
    }

    public void dispose() {

        for (Tile e : tiles) {
            e.dispose();
        }
    }

    void save() {
        FileHandle tmpe;
        tmpe = Gdx.files.local(handle.path());
        tmpe.delete();
        tmpe.writeString(width + "\n" + height + "\n", true);
        String f = "";
        for (int row = 0; row < width; row++) {
            for (int col = 0; col < height; col++) {
                for (Tile e : tiles) {
                    if (e.getX() / Const.INSTANCE.getTILESIZE() == col && e.getY() / Const.INSTANCE.getTILESIZE() == row) {
                        map[row][col] = e.getId();
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
        tmpe.writeString(f + "\n" + tileData, true);
    }

}

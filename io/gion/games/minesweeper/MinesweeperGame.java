/*
 * The MIT License (MIT)
 *
 * Minesweeper - Game using FXGL
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.gion.games.minesweeper;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;

import javafx.geometry.Point2D;

import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.concurrent.ThreadLocalRandom;
import java.util.ArrayList;
import java.util.List;


import static com.almasb.fxgl.dsl.FXGL.*;

public class MinesweeperGame extends GameApplication {

    private static final int screenWidth = 800;
    private static final int screenHeight = 800;

    private static final int numColumn = 10;
    private static final int numRow = 10;

    // define colors for different cells
    public static final Color colorCell = Color.ORANGE;
    public static final Color colorOpen = Color.GREEN;
    public static final Color colorMine = Color.RED;
    public static final Color colorFlag = Color.YELLOW;

    // define text / symbols for mined and flaged tiles
    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";

    public static final int TILE_TEXT_FONT_SIZE = 40;
    public static final String TILE_TEXT_FONT_TYPE = "verdana";

    public enum EntityType {
        TILE
    }

    private int countMinesOnField = 0;
    private int unusedFlags = 0;

    private Entity Cell[][] = new Entity[numColumn][numRow];
    private List<Entity> minedCells = new ArrayList<>();

    public static final int tileSizeX = screenWidth / numColumn - 1;
    public static final int tileSizeY = screenHeight / numRow - 1;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(screenWidth);
        settings.setHeight(screenHeight);
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new MinesweeperFactory());
        boolean isMine;

        for (int x = 0; x < numColumn; x++) {
            for (int y = 0; y < numRow; y++) {
                // Get integer random number (between 1 and 10)
                // if number < 1 set isMine true; results in 10% chance of getting a mine
                // nextInt is normally exclusive of the top value,
                // so add 1 to make it inclusive
                isMine = ThreadLocalRandom.current().nextInt(0, 9 + 1) < 1;

                // Draw game board
                Cell[x][y] = spawn("Tile", x * tileSizeX + x, y * tileSizeY + y);

                // FIX should be through some kind of spawn data / constructor
                // initialization of spawned cell
                getTile(Cell[x][y]).setIsMine(isMine);
                getTile(Cell[x][y]).setCoordX(x);
                getTile(Cell[x][y]).setCoordY(y);

                if (isMine) {
                    countMinesOnField++;
                    minedCells.add(Cell[x][y]);
                }
            }
        }
        countMineNeighbors();
        unusedFlags = countMinesOnField;
    }

    @Override
    protected void initInput() {
        Input userInput = getInput();
        userInput.addAction(new UserAction("OpenCell") {
            @Override
            protected void onActionEnd() {
                Point2D cursorPointInUI = userInput.getMousePositionUI();
                int posX = ((int)cursorPointInUI.getX()) / tileSizeX;
                int posY = ((int)cursorPointInUI.getY()) / tileSizeY;
            
                openTile(Cell[posX][posY]);
            }

        }, MouseButton.PRIMARY);

        userInput.addAction(new UserAction("MarkCell") {
            @Override
            protected void onActionEnd() {
                Point2D cursorPointInUI = userInput.getMousePositionUI();
                int posX = ((int)cursorPointInUI.getX()) / tileSizeX;
                int posY = ((int)cursorPointInUI.getY()) / tileSizeY;
            
                markTile(Cell[posX][posY]);
            }
        }, MouseButton.SECONDARY);
    }

    public static void main(String[] args) {
        launch(args);
    }

     private List<Entity> getNeighbors(Entity cellEntity) {
        List<Entity> result = new ArrayList<>();

        int coordX = getTile(cellEntity).getCoordX();
        int coordY = getTile(cellEntity).getCoordY();

        for (int y = coordY - 1; y <= coordY + 1; y++) {
            for (int x = coordX - 1; x <= coordX + 1; x++) {
                if (y < 0 || y >= numRow) {
                    continue;
                }
                if (x < 0 || x >= numColumn) {
                    continue;
                }
                if (Cell[x][y] == cellEntity) {
                    continue;
                }
                result.add(Cell[x][y]);
            }
        }
        return result;
    }

    private void countMineNeighbors() {
        List<Entity> cellNeighbors  = new ArrayList<>();

        // go through every known mined cells
        for (Entity minedCell : minedCells) {

            // get neighbors of mined cells
            cellNeighbors = getNeighbors(minedCell);

            // look for mined cells in neighbours
            // if the neighbour is not a mine, increment it minedNeighbourCount because we are a mined neighbour
            for (Entity cellEntity : cellNeighbors) {
                if (!getTile(cellEntity).getIsMine()) {
                    getTile(cellEntity).setMinedNeighbours(getTile(cellEntity).getMinedNeighbours() + 1);
                }
            }
        }
    }

    public void openTile(Entity cellEntity) {
        if (getTile(cellEntity).getIsOpen()) {
            return;
        }

        // Tile was closed, therefore open it
        getTile(cellEntity).setIsOpen(true);

        // Game Over
        if (getTile(cellEntity).getIsMine()) {
            
            setCellText(cellEntity, MINE);
            setCellColor(cellEntity, colorMine); 
        }
        else {
            
            setCellColor(cellEntity, colorOpen);

            // If tile has NO mined neighbours, open them all by calling openTile recursively
            if (getTile(cellEntity).getMinedNeighbours() == 0) {
                setCellText(cellEntity, "");

                for (Entity neighbors : getNeighbors(cellEntity)) {
                    openTile(neighbors);
                }
            }
            // If tile has mined neighbours, print number of mined neighbours on tile
            else {
                setCellText(cellEntity, String.valueOf(getTile(cellEntity).getMinedNeighbours()));
            }
        }
    }

    private void markTile(Entity cellEntity) {

        if (getTile(cellEntity).getIsOpen()) {
            return;
        }

        if (!getTile(cellEntity).getIsFlag() && unusedFlags == 0) {
            return;
        }

        // Tile has a flag therefore remove flag
        if (getTile(cellEntity).getIsFlag()) {
            unusedFlags++;

            getTile(cellEntity).setIsFlag(false);
            setCellText(cellEntity, "");
            setCellColor(cellEntity, colorCell);
        }
        // Tile has NO flag therefore add flag
        else {
            unusedFlags--;

            getTile(cellEntity).setIsFlag(true);
            setCellText(cellEntity, FLAG);
            setCellColor(cellEntity, colorFlag);
        }
    }

    private TileComponent getTile (Entity cellEntity) {
        return cellEntity.getComponent(TileComponent.class);
    }

    private void setCellColor(Entity cellEntity, Color color) {
        var tileRectangle = (Rectangle) cellEntity.getViewComponent().getChildren().get(0);
        tileRectangle.setFill(color);
    }

    private void setCellText(Entity cellEntity, String text) {
        var tileText = (Text) cellEntity.getViewComponent().getChildren().get(1);
        tileText.setText(text);
    }
}
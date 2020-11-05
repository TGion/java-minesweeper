package io.gion.games.minesweeper;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;

import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;

public class MinesweeperFactory implements EntityFactory {

        @Spawns("Tile")
        public Entity newTile(SpawnData data) {
                var tileText = new Text("");
                tileText.setStyle("-fx-font: " + MinesweeperGame.TILE_TEXT_FONT_SIZE + " " + MinesweeperGame.TILE_TEXT_FONT_TYPE + ";");
                tileText.setX(MinesweeperGame.TILE_TEXT_FONT_SIZE / 1.5);
                tileText.setY(MinesweeperGame.TILE_TEXT_FONT_SIZE * 1.5);
                //tileText.setTextAlignment(TextAlignment.CENTER);

                var tileColor = new Rectangle((int)MinesweeperGame.tileSizeX, (int)MinesweeperGame.tileSizeY, MinesweeperGame.colorCell);

                var tile = entityBuilder()
                        .from(data)
                        //.bbox(new HitBox(BoundingShape.box((int)MinesweeperGame.tileSizeX, (int)MinesweeperGame.tileSizeY)))
                        .type(MinesweeperGame.EntityType.TILE)
                        .view(tileColor)
                        .view(tileText)
                        .with(new TileComponent())
                        .build();

                //tile.getViewComponent().addEventHandler(MouseEvent.MOUSE_CLICKED, e -> FXGL.<MinesweeperGame>getAppCast().openTile(tile));

                return tile;
        }
}
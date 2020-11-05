package io.gion.games.minesweeper;

import com.almasb.fxgl.entity.component.Component;

public class TileComponent extends Component {

        private boolean isMine;
        private boolean isFlag;
        private boolean isOpen;

        private int minedNeighbours;

        private int coordX;
        private int coordY;

        // getter functions
        public boolean getIsMine() {
                return isMine;
        }

        public boolean getIsFlag() {
                return isFlag;
        }

        public boolean getIsOpen() {
                return isOpen;
        }

        public int getMinedNeighbours() {
                return minedNeighbours;
        }

        public int getCoordX() {
                return coordX;
        }

        public int getCoordY() {
                return coordY;
        }

        // setter functions
        public void setIsMine(boolean isMine) {
                this.isMine = isMine;
        }

        public void setIsFlag(boolean isFlag) {
                this.isFlag = isFlag;
        }

        public void setIsOpen(boolean isOpen) {
                this.isOpen = isOpen;
        }

        public void setMinedNeighbours(int minedNeighbours) {
                this.minedNeighbours = minedNeighbours;
        }

        public void setCoordX(int coordX) {
                this.coordX = coordX;
        }

        public void setCoordY(int coordY) {
                this.coordY = coordY;
        }
}
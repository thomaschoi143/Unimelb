/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */
package src.gameGrid;

public enum GridElementType {
    PathTile,
    WallTile,
    PillTile,
    GoldTile,
    IceTile,
    PacTile,
    TrollTile,
    TX5Tile,
    PortalWhiteTile,
    PortalYellowTile,
    PortalDarkGoldTile,
    PortalDarkGrayTile,
    Undefined;

    public static GridElementType charToGridElementType(char c) {
        return switch (c) {
            case 'a' -> PathTile;
            case 'b' -> WallTile;
            case 'c' -> PillTile;
            case 'd' -> GoldTile;
            case 'e' -> IceTile;
            case 'f' -> PacTile;
            case 'g' -> TrollTile;
            case 'h' -> TX5Tile;
            case 'i' -> PortalWhiteTile;
            case 'j' -> PortalYellowTile;
            case 'k' -> PortalDarkGoldTile;
            case 'l' -> PortalDarkGrayTile;
            default -> Undefined;
        };
    }

    public char toChar() {
        return switch (this) {
            case PathTile -> 'a';
            case WallTile -> 'b';
            case PillTile -> 'c';
            case GoldTile -> 'd';
            case IceTile -> 'e';
            case PacTile -> 'f';
            case TrollTile -> 'g';
            case TX5Tile -> 'h';
            case PortalWhiteTile -> 'i';
            case PortalYellowTile -> 'j';
            case PortalDarkGoldTile -> 'k';
            case PortalDarkGrayTile -> 'l';
            default -> '0';
        };
    }
}
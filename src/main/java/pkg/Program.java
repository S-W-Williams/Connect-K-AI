import connectK.CKPlayer;
import connectK.BoardModel;
import java.awt.Point;
import java.util.Random;

public class ColgateAI extends CKPlayer {

    public ColgateAI(byte player, BoardModel state) {
        super(player, state);
        teamName = "DummyAI";
    }

    @Override
    public Point getMove(BoardModel state) {
        Random randomGenerator = new Random();

        int width = state.getWidth()-1;
        int height = state.getHeight()-1;
        int i = randomGenerator.nextInt(width);
        int j = randomGenerator.nextInt(height);

        if(state.getSpace(i, j) == 0)
            return new Point(i,j);
        else
            return getMove(state);

    }

    @Override
    public Point getMove(BoardModel state, int deadline) {
        return getMove(state);
    }
}


import connectK.CKPlayer;
import connectK.BoardModel;

import java.awt.Point;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;



public class ColgateAI extends CKPlayer {
    long start = System.nanoTime();
    private int MAX_DEPTH;
    private int MINIMAX_CUTOFF;
    private int IDS_START_DEPTH;
    private double TIME_LIMIT; //in nanoseconds, .2 second buffer
    private boolean alphaBetaOn;
    private boolean idsOn;
    double myScore;
    double theirScore;
    
    
    public ColgateAI(byte player, BoardModel state) {
        super(player, state);
        teamName = "ColgateAI";
        MINIMAX_CUTOFF = 2;
        IDS_START_DEPTH = 1;
        MAX_DEPTH = 0;
        TIME_LIMIT = 4800000000.0;
        alphaBetaOn = true;
        idsOn = true;
    }

    //gets ai move from last move
    private byte reversePlayer(BoardModel state){
        if (state.getSpace(state.getLastMove()) == 1){
            player = 2;
            return 2;
        }
        else{
            player = 1;
            return 1;
        }
    }

    
    //Returns open spaces in board
    public ArrayList<AIMove> validOpenSpaces(BoardModel state) {
      ArrayList<AIMove> moves = new ArrayList<AIMove>();
      for(int col = 0; col < state.width; col++){
            for(int row = 0; row < state.height; row++) {
                if(state.pieces[col][row] == 0) {
                    
                    AIMove ai_move = new AIMove(0.0);
                    ai_move.setMove(new Point(col, row));
                    moves.add(ai_move);
                    
                    if (state.gravity){
                        break;
                  }
                }
            }
        }

        return moves;
    }
    
    //makes a clone of state and returns game state with placed piece
    public BoardModel applyMove(BoardModel state, AIMove eachMove){
        BoardModel clone = state.clone();
        clone.placePiece(eachMove.getPoint(), player);
        return clone;
    }
    
    //checks if game is over,  there are no more places to put pieces
    public boolean gameOver(BoardModel state){
        return state.winner() != -1;
    }
    
    public double findNeighbors(BoardModel state) {
        double score = 0;
        
        return score;
    }
    
    public double scoreHorizontal(BoardModel state) {
        double score = 0;
        return score;
    }
    
    public double scoreEmptySpaces(Point move, BoardModel state) {
        byte emtpyMove = (byte) 0;
        double score = 0;
        int width = state.getWidth();
        int height = state.getHeight();

        //North Search
        int emptySpaces = 0;
        for (int y = move.y; y < height; y++) {
            if (state.getSpace(move.x, y) == emtpyMove || state.getSpace(move.x, y) == this.player)
                emptySpaces += 1;
            else if (state.getSpace(move.x, y) != this.player)
                break;
        }
            
        //South
        emptySpaces -= 1;
        for (int y = move.y; y > -1; y--) {
            if (state.getSpace(move.x, y) == emtpyMove || state.getSpace(move.x, y) == this.player)
                emptySpaces += 1;
            else if (state.getSpace(move.x, y) != this.player)
                break;
        }
        if (emptySpaces >= state.kLength)
            score += (1 + (0.1 * emptySpaces));
        
        //East
        emptySpaces = 0;
        for (int x = move.x; x < width; x++) {
            if (state.getSpace(x, move.y) == emtpyMove || state.getSpace(x, move.y) == this.player)
                emptySpaces += 1;
            else if (state.getSpace(x, move.y) != this.player)
                break;
        }
        
        //West
        emptySpaces -= 1;
        for (int x = move.x; x > -1; x--) {
            if (state.getSpace(x, move.y) == emtpyMove || state.getSpace(x, move.y) == this.player)
                emptySpaces += 1;
            else if (state.getSpace(x, move.y) != this.player)
                break;
        }
        if (emptySpaces >= state.kLength)
            score += (1 + (0.1 * emptySpaces));
        
        //North East
        emptySpaces = 0;
        for (int x = move.x, y = move.y; x < width && y < height; x++, y++) {
            if (state.getSpace(x, y) == emtpyMove || state.getSpace(x, y) == this.player)
                emptySpaces += 1;
            else if (state.getSpace(x, y) != this.player)
                break;
        }
        
        //South East
        emptySpaces -= 1;
        for (int x = move.x, y = move.y; x < width && y > -1; x++, y--) {
            if (state.getSpace(x, y) == emtpyMove || state.getSpace(x, y) == this.player)
                emptySpaces += 1;
            else if (state.getSpace(x, y) != this.player)
                break;
       }
        if (emptySpaces >= state.kLength)
            score += (1 + (0.1 * emptySpaces));
        
        //North West
        emptySpaces = 0;
        for (int x = move.x, y = move.y; x > -1 && y < height; x--, y++) {
            if (state.getSpace(x, y) == emtpyMove || state.getSpace(x, y) == this.player)
                emptySpaces += 1;
            else if (state.getSpace(x, y) != this.player)
                break;
        }
        
        //South West
        emptySpaces -= 1;
        for (int x = move.x, y = move.y; x > -1 && y > -1; x--, y--) {
            if (state.getSpace(x, y) == emtpyMove || state.getSpace(x, y) == this.player)
                emptySpaces += 1;
            else if (state.getSpace(x, y) != this.player)
                break;
        }
        if (emptySpaces >= state.kLength)
            score += (1 + (0.1 * emptySpaces));
        
        return score;
    }
    
    
    //adds to score if we see chance to win
    public double scoreThreats(BoardModel state) {
        double score = 0;
        byte emtpyMove = (byte) 0;
        int width = state.getWidth();
        int height = state.getHeight();
        int scoreWeight = 7;
        
        //Vertical Search
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (state.getSpace(x, y) == this.player) {
                    //Horizontal Check
                    int threatLength = 0;
                    int potentialMoves = 0;
                    for (int i = x; i < width; i++) {
                        if (state.getSpace(i, y) == this.player)
                            threatLength += 1;
                        else {
                            if (state.getSpace(i, y) == emtpyMove) {
                                for (int i2 = i; i2 < width; i2 ++) {
                                    if (state.getSpace(i2, y) == emtpyMove)
                                        potentialMoves += 1;
                                }
                                break;
                            }
                            else
                                break;
                        }
                    }
                    if (threatLength + potentialMoves >= state.kLength)
                        score += (scoreWeight * threatLength);
                    
                    //Vertical Check
                    threatLength = 0;
                    potentialMoves = 0;
                    for (int i = y; i < height; i++) {
                        if (state.getSpace(x, i) == this.player)
                            threatLength += 1;
                        else {
                            if (state.getSpace(x, i) == emtpyMove) {
                                for (int i2 = i; i2 < height; i2++) {
                                    if (state.getSpace(x, i2) == emtpyMove)
                                        potentialMoves += 1;
                                }
                                break;
                            }
                            else
                                break;
                            
                        }
                    }
                    
                    if (threatLength + potentialMoves >= state.kLength)
                        score += (scoreWeight * threatLength);
                    
                    //Diagonal Check
                    threatLength = 0;
                    potentialMoves = 0;
                    for (int i = x, j = y; i < width && j < height; i++, j++) {
                        if (state.getSpace(i, j) == this.player)
                            threatLength += 1;
                        else {
                            if (state.getSpace(i , j) == emtpyMove) {
                                for (int i2 = i, j2 = j; i2 < width && j2 < height; i2++, j2++) {
                                    if (state.getSpace(i2, j2) == emtpyMove)
                                        potentialMoves += 1;
                                }
                                break;
                            }
                            else
                                break;
                        }
                            
                    }
                    
                    if (threatLength + potentialMoves >= state.kLength)
                        score += (scoreWeight * threatLength);
                }
            }
        }

        
        return score;
    }
    
    public byte getOpponent(byte player) {
        if (player == (byte) 1)
            return (byte) 2;
        else
            return (byte) 1;
    }
    
    
    //add to score if enemy is about to win
    public double scoreEnemyThreats(BoardModel state) {
        double score = 0;
        int width = state.getWidth();
        int height = state.getHeight();
        byte emtpyMove = (byte) 0;
        byte opponent = getOpponent(this.player);
        double scoreWeight = 20;
        
        //Vertical Search
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (state.getSpace(x, y) == opponent) {
                    
                    
                    //East
                    int threatLength = 0;
                    for (int i = x; i < width; i++) {

                        if (state.getSpace(i, y) == opponent){
                            threatLength += 1;

                        }
                        else {
                            if (state.getSpace(i, y) == this.player) {
                                score += (scoreWeight * threatLength);
                                break;
                            }
                            break;
                        }
                    }
                    
                    //West
                    threatLength = 0;
                    for (int i = x; i > -1; i--) {

                        if (state.getSpace(i, y) == opponent){
                            threatLength += 1;
                        }
                        else {
                            if (state.getSpace(i, y) == this.player) {
                                score += (scoreWeight * threatLength);
                                break;
                            }
                            break;
                        }
                    }
                    
                    //North
                    threatLength = 0;
                    for (int i = y; i < height; i++) {

                        if (state.getSpace(x, i) == opponent){
                            threatLength += 1;
                        }
                        else {
                            if (state.getSpace(x, i) == this.player) {
                                score += (scoreWeight * threatLength);
                                break;
                            }
                            break;
                        }
                    }
                    
                    //South
                    threatLength = 0;
                    for (int i = y; i > -1; i--) {

                        if (state.getSpace(x, i) == opponent){
                            threatLength += 1;

                        }
                        else {
                            if (state.getSpace(x, i) == this.player) {
                                score += (scoreWeight * threatLength);
                                break;
                            }
                            break;
                        }
                    }
                    
                    //North East
                    threatLength = 0;
                    for (int i = x, j = y; i < width && j < height; i++, j++) {

                        if (state.getSpace(i, j) == opponent){
                            threatLength += 1;

                        }
                        else {
                            if (state.getSpace(i, j) == this.player) {
                                score += (scoreWeight * threatLength);
                                break;
                            }
                            break;
                        }
                            
                    }
                    //South East
                    threatLength = 0;
                    for (int i = x, j = y; i < width && j > -1; i++, j--) {

                        if (state.getSpace(i, j) == opponent){
                            threatLength += 1;

                        }
                        else {
                            if (state.getSpace(i, j) == this.player) {
                                score += (scoreWeight * threatLength);
                                break;
                            }
                            break;
                        }
                            
                    }
                    //North West
                    threatLength = 0;
                    for (int i = x, j = y; i > -1 && j < height; i--, j++) {

                        if (state.getSpace(i, j) == opponent){
                            threatLength += 1;

                        }
                        else {
                            if (state.getSpace(i, j) == this.player) {
                                score += (scoreWeight * threatLength);
                                break;
                            }
                            break;
                        }
                            
                    }
                    //South West
                    threatLength = 0;
                    for (int i = x, j = y; i > -1 && j > -1; i--, j--) {

                        if (state.getSpace(i, j) == opponent){
                            threatLength += 1;

                        }
                        else {
                            if (state.getSpace(i, j) == this.player) {
                                score += (scoreWeight * threatLength);
                                break;
                            }
                            break;
                        }
                            
                    }
                    
                }
            }
        }
        
        
        return score;
    }
    

    

    
    public double heuristicFunction(BoardModel state) {
        double score = 0;
        
        byte player1 = (byte) 1;
        byte player2 = (byte) 2;
        byte emtpyMove = (byte) 0;
        
        //width is x, height is y
        int width = state.getWidth();
        int height = state.getHeight();
        int k = state.getkLength();
        
        List<Point> playerMoves = new ArrayList<Point>();
        List<Point> opponentMoves = new ArrayList<Point>();
        List<Point> emptyMoves = new ArrayList<Point>();
        
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (state.getSpace(x, y) == this.player)
                    playerMoves.add(new Point(x, y));
                else if (state.getSpace(x, y) == emtpyMove)
                    emptyMoves.add(new Point(x, y));
                else
                    opponentMoves.add(new Point(x, y));
            }
        }
        
        for (Point move : playerMoves) {
            score += scoreEmptySpaces(move, state);

        }
        myScore = scoreThreats(state);
        theirScore = scoreEnemyThreats(state);
        
      
        score += scoreThreats(state);  
        score += scoreEnemyThreats(state);

        
        
        Point temp = state.lastMove;
        //System.out.println("Point " + temp.x + ", " + temp.y + " has value of: " + score);
        return score;
    }

    //make random move
    public Point randomMove(BoardModel state){
        Random randomGenerator = new Random();

        int width = state.getWidth();
        int height = state.getHeight();
        
        int i = randomGenerator.nextInt(width);
        int j = randomGenerator.nextInt(height);
        Point p = new Point(i,j);
        if(state.getSpace(p) == 0){
            return new Point(i,j);  
        }
        else{
            return randomMove(state);
        }
    }
    

    
    /* old minimax search with alpha beta pruning
    public Point minimax_ab(BoardModel state, int depth, double alpha, double beta){
        Vector<AIMove> moves = validOpenSpaces(state); 
        if(state.getLastMove() == null) {
            player = 1;
        }else {
            player = reversePlayer(state);
        }
        double val = Integer.MAX_VALUE;
        Point move = null;

        for (AIMove eachMove : moves){
            val = minimizeVal(state.placePiece(eachMove.getPoint(), player), depth - 1, alpha, beta);
            
            if(val > alpha) {
                alpha = val;
                move = eachMove.getPoint();
            }
        }
        
        return move;
    }
    */
    
    
    //used to terminate out of loop if over time limit
    public boolean overTimeLimit(double time1){
        System.out.println("Current time: " + System.nanoTime());
        return (System.nanoTime() - time1) > TIME_LIMIT;
    }
    
    //minimax alpha beta pruning w/ iterative deepening, toggleable alphaBeta pruning and IDS
    public Point minimax_ab_ids(BoardModel state, int depth, double alpha, double beta, int idsCutoff, boolean alphaBetaOn, boolean idsOn){
        
        //nanoseconds for preciseness
        long start = System.nanoTime();
        double val = 0;
        int current_move = 0;
        
        ArrayList<AIMove> moves = validOpenSpaces(state); 
        
        //current best move
        Point best_move = moves.get(0).getPoint();
        
        //run ids w/ alpha beta pruning
        while (idsOn && alphaBetaOn) {
            
            //System.out.println("Current time: " + start);
            if ((System.nanoTime() - start) > TIME_LIMIT) {
                break;
            }

            //check previous iteration
            val = minimizeVal(state.placePiece(moves.get(current_move).getPoint(), player), depth + 1, alpha, beta, idsCutoff, alphaBetaOn);
            
            //System.out.println("Threat ratio: " + myScore/theirScore);
            if(val > alpha) {
                alpha = val;
            }

            //new searches
            for (int i = 0; i < moves.size(); i++) {
                //check time limit
                if ((System.nanoTime() - start) > TIME_LIMIT) {
                    break;
                }

                val = minimizeVal(state.placePiece(moves.get(i).getPoint(), player), depth + 1, alpha, beta, idsCutoff, alphaBetaOn);
                
                //assign best_move if better
                if(val > alpha && alphaBetaOn) {
                    alpha = val;

                    best_move = moves.get(i).getPoint();
                    
                    current_move = i;
                }

            }
            
            idsCutoff += 1;
        }
        
        //ids off, run alpha beta pruning search
        if (!idsOn && alphaBetaOn){
            if(state.getLastMove() == null) {
                player = 1;
            }else {
                player = reversePlayer(state);
            }
            
            
            
            val = Integer.MAX_VALUE;
            Point move = null;

            for (AIMove eachMove : moves){
                val = minimizeVal(state.placePiece(eachMove.getPoint(), player), depth + 1, alpha, beta, idsCutoff, alphaBetaOn);
                
                if(val > alpha && alphaBetaOn) {
                    alpha = val;
                    move = eachMove.getPoint();
                }
            }
            
            return move;
        }
        
        //run normal minimax
        if (!alphaBetaOn && !idsOn){
            val = 0;
            double maxValue = Integer.MIN_VALUE;
            depth = 0;
            Point move = null;

            
            for (AIMove eachMove : moves){
                val = minimizeVal(state.placePiece(eachMove.getPoint(), (byte) 2), depth + 1, alpha, beta, idsCutoff, alphaBetaOn);
                if(val > maxValue) {
                    maxValue = val;
                    move = eachMove.getPoint();
                }
            }
                
            return move;
        }
        
        
        //gets here if alphaBetaOn == true && idsOn == true
        return best_move;
    }
    

    
    
    //checks through states for min value
    private double minimizeVal(BoardModel state, int depth, double alpha, double beta, int idsCutoff, boolean alphaBetaOn) {
        ArrayList<AIMove> moves = validOpenSpaces(state); 
        double val = Integer.MAX_VALUE; 

        if ((depth >= idsCutoff && alphaBetaOn) || (depth == MINIMAX_CUTOFF && !alphaBetaOn && !idsOn) || moves.isEmpty() || ((System.nanoTime() - start) > TIME_LIMIT)){
            return heuristicFunction(state);
        
        } 
        else{
            
            //run w/ alpha beta pruning
            if (alphaBetaOn){
                for (AIMove eachMove : moves){
    
                    val = maximizeVal(applyMove(state, eachMove), depth + 1, alpha, beta, idsCutoff, alphaBetaOn);
                    
                    if (val < beta) {
                        beta = val;
                    }
            
                    if (alpha >= beta) {
                        return Integer.MIN_VALUE;
                    }
                }           
            
                return beta;
            }
            
            //default minimax
            else {
                
                val = 0; 
                double minimumVal = Integer.MIN_VALUE;
                
                for (AIMove eachMove : moves){
                    
                    
                    val = minimizeVal(state.placePiece(eachMove.getPoint(), getOpponent(player)), depth + 1, alpha, beta, idsCutoff, alphaBetaOn);
                    if (val < minimumVal){
                        minimumVal = val;
                    }
                }

                return minimumVal;
            }
        }
    }   

    //checks through states for max value
    private double maximizeVal(BoardModel state, int depth, double alpha, double beta, int idsCutoff, boolean alphaBetaOn) {
        ArrayList<AIMove> moves = validOpenSpaces(state); 
        
        double val = Integer.MIN_VALUE;
        
        if (depth >= idsCutoff || (depth == MINIMAX_CUTOFF && !alphaBetaOn && !idsOn) || moves.isEmpty() || ((System.nanoTime() - start) > TIME_LIMIT)){
            return heuristicFunction(state);
        
        } 
        
        else{
            
            //run w/ alpha beta pruning
            if (alphaBetaOn){
                for (AIMove eachMove : moves){
                    val = minimizeVal(applyMove(state, eachMove), depth + 1, alpha, beta, idsCutoff, alphaBetaOn);          
                    
                    if (val > alpha) {
                        alpha = val;
                    }
                    
                    if (alpha >= beta) {
                        return Integer.MAX_VALUE;
                    }           
                }   
                return alpha;
            }
            
            //default minimax
            else {
                val = 0; 
                double maximumVal = Integer.MIN_VALUE;
                
                for (AIMove eachMove : moves){
                    val = minimizeVal(state.placePiece(eachMove.getPoint(), player), depth + 1, alpha, beta, idsCutoff, alphaBetaOn);
                    if (val > maximumVal){
                        maximumVal = val;
                    }
                }

                return maximumVal;
            }
        }
        
    }


    @Override
    public Point getMove(BoardModel state) {

        //available options:
        
        //alpha beta pruning and iterative deepening
        // alphaBetaOn = true && idsOn = true
        
        //alpha beta pruning w/o iterative deepening
        // alphaBetaOn = true && idsOn = false
        
        //default minimax search
        // alphaBetaOn = false && idsOn = false

        return minimax_ab_ids(state, MAX_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, IDS_START_DEPTH, alphaBetaOn, idsOn);

        

    }


    @Override
    public Point getMove(BoardModel state, int deadline) {
        return getMove(state);
    }
}

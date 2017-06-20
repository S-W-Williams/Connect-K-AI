import java.awt.Point;

public class AIMove {
	private Point move;
	private double score;
	
	//constructor
	public AIMove(double score){
		this.score = score;
	}
	
	//accessors
	public Point getPoint(){
		return this.move;
	}
	
	public double getScore(){
		return this.score;
	}
	
	//mutators
	public void setMove(Point move){
		this.move = move;
	}
	public void setScore(double score){
		this.score = score;
	}
}

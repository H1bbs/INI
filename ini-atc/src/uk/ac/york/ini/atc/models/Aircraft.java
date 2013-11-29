package uk.ac.york.ini.atc.models;

import Waypoint;

import java.util.ArrayList;

import uk.ac.york.ini.atc.handlers.Input;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Aircraft extends Entity {

	/*protected Vector2 coords;
	protected int altitude;
	private int speed; // vectorise
	protected int radius;

	protected ArrayList<Waypoint> waypoints;

	protected String texture;*/
	// Old code. Replaced with separately written code below.
	
	protected Vector3 coords;
	protected Vector3 velocity;	
	protected int radius;
	protected int separationRadius;
	protected String texture;
	protected int maxTurningRate;
	protected int maxClimbRate;
	protected int maxSpeed;
	protected boolean isActive;
	protected boolean turningFlag; // May not be used
	protected ArrayList<Waypoint> waypoints;
	protected int sepRulesBreachCounter;

	public Aircraft() {
		// TODO Auto-generated constructor stub
	}

	public void update(Input input) {
		// TODO implement method
	}

	public void draw() {
		// TODO implement method
	}

	public boolean isActive() {
		// TODO implement method
		return false;
	}

	/**
	 * Regular getter for speed
	 * 
	 * @return int speed
	 */
	public int getSpeed() {
		return speed;
	}

	/**
	 * Set the speed More detail on this method
	 * 
	 * @param speed
	 *            int
	 * 
	 */
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	
	/**
	 * Regular regular getter for radius
	 * 
	 * @return int radius
	 */

	public int getRadius() {
		return radius;
	}

	/**
	 * Regular Set the radius
	 * 
	 * @param int radius
	 */

	public void setRadius(int radius) {
		this.radius = radius;
	}

	/**
	 * Regular getter for separationRadius
	 * 
	 * @return int separationRadius
	 */

	public int getSeparationRadius() {
		return separationRadius;
	}

	/**
	 * Regular Set the separationRadius
	 * 
	 * @param int separationRadius
	 */

	public void setSeparationRadius(int separationRadius) {
		this.separationRadius = separationRadius;
	}

	/**
	 * Regular getter for Texture
	 * 
	 * @return String Texture
	 */

	public String getTexture() {
		return texture;
	}

	/**
	 * Regular Set the Texture
	 * 
	 * @param String Texture
	 */

	public void setTexture(String texture){
		this.texture = texture;
	}

	/**
	 * Regular Set the maxTurningSpeed
	 * 
	 * @param int maxTurningSpeed
	 */


	public void setMaxTurningRate(int maxTurningRate){
		this.maxTurningRate = maxTurningRate;
	}

	/**
	 * Regular Set the maxClimbRate
	 * 
	 * @param int maxClimbRate
	 */


	public void setMaxClimbRate(int maxClimbRate){
		this.maxClimbRate = maxClimbRate;
	}

	/**
	 * Regular Set the maxSpeed
	 * 
	 * @param int maxSpeed
	 */

	public void setMaxSpeed(int maxSpeed){
		this.maxSpeed = maxSpeed;
	}

	public boolean getIsActive(){
		return isActive;
	}
	
	public void setIsActive(boolean isActive){
		this.isActive = isActive;
	}

	public void checkSpeed(){
		if (turningFlag){
			// check speed against maxTurningSpeed
		} 
	}

}

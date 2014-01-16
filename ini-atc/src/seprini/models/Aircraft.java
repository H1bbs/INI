package seprini.models;

import java.util.ArrayList;

import seprini.data.Config;
import seprini.data.Debug;
import seprini.models.types.AircraftType;
import seprini.screens.Screen;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;

public final class Aircraft extends Entity {

	private final int id;

	private static final float INITIAL_VELOCITY_SCALAR = 1f;
	private static final float SPEED_CHANGE = 0.1f;
	private static final int ALTITUDE_CHANGE = 100;

	private int altitude;
	private Vector2 velocity = new Vector2(0, 0);

	private final ArrayList<Waypoint> waypoints;

	private final float radius, separationRadius, maxTurningRate, maxClimbRate,
			maxSpeed, minSpeed;

	private float velocityScalar;

	private final int sepRulesBreachCounter = 0;
	private boolean breaching;
	private int lastTimeTurned;

	private boolean isActive = true;
	private boolean ignorePath = false; // When user has taken control of the
	// aircraft

	// used for smooth rotation, to remember the original angle to the next
	// waypoint
	private float startAngle;

	// whether the aircraft is selected by the player
	private boolean selected;

	private boolean turnRight, turnLeft;

	public Aircraft(AircraftType aircraftType, ArrayList<Waypoint> flightPlan,
			int id) {

		// allows drawing debug shape of this entity
		debugShape = true;

		this.id = id;

		// initialise all of the aircraft values according to the passed
		// aircraft type
		radius = aircraftType.getRadius();
		separationRadius = aircraftType.getSeparationRadius();
		texture = aircraftType.getTexture();
		maxTurningRate = aircraftType.getMaxTurningSpeed();
		maxClimbRate = aircraftType.getMaxClimbRate();
		maxSpeed = aircraftType.getMaxSpeed();
		minSpeed = maxSpeed - 1;
		velocityScalar = INITIAL_VELOCITY_SCALAR;
		velocity = aircraftType.getVelocity();
		altitude = 1000;

		// set the flightplan to the generated by the controller
		waypoints = flightPlan;

		// set the size
		size = new Vector2(76, 63);

		// set the coords to the entry point, remove it from the flight plan
		Waypoint entryPoint = waypoints.get(0);
		coords = new Vector2(entryPoint.getX(), entryPoint.getY());
		waypoints.remove(0);

		// set origin to center of the aircraft, makes rotation more intuitive
		this.setOrigin(size.x / 2, size.y / 2);

		this.setScale(0.5f);

		// set bounds so the aircraft is clickable
		this.setBounds(getX() - getWidth() / 1.5f, getY() - getWidth() / 1.5f,
				getWidth() * 2, getHeight() * 2);

		// set rotation & velocity angle to fit next waypoint
		float relativeAngle = relativeAngleToWaypoint();

		this.velocity.setAngle(relativeAngle);
		this.setRotation(relativeAngle);

		velocity.len();

		Debug.msg("||\nGenerated aircraft id " + id + "\nEntry point: "
				+ coords + "\nRelative angle to first waypoint: "
				+ relativeAngle + "\nVelocity" + velocity + "\nWaypoints: "
				+ waypoints + "\n||");
	}

	/**
	 * Additional drawing for if the aircraft is breaching
	 * 
	 * @param batch
	 */
	@Override
	protected void additionalDraw(SpriteBatch batch) {

		if (!ignorePath && !selected && !breaching)
			return;

		ShapeRenderer drawer = Screen.shapeDebugger;

		// debug line from aircraft centre to waypoint centre
		if (Config.DEBUG_UI) {
			Vector2 nextWaypoint = vectorToWaypoint();

			batch.end();

			drawer.begin(ShapeType.Line);
			drawer.setColor(1, 0, 0, 0);
			drawer.line(getX(), getY(), nextWaypoint.x, nextWaypoint.y);
			drawer.end();

			batch.begin();
		}

		if (ignorePath) {
			Waypoint exitpoint = waypoints.get(waypoints.size() - 1);

			batch.end();

			drawer.begin(ShapeType.Line);
			drawer.setColor(1, 0, 0, 0);
			drawer.line(getX(), getY(), exitpoint.getX(), exitpoint.getY());
			drawer.end();

			batch.begin();
		}

		if (selected || breaching) {
			batch.end();

			drawer.begin(ShapeType.Line);
			drawer.setColor(1, 0, 0, 0);
			drawer.circle(getX(), getY(), getWidth() / 2 + 5);
			drawer.end();

			batch.begin();
		}

	}

	/**
	 * Update the aircraft rotation & position
	 */
	public void act() {
		if (turnRight)
			turnRight();

		if (turnLeft)
			turnLeft();

		if (!ignorePath) {
			// Vector to next waypoint
			Vector2 nextWaypoint = vectorToWaypoint();

			float relativeAngle = relativeAngleToWaypoint(nextWaypoint);

			// smoothly rotate aircraft sprite
			// if current rotation is not the one needed
			if (getRotation() != relativeAngle) {
				// set the startAngle to remember it
				startAngle = relativeAngle;

				// making sure we rotate to the correct side, otherwise may
				// results
				// in a helicopter with no tail rotor
				if (startAngle < getRotation()) {
					rotate(-maxTurningRate);
				} else {
					rotate(maxTurningRate);
				}
			}

			// checking whether aircraft is at the next waypoint. Whether it's
			// close enough is dictated by the WP size in the config.
			if (nextWaypoint.sub(coords).len() < Config.WAYPOINT_SIZE.x / 2) {
				waypoints.remove(0);
			}

			// set velocity angle to fit rotation, allows for smooth turning
			velocity.setAngle(getRotation());
		}

		// For when the user takes control of the aircraft. Allows the aircraft
		// to detect when it is at its designated exit WP.
		if (waypoints.get(waypoints.size() - 1).cpy().getCoords().sub(coords)
				.len() < Config.EXIT_WAYPOINT_SIZE.x / 2) {
			waypoints.clear();
		}

		// finally updating coordinates
		coords.add(velocity.cpy().scl(velocityScalar));

		// updating bounds to make sure the aircraft is clickable
		this.setBounds(getX() - getWidth() / 1.5f, getY() - getWidth() / 1.5f,
				getWidth() * 2, getHeight() * 2);
	}

	/**
	 * Calculates the vector to the next waypoint
	 * 
	 * @return 3d vector to the next waypoint
	 */
	private Vector2 vectorToWaypoint() {
		// Creates a new vector to store the new velocity in temporarily
		Vector2 nextWaypoint = new Vector2();

		// converts waypoints coordinates into 3d vectors to enabled
		// subtraction.
		nextWaypoint.x = waypoints.get(0).getCoords().x;
		nextWaypoint.y = waypoints.get(0).getCoords().y;

		// round it to 2 points after decimal, makes it more manageable later
		nextWaypoint.x = (float) (Math.round(nextWaypoint.x * 100.0) / 100.0);
		nextWaypoint.y = (float) (Math.round(nextWaypoint.y * 100.0) / 100.0);

		return nextWaypoint;
	}

	/**
	 * Calculate relative angle of the aircraft to the next waypoint
	 * 
	 * @return relative angle in degrees, rounded to 2 points after decimal
	 */
	private float relativeAngleToWaypoint() {
		Vector2 nextWaypoint = vectorToWaypoint();

		return relativeAngleToWaypoint(nextWaypoint);
	}

	/**
	 * Calculate relative angle of the aircraft to a waypoint
	 * 
	 * @param waypoint
	 * @return angle in degrees, rounded to 2 points after decimal
	 */
	private float relativeAngleToWaypoint(Vector2 waypoint) {

		// degrees to nextWaypoint relative to aircraft
		float degrees = (float) ((Math.atan2(getX() - waypoint.x,
				-(getY() - waypoint.y)) * 180.0f / Math.PI) + 90.0f);

		// round it to 2 points after decimal so it's not rotating forever
		return Math.round(degrees * 100.0f) / 100.0f;
	}

	/**
	 * Adding a new waypoint to the head of the arraylist
	 * 
	 * @param newWaypoint
	 */
	public void insertWaypoint(Waypoint newWaypoint) {
		waypoints.add(0, newWaypoint);
	}

	/**
	 * Turns right by 5 degrees if the user presses the right key for more than
	 * 2000ms
	 */
	public void turnRight() {
		ignorePath = true;

		this.rotate(-maxTurningRate * 2);
		velocity.setAngle(getRotation());
	}

	/**
	 * Turns left by 5 degrees if the user presses the right key for more than
	 * 2000ms
	 */
	public void turnLeft() {
		ignorePath = true;

		this.rotate(maxTurningRate * 2);
		velocity.setAngle(getRotation());
	}

	/**
	 * Increase speed of the aircraft <br>
	 * Actually changes a scalar which is later multiplied by the velocity
	 * vector
	 * 
	 * @return <b>true</b> on success <br>
	 *         <b>false</b> when increased speed will be more than allowed
	 *         (maxSpeed)
	 */
	public boolean increaseSpeed() {
		if (velocity.cpy().scl(velocityScalar + SPEED_CHANGE).len() > maxSpeed)
			return false;

		velocityScalar += SPEED_CHANGE;

		Debug.msg("Decrease speed; Velocity Scalar: " + velocityScalar);

		return true;
	}

	/**
	 * Decrease speed of the aircraft <br>
	 * Actually changes a scalar which is later multiplied by the velocity
	 * vector
	 * 
	 * @return <b>true</b> on success <br>
	 *         <b>false</b> when decreased speed will be less than allowed
	 *         (minSpeed)
	 */
	public boolean decreaseSpeed() {
		if (velocity.cpy().scl(velocityScalar - SPEED_CHANGE).len() < minSpeed)
			return false;

		velocityScalar -= SPEED_CHANGE;

		Debug.msg("Increasing speed; Velocity scalar: " + velocityScalar);

		return true;
	}

	/**
	 * Increases rate of altitude change
	 */
	public void increaseAltitude() {
		this.altitude += ALTITUDE_CHANGE;
	}

	/**
	 * Decreasing rate of altitude change
	 */
	public void decreaseAltitude() {
		if (altitude - ALTITUDE_CHANGE < 0)
			return;

		altitude -= ALTITUDE_CHANGE;
	}

	public void turnRight(boolean set) {
		turnRight = set;
	}

	public void turnLeft(boolean set) {
		turnLeft = set;
	}

	/**
	 * Regular regular getter for radius
	 * 
	 * @return int radius
	 */
	public float getRadius() {
		return radius;
	}

	public Vector2 getCentreCoords() {
		return new Vector2(this.getX() + this.getOriginX(), this.getY()
				+ this.getOriginY());
	}

	public float getSeparationRadius() {
		return separationRadius;
	}

	public void isBreaching(boolean is) {
		this.breaching = is;
	}

	public int getAltitude() {
		return altitude;
	}

	/**
	 * Returns aircraft velocity scalar times 700
	 * 
	 * @return
	 */
	public float getSpeed() {
		return velocityScalar * 700f;
	}

	/**
	 * Returns false if aircraft flightplan is empty, true otherwise.
	 * 
	 * @return whether is active
	 */
	public boolean isActive() {
		// FIXME
		if (getX() < -10 || getY() < -10 || getX() > Config.SCREEN_WIDTH - 190
				|| getY() > Config.SCREEN_HEIGHT + 105) {
			this.isActive = false;

			Debug.msg("Aircraft id " + id
					+ ": Out of bounds, last coordinates: " + coords);
		}

		if (waypoints.size() == 0) {
			this.isActive = false;
			Debug.msg("Aircraft id " + id + ": Reached exit WP");
		}

		return isActive;
	}

	/**
	 * Setter for selected
	 * 
	 * @param newSelected
	 * @return whether is selected
	 */
	public boolean selected(boolean newSelected) {
		return this.selected = newSelected;
	}

	@Override
	public String toString() {
		return "Aircraft - x: " + getX() + " y: " + getY()
				+ "\n\r flight plan: " + waypoints.toString();
	}
}

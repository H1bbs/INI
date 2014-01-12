package seprini.controllers;

import java.util.HashMap;

import seprini.data.Art;
import seprini.data.Config;
import seprini.models.Aircraft;
import seprini.screens.GameScreen;
import seprini.screens.MenuScreen;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.esotericsoftware.tablelayout.Cell;

public final class SidebarController extends ChangeListener implements
		Controller {

	private final Table sidebar;

	private final AircraftController aircrafts;

	private Aircraft selectedAircraft;

	private final HashMap<String, TextButton> buttons = new HashMap<String, TextButton>();
	private final HashMap<String, Label> labels = new HashMap<String, Label>();

	private boolean allowNewWaypoints = false;
	private boolean allowRedirection = false;

	private final GameScreen screen;

	public SidebarController(Table sidebar, AircraftController aircrafts,
			GameScreen screen) {
		this.sidebar = sidebar;
		this.aircrafts = aircrafts;
		this.screen = screen;
	}

	/**
	 * Initialise all the buttons and labels
	 */

	public void init() {

		// wrapper for aicraft controls
		Table aircraftControls = new Table();

		// aircraftControls.setX(100);
		// aircraftControls.setY(650);
		aircraftControls.setFillParent(true);

		if (Config.DEBUG)
			aircraftControls.debug();

		aircraftControls.top();
		sidebar.addActor(aircraftControls);

		// wrapper for bottom buttons
		Table bottomButtons = new Table();

		bottomButtons.setFillParent(true);

		if (Config.DEBUG)
			aircraftControls.debug();

		bottomButtons.bottom();
		sidebar.addActor(bottomButtons);

		// adding labels to aircraft controls
		createLabel("", "Speed", aircraftControls).width(100);
		createLabel("", "Heading", aircraftControls).width(100);

		aircraftControls.row().width(100);

		// adding buttons to aircraft controls
		createButton("createWaypoint", "Create Waypoint", aircraftControls)
				.width(100);
		createButton("assignWaypoint", "Assign Waypoint", aircraftControls)
				.width(100);

		aircraftControls.row();

		createButton("accelerate", "Accelerate", aircraftControls).width(100);
		createButton("takeOff", "Take Off", aircraftControls).width(100);

		aircraftControls.row();

		createButton("decelerate", "Decelerate", aircraftControls).width(100);
		createButton("land", "Land", aircraftControls).width(100);

		aircraftControls.row().spaceTop(100);

		createButton("up", "Up", aircraftControls).width(100).colspan(2);

		aircraftControls.row();

		createButton("left", "Left", aircraftControls).width(100);
		createButton("right", "Right", aircraftControls).width(100);

		aircraftControls.row();

		createButton("down", "Down", aircraftControls).width(100).colspan(2);

		aircraftControls.row();

		// createLabel("", "Timer").width(100);

		aircraftControls.row();

		// adding buttons to bottom

		createButton("menu", "Menu", bottomButtons).width(100);
		createButton("pause", "Pause", bottomButtons).width(100);
		/**
		 * Currently not needed, not in specifications
		 * createLabel("aircraftCoordsLabel", "Coords X/Y: ").width(100);
		 * createLabel("aircraftCoordsText", "0").width(100);
		 */
	}

	/**
	 * Update the sidebar according to changes in the AircraftController
	 */
	public void update() {
		if ((selectedAircraft = aircrafts.getSelectedAircraft()) == null)
			return;

		// labels.get("aircraftText").setText(selectedAircraft.toString());
		// labels.get("aircraftCoordsText").setText(
		// Float.toString(Math.round(selectedAircraft.getX())) + " "
		// + Float.toString(Math.round(selectedAircraft.getY())));
	}

	/**
	 * Handles what happens after the 'create waypoint' button has been clicked
	 */
	private void createWaypointClicked() {
		allowNewWaypoints = (allowNewWaypoints) ? false : true;
	}

	/**
	 * Handles what happens after the 'assign waypoint' button has been clicked
	 */
	private void assignWaypointClicked() {
		allowRedirection = (allowRedirection) ? false : true;
	}

	/**
	 * Convinience method to create buttons and add them to the sidebar
	 * 
	 * @param name
	 * @param text
	 * @return
	 */
	private Cell<?> createButton(String name, String text) {
		TextButton button = new TextButton(text, Art.getSkin());
		button.addListener(this);

		buttons.put(name, button);

		return sidebar.add(button);
	}

	/**
	 * Convinience method to create buttons and add them to the sidebar
	 * 
	 * @param name
	 * @param text
	 * @return
	 */
	private Cell<?> createButton(String name, String text, Table parent) {
		TextButton button = new TextButton(text, Art.getSkin());
		button.addListener(this);

		buttons.put(name, button);

		return parent.add(button);
	}

	/**
	 * Convinience method to create labels and add them to the sidebar
	 * 
	 * @param name
	 * @param text
	 * @return
	 */
	private Cell<?> createLabel(String name, String text) {
		Label label = new Label(text, Art.getSkin());

		labels.put(name, label);

		return sidebar.add(label);
	}

	/**
	 * Convinience method to create labels and add them to the sidebar
	 * 
	 * @param name
	 * @param text
	 * @return
	 */
	private Cell<?> createLabel(String name, String text, Table parent) {
		Label label = new Label(text, Art.getSkin());

		labels.put(name, label);

		return parent.add(label);
	}

	@Override
	public void changed(ChangeEvent event, Actor actor) {

		if (actor.equals(buttons.get("createWaypoint")))
			createWaypointClicked();

		if (actor.equals(buttons.get("assignWaypoint")))
			assignWaypointClicked();

		if (actor.equals(buttons.get("left")))
			selectedAircraft.turnLeft();

		if (actor.equals(buttons.get("right")))
			selectedAircraft.turnRight();

		if (actor.equals(buttons.get("up")))
			selectedAircraft.increaseAltitude();

		if (actor.equals(buttons.get("down")))
			selectedAircraft.decreaseAltitude();

		if (actor.equals(buttons.get("menu")))
			screen.setScreen(new MenuScreen());

	}

	public boolean allowNewWaypoints() {
		return allowNewWaypoints;
	}

	public boolean isAllowRedirection() {
		return allowRedirection;
	}

}

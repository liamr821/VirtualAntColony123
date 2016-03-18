/* Some of the UI shown here is reused code from a different simulation in MASON that suited our purposes */
package sim.app.project;

import sim.engine.*;
import sim.display.*;
import sim.portrayal.grid.*;
import java.awt.*;
import javax.swing.*;

public class projectMainUI extends GUIState
    {
    public Display2D display;
    public JFrame displayFrame;

    FastValueGridPortrayal2D homePheromonePortrayal = new FastValueGridPortrayal2D("Home Pheromone");
    FastValueGridPortrayal2D foodPheromonePortrayal = new FastValueGridPortrayal2D("Food Pheromone");
    FastValueGridPortrayal2D sitesPortrayal = new FastValueGridPortrayal2D("Site", true); //this makes them immutable
    FastValueGridPortrayal2D obstaclesPortrayal = new FastValueGridPortrayal2D("Obstacle", true);  //this makes them immutable(unchanging)
    SparseGridPortrayal2D antPortrayal = new SparseGridPortrayal2D();
                
    public static void main(String[] args)
        {
        new projectMainUI().createController();
        }
    
    public projectMainUI() { super(new projectMain(System.currentTimeMillis())); }
    public projectMainUI(SimState state) { super(state); }
    

    public Object getSimulationInspectedObject() { return state; }  // allow the user to inspect the model

    public static String getName() { return "Virtual Ant Colony"; }
    
    public void setupPortrayals()
        {
        projectMain pm = (projectMain)state;
        
        // tell the portrayals what to portray and how to portray them
        homePheromonePortrayal.setField(pm.toHomeGrid);
        homePheromonePortrayal.setMap(new sim.util.gui.SimpleColorMap(
                0,
                projectMain.maxPheromone,
                
                Color.white, //new Color(0,255,0,0),
                new Color(0,255,0,255) )
            { public double filterLevel(double level) { return Math.sqrt(Math.sqrt(level)); } } );  // map with custom level filtering
        foodPheromonePortrayal.setField(pm.toFoodGrid);
        foodPheromonePortrayal.setMap(new sim.util.gui.SimpleColorMap(
                0,
                projectMain.maxPheromone,
                new Color(0,0,255,0),
                new Color(0,0,255,255) )
            { public double filterLevel(double level) { return Math.sqrt(Math.sqrt(level)); } } );  // map with custom level filtering
        sitesPortrayal.setField(pm.sites);
        sitesPortrayal.setMap(new sim.util.gui.SimpleColorMap(
                0,
                1,
                new Color(0,0,0,0),
                new Color(255,0,0,255) ));
        obstaclesPortrayal.setField(pm.obstacles);
        obstaclesPortrayal.setMap(new sim.util.gui.SimpleColorMap(
                0,
                1,
                new Color(0,0,0,0),
                new Color(128,64,64,255) ));
        antPortrayal.setField(pm.antgrid);
            
        // reschedule the displayer
        display.reset();

        // redraw the display
        display.repaint();
        }
    
    public void start()
        {
        super.start();  // set up everything but replacing the display
        // set up our portrayals
        setupPortrayals();
        }
            
    public void load(SimState state)
        {
        super.load(state);
        
        setupPortrayals();
        }

    public void init(Controller c)
        {
        super.init(c);
        
        // Make the Display2D.  We'll have it display stuff later.
        display = new Display2D(400,400,this); //400x400 display
        displayFrame = display.createFrame();
        c.registerFrame(displayFrame);   // registering the frame so it appears in the "Display" list
        displayFrame.setVisible(true);

        // attach the portrayals
        display.attach(homePheromonePortrayal,"Pheromones To Home");
        display.attach(foodPheromonePortrayal,"Pheromones To Food");
        display.attach(sitesPortrayal,"Site Locations");
        display.attach(obstaclesPortrayal,"Obstacles");
        display.attach(antPortrayal,"Agents");
        
        // specify the backdrop color  -- what gets painted behind the displays
        display.setBackdrop(Color.white);
        }
        
    public void quit()
        {
        super.quit();
        
        // disposing the displayFrame automatically calls quit() on the display,
        // so we don't need to do so ourselves here.
        if (displayFrame!=null) displayFrame.dispose();
        displayFrame = null;  
        display = null;       
        }
        
    }
    
    
    
    

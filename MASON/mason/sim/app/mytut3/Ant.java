/*
  Copyright 2006 by Sean Luke and George Mason University
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/

package sim.app.mytut3;
import sim.portrayal.*;
import sim.portrayal.simple.*;
import sim.util.*;
import sim.engine.*;
import java.awt.*;
import sim.portrayal.simple.OvalPortrayal2D;
/** A bouncing particle. */

public abstract class Ant extends OvalPortrayal2D implements Steppable
    {
    private static final long serialVersionUID = 1;

    public boolean getHasFoodItem()	{return hasFoodItem;}
    public void setHasFoodItem(boolean val) { hasFoodItem = val; }
    public boolean hasFoodItem = false;
    public boolean randomize = false;
    double reward  = 0;
    public int xdir;  // -1, 0, or 1
    public int ydir;  // -1, 0, or 1
    
    public Particle(double initialReward)
        {
        reward = initialReward;
        }

    public void putPheremone(final SimState state)
        {
        Tutorial3 tut = (Tutorial3)state;
        
        // We could just store my location internally, but for purposes of
        // show, let's get my position out of the particles grid
        Int2D location = tut.particles.getObjectLocation(this);

        // leave a trail
        tut.trails.field[location.x][location.y] = 1.0;
        
        // Randomize my direction if requested
        if (randomize)
            {
            xdir = tut.random.nextInt(3) - 1;
            ydir = tut.random.nextInt(3) - 1;
            randomize = false;
            }
        
        // move
        int newx = location.x + xdir;
        int newy = location.y + ydir;
        
        // reverse course if hitting boundary
        if (newx < 0) { newx++; xdir = -xdir; }
        else if (newx >= tut.trails.getWidth()) {newx--; xdir = -xdir; }
        if (newy < 0) { newy++ ; ydir = -ydir; }
        else if (newy >= tut.trails.getHeight()) {newy--; ydir = -ydir; }
        
        // set my new location
        Int2D newloc = new Int2D(newx,newy);
        tut.particles.setObjectLocation(this,newloc);
        
        // randomize everyone at that location if need be
        Bag p = tut.particles.getObjectsAtLocation(newloc);
        if (p.numObjs > 1)
            {
            for(int x=0;x<p.numObjs;x++)
                ((Particle)(p.objs[x])).randomize = true;
            }
        }
    }

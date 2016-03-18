
package sim.app.project;

import sim.field.grid.*;
import sim.portrayal.*;
import sim.portrayal.simple.*;
import sim.util.*;
import sim.engine.*;
import java.awt.*;

public class projectAnt extends OvalPortrayal2D implements Steppable
    {


    public boolean getHasFoodItem() { return hasFoodItem; }
    public void setHasFoodItem(boolean val) { hasFoodItem = val; }
    public boolean hasFoodItem = false;
    double reward = 0;
        
    int x;
    int y;
        
    Int2D last;
        
    public projectAnt(double initialReward) { reward = initialReward; }
        
        
        
        
    public void depositPheromone( final SimState state)
        {
        final projectMain pm = (projectMain)state;
                
        Int2D location = pm.antgrid.getObjectLocation(this);
        int x = location.x;
        int y = location.y;
                

            
            if (hasFoodItem)  // deposit food pheromone
                {
                double max = pm.toFoodGrid.field[x][y];
                for(int dx = -1; dx < 2; dx++)
                    for(int dy = -1; dy < 2; dy++)
                        {
                        int _x = dx+x;
                        int _y = dy+y;
                        if (_x < 0 || _y < 0 || _x >= projectMain.gridWidth || _y >= projectMain.gridHeight) continue;  // nothing to see here
                        double m = pm.toFoodGrid.field[_x][_y] * 
                            (dx * dy != 0 ? // diagonal corners
                            pm.diagonalCutDown : pm.updateCutDown) +
                            reward;
                        if (m > max) max = m;
                        }
                pm.toFoodGrid.field[x][y] = max;
                }
            else
                {
                double max = pm.toHomeGrid.field[x][y];
                for(int dx = -1; dx < 2; dx++)
                    for(int dy = -1; dy < 2; dy++)
                        {
                        int _x = dx+x;
                        int _y = dy+y;
                        if (_x < 0 || _y < 0 || _x >= projectMain.gridWidth || _y >= projectMain.gridHeight) continue;  // nothing to see here
                        double m = pm.toHomeGrid.field[_x][_y] * 
                            (dx * dy != 0 ? // diagonal corners
                            pm.diagonalCutDown : pm.updateCutDown) +
                            
                            reward;
                        if (m > max) max = m;
                        }
                pm.toHomeGrid.field[x][y] = max;
                }
            
        reward = 0.0;
        }

    public void act( final SimState state )
        {
        final projectMain pm = (projectMain)state;
                
        Int2D location = pm.antgrid.getObjectLocation(this);
        int x = location.x;
        int y = location.y;
                
        if (hasFoodItem)  // follow home pheromone
            {
            double max = projectMain.IMPOSSIBLY_BAD_PHEROMONE;
            int max_x = x;
            int max_y = y;
            int count = 2;
            for(int dx = -1; dx < 2; dx++)
                for(int dy = -1; dy < 2; dy++)
                    {
                    int _x = dx+x;
                    int _y = dy+y;
                    if ((dx == 0 && dy == 0) ||
                        _x < 0 || _y < 0 ||
                        _x >= projectMain.gridWidth || _y >= projectMain.gridHeight || 
                        pm.obstacles.field[_x][_y] == 1) continue;  
                    double m = pm.toHomeGrid.field[_x][_y];
                    if (m > max)
                        {
                        count = 2;
                        }
                    // no else, yes m > max is repeated
                    if (m > max || (m == max && state.random.nextBoolean(1.0 / count++)))  // this makes all "==" situations equally likely
                        {
                        max = m;
                        max_x = _x;
                        max_y = _y;
                        }
                    }
            if (max == 0 && last != null)  // nowhere to go!  Maybe go straight
            {
            if (state.random.nextBoolean(pm.momentumProbability))
                {
                int xm = x + (x - last.x);
                int ym = y + (y - last.y);
                if (xm >= 0 && xm < projectMain.gridWidth && ym >= 0 && ym < projectMain.gridHeight && pm.obstacles.field[xm][ym] == 0)
                    { max_x = xm; max_y = ym; }
                }
            }
            pm.antgrid.setObjectLocation(this, new Int2D(max_x, max_y));
            if (pm.sites.field[max_x][max_y] == projectMain.HOME)  // reward me next time!  And change my status
                { 
            	reward = pm.reward ;
            	hasFoodItem = ! hasFoodItem;
            	}
            }
        else
            {
            double max = projectMain.IMPOSSIBLY_BAD_PHEROMONE;
            int max_x = x;
            int max_y = y;
            int count = 2;
            for(int dx = -1; dx < 2; dx++)
                for(int dy = -1; dy < 2; dy++)
                    {
                    int _x = dx+x;
                    int _y = dy+y;
                    if ((dx == 0 && dy == 0) ||
                        _x < 0 || _y < 0 ||
                        _x >= projectMain.gridWidth || _y >= projectMain.gridHeight || 
                        pm.obstacles.field[_x][_y] == 1) continue;  
                    double m = pm.toFoodGrid.field[_x][_y];
                    if (m > max)
                        {
                        count = 2;
                        }
                    // no else, yes m > max is repeated
                    if (m > max || (m == max && state.random.nextBoolean(1.0 / count++)))  // this little magic makes all "==" situations equally likely
                        {
                        max = m;
                        max_x = _x;
                        max_y = _y;
                        }
                    }
            if  (max == 0 && last != null)  // if there's nowhere to go, maybe go straight
            {
            if (state.random.nextBoolean(pm.momentumProbability))
                {
                int xm = x + (x - last.x);
                int ym = y + (y - last.y);
                if (xm >= 0 && xm < projectMain.gridWidth && ym >= 0 && ym < projectMain.gridHeight && pm.obstacles.field[xm][ym] == 0)
                    { max_x = xm; max_y = ym; }
                }
            }
            
            pm.antgrid.setObjectLocation(this, new Int2D(max_x, max_y));
            if (pm.sites.field[max_x][max_y] == projectMain.FOOD || pm.sites.field[max_x][max_y] == projectMain.FOOD2 )  // reward me next time!  And change my status
                { reward = pm.reward; hasFoodItem = ! hasFoodItem; }
            }
        last = location;
        }

    public void step( final SimState state )
        {
        depositPheromone(state);
        act(state);
        }


    private Color noFoodColor = Color.black;
    private Color foodColor = Color.red;
    public final void draw(Object object, Graphics2D graphics, DrawInfo2D info)
        {
        if( hasFoodItem )
            graphics.setColor( foodColor );
        else
            graphics.setColor( noFoodColor );


        int x = (int)(info.draw.x - info.draw.width / 2.0);
        int y = (int)(info.draw.y - info.draw.height / 2.0);
        int width = (int)(info.draw.width);
        int height = (int)(info.draw.height);
        graphics.fillOval(x,y,width, height);

        }
    }
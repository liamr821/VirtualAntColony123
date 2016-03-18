 
 
package sim.app.project;
 
import sim.field.grid.*;
import sim.portrayal.*;
import sim.portrayal.simple.*;
import sim.util.*;
import sim.engine.*;
import java.awt.*;
 
public class projectAnt extends OvalPortrayal2D implements Steppable
    {
    private static final long serialVersionUID = 1;
 
    public boolean getHasFoodItem() { return hasFoodItem; }
    public void setHasFoodItem(boolean val) { hasFoodItem = val; }
    public boolean hasFoodItem = false;
    double reward = 0;
       
    int x;
    int y;
       
    Int2D last; //holds the location of the last occupied space
       
    public projectAnt(double initialReward) { reward = initialReward; } //ants have a reward parameter which provides pheromone
         
    public void depositPheromone( final SimState state)
        {
        final projectMain pm = (projectMain)state;
               
        Int2D location = pm.antgrid.getObjectLocation(this);
        int x = location.x;
        int y = location.y;
           
            if (hasFoodItem)  // deposit food pheromone
                {
                double maximum = pm.toFoodGrid.field[x][y]; //
                for(int xgrid = -1; xgrid < 2; xgrid++) {   // Ants deposit pheromones in locations all around it
                    for(int ygrid = -1; ygrid < 2; ygrid++) {
                        if((xgrid + x == 0) || (ygrid + y == 0) || xgrid > projectMain.gridWidth || ygrid > projectMain.gridHeight) continue; //pheromones not placed out of bounds
                        double p = pm.toFoodGrid.field[(x + xgrid)][(y + ygrid)] * pm.updateCutDown + reward;
                        if(p > maximum) maximum = p;
                    }
                pm.toFoodGrid.field[x][y] = maximum;
                }
                }
 
            else  //ants looking for food source
                {
                double maximum = pm.toHomeGrid.field[x][y];
                for(int xgrid = -1; xgrid < 2; xgrid++) {
                    for(int ygrid = -1; ygrid < 2; ygrid++) {
                        int x1 = x + xgrid;
                        int y1 = y + ygrid;
                        if(x1 < 0 || y1 < 0 || x1 >= projectMain.gridWidth || y1 >= projectMain.gridHeight) continue;
                        double p = pm.toHomeGrid.field[x1][y1] * pm.updateCutDown + reward;
                        if(p > maximum) maximum = p;
                    }
                }
                pm.toHomeGrid.field[x][y] = maximum;
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
            double best = projectMain.badPheromone;
            int best_x = x;
            int best_y = y;
            int decisionVariable = 2;
            for(int xgrid = -1; xgrid < 2; xgrid++) {
                for(int ygrid = -1; ygrid < 2; ygrid++) {
                    if((xgrid == 0 && ygrid == 0) || (xgrid + x < 0) || (ygrid + y < 0) || (xgrid + x >= projectMain.gridWidth)
                            || (ygrid + y >= projectMain.gridHeight) || pm.obstacles.field[(xgrid + x)][(ygrid + y)] == 1) {
                        continue; // if current coordinate in the loop is out of bounds just move to the next one
                    }
                    double p = pm.toHomeGrid.field[(xgrid + x)][(ygrid + y)];
                    if(p > best) {
                        decisionVariable = 2; //makes it so decisions are 50/50
                    }
                    if(p > best || (p== best && state.random.nextBoolean(1.0 / decisionVariable++))) { //if no decision looks better than another then choose a random one, the decision variable makes the randomness fair
                        best = p;
                        best_x = (xgrid + x);
                        best_y = (ygrid + y);
                    }
                }
            }
 
//random and momentum here
            pm.antgrid.setObjectLocation(this, new Int2D(best_x, best_y));
            if (pm.sites.field[best_x][best_y] == projectMain.HOME)  // reward me next time!  And change my status
                {
                reward = pm.reward ;
                hasFoodItem = ! hasFoodItem;
                }
            }
 
 
       
        else
            {
            double best = projectMain.badPheromone;
            int best_x = x;
            int best_y = y;
            int decisionVariable = 2;
            for(int xgrid = -1; xgrid < 2; xgrid++) {
                for(int ygrid = -1; ygrid < 2; ygrid++) {
                    if((xgrid == 0 && ygrid == 0) || (xgrid + x < 0) || (ygrid + y < 0) || (xgrid + x >= projectMain.gridWidth)
                            || (ygrid + y >= projectMain.gridHeight) || pm.obstacles.field[(xgrid + x)][(ygrid + y)] == 1) {
                        continue;
                    }
                    double p = pm.toFoodGrid.field[(xgrid + x)][(ygrid + y)];
                    if(p > best) {
                        decisionVariable = 2; //makes it so decisions are 50/50
                    }
                    if(p > best || (p== best && state.random.nextBoolean(1.0 / decisionVariable++))) {
                        best = p;
                        best_x = (xgrid + x);
                        best_y = (ygrid + y);
                    }
                }
            }
            if(best == 0 && last != null) { // if no obvious best choice try going in a straight line
                if(state.random.nextBoolean(pm.momentumProbability)) { //chance that straight line movement will occur
                    int nextX = x + (x- last.x);
                    int nextY = y + (y - last.y);
                    if(nextX >= 0 && nextY >= 0 && nextX < projectMain.gridWidth && nextY < projectMain.gridHeight && pm.obstacles.field[nextX][nextY] == 0) {
                        best_x = nextX;
                        best_y = nextY;
                    }
                }
            }
 
 
            pm.antgrid.setObjectLocation(this, new Int2D(best_x, best_y));
            if (pm.sites.field[best_x][best_y] == projectMain.FOOD)  // once food is found change state
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
 
        // this code was taken from OvalPortrayal2D
        int x = (int)(info.draw.x - info.draw.width / 2.0);
        int y = (int)(info.draw.y - info.draw.height / 2.0);
        int width = (int)(info.draw.width);
        int height = (int)(info.draw.height);
        graphics.fillOval(x,y,width, height);
 
        }
    }
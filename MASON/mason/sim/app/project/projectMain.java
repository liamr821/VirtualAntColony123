package sim.app.project;



import sim.engine.*;
import sim.field.grid.*;
import sim.util.*;


public class projectMain extends SimState
    {

    
    public static final double badPheromone = -1;
    public static final double maxPheromone = 3;
    public static final int homeXValMin = 75;
    public static final int homeXValMax = 75;
    public static final int homeYValMin = 75;
    public static final int homeYValMax = 75;
    public static final int foodXValMin = 25;
    public static final int foodXValMax = 25;
    public static final int foodYValMin = 25;
    public static final int foodYValMax = 25;
    public static final int HOME = 1;
    public static final int FOOD = 2;
    public static final int gridWidth = 100;
    public static final int gridHeight = 100;
    public static int numAnts = 500;
    public double evaporationConstant = 0.999;
    public double reward = 1.0;
    public double updateCutDown = 0.9; 
    public double momentumProbability = 0.8;

// These methods are so we can inspect the model and change the value of objects without needing to change our code.
    public int getNumAnts() { 
    	return numAnts; 
    	}
    public void setNumAnts(int val) {
    	if(val > 0)
    		numAnts = val; 
    	}
        
    public double getEvaporationConstant() {
    	return evaporationConstant; 
    	}
    public void setEvaporationConstant(double val) {
    	if (val >= 0 && val <= 1.0) 
    		evaporationConstant = val; 
    	}
    
    public double getCutDown(double val)	{
    	return updateCutDown;
    }
    
    public double getCutDown() {
    	return updateCutDown;
    	
    }
    public void setCutDown(double val) {
    	if (val >= 0 && val <= 1.0)
    		updateCutDown = val;
    }

    
    public double getReward() {
    	return reward;
    	
    }
    
    public void setReward(double val) {
    	if (val >= 0) 
    		reward = val; 
    	
    	}
    
    // create food site, home site, obstacles
    public IntGrid2D sites = new IntGrid2D(gridWidth, gridHeight,0);
    public DoubleGrid2D toFoodGrid = new DoubleGrid2D(gridWidth, gridHeight,0);
    public DoubleGrid2D toHomeGrid = new DoubleGrid2D(gridWidth, gridHeight,0);
    public SparseGrid2D antgrid = new SparseGrid2D(gridWidth, gridHeight);
    public IntGrid2D obstacles = new IntGrid2D(gridWidth, gridHeight,0);
    
    public projectMain(long seed)
        {
        super(seed);
        }

    public void start()
        {
        super.start(); //handles starting
        sites = new IntGrid2D(gridWidth, gridHeight,0);
        toFoodGrid = new DoubleGrid2D(gridWidth, gridHeight,0);
        toHomeGrid = new DoubleGrid2D(gridWidth, gridHeight,0);
        antgrid = new SparseGrid2D(gridWidth, gridHeight);
        obstacles = new IntGrid2D(gridWidth, gridHeight, 0);
        //places food and home sites
        for( int x = homeXValMin ; x <= homeXValMax ; x++ )
            for( int y = homeYValMin ; y <= homeYValMax ; y++ )
                sites.field[x][y] = HOME;
        for( int x = foodXValMin ; x <= foodXValMax ; x++ )
            for( int y = foodYValMin ; y <= foodYValMax ; y++ )
                sites.field[x][y] = FOOD;
        
        

        //places obstacles
        for( int x = 0 ; x < gridWidth ; x++ )
            for( int y = 0 ; y < gridHeight ; y++ )
                {
                obstacles.field[x][y] = 0;
                if( ((y-50)*0.707+(x-50)*0.707)*((y-50)*0.707+(x-50)*0.707)/36+
                    ((y-50)*0.707-(x-50)*0.707)*((y-50)*0.707-(x-50)*0.707)/750 <= 1 )
                    obstacles.field[x][y] = 1;
                
        
                if( ((y-30)*0.707+(x-45)*0.707)*((y-30)*0.707+(x-45)*0.707)/750+
                    ((y-30)*0.707-(x-45)*0.707)*((y-30)*0.707-(x-45)*0.707)/36 <= 1 )
                    obstacles.field[x][y] = 1; //this code for obstacles comes from another simulation, it suited our purposes and so we have reused it within our simulation.
                }
        
        
        //creates the specified number of ants
        for(int x = 0; x < numAnts; x++)	{
            
            projectAnt ant = new projectAnt(reward);
            antgrid.setObjectLocation(ant,(homeXValMax+homeXValMin)/2,(homeYValMax+homeYValMin)/2);
            schedule.scheduleRepeating(Schedule.EPOCH + x, 0, ant, 1); //schedules the creation of ants.
            }
        
    //schedules our pheromone to evaporate
    schedule.scheduleRepeating(Schedule.EPOCH,1, new Steppable()
    {
    public void step(SimState state) { toFoodGrid.multiply(evaporationConstant); toHomeGrid.multiply(evaporationConstant); }
    }, 1);
    
    }
    
        
		public static void main(String[] args)
		{
			doLoop(projectMain.class, args);
			System.exit(0);
		}    
		}
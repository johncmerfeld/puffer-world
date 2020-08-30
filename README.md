# Welcome to Puffer World

## To be renamed (The terrarium)
Currently packaged as jsimulate

## To run from command line (working on a standalone application)
```shell
# make sure Java is installed
# clone repo
# then...

# navigate to src directory
cd src
# compile
javac jsimulate/Main.java 
# run
java jsimulate.Main
```


## Version history

### Version 6.0 (July 6th):
  - **New creature: Scoopers**
    - Unlike Puffers, Scoopers gather food from afar and carry it back to their home
    - For now, their appearance is identical to Puffers, but they do not grow, reproduce, or starve
    - For now, bringing food back home has no effect
  - **New terrain: Homes**
    - All Scoopers have a home. Homes map to a family, binding Puffers and Scoopers
    - If there are more Puffers than Scoopers, the extra Puffers are homeless (will change)
    - For now, homes confer no protective benefits
  - Significant refactoring to better utilize inheritence and polymorphism

### Version 5.0 (June 26th):
  - **New behavior: Reproduction**
    - After a certain number of growth spurts, puffers die and produce offspring 
    - Number of offspring is proportional to size at time of death
    - Successive generations with same ancestry will not attack one another
  - **Simulator UI: Input parameters**
    - Enter the number of puffers, food generation interval, and world size
    - Press 'start' and see your world come to life!
  - **Performance improvements:**
    - Puffers now much more adept at getting around walls
    - Puffers no longer run off the edge of the map when being chased
    - Puffers better at charting a path away from predators when near walls and edges

### Version 4.0 (June 24th):
  - **Simulator UI: Pause, resume, and (buggy) restart**
  - **New behavior: Starvation**
    - Puffers that cannot find food die after a while
  - **New behavior: Exhaustion (temporary)**
    - Puffers die after a certain number of growth spurts
  - Crumbled walls persist as spots on the map
  - Fixes to smoothness of Puffer movement

### Version 3.0 (June 23rd):
  - **New Puffer attribute: Speed!**
    - Food provides nutrients for both size and speed
    - When Puffers reach certain milestones for either, the stat will increase
    - Food formed from decayed puffers will mimic the attributes of their living body
  - **New behavior: Predation**
    - When food is scare, large puffer will seek out smaller ones to attack
    - Small puffers can sense when they are being chased and will try to escape
  - **Simulator UI: sidebar with manual start button**
    - Still very much in beta, but an important step towards a general-use program

### Version 2.0 (June 22nd):
  - **New Terrain: Walls!**
    - Walls form from rotten food
    - Puffers cannot pass through walls
    - After a long time, walls will crumble
  - **Simulator UI: scrollable window for large worlds**
  - Food now generates randomly at regular intervals
  - Dead puffers will decay and become food after a while

### Version 1.0 (June 21st):
  - Multiple puffers seek out closest food
  - Puffers grow in size and get better eyesight after eating enough food
  - Big puffers kill small ones on contact

### Version 0.2 (June 20th):
  - Puffer seeks out nearby food
  - Puffer eats food on contact

### Version 0.1 (June 20th):
  - Food distributed randomly
  - Single puffer moves randomly

### Version 0.0 (June 19th, 2020):
  - Blank screen

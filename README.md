## Version history

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
    - Food formed from decayed puffers will mimic the attributes of their living bodyi
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

import numpy as np

import hexHelper as helper
from tile import Tile

GRID_CONFIGURATION = {
    'cell_radius': 6,
    'rows': 150,
    'cols': 150,
    'crop_bigger_grids': True
}

DENSITY_E = 0.015 # initial elevation density
DENSITY_P = 0.003 # initial precipitation density

MIN_WATER_LEVEL = 100 # any tiles with less than this amount at the end will lose it

P_STICK = 0.01 # probability of sticking
P_SPLIT = 0.003 # probability of splitting
P_MARK = 0.005 # probability of marking
SPEED = 200  # gif speed
TOTAL_TIME = 400 # number of generations

def randomSeed():
    result = []
    #alreadyDidOne = False
    for row in range(GRID_CONFIGURATION['rows']):
        rowResult = []
        for col in range(GRID_CONFIGURATION['cols']):
            activateE = np.random.random_sample() <= DENSITY_E
            activateP = np.random.random_sample() <= DENSITY_P
            rowResult.append(Tile(row, col, activateE, activateP))
        result.append(rowResult)
    return result

class Sim:
    def __init__(self, seed=None, ticks=TOTAL_TIME):
        assert ticks >= 0

        self.number_of_ticks = ticks
        self.count = 0
        self.helper = helper.GridHelper(**GRID_CONFIGURATION)

        if not seed:
            seed = randomSeed()
        seed = self.helper.sanitize(seed)

        self.illustrator = Sim.__set_up_illustrator(seed)
        self.map = Map(seed)

    def run(self):
        self.illustrator.draw(self.map)
        print('------- I HAVE INITIALIZED ---------')
        print(f"Initial active elevation tiles: {len(self.map._allActiveE())}")
        print(f"Initial active precipitation tiles: {len(self.map._allActiveP())}")

        # could also smooth every few generations...
        while self.count < self.number_of_ticks:
            self.map = self.map.tickX()
            if self.count % 4 == 0:
                self.map = self.map.tickWater()
            self.illustrator.draw(self.map)
            self.count += 1

        print('------- I AM SMOOTHING ---------')
        self.map = self.map.smooth(5)
        self.illustrator.draw(self.map)

        print('------- I AM RAINING ---------')
        for _ in range(100):
            self.map = self.map.tickRain()
            self.illustrator.draw(self.map)

        print('------- I AM EVAPORATING ---------')
        self.map = self.map.evaporate()
        self.illustrator.draw(self.map)        

        print('------- SAVING GIF --------')
        self.illustrator.save_gif()

    @staticmethod
    def __set_up_illustrator(seed):
        config = {
            'cell_radius': GRID_CONFIGURATION.get('cell_radius'),
            'row_count': len(seed),
            'col_count': len(seed[0])
        }
        return helper.Illustrator(SPEED, **config)

# this is the evolution logic...
# I think it's also a full representation of the board at any given time
# TODO: still would like internal methods to be more in terms of tiles...
class Map:
    def __init__(self, grid, previous=None):
        self._grid = grid
        self._previous = previous
        self._rows = len(self._grid)
        self._cols = len(self._grid[0])
        self._hotPoints = []
    
    def __str__(self):
        result = ""
        for row in range(self._rows):
            for col in range(self._cols):
                result += f"[{self.elevationAt(row, col)}]"
            result += "\n"
        return result

    # the purpose of this is to return the truth value of each point
    # in general, all we need this to return is a status that we can convert into a color
    def tick(self):
        allActive = self._allActiveE()
        for tile in allActive:
            tile.elevate()
            tile.deactivateE()
            # TODO: this creates perverse effects at edges because there are fewer neighbors to choose from
            n = self._randomNeighbor(tile)
            n.activateE()

        return Map(self._grid, self)
    
    # TODO: consolidate the tick methods
    # STICKER
    def tickS(self):
        allActive = self._allActiveE()
        for tile in allActive:
            tile.elevate()
            # with some probability, stay where we are
            if np.random.random_sample() <= P_STICK:
                continue

            tile.deactivateE()
            # TODO: this creates perverse effects at edges because there are fewer neighbors to choose from
            n = self._randomNeighbor(tile)
            n.activateE()

        return Map(self._grid, self)

    # SPLITTER
    def tickX(self):
        allActive = self._allActiveE()
        for tile in allActive:
            tile.elevate()
            tile.deactivateE()
            # TODO: this creates perverse effects at edges because there are fewer neighbors to choose from
            n = self._randomNeighbor(tile)
            n.activateE()

            # with some probability, activate an aditional tile
            if np.random.random_sample() <= P_SPLIT:
                n = self._randomNeighbor(tile)
                n.activateE()

        return Map(self._grid, self)
    
    # MARKER
    def tickM(self, water):
        allActive = self._allActiveE()
        for tile in allActive:
            tile.elevate()
            tile.deactivateE()
            # TODO: this creates perverse effects at edges because there are fewer neighbors to choose from
            n = self._randomNeighbor(tile)
            n.activateE()

            # with some probability, activate an aditional tile
            if np.random.random_sample() <= P_MARK:
                for n in self._neighbors(tile):
                    n.elevate()

        return Map(self._grid, self)
    
    def tickWater(self):
        allActive = self._allActiveP()
        for tile in allActive:
            tile.precipitate()
            tile.deactivateP()
            # TODO: this creates perverse effects at edges because there are fewer neighbors to choose from
            n = self._randomNeighbor(tile)
            n.activateP()

            # with some probability, activate an aditional tile
            if np.random.random_sample() <= P_MARK:
                for n in self._neighbors(tile):
                    n.precipitate()

        return Map(self._grid, self)
    
    def tickRain(self):
        totalWater = 0 
        for row in range(self._rows):
            for col in range(self._cols):
                currentTile = self._grid[row][col]
                if currentTile.precipitation <= 0:
                    continue
                else:
                    totalWater += currentTile.precipitation

                neighbors = self._neighbors(currentTile)

                # the rain algorithm!!
                waterToFlowDown = 0

                lowerNeighbors = [n for n in neighbors if n.waterAdjustedElevation() < currentTile.waterAdjustedElevation()]
                levelNeighbors = [n for n in neighbors if n.waterAdjustedElevation() == currentTile.waterAdjustedElevation()]

                if len(lowerNeighbors) > 0:
                # lose up to all of your water to downhill neighbors
                    waterToFlowDown = len(lowerNeighbors) / (len(lowerNeighbors) + len(levelNeighbors)) * currentTile.precipitation
                    waterPerLowerNeighbor = waterToFlowDown / len(lowerNeighbors)
                    for lowerNeighbor in lowerNeighbors:
                        lowerNeighbor.nextWaterLevel += waterPerLowerNeighbor
                        currentTile.nextWaterLevel -= waterPerLowerNeighbor

                if len(levelNeighbors) > 0:
                # exchange some water with neighbors at your level
                    waterToExchange = currentTile.precipitation - waterToFlowDown
                    waterPerLevelNeighbor = waterToExchange / (len(levelNeighbors) + 1)
                    for levelNeighbor in levelNeighbors:
                        levelNeighbor.nextWaterLevel += waterPerLevelNeighbor
                        currentTile.nextWaterLevel -= waterPerLevelNeighbor

               #if len(levelNeighbors) == 0 and len(lowerNeighbors) == 0:
               #    print(f"I'm [{currentTile.row}][{currentTile.col}] and I should be retaining {currentTile.precipitation} water...")

        #print(f"Total water on the map: {totalWater}")

        for row in range(self._rows):
            for col in range(self._cols):
                self._grid[row][col].precipitation = self._grid[row][col].nextWaterLevel
                self._grid[row][col].nextWaterLevel = self._grid[row][col].precipitation
        
        return Map(self._grid, self)
        
    
    def smooth(self, depth):
        newCellValues = []
        for row in range(self._rows):
            rowResult = []
            for col in range(self._cols):
                neighbors = self._neighbors(self._grid[row][col], depth)
                # new value is one third your own, two thirds the average of your neighbors
                # TODO: make this easier to tweak via a smoothing factor
                avg = np.mean(([n.elevation for n in neighbors] * 2) + ([self.elevationAt(row, col)] * len(neighbors)))
                rowResult.append(avg)
            newCellValues.append(rowResult)

        for row in range(self._rows):
            for col in range(self._cols):
                self._grid[row][col].elevation = newCellValues[row][col]
        
        return Map(self._grid, self)
    
    # TODO: level off the water as part of this
    # i.e. find pools and force them to level
    def evaporate(self):
        for row in range(self._rows):
            for col in range(self._cols):
                if self._grid[row][col].precipitation < MIN_WATER_LEVEL:
                    self._grid[row][col].precipitation = 0
        
        return Map(self._grid, self)
    
    def elevationAt(self, row, col):
        try:
            return self._grid[row][col].elevation
        except IndexError:
            # don't fully understand why this gets called with with 1 over the edge...
            return 0
    
    def precipitationAt(self, row, col):
        try:
            return self._grid[row][col].precipitation
        except IndexError:
            # don't fully understand why this gets called with with 1 over the edge...
            return 0

    def _neighbors(self, tile, depth = 1):
        #print(f"finding neighbors for [{tile.row}][{tile.col}]")
        positions = Map._relative_neighbor_coordinates(tile.row % 2)
        neighbors = []
        for (r, c) in positions:
            if tile.row + r >= 0 and tile.row + r < self._rows and tile.col + c >= 0 and tile.col + c < self._cols:
                neighbors.append(self._grid[tile.row + r][tile.col + c])

        #print(f"found {len(neighbors)} neighbors at depth 1")
        #global_depth = depth
        while depth > 1:
            # find neighbors of neighbors
            newNeighbors = []
            for n in neighbors:
                positions = Map._relative_neighbor_coordinates(n.row % 2)
                for (r, c) in positions:
                    if n.row + r >= 0 and n.row + r < self._rows and n.col + c >= 0 and n.col + c < self._cols and not (n.row + r == tile.row and n.col + c == tile.col) and self._grid[n.row + r][n.col + c] not in neighbors and self._grid[n.row + r][n.col + c] not in newNeighbors:
                        #print(f"found a new depth {global_depth - depth + 2} neighbor at [{n.row + r}][{n.col + c}]")
                        newNeighbors.append(self._grid[n.row + r][n.col + c])
            neighbors += newNeighbors
            
            depth -= 1

        return neighbors

    @staticmethod
    def _relative_neighbor_coordinates(offset):
        # offset is caused by alternating cell alignment in a hex grid
        left, right = -offset, -offset + 1
        return (
            (-1, left), (-1, right),
            (0, -1), (0, 1),
            (1, left), (1, right)
        )
    
    def _allActiveE(self):
        result = []
        for row_index, row in enumerate(self._grid):
            for col_index, _ in enumerate(row):
                tile = self._grid[row_index][col_index]
                if tile.activeE:
                    result.append(tile)
        return result
    
    def _allActiveP(self):
        result = []
        for row_index, row in enumerate(self._grid):
            for col_index, _ in enumerate(row):
                tile = self._grid[row_index][col_index]
                if tile.activeP:
                    result.append(tile)
        return result
    
    def _randomNeighbor(self, tile):
        neighbors = self._neighbors(tile)
        return np.random.choice(neighbors)


Sim().run()

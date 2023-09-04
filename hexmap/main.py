import numpy as np
import traceback

import hexHelper as helper
from tile import Tile

GRID_CONFIGURATION = {
    'cell_radius': 12,
    'rows': 70,
    'cols': 90,
    'crop_bigger_grids': True
}

DENSITY = 0.001 # initial truth density
SPEED = 200  # gif speed
TOTAL_TIME = 400 # number of generations

def randomSeed():
    result = []
    for row in range(GRID_CONFIGURATION['rows']):
        rowResult = []
        for col in range(GRID_CONFIGURATION['cols']):
            if np.random.random_sample() <= DENSITY:
                rowResult.append(Tile(row, col, True))
            else:
                rowResult.append(Tile(row, col, False))
        result.append(rowResult)
    return result

class Game:
    def __init__(self, seed=None, ticks=TOTAL_TIME):
        assert ticks >= 0

        self.number_of_ticks = ticks
        self.count = 0
        self.helper = helper.GridHelper(**GRID_CONFIGURATION)

        if not seed:
            seed = randomSeed()
        seed = self.helper.sanitize(seed)

        self.illustrator = Game.__set_up_illustrator(seed)
        self.generation = Generation(seed)

    def play(self):
        self.illustrator.draw(self.generation)
        print('------- I HAVE INITIALIZED ---------')
        print(f"Starting with {len(self.generation._allActive())} active tiles ")

        while self.count < self.number_of_ticks:
            self.generation = self.generation.tick()
            self.illustrator.draw(self.generation)
            self.count += 1

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
class Generation:
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
                result += f"[{self.valAt(row, col)}]"
            result += "\n"
        return result

    # the purpose of this is to return the truth value of each point
    # in general, all we need this to return is a status that we can convert into a color
    def tick(self):
        allActive = self._allActive()
        #for row_index, row in enumerate(self._grid):
        #    for col_index, _ in enumerate(row):
        #        self._grid[row_index % self._rows][col_index % self._cols].evolve()
        for tile in allActive:
            tile.evolve()
            tile.deactivate()
            n = self._randomNeighbor(tile)
            n.activate()

        return Generation(self._grid, self)
    
    def valAt(self, row, col):
        try:
            return self._grid[row][col].val
        except IndexError:
            # don't fully understand why this gets called with with 1 over the edge...
            return 0

    def _neighbors(self, cell):
        row, col = cell
        positions = Generation._relative_neighbor_coordinates(row % 2)
        neighbors = []
        for (r, c) in positions:
            if row + r >= 0 and row + r < self._rows and col + c >= 0 and col + c < self._cols:
                neighbors.append(self._grid[row + r][col + c])

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
    
    def _allActive(self):
        result = []
        for row_index, row in enumerate(self._grid):
            for col_index, _ in enumerate(row):
                tile = self._grid[row_index][col_index]
                if tile.active:
                    result.append(tile)
        return result
    
    def _randomNeighbor(self, tile):
        neighbors = self._neighbors((tile.row, tile.col))
        return np.random.choice(neighbors)


Game().play()

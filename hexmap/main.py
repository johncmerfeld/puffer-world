"""A variant of Conway's Game of Life on a hexagonal grid.

Rules: B2/S12
 - Dead cells with two live neighbours are born.
 - Live cells with one or two live neighbours survive.
 - All other live cells die.
"""
import os

import numpy as np

import hexHelper as helper
from tile import Tile

RULE_CONFIGURATION = {
    'b': (2,),  # birth
    's': (1, 2)  # survival
}

GRID_CONFIGURATION = {
    'cell_radius': 6,
    'rows': 70,
    'cols': 90,
    'crop_bigger_grids': True
}

# Colors taken from 'Nord' by Arctic Ice Studio
# https://git.io/nord
COLOR_CONFIGURATION = {
    'palette': 'dark_mode',
    'dark_mode': [
        (46, 52, 64),  # dead
        (89, 106, 152),  # dying
        (236, 239, 244),  # born
        (170, 189, 173)  # alive
    ],
    'light_mode': [
        (216, 222, 233),  # dead
        (236, 239, 244),  # dying
        (59, 66, 82),  # born
        (46, 52, 64)  # alive
    ]
}

DENSITY = 0.01 # initial truth density
SPEED = 100  # gif speed

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
    def __init__(self, seed=None, ticks=100):
        assert ticks >= 0

        self.number_of_ticks = ticks
        self.count = 0
        self.helper = helper.GridHelper(**GRID_CONFIGURATION)

        # FIXME: we're here, let's refactor these as objects
        if not seed:
            seed = randomSeed()
        # TODO: might not be picking up changes because I keep the initial seed within the boundaries
        seed = self.helper.sanitize(seed)

        self.illustrator = Game.__set_up_illustrator(seed)
        self.generation = Generation(seed)

    def play(self):
        self.illustrator.draw(self.generation)
        print('------- I HAVE INITIALIZED ---------')

        while self.count < self.number_of_ticks:
            print('------- TICKING... ---------')
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
        return helper.Illustrator(COLOR_CONFIGURATION, SPEED, **config)

# this is the evolution logic...
# I think it's also a full representation of the board at any given time
class Generation:
    def __init__(self, grid, previous=None):
        self._grid = grid
        self._previous = previous
        self._rows = len(self._grid)
        self._cols = len(self._grid[0])

    # the purpose of this is to return the truth value of each point
    # in general, all we need this to return is a status that we can convert into a color
    # FIXME: not true!! This updates the whole map. So they have to stay as Tiles!!
    def tick(self):
        newGrid = []
        # TODO: this is a very memory-inefficient way to do this but let's just see
        for row_index, row in enumerate(self._grid):
            rowResult = []
            for col_index, _ in enumerate(row):
                if self._is_born((row_index, col_index)) or self._survives((row_index, col_index)):
                    rowResult.append(Tile(row_index, col_index, True))
                else:
                    rowResult.append(Tile(row_index, col_index, False))        
            newGrid.append(rowResult)
        return Generation(newGrid, self)

    def is_alive(self, cell):
        row, col = cell
        return self._grid[row % self._rows][col % self._cols].val

    def was_alive(self, cell):
        if self._previous:
            return self._previous.is_alive(cell)
        else:
            return self.is_alive(cell)

    def _is_born(self, cell):
        livingNeighbors = [t.val for t in self._neighbours(cell)]
        return not self.is_alive(cell) \
               and sum(livingNeighbors) in RULE_CONFIGURATION.get('b')

    def _survives(self, cell):
        livingNeighbors = [t.val for t in self._neighbours(cell)]
        return self.is_alive(cell) \
               and sum(livingNeighbors) in RULE_CONFIGURATION.get('s')

    def _neighbours(self, cell):
        row, col = cell
        positions = Generation._relative_neighbour_coordinates(row % 2)

        neighbours = [
            self._grid[(row + r) % self._rows][(col + c) % self._cols]
            for (r, c) in positions
        ]

        return neighbours

    @staticmethod
    def _relative_neighbour_coordinates(offset):
        # offset is caused by alternating cell alignment in a hex grid
        left, right = -offset, -offset + 1
        return (
            (-1, left), (-1, right),
            (0, -1), (0, 1),
            (1, left), (1, right)
        )


Game().play()

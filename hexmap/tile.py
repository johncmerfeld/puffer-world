class Tile:

    def __init__(self, row, col, active):
        self.row = row
        self.col = col
        self.active = active
        self.val = 0

    # do I check here or at the board level?
    def evolve(self):
        #if self.active:
        self.val += 1
    
    def activate(self):
        self.active = True

    def deactivate(self):
        self.active = False
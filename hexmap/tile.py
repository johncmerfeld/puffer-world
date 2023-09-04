class Tile:

    def __init__(self, row, col, activeE, activeP):
        self.row = row
        self.col = col
        self.activeE = activeE
        self.activeP = activeP
        self.elevation = 0
        self.precipitation = 0
        self.nextWaterLevel = 0

    # do I check activation here or at the board level? for now, at the board level
    def elevate(self):
        #if self.active:
        self.elevation += 1

    def precipitate(self):
        self.precipitation += 100
        self.nextWaterLevel += 100
    
    def activateE(self):
        self.activeE = True

    def deactivateE(self):
        self.activeE = False

    def activateP(self):
        self.activeP = True

    def deactivateP(self):
        self.activeP = False

    def setPrecipitation(self, val):
        self.precipitation = val

    def setNextWaterLevel(self, val):
        self.nextWaterLevel = val

    def waterAdjustedElevation(self):
        return self.elevation + int(self.precipitation / 100)
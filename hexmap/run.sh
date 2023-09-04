#/usr/env/bin zsh

mv map.mp4 previous.mp4
python main.py
rm map.gif
open map.mp4

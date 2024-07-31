[![Demo Video]
(https://i.ytimg.com/vi/Kr9A4_cr0Kc/maxresdefault.jpg)]
(https://www.youtube.com/watch?v=Kr9A4_cr0Kc)

NOTE: If you have an older Java version, it may not work properly (I made this using Java 20).

How to play:
- Controls are listed in the about screen of the game
- Do not edit config.txt
- You can edit handling_settings.txt
- It is recommended to edit it according to the recommendations of the about menu, unless you want very fast controls
- All values must be positive integers
- ARR is how fast the piece moves left and right when holding down a key (in ms)
- DAS is how long it takes for the piece to start moving left or right when holding down a key (in ms) (how long you must hold down a key before the piece starts auto moving)
- SDF is how fast the piece falls when holding down the down key (in ms)
- Possible music types are "calm" and "battle" (without quotes, case-sensitive)
- You can only hold once per piece, to reset this, you must place the piece
- The Blitz score is increases based on the number of lines cleared at once (more lines = more points).
You also get points for combos (clearing lines consecutively)

Some small details / features that may be difficult to notice:
- The rotations have the SRS kicks implemented (https://harddrop.com/wiki/SRS).
This allows for more freedom when rotating pieces and moves such as T-Spin Triples (clearing 3 lines with a T-Spin)
- The backgrounds are randomised
- The sound that plays when clearing lines is different depending on how many lines you clear
- Some of the animations use an easing function (https://easings.net/)
- I have implemented an ARR, DAS and SDF system (https://tetris.wiki/DAS),
ARR is the speed of movement, while DAS is the initial delay. SDF is basically DAS / ARR for soft drops or down movement.
The settings can be set as low as zero (what most top tetris players use), which allows the game to be played at high speeds

List of bugs:
- The graphics may not work properly on all devices (the scale changes based on your computer settings. On windows, you must
change your scale to 100% in settings for the graphics to work properly)
- The movement animation for the buttons sometimes shakes, but this usually only happens if you try to make this happen (possibly due to race condition)
- The buttons sometimes do not play sound, this happens infrequently
- The movement of the board when starting a new game may sometimes not be in sync, but this is not very noticeable
- The stopwatch might break for times above 1 hour, but is it every unlikely that you will get a time above 1 hour
 (it should only take a couple of minutes at max to clear 40 lines)
- Text may look weird sometimes, but that is an issue with the font
- There may be some audio that plays when the sound setting is turned off (I think I fixed all of them, but I may have missed some)
- When playing the game multiple times without restarting, the performance starts to decrease. I have spent over an hour trying to locate the bug, but I have not been able to find it.
However, when I run the game with a profiler, it goes away for some reason.
- I am able to get it to run smoothly and without lag on my computer using the profiler mode of my IDE

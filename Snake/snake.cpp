/*
- - - - - - - - - - - - - - - - - - - - - -

Commands to compile and run:

    g++ -o snake snake.cpp -L/usr/X11R6/lib -lX11 -lstdc++
    ./snake

Note: the -L option and -lstdc++ may not be needed on some machines.
*/

#include <iostream>
#include <list>
#include <cstdlib>
#include <sys/time.h>
#include <math.h>
#include <stdio.h>
#include <unistd.h>
#include <vector>
#include <algorithm>
#include <string.h>
#include <sstream>
/*
 * Header files for X functions
 */
#include <X11/Xlib.h>
#include <X11/Xutil.h>

using namespace std;
 
/*
 * Global game state variables
 */
const int Border = 1;
const int BufferSize = 10;
const int FPS = 10;
const int width = 800;
const int height = 600;
const int blockSize = 10;
int heading = 2; // 1, 2, 3, 4 are up, right, down, left, respectively
vector<int> snake_pos_x;
vector<int> snake_pos_y;
bool running;
bool alive;
bool freeze;
bool moved = false;
int score = 0;
int speed = 5;
int fps_rate = 30;
int snake_speed;
unsigned long start;


/*
 * Information to draw on the window.
 */
struct XInfo {
	Display	 *display;
	int		 screen;
	Window	 window;
	GC		 gc[3];
	int		width;		// size of window
	int		height;
};


/*
 * Function to put out a message on error exits.
 */
void error( string str ) {
  cerr << str << endl;
  exit(0);
}


/*
 * An abstract class representing displayable things. 
 */

class Displayable {
	public:
		virtual void paint(XInfo &xinfo) = 0;
};

class Text : public Displayable {
public:
    virtual void paint(XInfo& xinfo) {
        XDrawImageString( xinfo.display, xinfo.window, xinfo.gc[0],
                          this->x, this->y, this->s.c_str(), this->s.length() );
    }

    Text(int x, int y, string s): x(x), y(y), s(s)  {}

private:
    int x;
    int y;
    string s; // string to show
};

class SideBar: public Displayable {
    public:
        virtual void paint(XInfo& xinfo){
            XDrawRectangle( xinfo.display, xinfo.window, xinfo.gc[0], 0, 0, 10, 250);
            XDrawRectangle( xinfo.display, xinfo.window, xinfo.gc[0], 0, 350, 10, 250);

            XDrawRectangle( xinfo.display, xinfo.window, xinfo.gc[0], 0, 590, 350, 10);
            XDrawRectangle( xinfo.display, xinfo.window, xinfo.gc[0], 450, 590, 350, 10);

            XDrawRectangle( xinfo.display, xinfo.window, xinfo.gc[0], 790, 0, 10, 250);
            XDrawRectangle( xinfo.display, xinfo.window, xinfo.gc[0], 790, 350, 10, 250);

            XDrawRectangle( xinfo.display, xinfo.window, xinfo.gc[0], 0, 0, 350, 10);
            XDrawRectangle( xinfo.display, xinfo.window, xinfo.gc[0], 450, 0, 350, 10);
            //middle obstacle
            XDrawRectangle( xinfo.display, xinfo.window, xinfo.gc[0], 300, 200, 200, 10);
            XDrawRectangle( xinfo.display, xinfo.window, xinfo.gc[0], 400, 200, 10, 200);
        }

        SideBar(){}
};

class Snake : public Displayable {
	public:
		virtual void paint(XInfo &xinfo) {
            for (int i = snake_pos_x.size()-1; i >= 0; i--){
                XFillRectangle(xinfo.display, xinfo.window, xinfo.gc[0], snake_pos_x[i], snake_pos_y[i], blockSize, blockSize);
            }
		}
		
		void move(XInfo &xinfo, int fruitx, int fruity) {
		    if (heading == 1){
		        if (x >= 350 && x <= 440 && y == 0){
		            y = 590;
		        } else {
		            y = snake_pos_y[0] - direction;
		        }
   		    }
		    else if (heading == 3){
                if (x >= 350 && x <= 440 && y >= 590){
                    y = 0;
                } else {
                    y = snake_pos_y[0] + direction;
                }
		    }
		    else if (heading == 2){
		        if (y <= 350 && y >= 250 && x >= 790){
		            x = 0;
		        } else {
		            x = snake_pos_x[0] + direction;
		        }
		    }
		    else if (heading == 4){
		        if (y <= 350 && y >= 250 && x == 0){
		            x = 790;
		        } else {
		            x = snake_pos_x[0] - direction;
                }
		    }

			if (x < 10 || x >= width - 10) {
			    if (y <= 240 || y >= 350){
                    alive = false;
                    return;
				}
			}

			if (y < 10 || y >= height - 10) {
			    if (x <= 340 || x >= 450){
                    alive = false;
                    return;
				}
			}

			if (x >= 300 && x <= 490 && y == 200){
                    alive = false;
                    return;
			}

            if (y >= 200 && y <= 390 && x == 400){
                    alive = false;
                    return;
            }

			for (int i = 0; i < snake_pos_x.size()-1; i++){
			    if (snake_pos_x[i] == x && snake_pos_y[i] == y){
			        alive = false;
			        return;
			    }
			}

            snake_pos_x.insert(snake_pos_x.begin(), x);
            snake_pos_y.insert(snake_pos_y.begin(), y);
            if (fruitx != x || fruity != y){
                snake_pos_x.pop_back();
                snake_pos_y.pop_back();
            } else{
                score ++;
                if (score % 10 == 0){
                    if (snake_speed <= 10) snake_speed = snake_speed + 5;
                    else snake_speed = snake_speed + 10;
                }
            }
		}

		void initialize_snake(){
            for (int i = 0; i < snake_length; i++){
                int x_pos = x + i * blockSize;
                snake_pos_x.insert(snake_pos_x.begin(), x_pos);
                snake_pos_y.insert(snake_pos_y.begin(), y);
            }
		};

		void reset_snake(Snake snake){
		    heading = 2;
		    snake_pos_x.clear();
		    snake_pos_y.clear();
		    direction = blockSize;
		    x = 100;
		    y = 450;
            snake_length = 3;
            initialize_snake();
		}

		int getX() {
			return x;
		}
		
		int getY() {
			return y;
		}

		Snake(float x, float y): x(x), y(y) {
			direction = blockSize;
            snake_length = 3;
            initialize_snake();
		}
	
	private:
		int x;
		int y;
		float direction;
		int snake_length;
};

class Fruit : public Displayable {
	public:
		virtual void paint(XInfo &xinfo) {
			XFillRectangle(xinfo.display, xinfo.window, xinfo.gc[0], x, y, blockSize, blockSize);
        }

        Fruit() {
            regenerate_fruit();
        }

        int shuffle_x(){
            int boundary = width / blockSize;
            x = (rand() % boundary) * blockSize;
        }

        int shuffle_y(){
            int boundary = height / blockSize;
            y = (rand() % boundary) * blockSize;
        }

        int getX() {
            return x;
        }

        int getY() {
            return y;
        }

        void regenerate(){
            shuffle_x();
            shuffle_y();
        }

        void regenerate_fruit(){
            regenerate();
            //make sure new fruit location is not on the snake or on score
            while (true){
                if (x == 0 || x == 790 || y == 0 || y == 590){ //avoid border
                    regenerate();
                    continue;
                }
                if ((x >= 300 && x <= 490 && y == 200) || (y >= 200 && y <= 390 && x == 400)){ // avoid middle obstacle
                    regenerate();
                    continue;
                }
                bool done = true;
                for (int i = 0; i < snake_pos_x.size(); i++){
                    if (snake_pos_x[i] == x && snake_pos_y[i] == y){
                        regenerate();
                        done = false;
                        break;
                    }
                }
                if (done) break;
            }
        }

        void get_fruit(){
            if (x == snake_pos_x[0] && y == snake_pos_y[0]){
                regenerate_fruit();
            }
        }

        void reset_fruit(Fruit fruit){
            regenerate_fruit();
        }

    private:
        int x;
        int y;
};


list<Displayable *> dList;           // list of Displayables
Snake snake(100, 450);
Fruit fruit;
SideBar sidebar;

/*
 * Initialize X and create a window
 */
void initX(int argc, char *argv[], XInfo &xInfo) {
	XSizeHints hints;
	unsigned long white, black;

   /*
	* Display opening uses the DISPLAY	environment variable.
	* It can go wrong if DISPLAY isn't set, or you don't have permission.
	*/	
	xInfo.display = XOpenDisplay( "" );
	if ( !xInfo.display )	{
		error( "Can't open display." );
	}
	
   /*
	* Find out some things about the display you're using.
	*/
	xInfo.screen = DefaultScreen( xInfo.display );

	white = XWhitePixel( xInfo.display, xInfo.screen );
	black = XBlackPixel( xInfo.display, xInfo.screen );

	hints.x = 100;
	hints.y = 100;
	hints.width = 800;
	hints.height = 600;
	hints.flags = PPosition | PSize;

	xInfo.window = XCreateSimpleWindow( 
		xInfo.display,				// display where window appears
		DefaultRootWindow( xInfo.display ), // window's parent in window tree
		hints.x, hints.y,			// upper left corner location
		hints.width, hints.height,	// size of the window
		Border,						// width of window's border
		black,						// window border colour
		white );					// window background colour
		
	XSetStandardProperties(
		xInfo.display,		// display containing the window
		xInfo.window,		// window whose properties are set
		"animation",		// window's title
		"Animate",			// icon's title
		None,				// pixmap for the icon
		argv, argc,			// applications command line args
		&hints );			// size hints for the window

	/* 
	 * Create Graphics Contexts
	 */
	int i = 0;
	xInfo.gc[i] = XCreateGC(xInfo.display, xInfo.window, 0, 0);
	XSetForeground(xInfo.display, xInfo.gc[i], BlackPixel(xInfo.display, xInfo.screen));
	XSetBackground(xInfo.display, xInfo.gc[i], WhitePixel(xInfo.display, xInfo.screen));
	XSetFillStyle(xInfo.display, xInfo.gc[i], FillSolid);
	XSetLineAttributes(xInfo.display, xInfo.gc[i],
	                     1, LineSolid, CapButt, JoinRound);

	XSelectInput(xInfo.display, xInfo.window, 
		ButtonPressMask | KeyPressMask | 
		PointerMotionMask | 
		EnterWindowMask | LeaveWindowMask |
		StructureNotifyMask);  // for resize events

	/*
	 * Put the window on the screen.
	 */
	XMapRaised( xInfo.display, xInfo.window );
	XFlush(xInfo.display);
}

/*
 * Function to repaint a display list
 */
void repaint( XInfo &xinfo) {
	list<Displayable *>::const_iterator begin = dList.begin();
	list<Displayable *>::const_iterator end = dList.end();

	XClearWindow( xinfo.display, xinfo.window );
	
	// get height and width of window (might have changed since last repaint)

	XWindowAttributes windowInfo;
/*
	XGetWindowAttributes(xinfo.display, xinfo.window, &windowInfo);
*/
	unsigned int height = windowInfo.height;
	unsigned int width = windowInfo.width;

	// big black rectangle to clear background
    
	// draw display list
	while( begin != end ) {
		Displayable *d = *begin;
		d->paint(xinfo);
		begin++;
	}
	XFlush( xinfo.display );
}

void handleKeyPress(XInfo &xinfo, XEvent &event) {
	KeySym key;
	char text[BufferSize];
	
	/*
	 * Exit when 'q' is typed.
	 * This is a simplified approach that does NOT use localization.
	 */
	int i = XLookupString( 
		(XKeyEvent *)&event, 	// the keyboard event
		text, 					// buffer when text will be written
		BufferSize, 			// size of the text buffer
		&key, 					// workstation-independent key symbol
		NULL );					// pointer to a composeStatus structure (unused)
	if ( i == 1) {
		printf("Got key press -- %c\n", text[0]);
		if (text[0] == 'q') {
			error("Terminating normally.");
		}
		if(!freeze){
            if (heading != 3 && text[0] == 'w' && moved) {
                heading = 1;
                moved = false;
            }
            else if (heading != 4 && text[0] == 'd' && moved) {
                heading = 2;
                moved = false;
            }
            else if (heading != 1 && text[0] == 's' && moved) {
                heading = 3;
                moved = false;
            }
            else if (heading != 2 && text[0] == 'a' && moved) {
                heading = 4;
                moved = false;
            }
            else if (text[0] == 'p'){
                freeze = true;
                string pause = "Game paused, press 'p' to resume";
                dList.push_back(new Text(250,300, pause));
                repaint(xinfo);
            }
        } else {
            if (text[0] == 'p'){
                freeze = false;
                dList.pop_back();

            }
        }

	}
}

string tostring(int i){
    ostringstream s;
    s << i;
    return s.str();
}

// get microseconds
unsigned long now() {
	timeval tv;
	gettimeofday(&tv, NULL);
	return tv.tv_sec * 1000000 + tv.tv_usec;
}

void handleAnimation(XInfo &xinfo, int inside) {
    /*
     * ADD YOUR OWN LOGIC
     * This method handles animation for different objects on the screen and readies the next frame before the screen is re-painted.
     */ 
	snake.move(xinfo, fruit.getX(), fruit.getY());

    //see if snake ate the fruit, then regenerate fruit
    fruit.get_fruit();
    moved = true;
}

void eventLoop(XInfo &xinfo, int fps_rate, int speed) {
	// Add stuff to paint to the display list
	dList.push_front(&snake);
    dList.push_front(&fruit);
    dList.push_front(&sidebar);
    snake_speed = speed + 5;
    string points = tostring(score);
    string score_board;

	XEvent event;
	unsigned long lastRepaint = 0;
	int inside = 0;
    start = now();
    while(alive){
        if (XPending(xinfo.display) > 0) {
            XNextEvent( xinfo.display, &event );
            cout << "event.type=" << event.type << "\n";
            switch( event.type ) {
                case KeyPress:
                    handleKeyPress(xinfo, event);
                    break;
                case EnterNotify:
                    inside = 1;
                    break;
                case LeaveNotify:
                    inside = 0;
                    break;
            }
        }
        usleep(1000000/fps_rate);
        unsigned long diff = now() - start;
        if (!freeze && (diff >= 1000000/snake_speed)) {
            handleAnimation(xinfo, inside);
            start = now();
        }
        points = tostring(score);
        score_board = "Score: "+ points;
        dList.push_front(new Text(10,600, score_board));
        repaint(xinfo);
        dList.pop_front();

	}
}

/*
 * Start executing here.
 *	 First initialize window.
 *	 Next loop responding to events.
 *	 Exit forcing window manager to clean up - cheesy, but easy.
 */

 void setup(XInfo &xinfo){
    string readme1 = "Welcome to Snake game! Here is how to play:";
    string readme2 = "use keys <W,A,S,D> to go up, down, left, right;";
    string readme3 = "eat fruit to increase your score, which is shown at left bottom corner;";
    string readme4 = "use the gap between borders to travel to the other side of the screen;";
    string readme5 = "the game is over if it eats itself or hit the border or the middle obstacle;";
    string readme6 = "press any of the keys <W,A,S,D> to start;";
    string readme7 = "press 'p' to pause the game and press 'q' to quit the game at any time";
    string userid = "Userid: sachen";
    string name = "Name: Siao Chen";
    XEvent event;
    KeySym key;
    char text[BufferSize];
    dList.push_back(new Text(200,200, name));
    dList.push_back(new Text(200,225, userid));
    dList.push_back(new Text(200,250, readme1));
    dList.push_back(new Text(200,275, readme2));
    dList.push_back(new Text(200,300, readme3));
    dList.push_back(new Text(200,325, readme4));
    dList.push_back(new Text(200,350, readme5));
    dList.push_back(new Text(200,375, readme6));
    dList.push_back(new Text(200,400, readme7));
    repaint(xinfo);
    while ( true ) {
        XNextEvent( xinfo.display, &event );
        if (event.type == KeyPress){
            int i = XLookupString((XKeyEvent*)&event, text, BufferSize, &key, 0 );
            if (key == 119 || key ==115 || key == 97 || key ==100){
                for (int i = 0; i < 9; i++){
                    dList.pop_back();
                }
                break;
            }
            else if (key == 113) {
                running = false;
                break;
            }
        }
        repaint(xinfo);
    }
 }

 void over(XInfo &xinfo){
    string points = tostring(score);
    string readme1 = "Game over, your score is: " + points;
    string readme2 = "would you want to play a new game?";
    string readme3 = "click 'r' to start a new game, 'q' to quit the game";

    XEvent event;
    KeySym key;
    char text[BufferSize];
    dList.push_back(new Text(200,225, readme1));
    dList.push_back(new Text(200,250, readme2));
    dList.push_back(new Text(200,275, readme3));
    repaint(xinfo);
    while ( true ) {
        XNextEvent( xinfo.display, &event );
        if (event.type == KeyPress){
            int i = XLookupString((XKeyEvent*)&event, text, BufferSize, &key, 0 );
            if (key == 114){
                dList.pop_back();
                dList.pop_back();
                dList.pop_back();
                snake.reset_snake(snake);
                fruit.reset_fruit(fruit);
                score = 0;
                break;
            }
            else if (key == 113){
                running = false;
                break;
            }
        }
        repaint(xinfo);
    }
 }

int main ( int argc, char *argv[] ) {
    srand(time(NULL));
	XInfo xInfo;
	running = true;
	initX(argc, argv, xInfo);

	if (argc == 3){
        fps_rate = atoi(argv[1]);
        speed = atoi(argv[2]);
        if (!(speed >= 1 && speed <= 10) || !(fps_rate >= 1 && fps_rate <= 100)){
            error("must enter speed between 1 to 10 or fps between 1 to 100");
        }
	}
	else if (argc == 2) error("can only accept two arguments or none");

    setup(xInfo);

    while (running){
        alive = true;
        freeze = false;
        eventLoop(xInfo, fps_rate, speed);
        over(xInfo);
    }
    XCloseDisplay(xInfo.display);
}

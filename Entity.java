/* Entity.java
 * An entity is any object that appears in the game.
 * It is responsible for resolving collisions and movement.
 */
 
 import java.awt.*;
 
 public abstract class Entity {

    // Java Note: the visibility modifier "protected"
    // allows the variable to be seen by this class,
    // any classes in the same package, and any subclasses
    // "private" - this class only
    // "public" - any class can see it
    
    protected double x;   // current x location
    protected double y;   // current y location
    protected Sprite sprite; // this entity's sprite
    protected double dx; // horizontal speed (px/s)  + -> right
    protected double dy; // vertical speed (px/s) + -> down
    protected int left;
    protected int right;
    protected int top;
    protected int bottom;
    protected TileMap map;
    protected Game game;
    
    // the following variables control animations for this entity
    private boolean animated = false;
	private String[] moveLeft = new String[1]; // stores an array of images for moving left
	private String[] moveRight = new String[1]; // stores an array of images for moving right
	private String[] jump = new String[1]; // stores an array of images for jumping
	private boolean isFacingRight = true; // true if sprite is facing right
	private int index = 0; // index of frame sprite is on
    
    private Rectangle me = new Rectangle(); // bounding rectangle of
                                            // this entity
    private Rectangle him = new Rectangle(); // bounding rect. of other
                                             // entities
                                             
    /* Constructor
     * input: reference to the image for this entity,
     *        initial x and y location to be drawn at
     */
     public Entity(String r, int newX, int newY, boolean animated) {
       x = newX;
       y = newY;
       if (animated) {
    	   this.animated = true;
    	   setAnimations(r);
       } else {
    	   sprite = (SpriteStore.get()).getSprite(r);
       }
     } // constructor
     
     /* determines and sets entity animation variables
      * direction of entity
      * selects correct image to use
      */
     private void setAnimations(String entityName) {
    	 
    	 // configure all moveLeft images
    	 for (int i = 0; i < moveLeft.length; i++) {
    		 moveLeft[i] = "sprites/" + entityName + "L" + i + ".png";
    	 }
    	 
    	 // configure all moveRight images
    	 for (int i = 0; i < moveRight.length; i++) {
    		 moveRight[i] = "sprites/" + entityName + "R" + i + ".png";
    	 }
    	 
    	// configure all moveUp images
    	 for (int i = 0; i < jump.length; i++) {
    		 jump[i] = "sprites/" + entityName + "U" + i + ".png";
    	 }
    	 sprite = (SpriteStore.get()).getSprite(moveRight[0]);
     }
    
     /* Takes an entity state (jumping, moving, attacking) and
      * changes the sprite to match the given animation.
      * 
      */
     public void updateAnimations() {
    	 if (animated) {
             // update direction
    		 if (dy < 0) {
    			 sprite = (SpriteStore.get()).getSprite(jump[0]);
    		 } else {
    			 if (dx < 0) {
        			 // isFacingRight = false;
        			 sprite = (SpriteStore.get()).getSprite(moveLeft[0]);
        		 } else if (dx > 0) {
        			 // isFacingRight = true;
        			 sprite = (SpriteStore.get()).getSprite(moveRight[0]);
        		 } 
    		 }
    	 }
     }

     /* move
      * input: delta - the amount of time passed in ms
      * output: none
      * purpose: after a certain amout of time has passed,
      *          update the location as well as the animation
      */
     public void move(long delta) {
    	 
    	 // check if it'll hit a platform
    	 left = (int) x;
         right = left + sprite.getWidth();
         top = (int) y;
         bottom = top + sprite.getHeight();
         if (dx < 0 && x < 0) {
             dx = 0;
         } else if (dx > 0 && x > 950) {
            dx = 0;
         } else if (dx < 0 && isTileLeft()) {
             dx = 0;
         } else if (dx > 0 && isTileRight()) {
             dx = 0;
         }
         if (dy < 0 && isTileAbove()) {
         	dy = 0;
         	game.stopJumping();
         } else if (isTileBelow() && !game.getJumping()) {
             dy = 0;
         }
     
		 // update location of entity based ov move speeds
		 x += (delta * dx) / 1000;
		 y += (delta * dy) / 1000;
     } // move

     // get and set velocities
     public void setHorizontalMovement(double newDX) {
       dx = newDX;
     } // setHorizontalMovement

     public void setVerticalMovement(double newDY) {
       dy = newDY;
     } // setVerticalMovement

     public double getHorizontalMovement() {
       return dx;
     } // getHorizontalMovement

     public double getVerticalMovement() {
       return dy;
     } // getVerticalMovement

     // get position
     public int getX() {
       return (int) x;
     } // getX

     public int getY() {
       return (int) y;
     } // getY
     
     public int getHeight() {
    	 return sprite.getHeight();
     }
     
     protected abstract boolean isTileAbove();
     
     public abstract boolean isTileBelow();
     
     protected abstract boolean isTileLeft();
     
     protected abstract boolean isTileRight();

    /*
     * Draw this entity to the graphics object provided at (x,y)
     */
     public void draw (Graphics g, double translateY) {
    	 sprite.draw(g,(int)x,(int) (y + translateY));
     }  // draw
     
    /* Do the logic associated with this entity.  This method
     * will be called periodically based on game events.
     */
     public void doLogic() {}
     
     /* collidesWith
      * input: the other entity to check collision against
      * output: true if entities collide
      * purpose: check if this entity collides with the other.
      */
     public boolean collidesWith(Entity other) {
       me.setBounds((int)x, (int)y, sprite.getWidth(), sprite.getHeight());
       him.setBounds(other.getX(), other.getY(), 
                     other.sprite.getWidth(), other.sprite.getHeight());
       return me.intersects(him);
     } // collidesWith
     
     /* collidedWith
      * input: the entity with which this has collided
      * purpose: notification that this entity collided with another
      * Note: abstract methods must be implemented by any class
      *       that extends this class
      */
      public abstract void collidedWith(Entity other);

 } // Entity class
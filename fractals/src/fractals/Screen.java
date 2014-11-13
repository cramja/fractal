package fractals;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;

import fractals.parallel.PFractalController;
import fractals.precision.ApfloatFractalController;

public class Screen extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;

	private static final int width = 500;
	private static final int height = 500;

	public boolean running = false;
	private Thread thread;
	private Mouse mouse;
	private Keyboard keyboard;

	private JFrame frame;
	private BufferedImage image = new BufferedImage(width, height,
			BufferedImage.TYPE_INT_RGB);
	private int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer())
			.getData();

	private FractalController fc = new PFractalController();
	
	public Screen() {
		Dimension dim = new Dimension(width, height);
		setPreferredSize(dim);
		keyboard = new Keyboard();
		mouse = new Mouse();
		addMouseListener(mouse);
		addMouseMotionListener(mouse);
		addKeyListener(keyboard);
		frame = new JFrame();
	}

	public synchronized void start() {
		running = true;
		thread = new Thread(this, "Display");
		thread.start();
	}

	public synchronized void stop() {
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			System.err.println(e.getMessage() + "\n");
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		long lastTime = System.nanoTime(); // for logic control
		final double ns = Math.pow(10, 9) / 60; // to seconds
		long timer = System.currentTimeMillis();
		double delta = 0; // in seconds
		int updates = 0;
		int frames = 0;
		requestFocus(); // method to get the canvas in focus
		while (running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns; // how many seconds have passed,
											// logic
			lastTime = now;
			while (delta >= 1) {
				update(); // update a number of times to "catch up"
				updates++;
				delta--;
			}
			render(); // renders a new frame
			frames++;
			if (System.currentTimeMillis() - timer > 1000) {
				frame.setTitle("@ " + frames + " fps");
				timer += 1000;
				updates = 0;
				frames = 0;
			}
		}
		stop();
	}

	public void update() {
		keyboard.update();
		if (keyboard.down) {
			fc.move(Direction.DOWN);
		}
		if (keyboard.left) {
			fc.move(Direction.LEFT);
		}
		if (keyboard.right) {
			fc.move(Direction.RIGHT);
		}
		if (keyboard.up) {
			fc.move(Direction.UP);
		}
		if(keyboard.plus){
			fc.increaseZoom();
		}
		if(keyboard.minus){
			fc.decreaseZoom();
		}
		if(keyboard.g_key){
			//fc.renderHiRes(width, height);
		}
	}

	public void render() {
		BufferStrategy bufferStrategy = getBufferStrategy();
		if (bufferStrategy == null) {
			createBufferStrategy(3); // triple buff
			return;
		}
		
		int[][] img = fc.getImage();
		int f_width = img.length;
		int f_height = img[0].length;
		double xr = (double)f_width/(double)width;
		double yr = (double)f_height/(double)height;
		for (int i = 0; i < pixels.length; i++) {
			int xx = (int) ((i%width)*xr);
			int yy = (int) ((i/height)*yr);
			pixels[i] = img[xx][yy];
		}
		
		Graphics g = bufferStrategy.getDrawGraphics();
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
		g.dispose();
		bufferStrategy.show(); // fill the screen with latest buffer
	}

	public static void main(String[] args) {
		Screen screen = new Screen();
		screen.frame.setResizable(false);
		screen.frame.setTitle("Fractals");
		screen.frame.add(screen);
		screen.frame.pack();
		screen.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		screen.frame.setLocationRelativeTo(null);
		screen.frame.setVisible(true);

		screen.start();
	}
}

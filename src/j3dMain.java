
import java.awt.BorderLayout;

import java.awt.GraphicsConfiguration;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.swing.JFrame;
import javax.vecmath.Point3d;

import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.Viewer;
import com.sun.j3d.utils.universe.ViewingPlatform;

@SuppressWarnings("serial")
public class j3dMain extends JFrame implements KeyListener{

	public static final int LOADSET = 0;
	public static final int LOADHILLS = 1;
	
	private SimpleUniverse u;
	private BranchGroup g;
	private boolean running;
	private Viewer viewer;
	private View view;
	private ViewingPlatform viewP;
	private j3dPerson player;
	@SuppressWarnings("unused")
	private static Transform3D viewt3d;
	@SuppressWarnings("unused")
	private static TransformGroup viewTG;
	private int xDim,yDim,zDim;
	private int loadType;
	private boolean thirdPerson;
	
	public j3dMain(int x, int y, int z, int loadType,boolean thirdPerson) throws Exception{
		
		super();
		xDim = x;
		yDim = y;
		zDim = z;
		this.loadType = loadType;
		this.thirdPerson = thirdPerson;
		running = true;
		Thread t1 = new Thread(new running());
		t1.start();
		init();
	}
	
	public void init() throws Exception{
		
		getContentPane().setLayout(new BorderLayout());
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		Canvas3D canvas = new Canvas3D(config);
		getContentPane().add(canvas, BorderLayout.CENTER);
		
		
		viewer = new Viewer(canvas);
		view = viewer.getView();
		view.setBackClipDistance(100);
		
		
		viewP = new ViewingPlatform();
		viewP.getViewPlatform().setActivationRadius(300f);
		
		viewTG = viewP.getViewPlatformTransform();
		viewt3d = new Transform3D();
		BufferedImage bg1= ImageIO.read(new File(getClass().getResource("res/stars.jpg").getFile()));
		ImageComponent2D img2d = new ImageComponent2D(1,bg1);
		Background back = new Background(img2d);
		
		BoundingSphere bgSphere = new BoundingSphere(new Point3d(0,0,0), 10000f);
		back.setApplicationBounds(bgSphere);
		
		viewP.addChild(back);
		
		u = new SimpleUniverse(viewP,viewer);
		u.getCanvas().addKeyListener(this);
		g = new BranchGroup();
		
		j3dLoadMap map = new j3dLoadMap();
		if(loadType == LOADHILLS){
			map.loadHills(xDim,yDim,zDim, 25, 10, 5, 1, 3, 3, 3, 1, 1, 1, 1, g);
		}else{
			map.loadSet(xDim,yDim,zDim,g);
		}
		player = new j3dPerson(viewP,map,xDim,yDim,zDim,thirdPerson,g);
		u.addBranchGraph(g);
		
	}
	
	public class running implements Runnable{
		public void run(){
			while(running){
				try{
					Thread.sleep(30);
					player.update();
				}
				catch(Exception e){
					
				}
			}
		}
	}
	public static void setViewt3d(Transform3D newt3d){
		viewt3d = newt3d;
	}
	
	public static void setViewTG(TransformGroup newTG){
		viewTG = newTG;
	}
	
	
	public j3dPerson getPerson(){return player;}
	
	public static void main(String[] args) throws Exception {
		//j3dMain frame = new j3dMain(50,50,50,j3dMain.LOADHILLS,true);
		j3dMain frame = new j3dMain(30,3,16,j3dMain.LOADSET,false);
		frame.setSize(500,500);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		if(arg0.getKeyCode() ==KeyEvent.VK_0){
			System.exit(EXIT_ON_CLOSE);
		} 

		player.keyPressed(arg0);
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		player.keyReleased(arg0);
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {}

}

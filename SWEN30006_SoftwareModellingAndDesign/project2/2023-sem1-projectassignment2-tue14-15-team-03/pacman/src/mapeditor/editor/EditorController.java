/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */
package src.mapeditor.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import src.mapeditor.checker.CheckerFactory;
import src.mapeditor.UI.View;
import src.game.GameFacade;
import src.mapeditor.grid.Camera;
import src.mapeditor.grid.GridCamera;
import src.mapeditor.grid.GridModel;
import src.mapeditor.UI.GridView;

import src.utility.XMLParser;

/**
 * Controller of the application.
 * 
 * @author Daniel "MaTachi" Jonsson
 * @version 1
 * @since v0.0.5
 * 
 */
public class EditorController implements ActionListener, GUIInformation {

	/**
	 * The model of the map editor.
	 */
	private static EditorController instance;
	private GridModel model;
	private Tile selectedTile;
	private Camera camera;

	private List<Tile> tiles;

	private GridView grid;
	private View view;
	private int gridWidth = Constants.mapWidth;
	private int gridHeight = Constants.mapHeight;
	private EditorState state;

	/**
	 * Construct the controller.
	 */
	private EditorController() {
		this.tiles = TileManager.getTilesFromFolder();
	}
	public static EditorController getInstance() {
		if (instance == null) {
			instance = new EditorController();
		}
		return instance;
	}

	public void startEditor() {
		this.model = new GridModel(Constants.mapWidth, Constants.mapHeight, tiles.get(0).getCharacter());
		init(Constants.mapWidth, Constants.mapHeight);
	}

	public void startEditor(GridModel targetModel) {
		this.model = targetModel;
		init(targetModel.getWidth(), targetModel.getHeight());
		grid.redrawGrid();
	}

	public void init(int width, int height) {
		this.state = EditorState.EDIT;
		this.camera = new GridCamera(model, Constants.gridWidth,
				Constants.gridHeight);
		grid = new GridView(this, camera, tiles); // Every tile is
													          // 30x30 pixels
		this.view = new View(this, camera, grid, tiles);
	}

	/**
	 * Different commands that comes from the view.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		for (Tile t : tiles) {
			if (e.getActionCommand().equals(
					Character.toString(t.getCharacter()))) {
				selectedTile = t;
				this.state = EditorState.EDIT;
				break;
			}
		}
		if (e.getActionCommand().equals("flipGrid")) {
			// view.flipGrid();
		} else if (e.getActionCommand().equals("save")) {
			saveFile();
		} else if (e.getActionCommand().equals("load")) {
			loadFile();
		} else if (e.getActionCommand().equals("update")) {
			updateGrid(gridWidth, gridHeight);
			model = new GridModel(gridWidth, gridHeight, tiles.get(0).getCharacter());
		} else if (e.getActionCommand().equals("start_game")) {
			startGame();
		}
	}

	public void updateGrid(int width, int height) {
		view.close();
		init(width, height);
	}

	public DocumentListener updateSizeFields = new DocumentListener() {

		public void changedUpdate(DocumentEvent e) {
			gridWidth = view.getWidth();
			gridHeight = view.getHeight();
		}

		public void removeUpdate(DocumentEvent e) {
			gridWidth = view.getWidth();
			gridHeight = view.getHeight();
		}

		public void insertUpdate(DocumentEvent e) {
			gridWidth = view.getWidth();
			gridHeight = view.getHeight();
		}
	};

	private void saveFile() {
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"xml files", "xml");
		chooser.setFileFilter(filter);
		File workingDirectory = new File(System.getProperty("user.dir"));
		chooser.setCurrentDirectory(workingDirectory);

		int returnVal = chooser.showSaveDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			try {
				File file = chooser.getSelectedFile();
				XMLParser.modelToXML(model, file);
				if (CheckerFactory.getInstance().getLevelChecker().check(model, file)) {
					state = EditorState.TESTABLE;
				}
			}  catch (FileNotFoundException e1) {
				JOptionPane.showMessageDialog(null, "Invalid file!", "error",
					JOptionPane.ERROR_MESSAGE);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void loadFile() {
		try {
			JFileChooser chooser = new JFileChooser();
			File selectedFile;
			File workingDirectory = new File(System.getProperty("user.dir"));
			chooser.setCurrentDirectory(workingDirectory);

			int returnVal = chooser.showOpenDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				selectedFile = chooser.getSelectedFile();
				if (selectedFile.canRead() && selectedFile.exists()) {
					model = XMLParser.readFileToModel(selectedFile);
					CheckerFactory.getInstance().getLevelChecker().check(model, selectedFile);
					updateGrid(model.getWidth(), model.getHeight());

					grid.redrawGrid();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void startGame() {
		if (state != EditorState.TESTABLE) {
			JOptionPane.showMessageDialog(null, "Map was not saved or passed level checking", "Map cannot be tested",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		Runnable testRunnable = new Runnable() {
			public void run() {
				state = EditorState.TEST;
				GameFacade gameFacade = new GameFacade(model);
				gameFacade.runGames();
				state = EditorState.TESTABLE;
			}
		};

		new Thread(testRunnable).start();

	}

	public void setState(EditorState state) {
		this.state = state;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Tile getSelectedTile() {
		return selectedTile;
	}
}

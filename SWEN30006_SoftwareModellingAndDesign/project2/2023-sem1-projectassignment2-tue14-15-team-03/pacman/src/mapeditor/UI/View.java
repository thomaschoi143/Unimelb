package src.mapeditor.UI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

import src.mapeditor.editor.Constants;
import src.mapeditor.editor.EditorController;
import src.mapeditor.editor.Tile;
import src.mapeditor.grid.Camera;


/**
 * The view of the application.
 * 
 * @author Daniel "MaTachi" Jonsson
 * @version 1
 * @since v0.0.5
 * 
 */
public class View {

	/**
	 * The JFrame.
	 */
	private JFrame frame;

	/**
	 * Settings to the right.
	 */
	// private JButton showGridButton;

	// private boolean showingGrid;

	private JTextField txtWidth;
	private JTextField txtHeight;

	/**
	 * Constructs the View.
	 * 
	 * @param editorController
	 *            The controller.
	 * @param model
	 *            The model.
	 */
	public View(EditorController editorController, Camera camera, JPanel grid,
				List<? extends Tile> tiles) {

		// showingGrid = true;

		grid.setPreferredSize(new Dimension(Constants.gridWidth
				* Constants.tileWidth, Constants.gridHeight
				* Constants.tileHeight));

		/** Create the bottom panel. */
		JPanel palette = new JPanel(new FlowLayout());
		for (Tile t : tiles) {
			JButton button = new JButton();
			button.setPreferredSize(new Dimension(Constants.tileWidth,
					Constants.tileHeight));
			button.setIcon(t.getIcon());
			button.addActionListener(editorController);
			button.setActionCommand(Character.toString(t.getCharacter()));
			palette.add(button);
		}

		/** Create the right panel. */
		// showGridButton = new JButton("Hide grid");
		// showGridButton.addActionListener(controller);
		// showGridButton.setActionCommand("flipGrid");

		JButton saveButton = new JButton("Save");
		saveButton.addActionListener(editorController);
		saveButton.setActionCommand("save");

		JButton loadButton = new JButton("Load");
		loadButton.addActionListener(editorController);
		loadButton.setActionCommand("load");

		JButton startGameButton = new JButton("Start Game");
		startGameButton.addActionListener(editorController);
		startGameButton.setActionCommand("start_game");

		JPanel right = new JPanel();
		right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
		Border border = BorderFactory.createEmptyBorder(10, 10, 10, 10);
		right.setBorder(border);
		// right.add(showGridButton);
		right.add(saveButton);
		right.add(loadButton);
		right.add(startGameButton);

		/** The top panel, that shows coordinates and stuff. */
		CameraInformationLabel cameraInformationLabel = new CameraInformationLabel(
				camera);
		GridMouseInformationLabel mouseInformationLabel = new GridMouseInformationLabel(
				grid);

		JLabel lblWidth = new JLabel("Width(min:32):");
		JLabel lblHeight = new JLabel("Height(min:20):");

		txtWidth = new JTextField(camera.getModelWidth() + "", 3);
		txtWidth.getDocument().addDocumentListener(editorController.updateSizeFields);
		txtWidth.setEnabled(false);
		txtHeight = new JTextField(camera.getModelHeight() + "", 3);
		txtHeight.getDocument()
				.addDocumentListener(editorController.updateSizeFields);
		txtHeight.setEnabled(false);
		JButton updateSize = new JButton("Reset");
		updateSize.addActionListener(editorController);
		updateSize.setActionCommand("update");

		JPanel top = new JPanel();
		top.setLayout(new FlowLayout(FlowLayout.LEFT));
		top.setBorder(border);
		top.add(cameraInformationLabel);
		top.add(mouseInformationLabel);
		top.add(lblWidth);
		top.add(txtWidth);
		top.add(lblHeight);
		top.add(txtHeight);
		top.add(updateSize);

		JPanel layout = new JPanel(new BorderLayout());
		JPanel test = new JPanel(new BorderLayout());
		test.add(top, BorderLayout.NORTH);
		test.add(grid, BorderLayout.WEST);
		test.add(right, BorderLayout.CENTER);
		layout.add(test, BorderLayout.NORTH);
		layout.add(palette, BorderLayout.CENTER);

		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Map Editor");
		frame.add(layout);
		frame.pack();
		frame.setVisible(true);
	}

	public int getWidth() {
		String value = txtWidth.getText();
		if (value.equals("")) {
			value = Constants.mapWidth + "";
		}
		return Integer.parseInt(value);
	}

	public int getHeight() {
		String value = txtHeight.getText();
		if (value.equals("")) {
			value = Constants.mapWidth + "";
		}
		return Integer.parseInt(value);
	}
	
	public void setSize(int width, int height){
		txtWidth.setText(width+"");
		txtHeight.setText(height+"");
	}

	public void close() {
		frame.dispose();
	}
	// /**
	// * Flip the grid on or off.
	// */
	// public void flipGrid() {
	// this.showingGrid = !this.showingGrid;
	// if (!showingGrid) {
	// showGridButton.setText("Show grid");
	// for (int y = 0; y < 20; y++) {
	// for (int x = 0; x < 32; x++) {
	// map[y][x].flipGrid();
	// }
	// }
	// } else {
	// showGridButton.setText("Hide grid");
	// for (int y = 0; y < 20; y++) {
	// for (int x = 0; x < 32; x++) {
	// map[y][x].flipGrid();
	// }
	// }
	// }
	// frame.repaint();
	// }
}

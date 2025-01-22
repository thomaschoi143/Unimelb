/* BasicGUI.java
   Author: Thomas Choi 1202247 */

package shared.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.*;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.FormSpecs;
import shared.host.Host;
import shared.host.HostProfile;
import shared.whiteboard.*;

public class BasicGUI {

	private JFrame frame;
	private JTextField statusInput;
	private JTextField textInput;
	private Host host;
	private JList peersList;
	private DefaultListModel<HostProfile> peersModel = new DefaultListModel<>();
	private static final int HEIGHT = 530;
	private static final int WIDTH = 1000;
	private final static Color DISABLE = new Color(199, 199, 199, 255);

	public BasicGUI(String title, Host host) {
		this.host = host;
		host.setGui(this);
		initialize(title);
	}

	private void initialize(String title) {
		frame = new JFrame();
		frame.setBounds(100, 100, WIDTH, HEIGHT);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(30dlu;default)"),
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(94dlu;default)"),
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(80dlu;default)"),
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(69dlu;default)"),
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(125dlu;default)"),
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(90dlu;default):grow"),},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(205dlu;default)"),
				FormSpecs.RELATED_GAP_ROWSPEC,}));
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setTitle(title);

		JLabel actionLabel = new JLabel("Action");
		frame.getContentPane().add(actionLabel, "2, 2, right, default");

		JComboBox<ActionOption> actionSelect = new JComboBox<>();
		for (Map.Entry<String, ActionCode> entry: WhiteBoard.ACTIONS.entrySet()) {
			ActionOption option = new ActionOption(entry.getKey(), entry.getValue());
			actionSelect.addItem(option);
			if (option.getName().equals("Free")) {
				actionSelect.setSelectedItem(option);
			}
		}
		actionSelect.setRenderer(new ObjectListCellRenderer());
		frame.getContentPane().add(actionSelect, "4, 2, fill, default");

		JLabel managerMessageLabel = new JLabel("Status");
		frame.getContentPane().add(managerMessageLabel, "6, 2, right, default");
		
		JLabel usernameLabel = new JLabel("Username");
		frame.getContentPane().add(usernameLabel, "6, 4, right, default");

		JButton quitButton = new JButton("Quit");
		frame.getContentPane().add(quitButton, "12, 2");

		JLabel colorLabel = new JLabel("Color");
		frame.getContentPane().add(colorLabel, "2, 4, right, default");

		JComboBox<ColorOption> colorSelect = new JComboBox<>();
		for (Map.Entry<String, Color> entry : WhiteBoard.COLORS.entrySet()) {
			ColorOption option = new ColorOption(entry.getKey(), entry.getValue());
			colorSelect.addItem(option);
			if (option.getName().equals("Black")) {
				colorSelect.setSelectedItem(option);
			}
		}
		colorSelect.setRenderer(new ObjectListCellRenderer());
		frame.getContentPane().add(colorSelect, "4, 4, fill, default");

		statusInput = new JTextField();
		frame.getContentPane().add(statusInput, "8, 2, 4, 1, fill, default");
		
		JTextField usernameInput = new JTextField();
		usernameInput.setEditable(false);
		usernameInput.setText(host.getUsername());
		frame.getContentPane().add(usernameInput, "8, 4, 4, 1, fill, default");
		
		JLabel textLabel = new JLabel("Text");
		frame.getContentPane().add(textLabel, "2, 6, right, default");

		textInput = new JTextField();
		textInput.setEnabled(false);
		textInput.setBackground(DISABLE);
		frame.getContentPane().add(textInput, "4, 6, fill, default");

		JLabel onlinePeersLabel = new JLabel("Online peers");
		frame.getContentPane().add(onlinePeersLabel, "12, 6");

		peersList = new JList<>(peersModel);
		peersList.setCellRenderer(new JListCellRenderer());
		JScrollPane peersListScroll = new JScrollPane (peersList,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		frame.getContentPane().add(peersListScroll, "12, 8, 1, 1, fill, fill");

		// chatroom
		frame.getContentPane().add(host.getChatPanel().getPanel(), "10, 8, fill, fill");

		// listeners
		actionSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ActionCode actionCode = ((ActionOption)actionSelect.getSelectedItem()).getValue();
				if (actionCode == ActionCode.TEXT) {
					textInput.setEnabled(true);
					textInput.setBackground(Color.WHITE);
				} else {
					textInput.setEnabled(false);
					textInput.setBackground(DISABLE);
					textInput.setText("");
				}
				host.getWhiteBoard().setAction(actionCode);
			}
		});

		colorSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Color color = ((ColorOption)colorSelect.getSelectedItem()).getValue();
				host.getWhiteBoard().setColor(color);
			}
		});

		quitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				host.broadcastQuit();
			}
		});

		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				host.broadcastQuit();
			}
		});

	}

	public boolean askDialog(String question, String title) {
		return JOptionPane.showConfirmDialog(frame, question, title, JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
	}

	public void notificationDialog(String message, String title) {
		JOptionPane.showConfirmDialog(frame, message, title, JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE);
	}

	public void putWhiteBoard(WhiteBoard whiteBoard) {
		frame.getContentPane().add(whiteBoard, "2, 8, 7, 1, fill, fill");
		frame.revalidate();
	}

	public String getText() {
		return textInput.getText();
	}

	public JFrame getFrame() {
		return frame;
	}

	public JTextField getStatusInput() {
		return statusInput;
	}

	public DefaultListModel<HostProfile> getPeersModel() {
		return peersModel;
	}

	public JList getPeersList() {
		return peersList;
	}

}

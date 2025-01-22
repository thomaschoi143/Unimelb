/* ServerGUI.java
   Author: Thomas Choi 1202247 */

package server.gui;

import javax.swing.*;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.FormSpecs;
import server.ClientProfile;
import server.DictionaryServer;
import server.StatusCode;
import server.WordDictionary;
import shared.Util;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ServerGUI {

	private JFrame frame;
	private JTextField portInput;
	private JTextField filepathInput;
	private JTextField clientsNum;
	private JButton quitButton;
	private JButton saveButton;
	private JButton restartButton;
	private DictionaryServer server;
	private JTextArea logArea;
	private DefaultListModel<ClientProfile> clientsModel = new DefaultListModel<>();
	private final static int WIDTH = 660;
	private final static int HEIGHT = 500;

	/**
	 * Create the application.
	 */
	public ServerGUI(String portStr, String filepath) {
		initialize(portStr, filepath);
	}

	private void initialize(String portStr, String filepath) {
		frame = new JFrame();
		frame.setBounds(100, 100, WIDTH, HEIGHT);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(47dlu;default)"),
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(154dlu;default)"),
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(95dlu;default)"),},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(209dlu;default)"),}));
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setTitle("Dictionary Server");
		
		JLabel portLabel = new JLabel("Port");
		frame.getContentPane().add(portLabel, "2, 2, right, default");

		portInput = new JTextField();
		portInput.setText(portStr);
		frame.getContentPane().add(portInput, "4, 2, fill, default");
		portInput.setColumns(10);
		
		restartButton = new JButton("Restart");
		restartButton.setEnabled(false);
		frame.getContentPane().add(restartButton, "6, 2");
		
		JLabel filepathLabel = new JLabel("Dictionary Filepath");
		frame.getContentPane().add(filepathLabel, "2, 4, right, default");

		filepathInput = new JTextField();
		filepathInput.setText(filepath);
		frame.getContentPane().add(filepathInput, "4, 4, fill, default");
		filepathInput.setColumns(10);
		
		quitButton = new JButton("Save Dictionary & Quit");
		frame.getContentPane().add(quitButton, "6, 4");
		
		JLabel clientsNumLabel = new JLabel("Current Number of Clients");
		frame.getContentPane().add(clientsNumLabel, "2, 6, right, default");

		clientsNum = new JTextField();
		clientsNum.setEditable(false);
		clientsNum.setText("0");
		frame.getContentPane().add(clientsNum, "4, 6, fill, default");
		clientsNum.setColumns(10);
		
		saveButton = new JButton("Save Dictionary");
		frame.getContentPane().add(saveButton, "6, 6");
		
		JLabel systemLogLabel = new JLabel("System Log");
		frame.getContentPane().add(systemLogLabel, "2, 8");
		
		JLabel clientsAreaLabel = new JLabel("Clients (click to kick)");
		frame.getContentPane().add(clientsAreaLabel, "6, 8");
		
		logArea = new JTextArea();
		logArea.setEditable(false);
		logArea.setLineWrap(true);
		JScrollPane logAreaScroll = new JScrollPane (logArea,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		frame.getContentPane().add(logAreaScroll, "2, 10, 3, 1, fill, fill");

		JList clientList = new JList<>(clientsModel);
		clientList.setCellRenderer(new ClientCellRenderer());
		JScrollPane clientListScroll = new JScrollPane (clientList,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		frame.getContentPane().add(clientListScroll, "6, 10, fill, fill");

		// Logic
		startServer(portStr, filepath);

		// Action Listeners
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				if (JOptionPane.showConfirmDialog(frame,
						"Are you sure you want to close this window? The dictionary will not be saved.", "Close Window?",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
					System.exit(0);
				}
			}
		});

		restartButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (restartButton.isEnabled()) {
					startServer(portInput.getText(), filepathInput.getText());
				}
			}
		});

		quitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (quitButton.isEnabled()) {
					shutdownServer();
				}
			}
		});

		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (saveButton.isEnabled()) {
					WordDictionary dict = server.getDict();
					synchronized (dict) {
						dict.saveDictionary(filepathInput.getText());
					}
				}
			}
		});

		clientList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				int index = clientList.locationToIndex(evt.getPoint());
				if (index >= 0) {
					ClientProfile client = clientsModel.getElementAt(index);
					if (JOptionPane.showConfirmDialog(frame,
							"Are you sure you want to close the connection with client " + Util.getIdStr(client.getId()), "Close Client?",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
						server.kickClient(client);
					}
				}
			}
		});
	}

	private void startServer(String portStr, String filepath) {
		server = new DictionaryServer(this);
		StatusCode response = server.startServer(portStr, filepath);
		if (response != StatusCode.RUNNING) {
			restartButton.setEnabled(true);
			quitButton.setEnabled(false);
			saveButton.setEnabled(false);
		} else {
			restartButton.setEnabled(false);
			quitButton.setEnabled(true);
			saveButton.setEnabled(true);
			server.start();
		}
	}

	private void shutdownServer() {
		if (server.getClientsNum() > 0) {
			if (JOptionPane.showConfirmDialog(frame,
					"There are still some clients online, are you sure you want to quit?", "Online Clients?",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION) {
				return;
			}
		}
		StatusCode response;
		synchronized (server.getDict()) {
			response = server.getDict().saveDictionary(filepathInput.getText());
		}
		if (response == StatusCode.NO_DICT) {
			if (JOptionPane.showConfirmDialog(frame,
					"Failed to save the dictionary, are you sure you want to quit? Click 'No' and correct the dictionary filepath.", "Quit Server?",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
				server.shutdown();
			}
		} else {
			server.shutdown();
		}
	}

	public JFrame getFrame() {
		return frame;
	}

	public JTextArea getLogArea() {
		return logArea;
	}

	public JTextField getFilepathInput() {
		return filepathInput;
	}

	public JTextField getClientsNum() {
		return clientsNum;
	}

	public DefaultListModel getClientsModel() {
		return clientsModel;
	}
}

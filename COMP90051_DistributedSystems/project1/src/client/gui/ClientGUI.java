/* ClientGUI.java
   Author: Thomas Choi 1202247 */

package client.gui;

import java.awt.*;

import javax.swing.*;

import client.DictionaryClient;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;

import shared.OperationCode;

import com.jgoodies.forms.layout.FormSpecs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientGUI {

	private JFrame frame;

	private DictionaryClient client;
	private JLabel connectionLabel;
	private JTextField portInput;
	private JTextField addressInput;
	private JButton connectButton;
	private JButton submitButton;
	private JTextArea responseArea;
	private final static Color DISABLE = new Color(199, 199, 199, 255);
	private final static Color ENABLE = new Color(255, 255, 255);
	private final static int WIDTH = 450;
	private final static int HEIGHT = 420;

	/**
	 * Create the application.
	 */
	public ClientGUI(DictionaryClient client, String serverAddress, String portStr) {
		this.client = client;
		initialize(serverAddress, portStr);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(String serverAddress, String portStr) {
		// GUI
		frame = new JFrame();
		frame.getContentPane().setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(35dlu;default)"),
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(179dlu;default):grow"),},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(46dlu;default)"),
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(51dlu;default)"),
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		frame.setSize(WIDTH, HEIGHT);
		frame.setResizable(false);
		frame.setTitle("Dictionary Client");

		JLabel lblNewLabel = new JLabel("Action");
		frame.getContentPane().add(lblNewLabel, "2, 2");
		
		JComboBox<ActionObject> actionSelect = new JComboBox<ActionObject>();
		actionSelect.addItem(new ActionObject("Search", OperationCode.SEARCH));
		actionSelect.addItem(new ActionObject("Add", OperationCode.ADD));
		actionSelect.addItem(new ActionObject("Update", OperationCode.UPDATE));
		actionSelect.addItem(new ActionObject("Remove", OperationCode.REMOVE));
		frame.getContentPane().add(actionSelect, "6, 2, fill, default");
		actionSelect.setRenderer(new ObjectListCellRenderer());

		// Action setting
		JLabel lblNewLabel_1 = new JLabel("Word");
		frame.getContentPane().add(lblNewLabel_1, "2, 4");

		JTextField wordInput = new JTextField();
		frame.getContentPane().add(wordInput, "6, 4, fill, default");
		wordInput.setColumns(10);
		
		JLabel lblNewLabel_2 = new JLabel("<html>Meanings<br/>(separate by ';')</html>");
		frame.getContentPane().add(lblNewLabel_2, "2, 6");
		
		JTextArea meaningsInput = new JTextArea();
		meaningsInput.setLineWrap(true);
		meaningsInput.setBackground(DISABLE);
		meaningsInput.setEnabled(false);
		JScrollPane meaningsInputScroll = new JScrollPane (meaningsInput,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		frame.getContentPane().add(meaningsInputScroll, "6, 6, fill, fill");
		
		submitButton = new JButton("Submit");
		frame.getContentPane().add(submitButton, "6, 8");
		
		JLabel lblNewLabel_3 = new JLabel("Response");
		frame.getContentPane().add(lblNewLabel_3, "2, 10");
		
		responseArea = new JTextArea();
		responseArea.setLineWrap(true);
		responseArea.setEditable(false);
		JScrollPane responseAreaScroll = new JScrollPane (responseArea,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		frame.getContentPane().add(responseAreaScroll, "6, 10, default, fill");

		// Connection Parameters
		connectionLabel = new JLabel("Connection Settings", SwingConstants.CENTER);
		frame.getContentPane().add(connectionLabel, "2, 12, 5, 1");

		JLabel serverAddressLabel = new JLabel("Address");
		frame.getContentPane().add(serverAddressLabel, "2, 14");

		addressInput = new JTextField();
		addressInput.setText(serverAddress);
		frame.getContentPane().add(addressInput, "6, 14, fill, default");
		addressInput.setColumns(10);

		JLabel portLabel = new JLabel("Port");
		frame.getContentPane().add(portLabel, "2, 16");

		portInput = new JTextField();
		portInput.setText(portStr);
		frame.getContentPane().add(portInput, "6, 16, fill, default");
		portInput.setColumns(10);

		connectButton = new JButton("Retry to Connect");
		connectButton.setVisible(false);
		frame.getContentPane().add(connectButton, "6, 18");

		// Logic
		String connectionResponse = client.connectToServer(serverAddress, portStr);
		connectionLabel.setText(String.format("Connection Settings - %s", connectionResponse));
		if (!connectionResponse.equals(DictionaryClient.CONNECTED)) {
			connectButton.setVisible(true);
			submitButton.setEnabled(false);
		}

		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				responseArea.setText(client.closeConnection());
				System.exit(0);
			}
		});

		submitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (submitButton.isEnabled()) {
					OperationCode operation = ((ActionObject)actionSelect.getSelectedItem()).getValue();
					String word = wordInput.getText();
					String meanings = meaningsInput.getText();

					String response = client.sendRequest(operation, word, meanings);
					responseArea.setText(response);

					if (response.equals(DictionaryClient.NOT_CONNECTED)) {
						connectionLabel.setText(String.format("Connection Settings - %s", response));
						connectButton.setVisible(true);
						submitButton.setEnabled(false);
					}
				}
			}
		});

		actionSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				OperationCode operation = ((ActionObject)actionSelect.getSelectedItem()).getValue();
				if (operation == OperationCode.SEARCH || operation == OperationCode.REMOVE) {
					meaningsInput.setText("");
					meaningsInput.setBackground(DISABLE);
					meaningsInput.setEnabled(false);
				} else {
					meaningsInput.setBackground(ENABLE);
					meaningsInput.setEnabled(true);
				}
			}
		});

		connectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				reconnect();
			}
		});
	}

	private void reconnect() {
		String connectionResponse = client.connectToServer(addressInput.getText(), portInput.getText());
		connectionLabel.setText(String.format("Connection Settings - %s", connectionResponse));
		if (connectionResponse.equals(DictionaryClient.CONNECTED)) {
			connectButton.setVisible(false);
			submitButton.setEnabled(true);
			responseArea.setText("");
		}
	}

	public JFrame getFrame() {
		return frame;
	}
}

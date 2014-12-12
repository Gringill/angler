package gui.panel;

import java.awt.Component;

import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class InputPathability extends JPanel {
	private ButtonGroup button_group = new ButtonGroup();

	private InputPathability() {

		JPanel panel = new JPanel();
		add(panel);

		JRadioButton rbtn_ground = new JRadioButton("Ground");
		rbtn_ground.setActionCommand("0");
		panel.add(rbtn_ground);
		button_group.add(rbtn_ground);

		JRadioButton rbtn_swimming = new JRadioButton("Swimming");
		rbtn_swimming.setActionCommand("1");
		panel.add(rbtn_swimming);
		button_group.add(rbtn_swimming);

		JRadioButton rbtn_flying = new JRadioButton("Flying");
		rbtn_flying.setActionCommand("2");
		panel.add(rbtn_flying);
		button_group.add(rbtn_flying);

		setVisible(true);
	}

	public static String execute(Component parent) {
		InputPathability tp = new InputPathability();
		int result = JOptionPane.showConfirmDialog(parent, tp, "Pathability Picker", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null);
		if (result == JOptionPane.OK_OPTION) {
			return tp.button_group.getSelection().getActionCommand();
		} else {
			return "0";
		}
	}
}

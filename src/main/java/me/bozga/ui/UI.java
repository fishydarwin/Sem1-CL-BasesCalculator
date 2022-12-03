package me.bozga.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import me.bozga.core.BaseNumber;
import me.bozga.core.BaseNumberOperators;

public class UI {

    private static JFrame frame;

    private static JTextField resultNumberField;
    private static JLabel arithmeticErrorLabel;

    private static JLabel baseConversionErrorLabel;

    /**
     * Initializes the main window.
     */
    public static void initialize() {
        frame = new JFrame("Bases Calculator");
        frame.setSize(640, 480);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTabbedPane pane = new JTabbedPane();
        pane.setForeground(Color.black);

        /*
         * Arithmetic operations panel
         */
        JPanel arithmeticOperationsPanel = new JPanel();
        arithmeticOperationsPanel.setLayout(new BoxLayout(arithmeticOperationsPanel, BoxLayout.Y_AXIS));
        pane.addTab("Arithmetic Operations", null, arithmeticOperationsPanel, "Perform arithmetic operations.");
        
        // settings panel
        JPanel arithmeticSettings = new JPanel();
        arithmeticSettings.setBorder(BorderFactory.createTitledBorder("Settings"));
        arithmeticSettings.setMaximumSize(new Dimension(640, 300));
        arithmeticOperationsPanel.add(arithmeticSettings);

        JLabel operationLabel = new JLabel("Operation");
        String[] allowedOperations = { "Addition", "Subtraction", "Multiplication by 1 digit", "Division by 1 digit" };
        JComboBox<String> operationComboBox = new JComboBox<>(allowedOperations);
        arithmeticSettings.add(operationLabel);
        arithmeticSettings.add(operationComboBox);

        JLabel baseLabel = new JLabel("Base");
        Byte[] allowedBases = { 2, 3, 4, 5, 6, 7, 8, 9, 10, 16 };
        JComboBox<Byte> baseComboBox = new JComboBox<>(allowedBases);
        baseComboBox.setSelectedIndex(8);
        arithmeticSettings.add(baseLabel);
        arithmeticSettings.add(baseComboBox);

        // perform panel
        JPanel arithmeticPerform = new JPanel();
        arithmeticPerform.setLayout(new BoxLayout(arithmeticPerform, BoxLayout.Y_AXIS));
        arithmeticPerform.setBorder(BorderFactory.createTitledBorder("Perform"));
        arithmeticOperationsPanel.add(arithmeticPerform);

        /* separator */ arithmeticPerform.add(Box.createVerticalGlue());

        JPanel firstNumberPanel = new JPanel();
        firstNumberPanel.setMaximumSize(new Dimension(640, 150));
        JLabel firstNumberLabel = new JLabel("A = ");
        JTextField firstNumberField = new JTextField(16);
        firstNumberPanel.add(firstNumberLabel);
        firstNumberPanel.add(firstNumberField);
        arithmeticPerform.add(firstNumberPanel);

        JPanel secondNumberPanel = new JPanel();
        secondNumberPanel.setMaximumSize(new Dimension(640, 150));
        JLabel secondNumberLabel = new JLabel("B = ");
        JTextField secondNumberField = new JTextField(16);
        secondNumberPanel.add(secondNumberLabel);
        secondNumberPanel.add(secondNumberField);
        arithmeticPerform.add(secondNumberPanel);

        /* separator */ arithmeticPerform.add(Box.createVerticalGlue());

        JPanel arithmeticCalculatePanel = new JPanel();
        arithmeticCalculatePanel.setMaximumSize(new Dimension(640, 150));
        JButton arithmeticCalculate = new JButton("Calculate");
        arithmeticCalculate.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { 
                calculateAction(operationComboBox, baseComboBox, firstNumberField, secondNumberField); 
            }
        });
        arithmeticCalculatePanel.add(arithmeticCalculate);
        arithmeticPerform.add(arithmeticCalculatePanel);

        JPanel resultNumberPanel = new JPanel();
        resultNumberPanel.setMaximumSize(new Dimension(640, 150));
        JLabel resultNumberLabel = new JLabel("Result");
        resultNumberField = new JTextField(16);
        resultNumberField.setEditable(false);
        resultNumberPanel.add(resultNumberLabel);
        resultNumberPanel.add(resultNumberField);
        arithmeticPerform.add(resultNumberPanel);

        JPanel arithmeticErrorPanel = new JPanel();
        arithmeticErrorPanel.setMaximumSize(new Dimension(640, 150));
        arithmeticErrorLabel = new JLabel("");
        arithmeticErrorPanel.add(arithmeticErrorLabel);
        arithmeticPerform.add(arithmeticErrorPanel);

        /* separator */ arithmeticPerform.add(Box.createVerticalGlue());

        /*
         * Base conversion panel
         */
        JPanel baseConversionPanel = new JPanel();
        pane.addTab("Base Conversion", null, baseConversionPanel, "Perform base conversions.");

        frame.add(pane);
        frame.setVisible(true);
    }

    /*
     * FUNCTIONALITY OF BUTTONS AND SUCH
     */
    
    private static void calculateAction(JComboBox<String> operationComboBox, 
                                    JComboBox<Byte> baseComboBox, 
                                    JTextField firstNumberField, 
                                    JTextField secondNumberField) 
    {
        // validate input
        try {

            Map<Character, Integer> additionalValueMapping = BaseNumber.NO_MAP;
            if (baseComboBox.getSelectedIndex() == 9) { additionalValueMapping = BaseNumber.BASE_16_MAP; }

            byte base = (byte) baseComboBox.getSelectedItem();
            int operation = operationComboBox.getSelectedIndex(); // 0 = ADD, 1 = SUB, 2 = MUL, 3 = DIV

            BaseNumber a = new BaseNumber(base, firstNumberField.getText(), additionalValueMapping);
            BaseNumber b = new BaseNumber(base, secondNumberField.getText(), additionalValueMapping);

            switch (operation) {
                case 0:
                    resultNumberField.setText(BaseNumberOperators.add(a, b).toString());
                    break;
                case 1:
                    // resultNumberField.setText(BaseNumberOperators.sub(a, b).toString());
                    break;
                case 2:
                    // resultNumberField.setText(BaseNumberOperators.mulDigit(a, b).toString());
                    break;
                case 3:
                    // resultNumberField.setText(BaseNumberOperators.divDigit(a, b).toString());
                    break;
            }

        } catch (Exception ex) {
            String message = ex.getMessage();
            if (message.length() > 128) { message = message.substring(0, 128) + " [...]"; }
            arithmeticErrorLabel.setText(message);
        }
    }
    
}

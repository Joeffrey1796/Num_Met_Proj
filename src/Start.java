import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

//? Import for backend math
import include.*;

class RoundedButton extends JButton {
    private int radius;
    public RoundedButton(String text, int radius) {
        super(text);
        this.radius = radius;
        setOpaque(false);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
    }
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        super.paintComponent(g2);
        g2.dispose();
    }
    @Override
    public void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(200,200,200));
        g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, radius, radius);
        g2.dispose();
    }
    @Override
    public boolean contains(int x, int y) {
        return new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius).contains(x, y);
    }
}

class RoundedBorder extends javax.swing.border.AbstractBorder {
    private int radius;
    RoundedBorder(int radius) {
        this.radius = radius;
    }
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        g.setColor(new Color(200,200,200));
        g.drawRoundRect(x, y, width-1, height-1, radius, radius);
    }
}

public class Start {
    public static final String FONT_NAME = "Times New Roman";
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Start::showMainMenu);
    }

    public static JButton createButton(String text, int radius, int fontSize) {
        RoundedButton btn = new RoundedButton(text, radius);
        btn.setFont(new Font(FONT_NAME, Font.BOLD, fontSize));
        btn.setBackground(new Color(40, 40, 40));
        btn.setForeground(Color.GREEN);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new RoundedBorder(radius));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        addHoverEffect(btn);
        return btn;
    }

    private static void showMainMenu() {
        JFrame frame = new JFrame("Data Structure Analysis X Numerical Methods");
        frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(1280, 720);
        frame.setLayout(null);
        frame.getContentPane().setBackground(new Color(25, 25, 25));

        JLabel title = new JLabel("Methods Calculator", SwingConstants.CENTER);
        title.setFont(new Font(FONT_NAME, Font.BOLD, 40));
        title.setForeground(Color.GREEN);
        title.setBounds(0, 200, 1280, 60);
        frame.add(title);

        JButton startButton = createButton("START", 60, 24);
        startButton.setBounds(540, 400, 200, 60);
        startButton.addActionListener(e -> {
            frame.dispose();
            new OptionFrame();
        });
        frame.add(startButton);

        int circleSize = 40;
        int margin = 20;
        JButton groupMembers = createButton("...", circleSize, 28);
        groupMembers.setBounds(540 + 200 + margin, 410, circleSize, circleSize);
        groupMembers.addActionListener(e -> {
            UIManager.put("OptionPane.messageFont", new Font(FONT_NAME, Font.BOLD, 26));
            UIManager.put("OptionPane.buttonFont", new Font(FONT_NAME, Font.PLAIN, 22));
            UIManager.put("OptionPane.minimumSize", new Dimension(500, 300));
            JOptionPane.showMessageDialog(frame,
                "<html><div style='text-align:left; color:black;'>"
                + "<b>Group Members:</b><br><br>"
                + "1. Butuhan, Eugene<br>"
                + "2. Castro, Christian Joeffrey<br>"
                + "3. Dela Cruz, Angel Anne<br>"
                + "4. Deliguer, Coleen<br>"
                + "</div></html>",
                "Group Members",
                JOptionPane.INFORMATION_MESSAGE);
        });
        frame.add(groupMembers);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void addHoverEffect(JButton btn) {
        Color normalBg = new Color(40, 40, 40);
        Color hoverBg = new Color(60, 80, 60);
        Color normalFg = Color.GREEN;
        Color hoverFg = Color.GREEN.darker();
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(hoverBg); btn.setForeground(hoverFg); btn.repaint();
            }
            @Override public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(normalBg); btn.setForeground(normalFg); btn.repaint();
            }
        });
    }
}

class MethodInputFrame extends JFrame {
    private String methodName;
    private List<JTextField> inputFields = new ArrayList<>();

    public static boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public MethodInputFrame(String methodName) {
        this.methodName = methodName;
        setTitle(methodName + " Input");
        setSize(500, 400);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(25, 25, 25));
        setLayout(new BorderLayout());

        JLabel label = new JLabel(methodName + ":", SwingConstants.CENTER);
        label.setFont(new Font(Start.FONT_NAME, Font.BOLD, 24));
        label.setForeground(Color.GREEN);
        add(label, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        inputPanel.setBackground(new Color(25, 25, 25));
        addPlaceholderField(inputPanel, "Enter equation, e.g. x^2 - 4");
        String m = methodName.toLowerCase();
        if (m.contains("secant") || m.contains("bisection") || m.contains("false position")) {
            addPlaceholderField(inputPanel, "Initial guess 1 (e.g. 1.0)");
            addPlaceholderField(inputPanel, "Initial guess 2 (e.g. 2.0)");
        } else {
            addPlaceholderField(inputPanel, "Initial guess (e.g. 1.0)");
        }
        addPlaceholderField(inputPanel, "Tolerance (e.g. 0.0001)");
        add(inputPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(new Color(25, 25, 25));
        JButton solveBtn = new JButton("Solve");
        JButton closeBtn = new JButton("Close");
        for (JButton b : new JButton[]{solveBtn, closeBtn}) {
            b.setFont(new Font(Start.FONT_NAME, Font.BOLD, 18));
            b.setBackground(new Color(60, 80, 60));
            b.setForeground(Color.GREEN);
        }
        solveBtn.addActionListener(e -> {
            //? Gather all input values
            List<String> inputs = new ArrayList<>();
            for (JTextField field : inputFields) {
                String text = field.getText();
                if (!text.equals(field.getClientProperty("placeholder"))) {
                    inputs.add(text);
                } else {
                    inputs.add(""); 
                }
            }
            
            
            boolean correct = true;
            //! Error check for empty fields
            for (String string : inputs) {
                if (string.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Error: Empty fields are not allowed.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    correct = false;
                    break;  // No need to check further if one field is empty
                }
            }

            //! Data type check
            if (correct) {  // Only proceed with data type check if fields aren't empty
                if (methodName.equals("Fixed-Point Iteration Method") || methodName.equals("Fixed-Point Iteration Method")) {
                    if (!isDouble(inputs.get(1))) {
                        JOptionPane.showMessageDialog(null, "Error: Invalid number format for guess 1.", "Input Error", JOptionPane.ERROR_MESSAGE);
                        correct = false;
                    } else if (!isDouble(inputs.get(2))) {
                        JOptionPane.showMessageDialog(null, "Error: Invalid number format for tolerance.", "Input Error", JOptionPane.ERROR_MESSAGE);
                        correct = false;
                    }
                } else {
                    if (!isDouble(inputs.get(1))) {
                        JOptionPane.showMessageDialog(null, "Error: Invalid number format for guess 1.", "Input Error", JOptionPane.ERROR_MESSAGE);
                        correct = false;
                    } else if (!isDouble(inputs.get(2))) {
                        JOptionPane.showMessageDialog(null, "Error: Invalid number format for guess 2.", "Input Error", JOptionPane.ERROR_MESSAGE);
                        correct = false;
                    } else if (!isDouble(inputs.get(3))) {
                        JOptionPane.showMessageDialog(null, "Error: Invalid number format for tolerance.", "Input Error", JOptionPane.ERROR_MESSAGE);
                        correct = false;
                    }
                }
            }
            if (correct) {
                new MethodsSolutionFrame(methodName, inputs);
            }
        });
        closeBtn.addActionListener(e -> dispose());
        btnPanel.add(solveBtn); btnPanel.add(closeBtn);
        add(btnPanel, BorderLayout.SOUTH);
        setVisible(true);
    }
    private void addPlaceholderField(JPanel panel, String placeholder) {
        JTextField field = new JTextField(placeholder);
        field.putClientProperty("placeholder", placeholder);
        inputFields.add(field);
        field.setForeground(new Color(144, 238, 144));
        field.setBackground(new Color(40, 40, 40));
        field.setCaretColor(Color.WHITE);
        field.setFont(new Font(Start.FONT_NAME, Font.PLAIN, 18));
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText(""); field.setForeground(Color.WHITE);
                }
            }
            @Override public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder); field.setForeground(new Color(144, 238, 144));
                }
            }
        });
        panel.add(field);
    }
}

//TODO: Implement backend api
class MethodsSolutionFrame extends JFrame {

    public MethodsSolutionFrame(String methodName, List<String> inputs) {
        setTitle(methodName + " Solution");
        setSize(800, 650);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(25, 25, 25));
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(25, 25, 25));

        JLabel label = new JLabel(methodName, SwingConstants.CENTER);
        label.setFont(new Font(Start.FONT_NAME, Font.BOLD, 22));
        label.setForeground(Color.GREEN);
        add(label, BorderLayout.CENTER);
        // setVisible(true);

        JTextArea solutionTextArea = new JTextArea();
        solutionTextArea.setEditable(false);
        solutionTextArea.setFont(new Font(Start.FONT_NAME, Font.PLAIN, 14));
        solutionTextArea.setForeground(Color.WHITE);
        solutionTextArea.setBackground(new Color(40, 40, 40));
        solutionTextArea.setLineWrap(true);
        solutionTextArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(solutionTextArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
        setVisible(true);
        

        //? Test
        // System.out.println("Test");
        // for (String string : inputs) {
        //     System.out.println(string);
        // }
        // System.out.println("Test");



        //? Test
        // System.out.println(correct);
        // System.out.println("Method Name: "  + methodName);
        
        List<String> soln = new ArrayList<>();
        List<String> ans = new ArrayList<>();
        String solutionText;
        
        switch (methodName) {
            case "Fixed-Point Iteration Method":
                
                break;
            case "Newton-Rhapson Method":

            break;
            
            
            case "Secant Method":
                Secant_Method smSolver = new Secant_Method();
                smSolver.solve(inputs.get(0), Double.parseDouble(inputs.get(1)), Double.parseDouble(inputs.get(2)));
                //? debug
                // boolean smSuccess = smSolver.solve(@param);
                // smSolver.printSolution(smSuccess);
                soln = smSolver.getSolutionSteps();
                ans = smSolver.getAnswers();
                soln.addAll(ans);
                
                // Join the solution steps with newlines and display in the text area
                solutionText = String.join("\n", soln);
                solutionTextArea.setText(solutionText);
                break;
            case "Bisection Method":
                Bisection bm_solver = new Bisection();
                bm_solver.solve(inputs.get(0), Double.parseDouble(inputs.get(1)), Double.parseDouble(inputs.get(2)));
                soln = bm_solver.getSolutionSteps();
                ans = bm_solver.getAnswers();
                soln.addAll(ans);
                
                // Join the solution steps with newlines and display in the text area
                solutionText = String.join("\n", soln);
                solutionTextArea.setText(solutionText);
                break;
            case "False Position or Regular Falsi Method":
                False_Position fp_solver = new False_Position();
                fp_solver.solve(inputs.get(0), Double.parseDouble(inputs.get(1)), Double.parseDouble(inputs.get(2)));
                soln = fp_solver.getSolutionSteps();
                ans = fp_solver.getAnswers();
                soln.addAll(ans);
                
                // Join the solution steps with newlines and display in the text area
                solutionText = String.join("\n", soln);
                solutionTextArea.setText(solutionText);
                break;
        
            default:
                break;
        }
        



    }
}


// TODO: Grab values from input fields;
class MatrixSolutionFrame extends JFrame {
    // public SolutionFrame() {
    //     setTitle("Solution");
    //     setSize(400, 250);
    //     setLocationRelativeTo(null);
    //     getContentPane().setBackground(new Color(25, 25, 25));
    //     setLayout(new BorderLayout());
    //     setVisible(true);
    // }

    public MatrixSolutionFrame(String methodName, double[][] matrix, double[] constants) {
        setTitle(methodName + " Solution");
        setSize(800, 650);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(25, 25, 25));
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(25, 25, 25));

        JLabel label = new JLabel(methodName, SwingConstants.CENTER);
        label.setFont(new Font(Start.FONT_NAME, Font.BOLD, 22));
        label.setForeground(Color.GREEN);
        add(label, BorderLayout.CENTER);
        // setVisible(true);

        JTextArea solutionTextArea = new JTextArea();
        solutionTextArea.setEditable(false);
        solutionTextArea.setFont(new Font(Start.FONT_NAME, Font.PLAIN, 14));
        solutionTextArea.setForeground(Color.WHITE);
        solutionTextArea.setBackground(new Color(40, 40, 40));
        solutionTextArea.setLineWrap(true);
        solutionTextArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(solutionTextArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
        setVisible(true);

        //? Test
        // for (double[] d : matrix) {
        //     for (double e : d) {
        //         System.out.println(e);
        //     }
        // }
        // System.out.println();
        // System.out.println();
        // System.out.println();
        // for (double d : constants) {
        //     System.out.println(d);
        // }
        List<String> soln = new ArrayList<>();
        List<String> ans = new ArrayList<>();
        String solutionText;

        switch (methodName) {
            case "Cramer's Rule":
                break;
            case "Gaussian Elimination":
                System.out.println("Test");
                Gaussian_Elimination geSolver = new Gaussian_Elimination();

                geSolver.solve(matrix, constants);
                soln = geSolver.getSolutionSteps();
                ans = geSolver.getAnswers();
                soln.addAll(ans);

                solutionText = String.join("\n", soln);
                solutionTextArea.setText(solutionText);
                break;
            case "Jacobi Method":
                break;
            case "Gauss-Seidel Method":
                break;
        
            default:
                break;
        }


    }
}

class MatrixInputFrame extends JFrame {
    private JTextField[][] matrixFields;
    private JTextField[] constantFields;

    public double[][] getMatrixValues() {
        int n = matrixFields.length;
        double[][] matrix = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                try {
                    matrix[i][j] = Double.parseDouble(matrixFields[i][j].getText());
                } catch (NumberFormatException e) {
                    throw new RuntimeException("Invalid matrix input at [" + (i+1) + "," + (j+1) + "]");
                }
            }
        }
        return matrix;
    }

    public double[] getConstantValues() {
        int n = constantFields.length;
        double[] constants = new double[n];
        for (int i = 0; i < n; i++) {
            try {
                constants[i] = Double.parseDouble(constantFields[i].getText());
            } catch (NumberFormatException e) {
                throw new RuntimeException("Invalid constant input at [" + (i+1) + "]");
            }
        }
        return constants;
    }

    private void styleMatrixField(JTextField tf, int row, int col) {
        tf.setForeground(new Color(144, 238, 144));
        tf.setBackground(new Color(40, 40, 40));
        tf.setCaretColor(Color.WHITE);
        tf.setFont(new Font(Start.FONT_NAME, Font.PLAIN, 16));
        tf.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (tf.getText().startsWith("A[")) {
                    tf.setText("");
                    tf.setForeground(Color.WHITE);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (tf.getText().isEmpty()) {
                    tf.setText("A[" + (row + 1) + "," + (col + 1) + "]");
                    tf.setForeground(new Color(144, 238, 144));
                }
            }
        });
    }

    private void styleConstantField(JTextField tf, int row) {
        tf.setForeground(new Color(144, 238, 144));
        tf.setBackground(new Color(40, 40, 40));
        tf.setCaretColor(Color.WHITE);
        tf.setFont(new Font(Start.FONT_NAME, Font.PLAIN, 16));
        tf.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (tf.getText().startsWith("B[")) {
                    tf.setText("");
                    tf.setForeground(Color.WHITE);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (tf.getText().isEmpty()) {
                    tf.setText("B[" + (row + 1) + "]");
                    tf.setForeground(new Color(144, 238, 144));
                }
            }
        });
    }




    public MatrixInputFrame(String methodName) {
        setTitle(methodName + " Input");
        setSize(700, 600);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(25, 25, 25));
        setLayout(new BorderLayout());

        JLabel label = new JLabel(methodName + ":", SwingConstants.CENTER);
        label.setFont(new Font(Start.FONT_NAME, Font.BOLD, 24));
        label.setForeground(Color.GREEN);
        add(label, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(25, 25, 25));
        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.setBackground(new Color(25, 25, 25));
        JLabel sizeLabel = new JLabel("Matrix size (n x n):");
        sizeLabel.setForeground(Color.GREEN);
        controlPanel.add(sizeLabel);
        JSpinner sizeSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 100, 1));
        controlPanel.add(sizeSpinner);
        JButton generateBtn = new JButton("Generate");
        generateBtn.setBackground(new Color(60, 80, 60));
        generateBtn.setForeground(Color.GREEN);
        controlPanel.add(generateBtn);
        centerPanel.add(controlPanel, BorderLayout.NORTH);
        JPanel matrixPanel = new JPanel(new GridBagLayout());
        matrixPanel.setBackground(new Color(25, 25, 25));
        JPanel constantsPanel = new JPanel(new GridBagLayout());
        constantsPanel.setBackground(new Color(25, 25, 25));
        JScrollPane matrixScroll = new JScrollPane(matrixPanel);
        matrixScroll.setPreferredSize(new Dimension(350, 250));
        centerPanel.add(matrixScroll, BorderLayout.CENTER);
        JScrollPane constantsScroll = new JScrollPane(constantsPanel);
        constantsScroll.setPreferredSize(new Dimension(120, 250));
        centerPanel.add(constantsScroll, BorderLayout.EAST);
        add(centerPanel, BorderLayout.CENTER);
        generateBtn.addActionListener(e -> {
            matrixPanel.removeAll(); constantsPanel.removeAll();
            int n = (Integer) sizeSpinner.getValue();
            matrixFields = new JTextField[n][n];
            constantFields = new JTextField[n];

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(3, 3, 3, 3);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            for (int row = 0; row < n; row++) {
                for (int col = 0; col < n; col++) {
                    gbc.gridx = col; gbc.gridy = row;
                    JTextField tf = new JTextField("A[" + (row + 1) + "," + (col + 1) + "]");
                    styleMatrixField(tf, row, col); // You can put your existing styling and focus code here
                    matrixFields[row][col] = tf;
                    matrixPanel.add(tf, gbc);
                }

                gbc.gridx = 0;
                gbc.gridy = row;
                JTextField tf = new JTextField("B[" + (row + 1) + "]");
                styleConstantField(tf, row); // Move your focus styling code into a method for clarity
                constantFields[row] = tf;
                constantsPanel.add(tf, gbc);
}
            matrixPanel.revalidate(); matrixPanel.repaint();
            constantsPanel.revalidate(); constantsPanel.repaint();
        });
        JButton closeButton = new JButton("Close");
        JButton solveButton = new JButton("Solve");
        for (JButton b : new JButton[]{solveButton, closeButton}) {
            b.setFont(new Font(Start.FONT_NAME, Font.BOLD, 18));
            b.setBackground(new Color(60, 80, 60));
            b.setForeground(Color.GREEN);
        }
        closeButton.addActionListener(e -> dispose());
        solveButton.addActionListener(e -> {
            try {
                double[][] matrix = getMatrixValues();
                double[] constants = getConstantValues();
                // new MatrixSolutionFrame(matrix, constants); // assuming you update MatrixSolutionFrame constructor
                new MatrixSolutionFrame(methodName, matrix, constants); // assuming you update MatrixSolutionFrame constructor
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(new Color(25, 25, 25));
        btnPanel.add(solveButton); btnPanel.add(closeButton);
        add(btnPanel, BorderLayout.SOUTH);
        setVisible(true);
    }
}

class OptionFrame extends JFrame {
    public OptionFrame() {
        setTitle("Choose a Method");
        setSize(1280, 720);
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(25, 25, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(18, 18, 18, 18);
        JLabel label = new JLabel("Select a Method:", SwingConstants.CENTER);
        label.setFont(new Font(Start.FONT_NAME, Font.BOLD, 32));
        label.setForeground(Color.GREEN);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.PAGE_START;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(label, gbc);
        String[] options = {
            "Fixed-Point Iteration Method",
            "Newton-Rhapson Method",
            "Secant Method",
            "Bisection Method",
            "False Position or Regular Falsi Method",
            "Cramer's Rule",
            "Gaussian Elimination",
            "Jacobi Method",
            "Gauss-Seidel Method"
        };
        gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        for (int i = 0; i < options.length; i++) {
            JButton btn = Start.createButton(options[i], 30, 20);
            btn.setPreferredSize(new Dimension(400, 50));
            String methodName = options[i];
            btn.addActionListener(e -> {
                if (methodName.contains("Cramer") || methodName.contains("Gaussian") || methodName.contains("Jacobi") || methodName.contains("Gauss-Seidel"))
                    new MatrixInputFrame(methodName);
                else new MethodInputFrame(methodName);
            });
            gbc.gridx = i % 2; gbc.gridy = 1 + (i / 2);
            mainPanel.add(btn, gbc);
        }
        setContentPane(mainPanel);
        getContentPane().setBackground(new Color(25, 25, 25));
        setLocationRelativeTo(null);
        setVisible(true);
    }
}

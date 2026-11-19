package calculator;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.plaf.basic.BasicButtonUI;

public class CalculatorFrame extends javax.swing.JFrame {

    private static final String APP_NAME = "Calculator";

    private static final Color BACKGROUND = new Color(30, 32, 42);
    private static final Color ACCENT = new Color(210, 76, 75);
    private static final Color HOVER = new Color(44, 47, 60);
    private static final Color PRESSED = new Color(58, 61, 76);

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(CalculatorFrame.class.getName());

    private final CalculatorEngine engine = new CalculatorEngine();

    public CalculatorFrame() {
        setUndecorated(true);
        initComponents();

        setTitle(APP_NAME);
        lblTitle.setText(APP_NAME.toUpperCase());
        setLocationRelativeTo(null);

        enableWindowDrag();
        setupDisplay();

        for (java.awt.Component c : pnlKeypad.getComponents()) {
            if (c instanceof JButton) {
                styleButton((JButton) c);
            }
        }

        setupKeyBindings();
        refreshDisplay();
    }

    private void enableWindowDrag() {
        MouseAdapter drag = new MouseAdapter() {
            private Point grabPoint = new Point();

            @Override
            public void mousePressed(MouseEvent e) {
                grabPoint = e.getPoint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (getExtendedState() != NORMAL) {
                    return;
                }
                Point p = e.getLocationOnScreen();
                setLocation(p.x - grabPoint.x, p.y - grabPoint.y);
            }
        };

        pnlTitleBar.addMouseListener(drag);
        pnlTitleBar.addMouseMotionListener(drag);
    }

    private void setupDisplay() {
        getContentPane().setBackground(BACKGROUND);

        txtDisplay.setOpaque(false);
        txtDisplay.setBackground(new Color(0, 0, 0, 0));
        txtDisplay.setBorder(null);
        txtDisplay.setEditable(false);
        txtDisplay.setFocusable(false);
        txtDisplay.setForeground(new Color(230, 230, 230));
        txtDisplay.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDisplay.setFont(new Font("Arial", Font.BOLD, 42));

        lblResult.setOpaque(false);
        lblResult.setForeground(new Color(200, 200, 200));
        lblResult.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblResult.setFont(new Font("Arial", Font.PLAIN, 22));
        lblResult.setText(" ");
    }

    private void styleButton(JButton button) {
        button.setUI(new BasicButtonUI());
        button.setBackground(BACKGROUND);
        button.setForeground(ACCENT);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setMargin(new Insets(0, 0, 0, 0));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(BACKGROUND);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(PRESSED);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button.setBackground(button.contains(e.getPoint()) ? HOVER : BACKGROUND);
            }
        });
    }

    private void setupKeyBindings() {
        getContentPane().setFocusable(true);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                getContentPane().requestFocusInWindow();
            }
        });

        for (char c = '0'; c <= '9'; c++) {
            String digit = String.valueOf(c);
            bindKey(KeyStroke.getKeyStroke(c), "digit" + digit, () -> engine.appendDigit(digit));
        }

        for (char c : "+-*/^".toCharArray()) {
            String op = String.valueOf(c);
            bindKey(KeyStroke.getKeyStroke(c), "operator" + op, () -> engine.chooseOperator(op));
        }

        bindKey(KeyStroke.getKeyStroke('.'), "decimal", engine::appendDecimal);
        bindKey(KeyStroke.getKeyStroke('%'), "percent", engine::percent);
        bindKey(KeyStroke.getKeyStroke('='), "equals", engine::calculate);
        bindKey(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter", engine::calculate);
        bindKey(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "backspace", engine::deleteLast);
        bindKey(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "clearEntry", engine::clearEntry);
        bindKey(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "clearAll", engine::clearAll);
    }

    private void bindKey(KeyStroke key, String name, Runnable action) {
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(key, name);
        getRootPane().getActionMap().put(name, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                perform(action);
            }
        });
    }

    private void perform(Runnable action) {
        action.run();
        refreshDisplay();
    }

    private void refreshDisplay() {
        txtDisplay.setText(engine.getDisplayText());
        txtDisplay.setCaretPosition(txtDisplay.getDocument().getLength());
        adjustDisplayFont();

        String live = engine.getLiveResult();
        lblResult.setText(live.isEmpty() ? " " : live);
    }

    private void adjustDisplayFont() {
        int length = txtDisplay.getText().length();
        int fontSize;

        if (length <= 8) {
            fontSize = 42;
        } else if (length <= 14) {
            fontSize = 34;
        } else if (length <= 20) {
            fontSize = 28;
        } else {
            fontSize = 22;
        }

        txtDisplay.setFont(new Font("Arial", Font.BOLD, fontSize));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlDisplay = new GradientPanel();
        lblResult = new javax.swing.JLabel();
        txtDisplay = new javax.swing.JTextField();
        pnlKeypad = new javax.swing.JPanel();
        btnCE = new javax.swing.JButton();
        btnC = new javax.swing.JButton();
        btnDEL = new javax.swing.JButton();
        btnSquareRoot = new javax.swing.JButton();
        btnSquare = new javax.swing.JButton();
        btn7 = new javax.swing.JButton();
        btn8 = new javax.swing.JButton();
        btn9 = new javax.swing.JButton();
        btnMultiply = new javax.swing.JButton();
        btnPower = new javax.swing.JButton();
        btn4 = new javax.swing.JButton();
        btn5 = new javax.swing.JButton();
        btn6 = new javax.swing.JButton();
        btnDivide = new javax.swing.JButton();
        btnReciprocal = new javax.swing.JButton();
        btn1 = new javax.swing.JButton();
        btn2 = new javax.swing.JButton();
        btn3 = new javax.swing.JButton();
        btnSubtract = new javax.swing.JButton();
        btnPercent = new javax.swing.JButton();
        btnDecimal = new javax.swing.JButton();
        btn0 = new javax.swing.JButton();
        btnSign = new javax.swing.JButton();
        btnAdd = new javax.swing.JButton();
        btnEquals = new javax.swing.JButton();
        pnlTitleBar = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        btnClose = new javax.swing.JButton();
        btnMinimize = new javax.swing.JButton();
        btnMaximize = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Calculator");
        setBackground(new java.awt.Color(11, 9, 10));
        setPreferredSize(new java.awt.Dimension(530, 830));
        setResizable(false);

        pnlDisplay.setBackground(new java.awt.Color(210, 76, 75));
        pnlDisplay.setForeground(new java.awt.Color(210, 76, 75));

        lblResult.setFont(new java.awt.Font("Arial", 1, 22)); // NOI18N
        lblResult.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblResult.setText("0");

        txtDisplay.setBackground(new java.awt.Color(210, 76, 75));
        txtDisplay.setFont(new java.awt.Font("Arial", 1, 42)); // NOI18N
        txtDisplay.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDisplay.setText("0");
        txtDisplay.setBorder(null);
        txtDisplay.addActionListener(this::txtDisplayActionPerformed);

        javax.swing.GroupLayout pnlDisplayLayout = new javax.swing.GroupLayout(pnlDisplay);
        pnlDisplay.setLayout(pnlDisplayLayout);
        pnlDisplayLayout.setHorizontalGroup(
            pnlDisplayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlDisplayLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlDisplayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtDisplay, javax.swing.GroupLayout.DEFAULT_SIZE, 475, Short.MAX_VALUE)
                    .addComponent(lblResult, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(20, 20, 20))
        );
        pnlDisplayLayout.setVerticalGroup(
            pnlDisplayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDisplayLayout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addComponent(txtDisplay, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(lblResult)
                .addContainerGap(42, Short.MAX_VALUE))
        );

        pnlKeypad.setBackground(new java.awt.Color(30, 32, 42));
        pnlKeypad.setLayout(new java.awt.GridLayout(5, 5));

        btnCE.setBackground(new java.awt.Color(30, 32, 42));
        btnCE.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        btnCE.setForeground(new java.awt.Color(210, 76, 75));
        btnCE.setText("CE");
        btnCE.setBorder(null);
        btnCE.setBorderPainted(false);
        btnCE.setFocusPainted(false);
        btnCE.setFocusable(false);
        btnCE.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btnCE.setOpaque(true);
        btnCE.addActionListener(this::btnCEActionPerformed);
        pnlKeypad.add(btnCE);

        btnC.setBackground(new java.awt.Color(30, 32, 42));
        btnC.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        btnC.setForeground(new java.awt.Color(210, 76, 75));
        btnC.setText("C");
        btnC.setBorder(null);
        btnC.setBorderPainted(false);
        btnC.setFocusPainted(false);
        btnC.setFocusable(false);
        btnC.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btnC.setOpaque(true);
        btnC.addActionListener(this::btnCActionPerformed);
        pnlKeypad.add(btnC);

        btnDEL.setBackground(new java.awt.Color(30, 32, 42));
        btnDEL.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        btnDEL.setForeground(new java.awt.Color(210, 76, 75));
        btnDEL.setText("DEL");
        btnDEL.setBorder(null);
        btnDEL.setBorderPainted(false);
        btnDEL.setFocusPainted(false);
        btnDEL.setFocusable(false);
        btnDEL.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btnDEL.setOpaque(true);
        btnDEL.addActionListener(this::btnDELActionPerformed);
        pnlKeypad.add(btnDEL);

        btnSquareRoot.setBackground(new java.awt.Color(30, 32, 42));
        btnSquareRoot.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        btnSquareRoot.setForeground(new java.awt.Color(210, 76, 75));
        btnSquareRoot.setText("√x");
        btnSquareRoot.setBorder(null);
        btnSquareRoot.setBorderPainted(false);
        btnSquareRoot.setFocusPainted(false);
        btnSquareRoot.setFocusable(false);
        btnSquareRoot.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btnSquareRoot.setOpaque(true);
        btnSquareRoot.addActionListener(this::btnSquareRootActionPerformed);
        pnlKeypad.add(btnSquareRoot);

        btnSquare.setBackground(new java.awt.Color(30, 32, 42));
        btnSquare.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        btnSquare.setForeground(new java.awt.Color(210, 76, 75));
        btnSquare.setText("x²");
        btnSquare.setBorder(null);
        btnSquare.setBorderPainted(false);
        btnSquare.setFocusPainted(false);
        btnSquare.setFocusable(false);
        btnSquare.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btnSquare.setOpaque(true);
        btnSquare.addActionListener(this::btnSquareActionPerformed);
        pnlKeypad.add(btnSquare);

        btn7.setBackground(new java.awt.Color(30, 32, 42));
        btn7.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        btn7.setForeground(new java.awt.Color(210, 76, 75));
        btn7.setText("7");
        btn7.setBorder(null);
        btn7.setBorderPainted(false);
        btn7.setFocusPainted(false);
        btn7.setFocusable(false);
        btn7.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btn7.setOpaque(true);
        btn7.addActionListener(this::btn7ActionPerformed);
        pnlKeypad.add(btn7);

        btn8.setBackground(new java.awt.Color(30, 32, 42));
        btn8.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        btn8.setForeground(new java.awt.Color(210, 76, 75));
        btn8.setText("8");
        btn8.setBorder(null);
        btn8.setBorderPainted(false);
        btn8.setFocusPainted(false);
        btn8.setFocusable(false);
        btn8.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btn8.setOpaque(true);
        btn8.addActionListener(this::btn8ActionPerformed);
        pnlKeypad.add(btn8);

        btn9.setBackground(new java.awt.Color(30, 32, 42));
        btn9.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        btn9.setForeground(new java.awt.Color(210, 76, 75));
        btn9.setText("9");
        btn9.setBorder(null);
        btn9.setBorderPainted(false);
        btn9.setFocusPainted(false);
        btn9.setFocusable(false);
        btn9.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btn9.setOpaque(true);
        btn9.addActionListener(this::btn9ActionPerformed);
        pnlKeypad.add(btn9);

        btnMultiply.setBackground(new java.awt.Color(30, 32, 42));
        btnMultiply.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        btnMultiply.setForeground(new java.awt.Color(210, 76, 75));
        btnMultiply.setText("X");
        btnMultiply.setBorder(null);
        btnMultiply.setBorderPainted(false);
        btnMultiply.setFocusPainted(false);
        btnMultiply.setFocusable(false);
        btnMultiply.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btnMultiply.setOpaque(true);
        btnMultiply.addActionListener(this::btnMultiplyActionPerformed);
        pnlKeypad.add(btnMultiply);

        btnPower.setBackground(new java.awt.Color(30, 32, 42));
        btnPower.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        btnPower.setForeground(new java.awt.Color(210, 76, 75));
        btnPower.setText("xʸ");
        btnPower.setBorder(null);
        btnPower.setBorderPainted(false);
        btnPower.setFocusPainted(false);
        btnPower.setFocusable(false);
        btnPower.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btnPower.setOpaque(true);
        btnPower.addActionListener(this::btnPowerActionPerformed);
        pnlKeypad.add(btnPower);

        btn4.setBackground(new java.awt.Color(30, 32, 42));
        btn4.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        btn4.setForeground(new java.awt.Color(210, 76, 75));
        btn4.setText("4");
        btn4.setBorder(null);
        btn4.setBorderPainted(false);
        btn4.setFocusPainted(false);
        btn4.setFocusable(false);
        btn4.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btn4.setOpaque(true);
        btn4.addActionListener(this::btn4ActionPerformed);
        pnlKeypad.add(btn4);

        btn5.setBackground(new java.awt.Color(30, 32, 42));
        btn5.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        btn5.setForeground(new java.awt.Color(210, 76, 75));
        btn5.setText("5");
        btn5.setBorder(null);
        btn5.setBorderPainted(false);
        btn5.setFocusPainted(false);
        btn5.setFocusable(false);
        btn5.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btn5.setOpaque(true);
        btn5.addActionListener(this::btn5ActionPerformed);
        pnlKeypad.add(btn5);

        btn6.setBackground(new java.awt.Color(30, 32, 42));
        btn6.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        btn6.setForeground(new java.awt.Color(210, 76, 75));
        btn6.setText("6");
        btn6.setBorder(null);
        btn6.setBorderPainted(false);
        btn6.setFocusPainted(false);
        btn6.setFocusable(false);
        btn6.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btn6.setOpaque(true);
        btn6.addActionListener(this::btn6ActionPerformed);
        pnlKeypad.add(btn6);

        btnDivide.setBackground(new java.awt.Color(30, 32, 42));
        btnDivide.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        btnDivide.setForeground(new java.awt.Color(210, 76, 75));
        btnDivide.setText("/");
        btnDivide.setBorder(null);
        btnDivide.setBorderPainted(false);
        btnDivide.setFocusPainted(false);
        btnDivide.setFocusable(false);
        btnDivide.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btnDivide.setOpaque(true);
        btnDivide.addActionListener(this::btnDivideActionPerformed);
        pnlKeypad.add(btnDivide);

        btnReciprocal.setBackground(new java.awt.Color(30, 32, 42));
        btnReciprocal.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        btnReciprocal.setForeground(new java.awt.Color(210, 76, 75));
        btnReciprocal.setText("1/x");
        btnReciprocal.setBorder(null);
        btnReciprocal.setBorderPainted(false);
        btnReciprocal.setFocusPainted(false);
        btnReciprocal.setFocusable(false);
        btnReciprocal.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btnReciprocal.setOpaque(true);
        btnReciprocal.addActionListener(this::btnReciprocalActionPerformed);
        pnlKeypad.add(btnReciprocal);

        btn1.setBackground(new java.awt.Color(30, 32, 42));
        btn1.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        btn1.setForeground(new java.awt.Color(210, 76, 75));
        btn1.setText("1");
        btn1.setBorder(null);
        btn1.setBorderPainted(false);
        btn1.setFocusPainted(false);
        btn1.setFocusable(false);
        btn1.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btn1.setOpaque(true);
        btn1.addActionListener(this::btn1ActionPerformed);
        pnlKeypad.add(btn1);

        btn2.setBackground(new java.awt.Color(30, 32, 42));
        btn2.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        btn2.setForeground(new java.awt.Color(210, 76, 75));
        btn2.setText("2");
        btn2.setBorder(null);
        btn2.setBorderPainted(false);
        btn2.setFocusPainted(false);
        btn2.setFocusable(false);
        btn2.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btn2.setOpaque(true);
        btn2.addActionListener(this::btn2ActionPerformed);
        pnlKeypad.add(btn2);

        btn3.setBackground(new java.awt.Color(30, 32, 42));
        btn3.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        btn3.setForeground(new java.awt.Color(210, 76, 75));
        btn3.setText("3");
        btn3.setBorder(null);
        btn3.setBorderPainted(false);
        btn3.setFocusPainted(false);
        btn3.setFocusable(false);
        btn3.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btn3.setOpaque(true);
        btn3.addActionListener(this::btn3ActionPerformed);
        pnlKeypad.add(btn3);

        btnSubtract.setBackground(new java.awt.Color(30, 32, 42));
        btnSubtract.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        btnSubtract.setForeground(new java.awt.Color(210, 76, 75));
        btnSubtract.setText("-");
        btnSubtract.setBorder(null);
        btnSubtract.setBorderPainted(false);
        btnSubtract.setFocusPainted(false);
        btnSubtract.setFocusable(false);
        btnSubtract.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btnSubtract.setOpaque(true);
        btnSubtract.addActionListener(this::btnSubtractActionPerformed);
        pnlKeypad.add(btnSubtract);

        btnPercent.setBackground(new java.awt.Color(30, 32, 42));
        btnPercent.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        btnPercent.setForeground(new java.awt.Color(210, 76, 75));
        btnPercent.setText("%");
        btnPercent.setBorder(null);
        btnPercent.setBorderPainted(false);
        btnPercent.setFocusPainted(false);
        btnPercent.setFocusable(false);
        btnPercent.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btnPercent.setOpaque(true);
        btnPercent.addActionListener(this::btnPercentActionPerformed);
        pnlKeypad.add(btnPercent);

        btnDecimal.setBackground(new java.awt.Color(30, 32, 42));
        btnDecimal.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        btnDecimal.setForeground(new java.awt.Color(210, 76, 75));
        btnDecimal.setText(".");
        btnDecimal.setBorder(null);
        btnDecimal.setBorderPainted(false);
        btnDecimal.setFocusPainted(false);
        btnDecimal.setFocusable(false);
        btnDecimal.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btnDecimal.setOpaque(true);
        btnDecimal.addActionListener(this::btnDecimalActionPerformed);
        pnlKeypad.add(btnDecimal);

        btn0.setBackground(new java.awt.Color(30, 32, 42));
        btn0.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        btn0.setForeground(new java.awt.Color(210, 76, 75));
        btn0.setText("0");
        btn0.setBorder(null);
        btn0.setBorderPainted(false);
        btn0.setFocusPainted(false);
        btn0.setFocusable(false);
        btn0.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btn0.setOpaque(true);
        btn0.addActionListener(this::btn0ActionPerformed);
        pnlKeypad.add(btn0);

        btnSign.setBackground(new java.awt.Color(30, 32, 42));
        btnSign.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        btnSign.setForeground(new java.awt.Color(210, 76, 75));
        btnSign.setText("+/-");
        btnSign.setBorder(null);
        btnSign.setBorderPainted(false);
        btnSign.setFocusPainted(false);
        btnSign.setFocusable(false);
        btnSign.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btnSign.setOpaque(true);
        btnSign.addActionListener(this::btnSignActionPerformed);
        pnlKeypad.add(btnSign);

        btnAdd.setBackground(new java.awt.Color(30, 32, 42));
        btnAdd.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        btnAdd.setForeground(new java.awt.Color(210, 76, 75));
        btnAdd.setText("+");
        btnAdd.setBorder(null);
        btnAdd.setBorderPainted(false);
        btnAdd.setFocusPainted(false);
        btnAdd.setFocusable(false);
        btnAdd.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btnAdd.setOpaque(true);
        btnAdd.addActionListener(this::btnAddActionPerformed);
        pnlKeypad.add(btnAdd);

        btnEquals.setBackground(new java.awt.Color(30, 32, 42));
        btnEquals.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        btnEquals.setForeground(new java.awt.Color(210, 76, 75));
        btnEquals.setText("=");
        btnEquals.setBorder(null);
        btnEquals.setBorderPainted(false);
        btnEquals.setFocusPainted(false);
        btnEquals.setFocusable(false);
        btnEquals.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btnEquals.setOpaque(true);
        btnEquals.addActionListener(this::btnEqualsActionPerformed);
        pnlKeypad.add(btnEquals);

        pnlTitleBar.setBackground(new java.awt.Color(30, 32, 42));
        pnlTitleBar.setPreferredSize(new java.awt.Dimension(501, 35));

        lblTitle.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(210, 76, 75));
        lblTitle.setText("CALCULATOR");

        btnClose.setBackground(new java.awt.Color(30, 32, 42));
        btnClose.setFont(new java.awt.Font("Arial", 1, 20)); // NOI18N
        btnClose.setForeground(new java.awt.Color(210, 76, 75));
        btnClose.setText("×");
        btnClose.setBorder(null);
        btnClose.setBorderPainted(false);
        btnClose.setContentAreaFilled(false);
        btnClose.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnClose.setFocusable(false);
        btnClose.setPreferredSize(new java.awt.Dimension(25, 25));
        btnClose.addActionListener(this::btnCloseActionPerformed);

        btnMinimize.setBackground(new java.awt.Color(30, 32, 42));
        btnMinimize.setFont(new java.awt.Font("Arial", 1, 20)); // NOI18N
        btnMinimize.setForeground(new java.awt.Color(210, 76, 75));
        btnMinimize.setText("−");
        btnMinimize.setBorder(null);
        btnMinimize.setBorderPainted(false);
        btnMinimize.setContentAreaFilled(false);
        btnMinimize.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnMinimize.setFocusable(false);
        btnMinimize.setPreferredSize(new java.awt.Dimension(25, 25));
        btnMinimize.addActionListener(this::btnMinimizeActionPerformed);

        btnMaximize.setBackground(new java.awt.Color(30, 32, 42));
        btnMaximize.setFont(new java.awt.Font("Segoe UI Symbol", 1, 15)); // NOI18N
        btnMaximize.setForeground(new java.awt.Color(210, 76, 75));
        btnMaximize.setText("▢");
        btnMaximize.setBorder(null);
        btnMaximize.setBorderPainted(false);
        btnMaximize.setContentAreaFilled(false);
        btnMaximize.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnMaximize.setFocusable(false);
        btnMaximize.setPreferredSize(new java.awt.Dimension(25, 25));
        btnMaximize.addActionListener(this::btnMaximizeActionPerformed);

        javax.swing.GroupLayout pnlTitleBarLayout = new javax.swing.GroupLayout(pnlTitleBar);
        pnlTitleBar.setLayout(pnlTitleBarLayout);
        pnlTitleBarLayout.setHorizontalGroup(
            pnlTitleBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTitleBarLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(lblTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 263, Short.MAX_VALUE)
                .addComponent(btnMinimize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMaximize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnlTitleBarLayout.setVerticalGroup(
            pnlTitleBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTitleBarLayout.createSequentialGroup()
                .addGroup(pnlTitleBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnMaximize, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlTitleBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblTitle)
                        .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnMinimize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlDisplay, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlKeypad, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlTitleBar, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(pnlTitleBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(pnlDisplay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(pnlKeypad, javax.swing.GroupLayout.DEFAULT_SIZE, 636, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCEActionPerformed
        perform(engine::clearEntry);
    }//GEN-LAST:event_btnCEActionPerformed

    private void btnCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCActionPerformed
        perform(engine::clearAll);
    }//GEN-LAST:event_btnCActionPerformed

    private void btnDELActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDELActionPerformed
        perform(engine::deleteLast);
    }//GEN-LAST:event_btnDELActionPerformed

    private void btnMultiplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMultiplyActionPerformed
        perform(() -> engine.chooseOperator("*"));
    }//GEN-LAST:event_btnMultiplyActionPerformed

    private void btn7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn7ActionPerformed
        perform(() -> engine.appendDigit("7"));
    }//GEN-LAST:event_btn7ActionPerformed

    private void btn8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn8ActionPerformed
        perform(() -> engine.appendDigit("8"));
    }//GEN-LAST:event_btn8ActionPerformed

    private void btn9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn9ActionPerformed
        perform(() -> engine.appendDigit("9"));
    }//GEN-LAST:event_btn9ActionPerformed

    private void btnDivideActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDivideActionPerformed
        perform(() -> engine.chooseOperator("/"));
    }//GEN-LAST:event_btnDivideActionPerformed

    private void btn4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn4ActionPerformed
        perform(() -> engine.appendDigit("4"));
    }//GEN-LAST:event_btn4ActionPerformed

    private void btn5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn5ActionPerformed
        perform(() -> engine.appendDigit("5"));
    }//GEN-LAST:event_btn5ActionPerformed

    private void btn6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn6ActionPerformed
        perform(() -> engine.appendDigit("6"));
    }//GEN-LAST:event_btn6ActionPerformed

    private void btnSubtractActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSubtractActionPerformed
        perform(() -> engine.chooseOperator("-"));
    }//GEN-LAST:event_btnSubtractActionPerformed

    private void btn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn1ActionPerformed
        perform(() -> engine.appendDigit("1"));
    }//GEN-LAST:event_btn1ActionPerformed

    private void btn2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn2ActionPerformed
        perform(() -> engine.appendDigit("2"));
    }//GEN-LAST:event_btn2ActionPerformed

    private void btn3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn3ActionPerformed
        perform(() -> engine.appendDigit("3"));
    }//GEN-LAST:event_btn3ActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        perform(() -> engine.chooseOperator("+"));
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnDecimalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDecimalActionPerformed
        perform(engine::appendDecimal);
    }//GEN-LAST:event_btnDecimalActionPerformed

    private void btn0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn0ActionPerformed
        perform(() -> engine.appendDigit("0"));
    }//GEN-LAST:event_btn0ActionPerformed

    private void btnSignActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSignActionPerformed
        perform(engine::toggleSign);
    }//GEN-LAST:event_btnSignActionPerformed

    private void btnEqualsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEqualsActionPerformed
        perform(engine::calculate);
    }//GEN-LAST:event_btnEqualsActionPerformed

    private void txtDisplayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDisplayActionPerformed
    }//GEN-LAST:event_txtDisplayActionPerformed

    private void btnSquareRootActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSquareRootActionPerformed
        perform(engine::squareRoot);
    }//GEN-LAST:event_btnSquareRootActionPerformed

    private void btnSquareActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSquareActionPerformed
        perform(engine::square);
    }//GEN-LAST:event_btnSquareActionPerformed

    private void btnPowerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPowerActionPerformed
        perform(() -> engine.chooseOperator("^"));
    }//GEN-LAST:event_btnPowerActionPerformed

    private void btnReciprocalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReciprocalActionPerformed
        perform(engine::reciprocal);
    }//GEN-LAST:event_btnReciprocalActionPerformed

    private void btnPercentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPercentActionPerformed
        perform(engine::percent);
    }//GEN-LAST:event_btnPercentActionPerformed

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        dispose();
    }//GEN-LAST:event_btnCloseActionPerformed

    private void btnMinimizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMinimizeActionPerformed
        setState(javax.swing.JFrame.ICONIFIED);
    }//GEN-LAST:event_btnMinimizeActionPerformed

    private void btnMaximizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMaximizeActionPerformed
        if (getExtendedState() == javax.swing.JFrame.MAXIMIZED_BOTH) {
            setExtendedState(javax.swing.JFrame.NORMAL);
            btnMaximize.setText("▢");
        } else {
            setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
            btnMaximize.setText("⧉");
        }
    }//GEN-LAST:event_btnMaximizeActionPerformed

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new CalculatorFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn0;
    private javax.swing.JButton btn1;
    private javax.swing.JButton btn2;
    private javax.swing.JButton btn3;
    private javax.swing.JButton btn4;
    private javax.swing.JButton btn5;
    private javax.swing.JButton btn6;
    private javax.swing.JButton btn7;
    private javax.swing.JButton btn8;
    private javax.swing.JButton btn9;
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnC;
    private javax.swing.JButton btnCE;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnDEL;
    private javax.swing.JButton btnDecimal;
    private javax.swing.JButton btnDivide;
    private javax.swing.JButton btnEquals;
    private javax.swing.JButton btnMaximize;
    private javax.swing.JButton btnMinimize;
    private javax.swing.JButton btnMultiply;
    private javax.swing.JButton btnPercent;
    private javax.swing.JButton btnPower;
    private javax.swing.JButton btnReciprocal;
    private javax.swing.JButton btnSign;
    private javax.swing.JButton btnSquare;
    private javax.swing.JButton btnSquareRoot;
    private javax.swing.JButton btnSubtract;
    private javax.swing.JLabel lblResult;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel pnlDisplay;
    private javax.swing.JPanel pnlKeypad;
    private javax.swing.JPanel pnlTitleBar;
    private javax.swing.JTextField txtDisplay;
    // End of variables declaration//GEN-END:variables
}

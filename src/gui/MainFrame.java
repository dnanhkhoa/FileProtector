package gui;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Observable;
import java.util.Observer;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import algorithm.AES;
import algorithm.DES;
import core.Processor;
import enums.AlgorithmEnum;
import enums.ModeOfOperationEnum;
import enums.PaddingModeEnum;
import structure.ProgressInfo;

public class MainFrame extends JFrame implements Observer {

    private static final long              serialVersionUID = 1L;
    private JPanel                         mainPane;
    private JTextField                     txtInputFile;
    private JTextField                     txtOutputFile;
    private JPasswordField                 txtPassword;
    private JComboBox<AlgorithmEnum>       cbbAlgorithm;
    private JComboBox<ModeOfOperationEnum> cbbModeOfOperation;
    private JComboBox<PaddingModeEnum>     cbbPaddingMode;
    private JButton                        btnDo;
    private JProgressBar                   pbProcessInfo;
    private JLabel                         lblTimeLeftInfo;

    private Processor                      processor;
    private boolean                        isRunning;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Throwable e) {
            e.printStackTrace();
        }

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    MainFrame frame = new MainFrame();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public MainFrame() {
        // Initialize form
        setResizable(false);
        setTitle("File Protector");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 500, 350);
        mainPane = new JPanel();
        mainPane.setFont(new Font("Tahoma", Font.PLAIN, 14));
        mainPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(mainPane);
        mainPane.setLayout(new FormLayout(new ColumnSpec[] {
                FormSpecs.RELATED_GAP_COLSPEC, ColumnSpec.decode("default:grow"), FormSpecs.RELATED_GAP_COLSPEC,
        }, new RowSpec[] {
                FormSpecs.RELATED_GAP_ROWSPEC, RowSpec.decode("default:grow"), FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC,
        }));

        JPanel filePane = new JPanel();
        filePane.setFont(new Font("Tahoma", Font.PLAIN, 14));
        mainPane.add(filePane, "2, 2, fill, fill");
        filePane.setLayout(new FormLayout(new ColumnSpec[] {
                FormSpecs.DEFAULT_COLSPEC, FormSpecs.RELATED_GAP_COLSPEC, ColumnSpec.decode("default:grow"),
                FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC,
        }, new RowSpec[] {
                FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.PARAGRAPH_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.PARAGRAPH_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
        }));

        JLabel lblInputFile = new JLabel("Input file");
        lblInputFile.setFont(new Font("Tahoma", Font.PLAIN, 14));
        filePane.add(lblInputFile, "1, 1, right, default");

        txtInputFile = new JTextField();
        txtInputFile.setEditable(false);
        txtInputFile.setFont(new Font("Tahoma", Font.PLAIN, 14));
        filePane.add(txtInputFile, "3, 1, fill, default");
        txtInputFile.setColumns(10);

        JButton btnInputFile = new JButton("...");
        btnInputFile.setFocusable(false);
        btnInputFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleInputFileButton();
            }
        });

        btnInputFile.setFont(new Font("Tahoma", Font.PLAIN, 14));
        filePane.add(btnInputFile, "5, 1");

        JLabel lblOutputFile = new JLabel("Output file");
        lblOutputFile.setFont(new Font("Tahoma", Font.PLAIN, 14));
        filePane.add(lblOutputFile, "1, 3, right, default");

        txtOutputFile = new JTextField();
        txtOutputFile.setEditable(false);
        txtOutputFile.setFont(new Font("Tahoma", Font.PLAIN, 14));
        filePane.add(txtOutputFile, "3, 3, fill, default");
        txtOutputFile.setColumns(10);

        JButton btnOutputFile = new JButton("...");
        btnOutputFile.setFocusable(false);
        btnOutputFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleOutputFileButton();
            }
        });

        btnOutputFile.setFont(new Font("Tahoma", Font.PLAIN, 14));
        filePane.add(btnOutputFile, "5, 3");

        JLabel lblAlgorithm = new JLabel("Algorithm");
        lblAlgorithm.setFont(new Font("Tahoma", Font.PLAIN, 14));
        filePane.add(lblAlgorithm, "1, 5, right, default");

        cbbAlgorithm = new JComboBox<AlgorithmEnum>();
        cbbAlgorithm.setFocusable(false);
        cbbAlgorithm.setModel(new DefaultComboBoxModel<AlgorithmEnum>(AlgorithmEnum.values()));
        cbbAlgorithm.setFont(new Font("Tahoma", Font.PLAIN, 14));
        filePane.add(cbbAlgorithm, "3, 5, 3, 1, fill, default");

        JLabel lblModeOfOperation = new JLabel("Mode of operation");
        lblModeOfOperation.setFont(new Font("Tahoma", Font.PLAIN, 14));
        filePane.add(lblModeOfOperation, "1, 7, right, default");

        cbbModeOfOperation = new JComboBox<ModeOfOperationEnum>();
        cbbModeOfOperation.setFocusable(false);
        cbbModeOfOperation.setModel(new DefaultComboBoxModel<ModeOfOperationEnum>(ModeOfOperationEnum.values()));
        cbbModeOfOperation.setFont(new Font("Tahoma", Font.PLAIN, 14));
        filePane.add(cbbModeOfOperation, "3, 7, 3, 1, fill, default");

        JLabel lblPaddingMode = new JLabel("Padding mode");
        lblPaddingMode.setFont(new Font("Tahoma", Font.PLAIN, 14));
        filePane.add(lblPaddingMode, "1, 9, right, default");

        cbbPaddingMode = new JComboBox<PaddingModeEnum>();
        cbbPaddingMode.setFocusable(false);
        cbbPaddingMode.setModel(new DefaultComboBoxModel<PaddingModeEnum>(PaddingModeEnum.values()));
        cbbPaddingMode.setFont(new Font("Tahoma", Font.PLAIN, 14));
        filePane.add(cbbPaddingMode, "3, 9, 3, 1, fill, default");

        JLabel lblPassword = new JLabel("Password");
        lblPassword.setFont(new Font("Tahoma", Font.PLAIN, 14));
        filePane.add(lblPassword, "1, 11, right, default");

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Tahoma", Font.PLAIN, 14));
        filePane.add(txtPassword, "3, 11, 3, 1, fill, default");

        JPanel infoPane = new JPanel();
        infoPane.setFont(new Font("Tahoma", Font.PLAIN, 14));
        mainPane.add(infoPane, "2, 4, fill, fill");
        infoPane.setLayout(new FormLayout(new ColumnSpec[] {
                ColumnSpec.decode("default:grow"), FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC,
                FormSpecs.RELATED_GAP_COLSPEC, ColumnSpec.decode("max(60dlu;pref)"),
        }, new RowSpec[] {
                FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
        }));

        JLabel lblTimeLeft = new JLabel("Time left");
        lblTimeLeft.setFont(new Font("Tahoma", Font.PLAIN, 14));
        infoPane.add(lblTimeLeft, "1, 1");

        lblTimeLeftInfo = new JLabel("00:00:00");
        lblTimeLeftInfo.setFont(new Font("Tahoma", Font.PLAIN, 14));
        infoPane.add(lblTimeLeftInfo, "3, 1");

        btnDo = new JButton("Encrypt");
        btnDo.setFocusable(false);
        btnDo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleDoButton();
            }
        });

        btnDo.setFont(new Font("Tahoma", Font.PLAIN, 14));
        infoPane.add(btnDo, "5, 1, 1, 3");

        pbProcessInfo = new JProgressBar();
        pbProcessInfo.setFont(new Font("Tahoma", Font.PLAIN, 14));
        infoPane.add(pbProcessInfo, "1, 3, 3, 1");

        // Initialize variables
        initialize();
    }

    public void initialize() {
        processor = new Processor();
        processor.registerAlgorithm(new AES());
        processor.registerAlgorithm(new DES());

        processor.registerObserver(this);

        changeState(false);
        changeMode(true);
    }

    public void handleInputFileButton() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Open file");
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            txtInputFile.setText(selectedFile.toString());

            boolean isEncryptedMode = processor.isEncryptedFile(selectedFile);
            if (isEncryptedMode) {
                txtOutputFile.setText(txtInputFile.getText().replace(".enc", "") + ".dec");
            } else {
                txtOutputFile.setText(txtInputFile.getText() + ".enc");
            }

            // Check file
            changeMode(!isEncryptedMode);
            // Reset progress info
            clearProgressInfo();
        }
    }

    public void handleOutputFileButton() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save file");
        fileChooser.setSelectedFile(new File(txtOutputFile.getText()));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            txtOutputFile.setText(fileChooser.getSelectedFile().toString());
        }
    }

    public void handleDoButton() {
        if (isRunning) {
            JOptionPane.showMessageDialog(this, "Process is running!");
        } else {
            changeState(true);

            if (txtInputFile.getText().isEmpty() || txtOutputFile.getText().isEmpty()
                    || txtPassword.getPassword().length == 0) {
                JOptionPane.showMessageDialog(this, "Some required fields have not been entered!");
            } else {
                final Frame component = this;
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            processor.process(new File(txtInputFile.getText()), new File(txtOutputFile.getText()),
                                    String.valueOf(txtPassword.getPassword()),
                                    (AlgorithmEnum) cbbAlgorithm.getSelectedItem(),
                                    (ModeOfOperationEnum) cbbModeOfOperation.getSelectedItem(),
                                    (PaddingModeEnum) cbbPaddingMode.getSelectedItem());

                            JOptionPane.showMessageDialog(component, "Done!");
                        } catch (Exception e) {
                            e.printStackTrace();
                            JOptionPane.showMessageDialog(component, e.getMessage());
                        } finally {
                            changeState(false);
                        }
                    }
                });
                thread.start();
            }
        }
    }

    public void changeMode(boolean isEncryptMode) {
        if (isEncryptMode) {
            cbbAlgorithm.setEnabled(true);
            cbbModeOfOperation.setEnabled(true);
            cbbPaddingMode.setEnabled(true);
            btnDo.setText("Encrypt");
        } else {
            cbbAlgorithm.setEnabled(false);
            cbbModeOfOperation.setEnabled(false);
            cbbPaddingMode.setEnabled(false);
            btnDo.setText("Decrypt");
        }
    }

    public void changeState(boolean isRunning) {
        this.isRunning = isRunning;
        btnDo.setEnabled(!isRunning);
    }

    public void clearProgressInfo() {
        lblTimeLeftInfo.setText("00:00:00");
        pbProcessInfo.setValue(0);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof ProgressInfo) {
            ProgressInfo processInfo = (ProgressInfo) arg;
            lblTimeLeftInfo.setText(processInfo.getTimeLeft());
            pbProcessInfo.setValue(processInfo.getProgressValue());
        }
    }

}

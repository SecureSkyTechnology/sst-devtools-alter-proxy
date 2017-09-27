package com.sst.devtools.alterproxy.swingui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;
import com.sst.devtools.alterproxy.AppConfig;
import com.sst.devtools.alterproxy.LFSMapEntry;
import com.sst.devtools.alterproxy.LFSMappableHttpFiltersSource;
import com.sst.devtools.alterproxy.LFSMapper;
import com.sst.devtools.alterproxy.VersionInfo;

import net.lightbody.bmp.mitm.manager.ImpersonatingMitmManager;
import net.miginfocom.swing.MigLayout;

public class MainPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(MainPanel.class);

    private JButton btnAdd;
    private JButton btnUp;
    private JButton btnEdit;
    private JButton btnDown;
    private JButton btnDelete;
    private JButton btnStart;
    private JButton btnStop;
    private JTable tblLFSMapping;
    private JSpinner spnListeningPort;
    private JTextArea txtaLog;
    private JTextArea txtaVersions;

    private List<LFSMapEntry> mapEntries = new ArrayList<>();
    private final ImpersonatingMitmManager mitmManager =
        ImpersonatingMitmManager.builder().trustAllServers(true).build();
    private HttpProxyServer server;

    /**
     * Create the panel.
     */
    public MainPanel() {
        setLayout(new BorderLayout(0, 0));

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        add(tabbedPane, BorderLayout.CENTER);

        JPanel panelProxyControl = new JPanel();
        tabbedPane.addTab("Proxy Control", null, panelProxyControl, null);
        panelProxyControl.setLayout(new MigLayout("", "[100px:n,fill][100px:n,fill][grow]", "[][][][][][][grow][]"));

        JLabel lblListeningPort = new JLabel("listening port :");
        lblListeningPort.setHorizontalAlignment(SwingConstants.RIGHT);
        panelProxyControl.add(lblListeningPort, "cell 0 0");

        spnListeningPort = new JSpinner();
        spnListeningPort.setValue(AppConfig.DEFAULT_LISTENING_PORT);
        panelProxyControl.add(spnListeningPort, "cell 1 0,growx");

        btnAdd = new JButton("add");
        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                IMapEntryEditorNotifier notifier = new MapEntryEditorNotifierImpl(MainPanel.this);
                MapEntryEditDialog dlg =
                    new MapEntryEditDialog(MainPanel.this.getWindowFrame(), "add mapping", notifier);
                dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                dlg.setVisible(true);
            }
        });
        panelProxyControl.add(btnAdd, "cell 0 1");

        JScrollPane scrollPaneLFSMapping = new JScrollPane();
        panelProxyControl.add(scrollPaneLFSMapping, "cell 1 1 2 6,grow");

        tblLFSMapping = new JTable();
        tblLFSMapping.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblLFSMapping.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tblLFSMapping.setModel(
            new DefaultTableModel(
                new Object[][] {},
                new String[] {
                    "hostname",
                    "path prefix (terminate with /)",
                    "local path",
                    "\"/\" handling",
                    "targe filename extensions",
                    "text charset" }) {
                private static final long serialVersionUID = 1L;

                @SuppressWarnings("rawtypes")
                Class[] columnTypes =
                    new Class[] { String.class, String.class, String.class, String.class, String.class, String.class };

                @Override
                @SuppressWarnings({ "unchecked", "rawtypes" })
                public Class getColumnClass(int columnIndex) {
                    return columnTypes[columnIndex];
                }

                @Override
                public boolean isCellEditable(int row, int column) {
                    // disable all cell editing feature.
                    return false;
                }
            });
        tblLFSMapping.getColumnModel().getColumn(1).setPreferredWidth(200);
        tblLFSMapping.getColumnModel().getColumn(2).setPreferredWidth(200);
        tblLFSMapping.getColumnModel().getColumn(3).setPreferredWidth(100);
        tblLFSMapping.getColumnModel().getColumn(4).setPreferredWidth(150);
        tblLFSMapping.getColumnModel().getColumn(5).setPreferredWidth(100);
        tblLFSMapping.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                if (me.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(me)) {
                    MainPanel.this.onEditButtonClicked();
                }
            }
        });
        scrollPaneLFSMapping.setViewportView(tblLFSMapping);

        btnUp = new JButton("up");
        btnUp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final int selectedIndex = tblLFSMapping.getSelectedRow();
                if (0 > selectedIndex) {
                    return;
                }
                final int newSelectedIndex = selectedIndex - 1;
                if (0 > newSelectedIndex) {
                    return;
                }

                LFSMapEntry m = mapEntries.get(selectedIndex);
                mapEntries.set(selectedIndex, mapEntries.get(newSelectedIndex));
                mapEntries.set(newSelectedIndex, m);

                DefaultTableModel model = (DefaultTableModel) tblLFSMapping.getModel();
                model.moveRow(selectedIndex, selectedIndex, newSelectedIndex);
                tblLFSMapping.setRowSelectionInterval(newSelectedIndex, newSelectedIndex);
            }
        });
        panelProxyControl.add(btnUp, "cell 0 2");

        btnEdit = new JButton("edit");
        btnEdit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MainPanel.this.onEditButtonClicked();
            }
        });
        panelProxyControl.add(btnEdit, "cell 0 3");

        btnDown = new JButton("down");
        btnDown.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final int selectedIndex = tblLFSMapping.getSelectedRow();
                if (0 > selectedIndex) {
                    return;
                }
                final int newSelectedIndex = selectedIndex + 1;
                if (newSelectedIndex > (tblLFSMapping.getRowCount() - 1)) {
                    return;
                }

                LFSMapEntry m = mapEntries.get(selectedIndex);
                mapEntries.set(selectedIndex, mapEntries.get(newSelectedIndex));
                mapEntries.set(newSelectedIndex, m);

                DefaultTableModel model = (DefaultTableModel) tblLFSMapping.getModel();
                model.moveRow(selectedIndex, selectedIndex, newSelectedIndex);
                tblLFSMapping.setRowSelectionInterval(newSelectedIndex, newSelectedIndex);
            }
        });
        panelProxyControl.add(btnDown, "cell 0 4");

        btnDelete = new JButton("delete");
        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final int selectedIndex = tblLFSMapping.getSelectedRow();
                if (0 > selectedIndex) {
                    return;
                }

                mapEntries.remove(selectedIndex);

                DefaultTableModel model = (DefaultTableModel) tblLFSMapping.getModel();
                model.removeRow(selectedIndex);
                tblLFSMapping.clearSelection();
            }
        });
        panelProxyControl.add(btnDelete, "cell 0 5");

        btnStart = new JButton("start");
        btnStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveConfig();
                LFSMapper lfsm = new LFSMapper();
                for (LFSMapEntry m : mapEntries) {
                    lfsm.addMap(m);
                }
                final int portnum = Integer.parseInt(spnListeningPort.getValue().toString());
                LOG.info("server starting at port:{} ...", portnum);
                server =
                    DefaultHttpProxyServer
                        .bootstrap()
                        .withAddress(new InetSocketAddress("0.0.0.0", portnum))
                        .withManInTheMiddle(mitmManager)
                        .withFiltersSource(new LFSMappableHttpFiltersSource(lfsm))
                        .start();
                LOG.info("server started at port:{}", portnum);
                btnAdd.setEnabled(false);
                btnUp.setEnabled(false);
                btnEdit.setEnabled(false);
                btnDown.setEnabled(false);
                btnDelete.setEnabled(false);
                btnStart.setEnabled(false);
                btnStop.setEnabled(true);
                JOptionPane.showMessageDialog(getWindowFrame(), "server started successfuly at port:" + portnum);
            }
        });
        panelProxyControl.add(btnStart, "cell 0 7");

        btnStop = new JButton("stop");
        btnStop.setEnabled(false);
        btnStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (Objects.nonNull(server)) {
                    LOG.info("server stopping ...");
                    server.stop();
                    LOG.info("server stopped.");
                    btnAdd.setEnabled(true);
                    btnUp.setEnabled(true);
                    btnEdit.setEnabled(true);
                    btnDown.setEnabled(true);
                    btnDelete.setEnabled(true);
                    btnStart.setEnabled(true);
                    btnStop.setEnabled(false);
                    JOptionPane.showMessageDialog(getWindowFrame(), "server stopped.");
                }
            }
        });
        panelProxyControl.add(btnStop, "cell 1 7");

        JPanel panelLog = new JPanel();
        tabbedPane.addTab("Log", null, panelLog, null);
        panelLog.setLayout(new GridLayout(1, 0, 0, 0));

        JScrollPane scrollPaneLog = new JScrollPane();
        scrollPaneLog.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPaneLog.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        panelLog.add(scrollPaneLog);

        txtaLog = new JTextArea();
        txtaLog.setEditable(false);
        scrollPaneLog.setViewportView(txtaLog);

        JPanel panelVersions = new JPanel();
        tabbedPane.addTab("Version", null, panelVersions, null);
        panelVersions.setLayout(new GridLayout(1, 0, 0, 0));

        JScrollPane scrollPaneVersions = new JScrollPane();
        scrollPaneVersions.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPaneVersions.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        panelVersions.add(scrollPaneVersions);

        txtaVersions = new JTextArea();
        txtaVersions.setEditable(false);
        scrollPaneVersions.setViewportView(txtaVersions);

        loadConfig();
        LogbackSwingTextareaAppender.addToRootLogger(txtaLog);
    }

    public void loadConfig() {
        if (AppConfig.DEFAULT_CONFIG_FILE.exists()) {
            try {
                AppConfig config = AppConfig.load(AppConfig.DEFAULT_CONFIG_FILE);
                spnListeningPort.setValue(config.getPort());
                mapEntries = config.convertToLFSMapEntries();
                for (LFSMapEntry m : mapEntries) {
                    DefaultTableModel model = (DefaultTableModel) tblLFSMapping.getModel();
                    model.addRow(m.toJTableRow());
                }
                LOG.info("configuration loaded successfully");
            } catch (IOException ex) {
                LOG.error("configuration load failed.", ex);
                JOptionPane.showMessageDialog(
                    this.getWindowFrame(),
                    ex.getMessage(),
                    "configuration load failed.",
                    JOptionPane.WARNING_MESSAGE);
            }
        }
        StringBuilder versionInfo = new StringBuilder();
        versionInfo.append(VersionInfo.getManifestInfo());
        versionInfo.append("\n----------------------------\n");
        versionInfo.append(VersionInfo.getThirdPartyLicenses());
        versionInfo.append("\n----------------------------\n");
        versionInfo.append(VersionInfo.getSystemProperties());
        txtaVersions.setText(versionInfo.toString());
        txtaVersions.setCaretPosition(0);
    }

    public void saveConfig() {
        AppConfig config = new AppConfig();
        config.setPort(Integer.parseInt(spnListeningPort.getValue().toString()));
        config.updateMapEntries(mapEntries);
        try {
            config.save(AppConfig.DEFAULT_CONFIG_FILE);
            LOG.info("configuration saved successfully");
        } catch (IOException ex) {
            LOG.error("configuration save failed.", ex);
            JOptionPane.showMessageDialog(
                this.getWindowFrame(),
                Throwables.getStackTraceAsString(ex),
                "configuration save failed  : " + ex.getMessage(),
                JOptionPane.WARNING_MESSAGE);
        }
    }

    public JFrame getWindowFrame() {
        return (JFrame) SwingUtilities.getWindowAncestor(this);
    }

    public void addLFSMapEntry(LFSMapEntry newEntry) {
        mapEntries.add(newEntry);
        DefaultTableModel model = (DefaultTableModel) tblLFSMapping.getModel();
        model.addRow(newEntry.toJTableRow());
    }

    public void onEditButtonClicked() {
        final int selectedIndex = tblLFSMapping.getSelectedRow();
        if (0 > selectedIndex) {
            return;
        }
        LFSMapEntry m = mapEntries.get(selectedIndex);
        IMapEntryEditorNotifier notifier = new MapEntryEditorNotifierImpl(MainPanel.this, selectedIndex, m);

        MapEntryEditDialog dlg = new MapEntryEditDialog(MainPanel.this.getWindowFrame(), "edit mapping", notifier);
        dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dlg.setVisible(true);
    }

    public void updateLFSMapEntry(LFSMapEntry newEntry, int replaceIndex) {
        mapEntries.set(replaceIndex, newEntry);
        DefaultTableModel model = (DefaultTableModel) tblLFSMapping.getModel();
        Object[] newColumns = newEntry.toJTableRow();
        for (int i = 0; i < newColumns.length; i++) {
            model.setValueAt(newColumns[i], replaceIndex, i);
        }
    }
}

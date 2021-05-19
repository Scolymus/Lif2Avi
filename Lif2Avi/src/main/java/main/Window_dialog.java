package main;
/*
 * To the extent possible under law, the ImageJ developers have waived
 * all copyright and related or neighboring rights to this tutorial code.
 *
 * See the Unlicense for details:
 *     https://unlicense.org/
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.NumberFormatter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.imagej.ops.OpService;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;

import org.scijava.Context;
import org.scijava.app.StatusService;
import org.scijava.command.CommandService;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.thread.ThreadService;
import org.scijava.ui.UIService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.measure.Calibration;
import ij.plugin.Duplicator;
import ij.text.TextPanel;
import ij.text.TextWindow;

public class Window_dialog {

	@Parameter
	private OpService ops;

	@Parameter
	private LogService log;

	@Parameter
	private StatusService status;

	@Parameter
	private CommandService cmd;

	@Parameter
	private ThreadService thread;

	@Parameter
	private UIService ui;

	
	//General Variables
	public String filename = "";
	public JFrame frmAuto;
	private int[] Mag;
	private String[] states = {"Local input","Local input"};
	private Calibration cal;
	String lista_auto_main[][]; 
	boolean ffmpeg = true;		
	
	//General panel
	private JFormattedTextField txt_FPS;	
	JTextField txt_route;
	private JComboBox cmb_time;
	private JComboBox cmb_scale;
	private JComboBox cmb_mode;
	private JCheckBox chk_scale;
	private JCheckBox chk_time;
	private JCheckBox chk_contrast;
	private JTabbedPane Tbp;
	private JCheckBox chk_divide;
	private JButton btn_save;
	JButton btn_browse;
	
	//Scale panel
	private JComboBox cmb_magnification;
	private JComboBox cmb_units;
	private JComboBox cmb_scale_location;
	private JComboBox cmb_colour_scale;
	private JComboBox cmb_colour_units;
	private JCheckBox ckb_serif;
	private JCheckBox ckb_bold;
	private JCheckBox ckb_overlay;
	private JCheckBox ckb_scale_all;	
	public boolean conf_mag = false;
	public boolean conf_unitx = false;
	public boolean conf_unitb = false;	
	private JTextField txt_scale;
	private JTextField txt_scale_height;
	private JTextField txt_font_size;
	
	//Timer panel
	private JRadioButton rdb_units;
	private JRadioButton rdb_zero;
	private JComboBox cmb_time_units;
	private JCheckBox ckb_time_anti;
	private JLabel lbl_decimal;
	public boolean conf_unitt = false;
	private JTextField txt_time_start;
	private JTextField txt_time_interval;
	private JTextField txt_decimal;
	private JTextField txt_time_x;
	private JTextField txt_time_y;
	private JTextField txt_time_font;
	private JTextField txt_route_ffmpeg;

	//Bulk panel
	private JList lst_bulk;
	final DefaultListModel<String> lst_bulk_model = new DefaultListModel<String>();
	JCheckBox chk_bulk;

	//Others panel
	private JCheckBox chk_time_step;
	private JComboBox cmb_binning;
	private JLabel lbl_Binning;
	private JCheckBox chk_metadata;
	private JScrollPane scrollPane;
	private JTextField txt_contrast;
	
	/**
	 * Initialize the contents of the frame.
	 */
	public Window_dialog() {
		frmAuto = new JFrame();
		frmAuto.setTitle("AutoSave plugin!");
		frmAuto.setResizable(false);
		frmAuto.setBounds(100, 100, 483, 205);
		frmAuto.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frmAuto.getContentPane().setLayout(null);
		
		Tbp = new JTabbedPane(JTabbedPane.TOP);
		Tbp.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if(Tbp.getSelectedIndex()==0 && chk_scale!=null){
					if(chk_scale.isSelected()){
						load_xml("sx");
						try {
							cmb_scale.setSelectedItem(states[0]);
						} catch (Exception e) {
						}
					}
					if(chk_time.isSelected()){
						load_xml("st");
						try {
							cmb_time.setSelectedItem(states[1]);
						} catch (Exception e) {
						}
					}
				}
				if (cmb_time_units!=null) {
					lista_auto_main = auto_detect(lista_auto_main);
				}
			}
		});
		Tbp.setToolTipText("");
		Tbp.setBounds(5, 11, 467, 164);
		frmAuto.getContentPane().add(Tbp);
		
		JPanel panel = new JPanel();
		Tbp.addTab("General", null, panel, null);
		panel.setLayout(null);
		
		cmb_mode = new JComboBox();
		cmb_mode.setModel(new DefaultComboBoxModel(new String[] {"AVI-JPEG", "AVI-PNG"}));
		cmb_mode.setBounds(125, 104, 89, 20);
		panel.add(cmb_mode);
		
		JLabel label = new JLabel("Mode:");
		label.setBounds(83, 107, 49, 14);
		panel.add(label);
		
	    NumberFormatter formatter = new NumberFormatter(NumberFormat.getInstance());
	    formatter.setValueClass(Integer.class);
	    formatter.setMinimum(1);
	    formatter.setMaximum(Integer.MAX_VALUE);
	    formatter.setAllowsInvalid(false);
	    formatter.setCommitsOnValidEdit(true);
		txt_FPS = new JFormattedTextField(formatter);				
		txt_FPS.addFocusListener(new FocusAdapter() {
	        @Override
			public void focusGained(FocusEvent arg0) {
	        	lista_auto_main = auto_detect(lista_auto_main);
			}
		});
		txt_FPS.setText("25");
		txt_FPS.setBounds(39, 104, 37, 20);
		panel.add(txt_FPS);
		
		JLabel label_1 = new JLabel("FPS:");
		label_1.setBounds(10, 107, 28, 14);
		panel.add(label_1);
		
		JLabel label_2 = new JLabel("Save in...");
		label_2.setBounds(10, 15, 56, 14);
		panel.add(label_2);
		
		txt_route = new JTextField();
		txt_route.setColumns(10);
		txt_route.setBounds(68, 12, 292, 20);
		panel.add(txt_route);
		
		btn_browse = new JButton("Browse...");
		btn_browse.addActionListener(new ActionListener() {
		      public void actionPerformed(ActionEvent ev) {
		    	  Browse("route");
		      }
		});		
		btn_browse.setBounds(362, 11, 89, 23);
		panel.add(btn_browse);
		
		btn_save = new JButton("SAVE VIDEOS!");
		btn_save.setBounds(329, 99, 122, 31);
		btn_save.addActionListener(new ActionListener() {
		      public void actionPerformed(ActionEvent ev) {
		    	  Save();
		      }
		});		
		panel.add(btn_save);
		
		cmb_scale = new JComboBox();
		cmb_scale.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				if (cmb_scale.getItemCount()!=0){
					if (!cmb_scale.getSelectedItem().toString().equals("Local input")){
						states[0] = cmb_scale.getSelectedItem().toString();						
						carga_conf("sx",cmb_scale.getSelectedItem().toString());
					}
				}
			}
		});
		cmb_scale.setEnabled(false);
		cmb_scale.setBounds(144, 74, 146, 20);
		panel.add(cmb_scale);
		load_xml("sx");
		
		final JLabel lbl_loadcnf = new JLabel("Load configurations:");
		lbl_loadcnf.setEnabled(false);
		lbl_loadcnf.setBounds(10, 77, 122, 14);
		panel.add(lbl_loadcnf);
		
		cmb_time = new JComboBox();
		cmb_time.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				if (cmb_time.getItemCount()!=0){
					if (!cmb_time.getSelectedItem().toString().equals("Local input")){
						states[1] = cmb_time.getSelectedItem().toString();						
						carga_conf("st",cmb_time.getSelectedItem().toString());
					}
				}
			}
		});
		cmb_time.setEnabled(false);
		cmb_time.setBounds(305, 73, 146, 20);
		panel.add(cmb_time);
		load_xml("st");
		
		chk_scale = new JCheckBox("Include scale bar");
		chk_scale.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if (chk_scale.isSelected()==true) {
					cmb_scale.setEnabled(true);
					chk_divide.setEnabled(true);
					lbl_loadcnf.setEnabled(true);
					load_xml("sx");
					try {
						cmb_scale.setSelectedItem(states[0]);
					} catch (Exception e) {
					}
				}else{
					cmb_scale.setEnabled(false);
					if (chk_time.isSelected()==false){
						lbl_loadcnf.setEnabled(false);
						if (!chk_contrast.isSelected()){
							chk_divide.setSelected(false);
							chk_divide.setEnabled(false);
						}
					}
				}
			}
		});
		chk_scale.setBounds(113, 40, 122, 23);
		panel.add(chk_scale);
		
		chk_time = new JCheckBox("Include time");
		chk_time.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {			
				if (chk_time.isSelected()==true) {
					cmb_time.setEnabled(true);
					lbl_loadcnf.setEnabled(true);
					chk_divide.setEnabled(true);
					load_xml("st");
					try {
						cmb_time.setSelectedItem(states[1]);
					} catch (Exception e) {
					}
				}else{
					cmb_time.setEnabled(false);		
					if (chk_scale.isSelected()==false){
						lbl_loadcnf.setEnabled(false);
						chk_divide.setSelected(false);
						chk_divide.setEnabled(false);
					}
				}
			}
		});
		
		chk_time.setBounds(240, 40, 95, 23);
		panel.add(chk_time);
		
		chk_divide = new JCheckBox("Divide videos");
		chk_divide.setEnabled(false);
		chk_divide.setBounds(222, 103, 99, 23);
		panel.add(chk_divide);
		
		chk_bulk = new JCheckBox("Bulk process");
		chk_bulk.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if (chk_bulk.isSelected()){
					btn_browse.setEnabled(false);
					txt_route.setEnabled(false);
				}else{
					btn_browse.setEnabled(true);
					txt_route.setEnabled(true);
				}				
			}
		});
		chk_bulk.setBounds(10, 40, 131, 23);
		panel.add(chk_bulk);
		
		chk_contrast = new JCheckBox("Auto Contrast");
		chk_contrast.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {			
				if (chk_contrast.isSelected()==true) {
					chk_divide.setEnabled(true);
					chk_divide.setSelected(true);
				}
			}
		});
		chk_contrast.setBounds(339, 40, 112, 24);
		panel.add(chk_contrast);
		
		JPanel panel_1 = new JPanel();
		Tbp.addTab("Scale bar", null, panel_1, null);
		panel_1.setLayout(null);
		
		JLabel lbl_mag = new JLabel("Magnification:");
		lbl_mag.setBounds(10, 14, 82, 14);
		panel_1.add(lbl_mag);
		
		cmb_magnification = new JComboBox();
		cmb_magnification.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				lista_auto_main = auto_detect(lista_auto_main);
			}
		});
		cmb_magnification.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				if (conf_mag == false &&
						cmb_magnification.
						getItemAt(cmb_magnification.getSelectedIndex()).toString().equals("New...")){
					cmb_magnification.setSelectedIndex(0);
					conf_mag = true;
					New_configuration frmAuto2 = new New_configuration();
					String[] pasa = {"Name:","Magnification","true","g"};
					frmAuto2.main(pasa);
					if (frmAuto2.accept == true){
						load_xml("g");
					}					
				}
				if (cmb_magnification.getItemCount()!=0){
					if (!cmb_magnification.
						getItemAt(cmb_magnification.getSelectedIndex()).toString().equals("New...")){
					conf_mag = false;					
					}
				}
			}
		});
		//cmb_magnification.setModel(new DefaultComboBoxModel(new String[] {"4", "10", "20", "40", "63", "New..."}));
		load_xml("g");
		cmb_magnification.setBounds(95, 11, 74, 20);
		panel_1.add(cmb_magnification);
		
		JLabel lblNewLabel_1 = new JLabel("Units:");
		lblNewLabel_1.setBounds(310, 14, 46, 14);
		panel_1.add(lblNewLabel_1);
		
		cmb_units = new JComboBox();
		cmb_units.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				lista_auto_main = auto_detect(lista_auto_main);
			}
		});
		cmb_units.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				if (conf_unitx == false &&
						cmb_units.
						getItemAt(cmb_units.getSelectedIndex()).toString().equals("New...")){
					cmb_units.setSelectedIndex(0);
					conf_unitx = true;
					New_configuration frmAuto2 = new New_configuration();
					String[] pasa = {"Name:","Space units","false","x"};
					frmAuto2.main(pasa);
					if (frmAuto2.accept == true){						
						load_xml("x");
					}					
				}
				if (cmb_units.getItemCount()!=0){
					if (!cmb_units.
						getItemAt(cmb_units.getSelectedIndex()).toString().equals("New...")){
						conf_unitx = false;					
					}
				}
			}
		});
		//cmb_units.setModel(new DefaultComboBoxModel(new String[] {"mm", "um", "nm", "Do not show", "New..."}));
		load_xml("x");
		cmb_units.setBounds(350, 11, 101, 20);
		panel_1.add(cmb_units);
		
		JLabel lblLocation = new JLabel("Location:");
		lblLocation.setBounds(10, 110, 54, 14);
		panel_1.add(lblLocation);
		
		JLabel lblSize = new JLabel("Scale bar size:");
		lblSize.setBounds(10, 82, 90, 14);
		panel_1.add(lblSize);
		
		txt_scale = new JTextField();
		txt_scale.setHorizontalAlignment(SwingConstants.CENTER);
		txt_scale.setText("100");
		txt_scale.setColumns(10);
		txt_scale.setBounds(103, 79, 54, 20);
		panel_1.add(txt_scale);
		
		JLabel lblHeightpx = new JLabel("Height (px):");
		lblHeightpx.setBounds(167, 82, 74, 14);
		panel_1.add(lblHeightpx);
		
		txt_scale_height = new JTextField();
		txt_scale_height.setHorizontalAlignment(SwingConstants.CENTER);
		txt_scale_height.setText("10");
		txt_scale_height.setColumns(10);
		txt_scale_height.setBounds(246, 79, 74, 20);
		panel_1.add(txt_scale_height);
		
		ckb_scale_all = new JCheckBox("Label all frames");
		ckb_scale_all.setSelected(true);
		ckb_scale_all.setBounds(330, 79, 121, 23);
		panel_1.add(ckb_scale_all);
		
		cmb_scale_location = new JComboBox();
		cmb_scale_location.setModel(new DefaultComboBoxModel(new String[] {"Upper Right", "Lower Right", "Upper Left", "Lower Left", "At Selection"}));
		cmb_scale_location.setBounds(66, 107, 95, 20);
		panel_1.add(cmb_scale_location);
		
		JLabel lblFontSize = new JLabel("Font size:");
		lblFontSize.setBounds(10, 42, 54, 14);
		panel_1.add(lblFontSize);
		
		txt_font_size = new JTextField();
		txt_font_size.setHorizontalAlignment(SwingConstants.CENTER);
		txt_font_size.setText("20");
		txt_font_size.setColumns(10);
		txt_font_size.setBounds(67, 40, 33, 20);
		panel_1.add(txt_font_size);
		
		JLabel lblColour = new JLabel("Colour:");
		lblColour.setBounds(316, 43, 46, 14);
		panel_1.add(lblColour);
		
		cmb_colour_units = new JComboBox();
		cmb_colour_units.setModel(new DefaultComboBoxModel(new String[] {"White", "Black", "Light Gray", "Gray", "Dark Gray", "Red", "Green", "Blue", "Yellow"}));
		cmb_colour_units.setBounds(361, 41, 90, 20);
		panel_1.add(cmb_colour_units);
		
		ckb_serif = new JCheckBox("Serif font");
		ckb_serif.setBounds(107, 39, 81, 23);
		panel_1.add(ckb_serif);
		
		ckb_bold = new JCheckBox("Bold");
		ckb_bold.setBounds(185, 39, 54, 23);
		panel_1.add(ckb_bold);
		
		ckb_overlay = new JCheckBox("Overlay?");
		ckb_overlay.setBounds(238, 39, 79, 23);
		panel_1.add(ckb_overlay);
		
		JLabel label_3 = new JLabel("Colour:");
		label_3.setBounds(167, 110, 46, 14);
		panel_1.add(label_3);
		
		cmb_colour_scale = new JComboBox();
		cmb_colour_scale.setModel(new DefaultComboBoxModel(new String[] {"White", "Black", "Light Gray", "Gray", "Dark Gray", "Red", "Green", "Blue", "Yellow"}));
		cmb_colour_scale.setBounds(212, 107, 95, 20);
		panel_1.add(cmb_colour_scale);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_3.setBounds(10, 69, 440, 2);
		panel_1.add(panel_3);
		
		JButton btn_save_scale = new JButton("Save configuration!");
		btn_save_scale.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				New_configuration frmAuto2 = new New_configuration();
				String[] pasa = {"Name:","Space units","false","s"};
				frmAuto2.main(pasa);
				if (frmAuto2.accept == true){						
					save_xml("sx",frmAuto2.key);
				}				
			}
		});
		btn_save_scale.setBounds(313, 103, 143, 27);
		panel_1.add(btn_save_scale);
		
		cmb_binning = new JComboBox();
		cmb_binning.setBounds(227, 11, 74, 20);
		cmb_binning.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				lista_auto_main = auto_detect(lista_auto_main);
			}
		});
		cmb_binning.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				if (conf_unitb == false &&
						cmb_binning.
						getItemAt(cmb_binning.getSelectedIndex()).toString().equals("New...")){
					cmb_binning.setSelectedIndex(0);
					conf_unitb = true;
					New_configuration frmAuto2 = new New_configuration();
					String[] pasa = {"Name:","Space units","false","b"};
					frmAuto2.main(pasa);
					if (frmAuto2.accept == true){						
						load_xml("b");
					}					
				}
				if (cmb_binning.getItemCount()!=0){
					if (!cmb_binning.
						getItemAt(cmb_binning.getSelectedIndex()).toString().equals("New...")){
						conf_unitb = false;					
					}
				}
			}
		});
		//cmb_units.setModel(new DefaultComboBoxModel(new String[] {"0", "2", "4", "New..."}));
		load_xml("b");
		panel_1.add(cmb_binning);
		
		lbl_Binning = new JLabel("Binning:");
		lbl_Binning.setBounds(177, 14, 54, 14);
		panel_1.add(lbl_Binning);
		
		JPanel panel_2 = new JPanel();
		Tbp.addTab("Time stamper", null, panel_2, null);
		panel_2.setLayout(null);
		
		JLabel lblNewLabel_2 = new JLabel("Starting time:");
		lblNewLabel_2.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblNewLabel_2.setBounds(20, 33, 96, 14);
		panel_2.add(lblNewLabel_2);
		
		txt_time_start = new JTextField();
		txt_time_start.setHorizontalAlignment(SwingConstants.CENTER);
		txt_time_start.setText("0");
		txt_time_start.setBounds(105, 30, 73, 20);
		panel_2.add(txt_time_start);
		txt_time_start.setColumns(10);
		
		JLabel lblNewLabel_3 = new JLabel("Interval:");
		lblNewLabel_3.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblNewLabel_3.setBounds(188, 33, 46, 14);
		panel_2.add(lblNewLabel_3);
		
		txt_time_interval = new JTextField();
		txt_time_interval.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				lista_auto_main = auto_detect(lista_auto_main);
			}
		});
		txt_time_interval.setHorizontalAlignment(SwingConstants.CENTER);
		txt_time_interval.setText("40");
		txt_time_interval.setColumns(10);
		txt_time_interval.setBounds(240, 30, 73, 20);
		panel_2.add(txt_time_interval);
		
		rdb_units = new JRadioButton("Units");
		rdb_units.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {			
				if (rdb_units.isSelected()==true) {
					cmb_time_units.setEnabled(true);
					lbl_decimal.setEnabled(true);
					txt_decimal.setEnabled(true);
					rdb_zero.setSelected(false);
				}else if(rdb_zero.isSelected()==false){
					rdb_units.setSelected(true);
				}
			}
		});
		rdb_units.setAlignmentX(Component.CENTER_ALIGNMENT);
		rdb_units.setBounds(89, 58, 55, 23);
		panel_2.add(rdb_units);
		
		rdb_zero = new JRadioButton("00:00");
		rdb_zero.setSelected(true);
		rdb_zero.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {			
				if (rdb_zero.isSelected()==true) {
					cmb_time_units.setEnabled(false);
					lbl_decimal.setEnabled(false);
					txt_decimal.setEnabled(false);
					rdb_units.setSelected(false);
				}else if(rdb_units.isSelected()==false){
					rdb_zero.setSelected(true);
				}
			}	
		});
		rdb_zero.setAlignmentX(Component.CENTER_ALIGNMENT);
		rdb_zero.setBounds(20, 58, 65, 23);
		panel_2.add(rdb_zero);
		
		cmb_time_units = new JComboBox();
		cmb_time_units.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				lista_auto_main = auto_detect(lista_auto_main);
			}
		});
		//cmb_time_units.setModel(new DefaultComboBoxModel(new String[] {"New..."}));
		load_xml("t");
		cmb_time_units.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				if (conf_unitt == false &&
						cmb_time_units.
						getItemAt(cmb_time_units.getSelectedIndex()).toString().equals("New...")){
					cmb_time_units.setSelectedIndex(0);
					conf_unitt = true;
					New_configuration frmAuto2 = new New_configuration();
					String[] pasa = {"Name:","Time units","false","t"};
					frmAuto2.main(pasa);
					if (frmAuto2.accept == true){
						load_xml("t");
					}					
				}
				if (cmb_time_units.getItemCount()!=0){
					if (!cmb_time_units.
							getItemAt(cmb_time_units.getSelectedIndex()).toString().equals("New...")){
						conf_unitt = false;					
					}
				}
			}
		});
		cmb_time_units.setEnabled(false);
		cmb_time_units.setBounds(153, 59, 137, 20);
		panel_2.add(cmb_time_units);
		
		lbl_decimal = new JLabel("Decimal places:");
		lbl_decimal.setEnabled(false);
		lbl_decimal.setAlignmentX(Component.CENTER_ALIGNMENT);
		lbl_decimal.setBounds(298, 62, 97, 14);
		panel_2.add(lbl_decimal);
		
		txt_decimal = new JTextField();
		txt_decimal.setHorizontalAlignment(SwingConstants.CENTER);
		txt_decimal.setEnabled(false);
		txt_decimal.setText("0");
		txt_decimal.setColumns(10);
		txt_decimal.setBounds(400, 59, 55, 20);
		panel_2.add(txt_decimal);
		
		JLabel lblNewLabel_5 = new JLabel("Location (px):");
		lblNewLabel_5.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblNewLabel_5.setBounds(112, 92, 84, 14);
		panel_2.add(lblNewLabel_5);
		
		txt_time_x = new JTextField();
		txt_time_x.setHorizontalAlignment(SwingConstants.CENTER);
		txt_time_x.setText("5");
		txt_time_x.setColumns(10);
		txt_time_x.setBounds(195, 89, 38, 20);
		panel_2.add(txt_time_x);
		
		JLabel lblX = new JLabel("x");
		lblX.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblX.setBounds(238, 92, 12, 14);
		panel_2.add(lblX);
		
		txt_time_y = new JTextField();
		txt_time_y.setHorizontalAlignment(SwingConstants.CENTER);
		txt_time_y.setText("5");
		txt_time_y.setColumns(10);
		txt_time_y.setBounds(252, 89, 38, 20);
		panel_2.add(txt_time_y);
		
		JLabel lblY = new JLabel("y");
		lblY.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblY.setBounds(295, 92, 12, 14);
		panel_2.add(lblY);
		
		JLabel lblNewLabel_6 = new JLabel("Font size:");
		lblNewLabel_6.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblNewLabel_6.setBounds(327, 33, 68, 14);
		panel_2.add(lblNewLabel_6);
		
		txt_time_font = new JTextField();
		txt_time_font.setHorizontalAlignment(SwingConstants.CENTER);
		txt_time_font.setText("20");
		txt_time_font.setColumns(10);
		txt_time_font.setBounds(390, 30, 65, 20);
		panel_2.add(txt_time_font);
		
		ckb_time_anti = new JCheckBox("Antialising?");
		ckb_time_anti.setSelected(true);
		ckb_time_anti.setAlignmentX(Component.CENTER_ALIGNMENT);
		ckb_time_anti.setBounds(19, 88, 97, 23);
		panel_2.add(ckb_time_anti);
		
		JButton btn_save_time = new JButton("Save configuration!");
		btn_save_time.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				New_configuration frmAuto2 = new New_configuration();
				String[] pasa = {"Name:","Space units","false","s"};
				frmAuto2.main(pasa);
				if (frmAuto2.accept == true){						
					save_xml("st",frmAuto2.key);
				}				
			}
		});
		btn_save_time.setAlignmentX(Component.CENTER_ALIGNMENT);
		btn_save_time.setBounds(312, 87, 143, 27);
		panel_2.add(btn_save_time);
		
		JPanel panel_5 = new JPanel();
		Tbp.addTab("Bulk process", null, panel_5, null);
		panel_5.setLayout(null);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(6, 39, 450, 91);
		panel_5.add(scrollPane);
		
		lst_bulk = new JList();
		lst_bulk.setVisibleRowCount(5);
		lst_bulk.setModel(lst_bulk_model);
		scrollPane.setViewportView(lst_bulk);
		
		//Add files by drag and drop. Can be either on scroll or on table
		lst_bulk.setDropTarget(new DropTarget(){
            /**
			 * 
			 */
			private static final long serialVersionUID = 2;

			@Override
            public synchronized void drop(DropTargetDropEvent dtde) {              
            	drop_files(dtde);
            }
        });
		scrollPane.setDropTarget(new DropTarget(){
            /**
			 * 
			 */
			private static final long serialVersionUID = 3;

			@Override
            public synchronized void drop(DropTargetDropEvent dtde) {
            	drop_files(dtde);
            }
        });
		
		JButton btn_bulk_add = new JButton("Add...");
		btn_bulk_add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Browse("bulk");
			}
		});
		btn_bulk_add.setBounds(126, 5, 98, 26);
		panel_5.add(btn_bulk_add);
		
		JButton btn_bulk_remove = new JButton("Remove");
		btn_bulk_remove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				remove();
			}
		});
		btn_bulk_remove.setBounds(230, 5, 98, 26);
		panel_5.add(btn_bulk_remove);
				
		JPanel panel_4 = new JPanel();
		Tbp.addTab("Other options", null, panel_4, null);
		panel_4.setLayout(null);
		
		JLabel lblFfmpegDirectory = new JLabel("ffmpeg directory:");
		lblFfmpegDirectory.setBounds(12, 15, 109, 14);
		panel_4.add(lblFfmpegDirectory);
		
		txt_route_ffmpeg = new JTextField();
		txt_route_ffmpeg.setBounds(116, 12, 244, 20);
		panel_4.add(txt_route_ffmpeg);
		txt_route_ffmpeg.setColumns(10);
		
		JButton btn_browse_others = new JButton("Browse...");
		btn_browse_others.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Browse("ffmpeg");
			}
		});
		btn_browse_others.setBounds(362, 11, 89, 23);
		panel_4.add(btn_browse_others);
		
		chk_time_step = new JCheckBox("Write time step in a file");
		chk_time_step.setBounds(8, 40, 162, 24);
		panel_4.add(chk_time_step);
		
		JButton btn_save_others = new JButton("Save configuration!");
		btn_save_others.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				save_xml("ot","general");
			}
		});
		btn_save_others.setAlignmentX(0.5f);
		btn_save_others.setBounds(313, 103, 143, 27);
		panel_4.add(btn_save_others);
		
		chk_metadata = new JCheckBox("Write metadata in a file");
		chk_metadata.setBounds(8, 64, 162, 24);
		panel_4.add(chk_metadata);
		
		JLabel lblNewLabel = new JLabel("Auto contrast threshold:");
		lblNewLabel.setBounds(217, 44, 143, 16);
		panel_4.add(lblNewLabel);
		
		txt_contrast = new JTextField();
		txt_contrast.setBounds(362, 42, 89, 20);
		panel_4.add(txt_contrast);
		txt_contrast.setColumns(10);
		load_xml("ot");
	}

    public void Browse(String field) {
    	JFileChooser j = new JFileChooser();
    	try{
    		if (field.equals("bulk")){
            	j.setFileSelectionMode(JFileChooser.FILES_ONLY);
            	j.setMultiSelectionEnabled(true);
            	FileNameExtensionFilter filter = new FileNameExtensionFilter("Lif File","lif");
            	j.setFileFilter(filter);
            	
            	if (lst_bulk_model.getSize()>0){
        			if (!lst_bulk_model.getElementAt(lst_bulk_model.getSize()-1).equals("")){
        				j.setCurrentDirectory(new File(lst_bulk_model.getElementAt(lst_bulk_model.getSize()-1).toString()));
        			}
        		}
            	
            	j.showOpenDialog(frmAuto);
            	
            	File files[] = j.getSelectedFiles();
            	if (files.length>0){
            		for (int i = 0;i<files.length;i++){
                		lst_bulk_model.addElement(files[i].getAbsolutePath());          			
            		}
            	}
    		}else{
    	    	j.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        		if (field.equals("route") && !txt_route.equals("")){
        			j.setCurrentDirectory(new File(txt_route.getText()));  				
        		}else if (field.equals("ffmpeg") && !txt_route_ffmpeg.equals("")){
        			j.setCurrentDirectory(new File(txt_route_ffmpeg.getText()));  		
        		}
        		
        		j.showOpenDialog(frmAuto);
        		filename = j.getSelectedFile().getAbsolutePath();
        		if (filename != null){
        			if (field.equals("route")){
            			txt_route.setText(filename);   				
        			}else if (field.equals("ffmpeg")){
        				txt_route_ffmpeg.setText(filename);   	
        			}
        		}
    		}
    	}catch (Exception er){
    			
    	}
    } 
    
    public void remove() {
    	int limit = lst_bulk.getSelectedIndices().length;    	
    	for (int i = limit-1; i>-1;i--){
    		lst_bulk_model.remove(lst_bulk.getSelectedIndices()[i]);
    	}
    } 
    
    public void Save() {
    	Thread worker = new Thread() {
            public void run() {
            	int runs;
            	if (chk_bulk.isSelected()){
            		runs = lst_bulk_model.getSize();
            	}else{
            		runs = 1;
            	}
            	
            	String lista_auto[][] = null;
            	
            	for (int run_i = 0; run_i<runs; run_i++){
                	if (chk_bulk.isSelected()){
                		WindowManager.closeAllWindows();
                		IJ.run("Bio-Formats Importer", "open=["+lst_bulk_model.get(run_i)+"] open_all_series display_metadata use_virtual_stack window_less");
                		filename = lst_bulk_model.get(run_i).toString();
                		if (filename.substring(2, 3).equals("\\")){
                			filename = filename.substring(0, filename.lastIndexOf("\\")+1);
                		}else{
                			filename = filename.substring(0, filename.lastIndexOf("/")+1);
                		}
                	}else{
                		filename = txt_route.getText();
                	}
                	
                	if (WindowManager.getIDList()==null) return;
                	btn_save.setEnabled(false);
              				              	
              		//Check route
              		if (filename==null || filename.isEmpty()) {
              			if (!chk_bulk.isSelected()){
              				IJ.showMessage("Introduce a folder name!");   	
              				return;
              			}
              		}
              		
              		if (!filename.endsWith("\\")) filename = filename + "\\";    		
              		
              		File carpeta = new File(filename);
              		if (!carpeta.exists()) {
              			int dialogButton = JOptionPane.YES_NO_OPTION;
              			int dialogResult = JOptionPane.showConfirmDialog
              					(null, "This folder does not exist! Would you like to create it","Warning",dialogButton);
              			if(dialogResult == JOptionPane.YES_OPTION){
              				carpeta.mkdirs();
              			}else{
                  			return;
              			}
              		}
              		         		
              		//Obtain video format
              	    int option = cmb_mode.getSelectedIndex();
              	    String ruta_type = "";
              	    
              	    if (option == 0){
              	    	ruta_type = new StringBuilder("compression=JPEG frame=").toString();
              	    } else if (option == 1){
              	    	ruta_type = new StringBuilder("compression=PNG frame=").toString();
              	    }         	            	    

              	    //For each image
          			int lista[] = WindowManager.getIDList();
          			
          			//For memory stuff
              	    long init_memory = IJ.currentMemory();          	    
          			int nframes = 0;
          			
          			//For scale, time and save
              	    String ruta = "";
              	    String scale_bar = null;      			
              	    double px;
              	    double fps;
              	    String unit = "";
              	    String time = null;
              	    String time_unit = "";
              		int interval = 0;
              		String contrast = null;
              		
              	    //Obtain FPS, interval and magnification array
              		lista_auto = auto_detect(lista_auto);
              		
              	    //Do I want to write metadata?
              		String[] names_windows = WindowManager.getNonImageTitles();
            		for (int i=0;i<names_windows.length;i++){
              			if (names_windows[i].contains("Original Metadata - ")){		//CHANGE!!!
              			//if (names_windows[i].contains("Results")){
              				TextWindow Metadata_win = (TextWindow) WindowManager.getWindow(names_windows[i]);
              				TextPanel Metadata = Metadata_win.getTextPanel();
              				BufferedReader read = new BufferedReader(new StringReader(Metadata.getText()));
              				File save_metadata = new File(filename+"metadata.txt");
                      	    FileOutputStream save__metadata = null;
              				String line;
              				
            			  	try {
                   	    		if (!save_metadata.exists()){
                   	    			save_metadata.createNewFile();
                  	    		}
        						save__metadata = new FileOutputStream(save_metadata, true);              	    		
            					while ((line = read.readLine()) != null) {
            						save__metadata.write(line.getBytes());
            					 }
        						save__metadata.close();
            				} catch (IOException e) {
            					// TODO Auto-generated catch block
            					e.printStackTrace();
            				}     
            				break;
            			}
            		}
              	    
              	    //Do for each video/image!
              	    for (int i=0;i<lista.length;i++){
              	    	if (WindowManager.getImage(lista[i]) == null) continue;
              	    	IJ.selectWindow(lista[i]);
              	    	
              	    	px = -1;
              	    	fps = -1;
              	    	
              	    	//We check if in the array exist this image
              	    	if (lista_auto == null) lista_auto = new String[0][0];
              	    	for (int j=0;j<lista_auto.length;j++){
              	    		if (lista_auto[j][0]!= null){
              	    			if (IJ.getImage().getTitle().contains(lista_auto[j][0].replace(" Image", ""))){
              	    				fps = (1/Double.valueOf(lista_auto[j][1]));          	    			
              	    				px = 6.45*Double.valueOf(lista_auto[j][3])/Double.valueOf(lista_auto[j][2]);
                  	    			unit = "um";
                  	    			//This could be buggy! I don't know how it treat it for
                  	    			//more than 60 s. Should be checked!!!
                  	    			double temp = Double.valueOf(lista_auto[j][1]);
                  	    			if (temp < 1){
                  	    				temp = temp * 1000;
                  	    				time_unit = "ms";
                  	    			}else{              	    			
                  	    				time_unit = "s";
                  	    			}
                  	    			interval = (int) temp;              	    		
              	    				break;
              	    			}
              	    		}
              	    	}
              	    	
              	    	//If we still have -1 value => get the value from the box
              	    	if (px == -1){
              	    		if (Mag != null){
                  	    		px = 6.45*Double.valueOf(cmb_binning.getSelectedItem().toString())/Mag[cmb_magnification.getSelectedIndex()];
              	    		}else{
                  	    		px = 6.45/1;
              	    		}
              	    		fps = Integer.valueOf(txt_FPS.getText());
              	    		unit = cmb_units.getItemAt(cmb_units.getSelectedIndex()).toString();
              	    		interval = Integer.valueOf(txt_time_interval.getText());
              	    		time_unit = cmb_time_units.getItemAt(cmb_time_units.getSelectedIndex()).toString(); 
              	    	}
              	    	
              	    	cal = IJ.getImage().getCalibration();

              	    	//Do we need contrast?
              	    	if (chk_contrast.isSelected()) {
              	    		//We build enhance contrast command
           					try{  
           						double d = Double.parseDouble(txt_contrast.getText()); 
           						contrast = new StringBuilder("saturated=").append(txt_contrast.getText()).toString();
           					}catch(NumberFormatException nfe){  
           						contrast = new StringBuilder("saturated=").append(".35").toString();
           					}            
              	    	}         	    	
              	    	
              	    	
              	    	//Do we need scale bar?
              	    	if (chk_scale.isSelected() && px > 0) {
              	    		//We build scale_bar command
                  	    	scale_bar = new StringBuilder("distance=").append(px).
                  	    			append(" known=1 pixel=1 unit=").
                  	    			append(unit).append(" global").toString();
                  	    	
                  	    	//We execute the set scale plugin. 
                  	    	//Important! we need to set a new calibration afterwards
                  	    	//because if not IJ do not save the video with the required FPS
                  	    	//This only seems to happen after scale_bar plugin is called.
                  	    	IJ.selectWindow(lista[i]);
                  	    	cal = IJ.getImage().getCalibration();
                  	    	IJ.run("Set Scale...", scale_bar);
                  	    	//We build the scale_bar command
                     		String width = Integer.valueOf(txt_scale.getText()).toString();
                      		String height = Integer.valueOf(txt_scale_height.getText()).toString();
                      		String font = Integer.valueOf(txt_scale_height.getText()).toString();
                      		String color = cmb_colour_units.getItemAt(cmb_colour_units.getSelectedIndex()).toString();
                      		String backcolor = cmb_colour_scale.getItemAt(cmb_colour_scale.getSelectedIndex()).toString();
                      		String location = cmb_scale_location.getItemAt(cmb_scale_location.getSelectedIndex()).toString();
                      		
                  	    	scale_bar = new StringBuilder("width=").append(width).append(" height=").append(height).
                  	    			append(" font=").append(font).append(" color=").append(color).
                  	    			append(" background=").append(backcolor).append(" location=[").append(location).
                  	    			append("]").toString();    
                  	    	
                  	    	if (unit.equals("Do not show")){
                  	    		scale_bar = new StringBuilder(scale_bar).append(" hide").toString();
                  	    	}
                  	    	
                  	    	if (ckb_serif.isSelected()){
                  	    		scale_bar = new StringBuilder(scale_bar).append(" bold").toString();
                  	    	}
                  	    	
                  	    	if (ckb_serif.isSelected()){
                  	    		scale_bar = new StringBuilder(scale_bar).append(" serif").toString();
                  	    	}
                  	    	
                  	    	if (ckb_overlay.isSelected()){
                  	    		scale_bar = new StringBuilder(scale_bar).append(" overlay").toString();
                  	    	}
                  	    	
                  	    	if (ckb_scale_all.isSelected()){
                  	    		scale_bar = new StringBuilder(scale_bar).append(" label").toString();
                  	    	}
              	    	}
              	    	
              	    	//Do we need a time stamp?
                  	    if (chk_time.isSelected()) {
                  	    	String start = Integer.valueOf(txt_time_start.getText()).toString();    	              	    	
                      		String x = Integer.valueOf(txt_time_x.getText()).toString();
                      		String y = Integer.valueOf(txt_time_y.getText()).toString();
                      		String font = Integer.valueOf(txt_time_font.getText()).toString();
                      		
                  	    	time = new StringBuilder("starting=").append(start).
              	    			append(" interval=").append(interval).append(" x=").append(x).
              	    			append(" y=").append(y).append(" font=").append(interval).toString();   
                  	    	
                  	    	if (rdb_zero.isSelected()){
                  	    		time = new StringBuilder(time).append(" '00 decimal=[]").toString();
                  	    	}else{
                      	    	start = Integer.valueOf(txt_decimal.getText()).toString();
                  	    		time = new StringBuilder(time).append(" decimal=").append(start).toString();    	    		
                  	    	}
                  	    	
                  	    	if (ckb_time_anti.isSelected()){
                  	    		time = new StringBuilder(time).append(" anti-aliased").toString();	    		
                  	    	}
                  	    	
                  	    	if (rdb_zero.isSelected()){
                  	    		time = new StringBuilder(time).append(" or=[]").toString();
                  	    	}else{	    		
                  	    		time = new StringBuilder(time).append(" or=").append(time_unit).toString();    	    		
                  	    	}    	       
                  	    }
              	    	
              	    	//We build the string command to save the file
              	    	ruta = new StringBuilder(ruta_type).append(String.valueOf(fps)).
              	    			append(" save=[").append(filename).
              	    			append(IJ.getImage().getTitle().replace(".tif", ""))
              	    			.append(".avi]").toString();    
              	    	
                  	    //Now, we get the # of frames. Sometimes, IJ gets it as z-frame, others as
              	    	//t-frames => we need these lines to get the # in any case
              	    	if (IJ.getImage().getNSlices()==1 && IJ.getImage().getNFrames() != 1){
              	    		nframes = IJ.getImage().getNFrames();
              	    	}else if (IJ.getImage().getNSlices()!=1 && IJ.getImage().getNFrames() == 1){
              	    		nframes = IJ.getImage().getNSlices();
              	    	}else{
              	    		nframes = 1;
              	    	}
              	    	
              	    	//We prepare the substack command to convert the virtualstack to non virtualstack
                  	    String stack = new StringBuilder("  slices=1-").append(nframes).toString();        	            	   
                  	    int ID_parent = WindowManager.getCurrentImage().getID();
                  	    
                  	    //Do we need to divide the video?
                  	    if (chk_divide.isSelected()){
                  	    	//Is ffmpeg route ok?
                  	    	if (!txt_route_ffmpeg.getText().endsWith("\\")){
          	    				if (txt_route_ffmpeg.getText().substring(2, 3).equals("\\")){
                      	    		txt_route_ffmpeg.setText(txt_route_ffmpeg.getText()+"\\");
          	    				}
                  	    	}
                	    	if (!txt_route_ffmpeg.getText().endsWith("/")){
          	    				if (txt_route_ffmpeg.getText().substring(2, 3).equals("/")){
                      	    		txt_route_ffmpeg.setText(txt_route_ffmpeg.getText()+"/");
          	    				}
                  	    	}             	    	
                  	    	//!!!!! only windows !!!!!
                  	    	if (IJ.isWindows()){
                      	    	File ffmpegf = new File(txt_route_ffmpeg.getText()+"bin/ffmpeg.exe");
                      	    	if (!ffmpegf.exists()){
                      	    		int confirm = JOptionPane.showOptionDialog(frmAuto,
                      	    			   "ffmpeg not found. Please, verify the route under 'Other options' tab. Do you want to continue?", 
                      	    			   "ffmpeg not found",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,null,null);
                    				if (JOptionPane.OK_OPTION != confirm){
                    						return;
                    				}
                    				ffmpeg = false;
                      	    	}
                  	    	}
                  	    	              	    	
                  	    	//Let's see how long are going to be the mini-videos
                  	    	int size[] = IJ.getImage().getDimensions();
                  	    	double frame_size = size[0]*size[1]*IJ.getImage().getBitDepth()*1.1;
                  	    	int Aframe = (int) Math.ceil((IJ.maxMemory()-init_memory)/frame_size);
                  	    	
                  	    	//Count the # of mini-videos
                  	    	int count = 1;	//# of frames recorded
                  	    	int num = 1;	//# of mini-videos
                  	    	
                  	    	//Replace filename to add the # of the video
                  	    	ruta = ruta.replace(".avi]", "@@@" + num + ".avi]");

                  	    	//Needed for joining the videos
                      	    File save_route = new File(filename+"temp.txt");
                      	    FileOutputStream save__route = null;
                  	    	try {                     	    
                  	    		if (!save_route.exists()){
                  	    			save_route.createNewFile();
                  	    		}else{
                  	    			save_route.delete();
                  	    			save_route.createNewFile();              	    			
                  	    		}
                      	    	save__route = new FileOutputStream(save_route, true);
    						} catch (IOException e) {
    							// TODO Auto-generated catch block
    							e.printStackTrace();
    						}
                  	    	String after = IJ.getImage().getTitle();
                  	    	
                  	    	//While we don't reach the limit...
                  	    	while (count-1<nframes){
                  	    		//We summ Aframes. If we reach the limit we just put the limit.
                  	    		if (count+Aframe>nframes) {	//limit
                  	    			stack = new StringBuilder("  slices=").append(count).append("-").
                  	    					append(nframes).toString();
                  	    		}else{						//no limit
                  	    			stack = new StringBuilder("  slices=").append(count).append("-").
                  	    					append(count+Aframe-1).toString();
                  	    		}
                  	    		
                  	    		//Call this to save the substack with time and scale_bar
/*
                          	    if (chk_scale.isSelected() && chk_time.isSelected() ) {
                          	    	save_video(stack, ID_parent, scale_bar, time, ruta, nframes, fps);
                          	    }else if (chk_scale.isSelected()){
                          	    	save_video(stack, ID_parent, scale_bar, null, ruta, nframes, fps);
                          	    }else if (chk_scale.isSelected()){
                          	    	save_video(stack, ID_parent, null, time, ruta, nframes, fps);
                          	    }else{
                          	    	save_video(stack, ID_parent, null, null, ruta, nframes, fps);
                          	    }
*/
                  	    		save_video(stack, ID_parent, scale_bar, time, contrast, ruta, nframes, fps);
                  	    		
                          	    //We save the filename of the recent video created
                          	    try{
                              	    save__route.write(("file '"+filename+after+ "@@@" + num + ".avi"+"'\n").getBytes());
    							} catch (IOException e) {
    								// TODO Auto-generated catch block
    								e.printStackTrace();
    							}
                          	    
                  	    		count += Aframe;
                  	    		ruta = ruta.replace("@@@" + num + ".avi]", "@@@" + String.valueOf(num+1) + ".avi]");           	    		
                  	    		num++;
                  	    	}
                      	    try {
    							save__route.close();
    						} catch (IOException e1) {
    							// TODO Auto-generated catch block
    							e1.printStackTrace();
    						}
                  	    	IJ.selectWindow(ID_parent);
              	    		IJ.run("Close");     
                  	    	
              	    		//Now we join the videos!
              	    		try {
              	    			String call = null;
              	    			if (IJ.isWindows()){
              	    				if (txt_route_ffmpeg.getText().substring(2, 3).equals("\\")){
                  	    				call = new StringBuilder(txt_route_ffmpeg.getText()+"bin\\ffmpeg.exe").
                  	    						append(" -y -f concat -safe 0 -i \"").
                  	    						append(filename+"temp.txt\" -codec copy \"").
                  	    						append(filename+after+".avi\"").toString();
              	    				}else{
              	    					call = new StringBuilder(txt_route_ffmpeg.getText()+"bin/ffmpeg.exe").
              	    						append(" -y -f concat -safe 0 -i \"").
              	    						append(filename+"temp.txt\" -codec copy \"").
              	    						append(filename+after+".avi\"").toString();
              	    				}
              	    			}else{
              	    				call = new StringBuilder("ffmpeg").
              	    						append(" -y -f concat -safe 0 -i \"").
              	    						append(filename+"temp.txt\" -codec copy \"").
              	    						append(filename+after+".avi\"").toString();
              	    			}
              	    			String caca = call;
              	    			//IJ.log(call);
              	    			//IJ.error(call);
              	    			Process p = Runtime.getRuntime().exec(call);
              	    			try {          	    				
              	    				ReadStream s1 = new ReadStream("stdin", p.getInputStream ());
              	    				ReadStream s2 = new ReadStream("stderr", p.getErrorStream ());
              	    				s1.start ();
              	    				s2.start ();
              	    				/*IJ.error(s1.toString());
              	    				IJ.error(s2.toString());
              	    				*/
              	    				p.waitFor();
              	    				//IJ.error(s1.toString());
              	    				//IJ.error(s2.toString());
    							} catch (InterruptedException e) {
    								// TODO Auto-generated catch block
    								e.printStackTrace();
    							} finally {
    							    if(p != null)
    							        p.destroy();
    							}
    						} catch (IOException e) {
    							// TODO Auto-generated catch block
    							e.printStackTrace();
    						}
              	    		          	    		
              	    		//Now we can delete the minivideos
              	    		FileInputStream fstream;
    						try {
    							fstream = new FileInputStream(filename+"temp.txt");
    	          	    		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

    	          	    		String strLine;

    	          	    		//Read File Line By Line
    	          	    		while ((strLine = br.readLine()) != null)   {
    	          	    		  strLine = strLine.replace("file ", "").replace("'", "");	  
    	          	    		  File del = new File(strLine);
    	          	    		  del.delete();
    	          	    		}					

    	          	    		//Close the input stream
    	          	    		br.close();
    						} catch (Exception e) {
    							// TODO Auto-generated catch block
    							e.printStackTrace();
    						}
              	    		
              	    		//Finally, I delete the temp file
                  	    	save_route.delete();
             	    	
                  	    }else{              	  
                  	    	stack = new StringBuilder("  slices=1-").append(nframes).toString();
                  	    	/*
                      	    if (chk_scale.isSelected() && chk_time.isSelected() ) {
                      	    	save_video(stack, ID_parent, scale_bar, time, ruta, nframes, fps);
                      	    }else if (chk_scale.isSelected()){
                      	    	save_video(stack, ID_parent, scale_bar, null, ruta, nframes, fps);
                      	    }else if (chk_scale.isSelected()){
                      	    	save_video(stack, ID_parent, null, time, ruta, nframes, fps);
                      	    }else{
                      	    	save_video(stack, ID_parent, null, null, ruta, nframes, fps);
                      	    }*/
              	    		save_video(stack, ID_parent, scale_bar, time, contrast, ruta, nframes, fps);

                  	    }
              	    }
            	}
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                      btn_save.setEnabled(true);
                    }
                  });
            }
    	};
    	worker.start();
    }	

    public void save_video(String stack, int ID_parent, String scale_bar, String time, String contrast, String ruta, int nframes, double fps){
    	//Do we need to include a scalebar or stamp the time?
    	boolean result = false;

	    if (scale_bar!=null || time!=null || contrast != null) {
	    	//Convert the virtualstack into a stack	    	
    		while (result==false) {
    			try{
    				//IJ.selectWindow(ID_parent);
    		    	//IJ.run("Make Substack...", stack);
    				
    				
       				//IJ.selectWindow(ID_parent);
    		    	//IJ.run("Make Substack...", stack);
    				//stack_maker a = new stack_maker();
    				//a.run(stack);
    				IJ.selectWindow(ID_parent);
    				stack = stack.replace(" slices=", "").replace(" ","");
    				String parameters[] = stack.split("-");
    				ImagePlus copy = new Duplicator().run(IJ.getImage(),
    						Integer.valueOf(parameters[0]),Integer.valueOf(parameters[1]));
    				copy.show();
    		    	result = true;
    			}catch(Exception e){
    				result = false;
    			}
    		}

	    	
	    	//Do we have a single video? Then close the original...
	    	if (!chk_divide.isSelected()){
	    		int ID_active = WindowManager.getCurrentImage().getID();
	    		IJ.selectWindow(ID_parent);
	    		IJ.run("Close");     
	    		IJ.selectWindow(ID_active);
	    	}
	    	
	    	//run the auto contrast command
	    	if (contrast!=null){
	    		result = false;
	    		while (result==false) {
	    			try{
	    				IJ.run("Enhance Contrast...", contrast);
	    		    	result = true;
	    			}catch(Exception e){
	    		    	result = false;
	    			}
	    		}
	    	}
	    	
	    	//run the scale bar command
	    	if (scale_bar!=null){
	    		result = false;
	    		while (result==false) {
	    			try{
	    				IJ.run("Scale Bar...", scale_bar);
	    		    	result = true;
	    			}catch(Exception e){
	    		    	result = false;
	    			}
	    		}
	    	}
	    	String vaya = "AAA";
	    	vaya = "BBB";
	    	//run the time stamper command	    	
	    	if (time!=null && nframes!=1){
	    		result = false;
	    		while (result==false) {
	    			try{
	    	    		IJ.run("Time Stamper", time);
	    		    	result = true;
	    			}catch(Exception e){
	    		    	result = false;
	    			}
	    		}
	    	}
	    	
	    	//We impose our old calibration but changing the fps value. (It's needed due a bug in original 
	    	// save in command from imageJ)
		    cal.fps=fps;
		    IJ.getImage().setGlobalCalibration(cal);
		    IJ.getImage().setCalibration(cal);
	    }else{
	    	IJ.selectWindow(ID_parent);    	
	    }
	    
	    //Now, we save the series into an avi file. Watch out! It can be a single image!
	    if (nframes!=1){
    		result = false;
    		while (result==false) {
    			try{
    		    	IJ.run("AVI... ", ruta);
    		    	result = true;
    			}catch(Exception e){
    		    	result = false;
    			}
    		}
	    }else{
	    	IJ.saveAs("Tiff", filename+IJ.getImage().getTitle()+".tif");
	    }
	    
	    //We save in a txt file the frame rate (ms/image)
    	if (chk_time_step.isSelected()){
    		try {			
    			File time_lapse = new File(filename+"lapse.txt");    	
    			if (!time_lapse.exists()){
    				time_lapse.createNewFile();
    			}
    			FileOutputStream time__lapse = new FileOutputStream(time_lapse, true);
    			String[] names_windows = WindowManager.getNonImageTitles();
    			if (names_windows.length!=0){
    				time__lapse.write(("A   "+ruta+ "   " + String.valueOf(1.0/fps)+"\n").getBytes());
    			}else{
    				time__lapse.write(("0   "+ruta+ "   " + String.valueOf(1.0/fps)+"\n").getBytes());
    			}
    			time__lapse.close();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} 
    	}    	    
    	
    	IJ.run("Close");
    	
    	//IJ.freeMemory(); because: 
    	//nonsense: http://imagejdocu.tudor.lu/doku.php?id=howto:java:imagej_performance_tuning    	
    }
    
    public void save_xml(String option, String key){
        try {				
        	Document dom;
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();		
            DocumentBuilder db = dbf.newDocumentBuilder();
            
        	String ruta = new StringBuilder(IJ.getDirectory("imagej").
        			toString()).append("AutoSave_.xml").toString();
        	File comprueba = new File(ruta);
        	
        	Element rootEle;
        	if (comprueba.exists()){
        		dom = db.parse(comprueba);
        		rootEle = dom.getDocumentElement();
        		//If it exists, then delete it...
        		NodeList elements = rootEle.getElementsByTagName(option);
        		for (int i=0;i<elements.getLength();i++){
        			if(!option.equals("ot")){
        				if (elements.item(i).getChildNodes().item(1).getTextContent().equals(key)){
        					int confirm = JOptionPane.
        						showConfirmDialog(frmAuto,"This name exists in the DDBB. Data will be overwritten.");
        						if (JOptionPane.OK_OPTION != confirm){
        							return;
        						}
        				}
        				rootEle.removeChild(elements.item(i));
        			}else{
        				rootEle.removeChild(elements.item(i));
        			}
        		}        		
        	}else{
        		dom = db.newDocument();
        		rootEle = dom.createElement("Options");
        		dom.appendChild(rootEle);
        	}       
        	
        	Element child = dom.createElement(option);
            if (option.equals("sx")){
            	Element child2 = dom.createElement("n");
            	child2.setTextContent(key);
            	child.appendChild(child2);
            	child2 = dom.createElement("m");	//Magnification
            	child2.setTextContent(cmb_magnification.getSelectedItem().toString());
            	child.appendChild(child2);
            	child2 = dom.createElement("u");	//Units
            	child2.setTextContent(cmb_units.getSelectedItem().toString());
            	child.appendChild(child2);            	
            	child2 = dom.createElement("fs");	//Font size
            	child2.setTextContent(txt_font_size.getText().toString());
            	child.appendChild(child2);            	
            	child2 = dom.createElement("sf");	//Serif
            	child2.setTextContent(String.valueOf(ckb_serif.isSelected()));
            	child.appendChild(child2);            	
            	child2 = dom.createElement("bd");	//Bold
            	child2.setTextContent(String.valueOf(ckb_bold.isSelected()));
            	child.appendChild(child2);            	
            	child2 = dom.createElement("oy");	//Overlay
            	child2.setTextContent(String.valueOf(ckb_overlay.isSelected()));
            	child.appendChild(child2);            	
            	child2 = dom.createElement("cl");	//Colour
            	child2.setTextContent(cmb_colour_units.getSelectedItem().toString());
            	child.appendChild(child2);            	
            	child2 = dom.createElement("bs");	//Scale bar size
            	child2.setTextContent(txt_scale.getText());
            	child.appendChild(child2);            	
            	child2 = dom.createElement("ht");	//Height
            	child2.setTextContent(txt_scale_height.getText());
            	child.appendChild(child2);            	
            	child2 = dom.createElement("la");	//Label all
            	child2.setTextContent(String.valueOf(ckb_scale_all.isSelected()));
            	child.appendChild(child2);            	
            	child2 = dom.createElement("lo");	//Location
            	child2.setTextContent(cmb_scale_location.getSelectedItem().toString());
            	child.appendChild(child2);            	
            	child2 = dom.createElement("cb");	//Colour background
            	child2.setTextContent(cmb_colour_scale.getSelectedItem().toString());
            	child.appendChild(child2);            	
            	child2 = dom.createElement("b");	//Binning
            	child2.setTextContent(cmb_binning.getSelectedItem().toString());
            	child.appendChild(child2);                  	
            }else if (option.equals("st")){
            	Element child2 = dom.createElement("n");
            	child2.setTextContent(key);
            	child.appendChild(child2);
            	child2 = dom.createElement("s");	//Start
            	child2.setTextContent(txt_time_start.getText());
            	child.appendChild(child2);
            	child2 = dom.createElement("i");	//Interval
            	child2.setTextContent(txt_time_interval.getText());
            	child.appendChild(child2);
            	child2 = dom.createElement("fs");	//Font size
            	child2.setTextContent(txt_time_font.getText().toString());
            	child.appendChild(child2);
            	child2 = dom.createElement("u");	//Units            	
            	if (rdb_zero.isSelected()){
                	child2.setTextContent("00");
            	}else{
                	child2.setTextContent(cmb_time_units.getSelectedItem().toString());
            	}
            	child.appendChild(child2);
            	child2 = dom.createElement("d");	//Decimal
            	child2.setTextContent(txt_decimal.getText());
            	child.appendChild(child2);
            	child2 = dom.createElement("a");	//a
            	child2.setTextContent(String.valueOf(ckb_time_anti.isSelected()));
            	child.appendChild(child2);
            	child2 = dom.createElement("x");	//x
            	child2.setTextContent(txt_time_x.getText());
            	child.appendChild(child2);
            	child2 = dom.createElement("y");	//y
            	child2.setTextContent(txt_time_y.getText());
            	child.appendChild(child2);    
            }else if (option.equals("ot")){
                child.setAttribute("ac", txt_contrast.getText());	//Auto contrast trheshold
                child.setAttribute("f", txt_route_ffmpeg.getText());	//ffmpeg
            	if (chk_time_step.isSelected()){						//time interval
            		child.setAttribute("ts", "t");
            	}else{
            		child.setAttribute("ts", "f");
            	}       
            	if (chk_metadata.isSelected()){						//metadata
            		child.setAttribute("m", "t");
            	}else{
            		child.setAttribute("m", "f");
            	}      
            }

            dom.getDocumentElement().appendChild(child);

            Transformer tr = TransformerFactory.newInstance().newTransformer();
            tr.setOutputProperty(OutputKeys.INDENT, "yes");
            tr.setOutputProperty(OutputKeys.METHOD, "xml");
            tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            //tr.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "autosave_.dtd");
            tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            // send DOM to file
            tr.transform(new DOMSource(dom), 
                new StreamResult(new FileOutputStream(ruta)));	 
        	
        	
		} catch (Exception e) {
				e.printStackTrace();
        }   	
    }
    
    public void load_xml(String option){
        try {				
        	Document dom;
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();		
            DocumentBuilder db = dbf.newDocumentBuilder();
            
        	String ruta = new StringBuilder(IJ.getDirectory("imagej").
        			toString()).append("AutoSave_.xml").toString();
        	File comprueba = new File(ruta);
        	
        	Element rootEle;
        	if (comprueba.exists()){
        		dom = db.parse(comprueba);
        		rootEle = dom.getDocumentElement();
        		NodeList elements = rootEle.getElementsByTagName(option);
        		
        		if (option.equals("g")) {
        			Mag = null;
        			if (elements.getLength()!=0){
        				Mag = new int[elements.getLength()];
        			}
        			cmb_magnification.removeAllItems();
        		}else if (option.equals("x")) {
        			cmb_units.removeAllItems();
        		}else if (option.equals("b")) {
        			cmb_binning.removeAllItems();        			
        		}else if (option.equals("t")) {
        			cmb_time_units.removeAllItems();
        		}else if (option.equals("sx")) {
        			cmb_scale.removeAllItems();
        			cmb_scale.addItem("Local input");
        		}else if (option.equals("st")) {
        			cmb_time.removeAllItems();     
        			cmb_time.addItem("Local input");
        		}
        				
        		for (int i = 0; i<elements.getLength(); i++){
        			if (option.equals("g")) {
        				if (elements.item(i).getAttributes().getLength()!=0){
        					cmb_magnification.addItem(elements.item(i).getAttributes().item(0).getNodeValue());
        					Mag[i] = Integer.valueOf(elements.item(i).getAttributes().item(1).getNodeValue());
        				}
        			}else if (option.equals("x")) {
        				if (elements.item(i).getAttributes().getLength()!=0){
        					cmb_units.addItem(elements.item(i).getAttributes().item(0).getNodeValue());
        				}
        			}else if (option.equals("b")) {
        				if (elements.item(i).getAttributes().getLength()!=0){
        					cmb_binning.addItem(elements.item(i).getAttributes().item(0).getNodeValue());
        				}
        			}else if (option.equals("t")) {
        				if (elements.item(i).getAttributes().getLength()!=0){
        					cmb_time_units.addItem(elements.item(i).getAttributes().item(0).getNodeValue());
        				}
        			}else if (option.equals("sx")) {
        				cmb_scale.addItem(elements.item(i).getChildNodes().item(1).getTextContent());    		
        			}else if (option.equals("st")) {
        				cmb_time.addItem(elements.item(i).getChildNodes().item(1).getTextContent());   				
        			}else if (option.equals("ot")) {
        				if (elements.item(i).getAttributes().getLength()!=0){
        					txt_route_ffmpeg.setText(elements.item(i).getAttributes().item(1).getNodeValue());
        					if (elements.item(i).getAttributes().item(2).getNodeValue().equals("t")){
        						chk_time_step.setSelected(true);
        					}else{
        						chk_time_step.setSelected(false);
        					}
           					if (elements.item(i).getAttributes().item(3).getNodeValue().equals("t")){
        						chk_metadata.setSelected(true);
        					}else{
        						chk_metadata.setSelected(false);
        					}     
           					txt_contrast.setText(elements.item(i).getAttributes().item(0).getNodeValue());	//Auto contrast trheshold
           					try{  
           						double d = Double.parseDouble(txt_contrast.getText());  
           					}catch(NumberFormatException nfe){  
           						txt_contrast.setText(".35");
           					}  
        				}
        			}else if (option.equals("of")) {
        				if (elements.item(i).getAttributes().getLength()!=0){
        					txt_route_ffmpeg.setText(elements.item(i).getAttributes().item(0).getNodeValue());
        				}
        			}
        		}        	
        	}             
    		if (option.equals("g")) {
    			if (cmb_magnification.getItemCount()==0){
    				cmb_magnification.addItem("");
    			}
    			cmb_magnification.addItem("New...");
    		}else if (option.equals("x")) {
				cmb_units.addItem("Do not show");
				cmb_units.addItem("New...");  
    		}else if (option.equals("b")) {
    			if (cmb_binning.getItemCount()==0){
    				cmb_binning.addItem("");
    			}
				cmb_binning.addItem("New..."); 
    		}else if (option.equals("t")) {
    			if (cmb_time_units.getItemCount()==0){
    				cmb_time_units.addItem("");
    			}
    			cmb_time_units.addItem("New...");     
    		}
		} catch (Exception e) {
				e.printStackTrace();
        }
    }
    
    public void carga_conf(String option, String key){
    	try {				
        	Document dom;
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();		
            DocumentBuilder db = dbf.newDocumentBuilder();
            
        	String ruta = new StringBuilder(IJ.getDirectory("imagej").
        			toString()).append("AutoSave_.xml").toString();
        	File comprueba = new File(ruta);
        	
        	Element rootEle;
        	if (comprueba.exists()){
        		dom = db.parse(comprueba);
        		rootEle = dom.getDocumentElement();
        		NodeList elements = rootEle.getElementsByTagName(option);
        		
        		if (option.equals("sx")) {
        			for (int i = 0; i<elements.getLength(); i++){
            			if (elements.item(i).getChildNodes().item(1).getTextContent().equals(key)) {
            				NodeList nodes = elements.item(i).getChildNodes(); //nodes are like {"/n",data,"/n",data...}     				
                        	cmb_magnification.setSelectedItem(nodes.item(3).getTextContent());
                        	cmb_units.setSelectedItem(nodes.item(5).getTextContent());
                        	txt_font_size.setText(nodes.item(7).getTextContent());
                        	ckb_serif.setSelected(Boolean.valueOf(nodes.item(9).getTextContent()));
                        	ckb_bold.setSelected(Boolean.valueOf(nodes.item(11).getTextContent()));
                        	ckb_overlay.setSelected(Boolean.valueOf(nodes.item(13).getTextContent()));
                        	cmb_colour_units.setSelectedItem(nodes.item(15).getTextContent());                        	                        	
                        	txt_scale.setText(nodes.item(17).getTextContent());
                        	txt_scale_height.setText(nodes.item(19).getTextContent());
                        	ckb_scale_all.setSelected(Boolean.valueOf(nodes.item(21).getTextContent()));
                        	cmb_scale_location.setSelectedItem(nodes.item(23).getTextContent());
                        	cmb_colour_scale.setSelectedItem(nodes.item(25).getTextContent());
                        	cmb_binning.setSelectedItem(nodes.item(27).getTextContent());
            				break;
            			}
            		}        	
        		}else if (option.equals("st")) {
        			for (int i = 0; i<elements.getLength(); i++){
            			if (elements.item(i).getChildNodes().item(1).getTextContent().equals(key)) {
            				NodeList nodes = elements.item(i).getChildNodes();            				
            				txt_time_start.setText(nodes.item(3).getTextContent());
                        	txt_time_interval.setText(nodes.item(5).getTextContent());                  	                        	
                        	txt_time_font.setText(nodes.item(7).getTextContent());
                        	if (nodes.item(9).getTextContent().equals("00")){
                        		rdb_zero.setSelected(true);
                        	}else{
                        		rdb_units.setSelected(true);
                        		cmb_time_units.setSelectedItem(nodes.item(9).getTextContent());                             		
                        	}                        	                        	
                        	txt_decimal.setText(nodes.item(11).getTextContent());
                        	ckb_time_anti.setSelected(Boolean.valueOf(nodes.item(13).getTextContent()));
                        	txt_time_x.setText(nodes.item(15).getTextContent());
                        	txt_time_y.setText(nodes.item(17).getTextContent());                        	
            				break;        	
            			}
            		}
        		}
        	}             
		} catch (Exception e) {
				e.printStackTrace();
        }
    }
    
    public String[][] auto_detect(String lista_auto[][]){
  		String[] names_windows = WindowManager.getNonImageTitles();
  		if (names_windows.length==0){
  			txt_FPS.setBackground(Color.WHITE);
			txt_time_interval.setBackground(Color.WHITE);   
			cmb_units.setBackground(Color.WHITE);
			cmb_time_units.setBackground(Color.WHITE);
			cmb_magnification.setBackground(Color.WHITE);
			cmb_binning.setBackground(Color.WHITE);			
  			lista_auto = null;
  		}
  		for (int i=0;i<names_windows.length;i++){
  			if (names_windows[i].contains("Original Metadata - ")){
  			//if (names_windows[i].contains("Results")){
  				TextWindow Metadata_win = (TextWindow) WindowManager.getWindow(names_windows[i]);
  				TextPanel Metadata = Metadata_win.getTextPanel();
  				BufferedReader read = new BufferedReader(new StringReader(Metadata.getText()));
  				int size_list = 0;
  				String line = null;
  				lista_auto = null;
  				Boolean introduce = true;
  				
  				try {
  					int k = 0;
					while ((line = read.readLine()) != null) {
						if (!line.contains("Name") && introduce == true){
							if (size_list>0){
								introduce = false;
							}
						}
						
					    if (line.contains("Name") && introduce == true){
					    	size_list++;					    	
					    }

					    if (line.contains("ATLCameraSettingDefinition|BinningValuePrecise")){
					    	if (lista_auto==null){
					    		lista_auto = new String[size_list][4];
					    	}					    	
					    	String temp = line.substring(line.lastIndexOf("|BinningValuePrecise")+20);
					    	lista_auto[k][3] = temp.replace("\t","");//[2];
					    }					
					    
					    if (line.contains("ATLCameraSettingDefinition|CycleTime")){
					    	String value = line.substring(line.lastIndexOf("|CycleTime")+10).replace("\t","");
					    	String name = line.substring(0,line.indexOf("ATLCameraSettingDefinition|CycleTime")-1);
					    	lista_auto[k][0] = name;//.split("\t")[1]; //!!!!QUITAR PARA LA VERSI�N FINAL!!!! (DEJARLO COMO TEMP[0]
					    	lista_auto[k][1] = value;						    	
					    }
					    
					    if (line.contains("ATLCameraSettingDefinition|Magnification")){
					    	String temp = line.substring(line.lastIndexOf("|Magnification")+14).replace("\t", "");
					    	lista_auto[k][2] = temp;//split("\t")[2];
					    	k++;
						    if (k==lista_auto.length) break;
					    }
					 }
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}     
  				txt_FPS.setBackground(Color.GREEN);
  				txt_time_interval.setBackground(Color.GREEN);   
  				cmb_units.setBackground(Color.GREEN);
  				cmb_time_units.setBackground(Color.GREEN);
  				cmb_magnification.setBackground(Color.GREEN);
  				cmb_binning.setBackground(Color.GREEN);
  				break;
  			}
  			txt_FPS.setBackground(Color.WHITE);
			txt_time_interval.setBackground(Color.WHITE);   
			cmb_units.setBackground(Color.WHITE);
			cmb_time_units.setBackground(Color.WHITE);
			cmb_magnification.setBackground(Color.WHITE);
			cmb_binning.setBackground(Color.GREEN);
  			lista_auto = null;
  		}
  		return lista_auto;
    }
    
	/**
	 * Action performed to add files by drag and drop for jtable add_table.
	 * 
	 * @param dtde   The DropTargetDropEvent of the component
	 * 
	 * @return void
	*/
	
	public void drop_files(DropTargetDropEvent dtde){
        // handle drop outside current table (e.g. add row)
    	dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
        Transferable t = dtde.getTransferable();
		try {
			//Obtain all files dragged
			List<File> fileList = (List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);

			//For each: add it
		    for (int i = 0; i<fileList.size(); i++){	
		    	File f = (File)fileList.get(i);
		    	//if it is a directory, then we look for subdirectories
		    	if (f.isDirectory()){
		    		List<File> subfiles = new ArrayList<File>();
		    		searchForFiles(f,subfiles);
		    		
		    		//Only files are added. Folders not.
		    		for (int j = 0; j<subfiles.size();j++){
			    		lst_bulk_model.addElement(subfiles.get(j).getAbsolutePath());          			
		    		}
		    	}else{
		    		lst_bulk_model.addElement(f.getAbsolutePath());          			
		    	}
		    }			
		} catch (UnsupportedFlavorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	
	/**
	 * Looks for all files that exist in a folder and subfolders.
	 * 
	 * @param root   Folder to look at.
	 * @param datOnly   List of files and folders just in the first level of root.
	 * 
	 * @return void, although datOnly is modified with the list of files & folders
	*/
	
	public static void searchForFiles(File root, List<File> datOnly) {
	    if(root == null || datOnly == null) return; //just for safety   
	    if(root.isDirectory()) {
	        for(File file : root.listFiles()) {
	            searchForFiles(file, datOnly);
	        }
	    } else if(root.isFile()) {
	        datOnly.add(root);
	    }
	}	
	
}

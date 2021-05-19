package main;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import ij.IJ;

import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.awt.event.ActionEvent;

public class New_configuration extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField txt_name;
	private JTextField txt_mag;
	private static New_configuration dialog;
	public static String lbl_name = "";
	public static String lbl_unit = "";
	public static Boolean visible = false;
	public static String key = "";
	public static Boolean accept = false;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			lbl_name = args[0];
			lbl_unit = args[1];
			visible = Boolean.valueOf(args[2]);
			key = args[3];
			
			dialog = new New_configuration();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public New_configuration() {
		setModal(true);
		setResizable(false);
		setTitle("New configuration...");
		setAlwaysOnTop(true);
		if (visible == false){
			setBounds(100, 100, 214, 96);
		}else{
			setBounds(100, 100, 214, 150);			
		}
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JLabel lblNewLabel = new JLabel(lbl_unit);
		lblNewLabel.setBounds(10, 36, 87, 14);
		contentPanel.add(lblNewLabel);
		
		if (visible == true){
			JLabel lblNewLabel_1 = new JLabel("(NOTE: 1 px = 6.45/magnification)");
			lblNewLabel_1.setBounds(10, 60, 195, 14);
			contentPanel.add(lblNewLabel_1);
		}
		
		JLabel lblName = new JLabel(lbl_name);
		lblName.setBounds(10, 11, 46, 14);
		contentPanel.add(lblName);
		
		txt_name = new JTextField();
		txt_name.setBounds(57, 8, 143, 20);
		contentPanel.add(txt_name);
		txt_name.setColumns(10);
		
		txt_mag = new JTextField();
		txt_mag.setColumns(10);
		txt_mag.setBounds(93, 33, 107, 20);
		contentPanel.add(txt_mag);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
			            try {
			            	if (txt_name.getText().equals("")){
			            		key = "";
			            		dialog.dispose();
			            		return;
			            	}
			                if (key.equals("g")){
				            	if (txt_mag.getText().equals("")){
				            		dialog.dispose();
				            		return;
				            	}
			                }
			            	if (key.equals("s")){
			            		key = txt_name.getText();
			            		accept = true;
			            		dialog.dispose();
			            		return;
			            	}
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
			            	}else{
			            		dom = db.newDocument();
			            		rootEle = dom.createElement("Options");
			            		dom.appendChild(rootEle);
			            	}             
			                
			                Element child = dom.createElement(key);
			                if (key.equals("g")){
			                	child.setAttribute("v", txt_mag.getText());
			                }
			                child.setAttribute("n", txt_name.getText());
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
			                accept = true;
			                dialog.dispose();
						} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
				        }       
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						dialog.dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
}

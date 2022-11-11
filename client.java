import java.net.*;
import java.util.StringTokenizer;
import javax.swing.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;

public class client implements ActionListener {
	private JFrame clientframe;
	private JPanel userpanel, panel1;
	private JLabel textback, listback, clientinfo;
	private JLayeredPane pane;
	private JTextArea chatwindow;
	private JTextField usertext;
	private JButton settings, fileselection, send, logout;
	private JMenuBar menubar;
	private JMenu background, foreground, font;
	private JMenuItem bred, bblue, bmagenta, bblack, bcyan, bwhite, bgray, bpink;
	private JMenuItem fred, fblue, fmagenta, fblack, fcyan, fwhite, fgray, fpink;
	private JMenuItem bradley, californian, comic;
	private JScrollPane scroll;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String serverIP;
	private Socket connection;

	public client(String name) {
		serverIP = "127.0.0.1";
		clientframe = new JFrame(name);
		clientframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		clientframe.setSize(new Dimension(1000, 720));
		clientframe.setResizable(false);

		userpanel = new JPanel();

		ImageIcon textfieldback = new ImageIcon("images\\messagearea.jpg");
		textback = new JLabel("", textfieldback, JLabel.LEADING);
		textback.setOpaque(true);
		textback.setBounds(200, 0, 1920, 720);

		ImageIcon clientlistback = new ImageIcon("images\\clientlist.png");
		listback = new JLabel("", clientlistback, JLabel.LEADING);
		listback.setOpaque(true);
		listback.setBounds(0, 0, 350, 720);

		ImageIcon icon = new ImageIcon("images\\TMlogo.png");
		clientframe.setIconImage(icon.getImage());

		pane = new JLayeredPane();
		pane.setBounds(0, 0, 1000, 720);

		usertext = new JTextField(40);
		userpanel.add(usertext);
		usertext.setEditable(true);
		usertext.addActionListener(this);
		usertext.setEditable(false);
		userpanel.setBounds(350, 650, 430, 30);

		settings = new JButton("Settings");
		settings.setFocusable(false);
		settings.setBounds(0, 645, 300, 40);
		settings.addActionListener(this);
		settings.setHorizontalTextPosition(JButton.LEFT);
		settings.setVerticalTextPosition(JButton.CENTER);

		fileselection = new JButton("File");
		fileselection.setFocusable(false);
		fileselection.setBounds(600, 645, 80, 40);
		fileselection.addActionListener(this);
		textback.add(fileselection);

		send = new JButton("Send");
		send.setFocusable(false);
		send.addActionListener(this);
		send.setBounds(700, 645, 70, 40);
		textback.add(send);

		chatwindow = new JTextArea();
		chatwindow.setLineWrap(true);
		chatwindow.setEditable(false);
		chatwindow.setFont(new Font("Cambria Math", Font.ITALIC, 18));
		scroll = new JScrollPane(chatwindow);
		scroll.setBounds(150, 100, 635, 400);
		textback.add(scroll);

		menubar = new JMenuBar();

		logout = new JButton("Logout");
		logout.setFocusable(false);
		logout.addActionListener(this);

		background = new JMenu("Background");
		foreground = new JMenu("Foreground");
		font = new JMenu("Font");

		bradley = new JMenuItem("Bradley Hand ITC");
		bradley.addActionListener(this);
		californian = new JMenuItem("Californian FB");
		californian.addActionListener(this);
		comic = new JMenuItem("Comic Sans MS");
		comic.addActionListener(this);

		font.add(bradley);
		font.add(californian);
		font.add(comic);

		bred = new JMenuItem("Red");
		bred.addActionListener(this);
		bblack = new JMenuItem("Black");
		bblack.addActionListener(this);
		bblue = new JMenuItem("Blue");
		bblue.addActionListener(this);
		bmagenta = new JMenuItem("Magenta");
		bmagenta.addActionListener(this);
		bcyan = new JMenuItem("Cyan");
		bcyan.addActionListener(this);
		bgray = new JMenuItem("Gray");
		bgray.addActionListener(this);
		bpink = new JMenuItem("Pink");
		bpink.addActionListener(this);
		bwhite = new JMenuItem("White");
		bwhite.addActionListener(this);

		fred = new JMenuItem("Red");
		fred.addActionListener(this);
		fblue = new JMenuItem("Blue");
		fblue.addActionListener(this);
		fmagenta = new JMenuItem("Magenta");
		fmagenta.addActionListener(this);
		fcyan = new JMenuItem("Cyan");
		fcyan.addActionListener(this);
		fblack = new JMenuItem("Black");
		fblack.addActionListener(this);
		fwhite = new JMenuItem("White");
		fwhite.addActionListener(this);
		fgray = new JMenuItem("Gray");
		fgray.addActionListener(this);
		fpink = new JMenuItem("Pink");
		fpink.addActionListener(this);

		background.add(bred);
		background.add(bblue);
		background.add(bmagenta);
		background.add(bcyan);
		background.add(bblack);
		background.add(bwhite);
		background.add(bgray);
		background.add(bpink);

		foreground.add(fred);
		foreground.add(fblue);
		foreground.add(fmagenta);
		foreground.add(fcyan);
		foreground.add(fblack);
		foreground.add(fwhite);
		foreground.add(fgray);
		foreground.add(fpink);

		menubar.add(background);
		menubar.add(foreground);
		menubar.add(font);
		menubar.add(logout);

		panel1 = new JPanel();
		panel1.setBounds(0, 640, 350, 50);
		panel1.add(menubar);
		listback.add(panel1);

		clientinfo = new JLabel();
		clientinfo.setBounds(0, 0, 100, 400);
		clientinfo.setFont(new Font("Cambria Math", Font.ITALIC, 14));
		listback.add(clientinfo);

		pane.add(userpanel);
		pane.add(listback);
		pane.add(textback);

		clientframe.add(pane);
		clientframe.setVisible(true);
	}

	public void startrunning() {
		try {
			connecttoserver();
			streams();
			chatting();
		} catch (EOFException e) {
			showmessage("\n client terminated connection\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void closethingsup() {
		showmessage("\nClosing things up..");
		typepermissions(false);
		try {
			output.close();
			input.close();
			connection.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendmessage(String string) {
		try {
			output.writeObject("client: " + string);
			output.flush();
			showmessage("\n client: " + string);
		} catch (IOException e) {
			chatwindow.append("\n oops, something happend..");
		}
	}

	private void chatting() throws IOException {
		String msg = " You are connected\n";
		String original, newstring = "", word = "";
		String filenamex = "", filesizex = "0";
		typepermissions(true);
		int c = 1;
		do {
			try {
				if (c == 1) {
					msg = (String) input.readObject();
					showmessage("\n" + msg);
					StringTokenizer obj = new StringTokenizer(msg, " ");
					while (obj.hasMoreTokens()) {
						if (obj.nextToken().equalsIgnoreCase("incomingfile:")) {
							c = 0;
							break;
						} else
							c = 1;
					}
				} else {
					original = msg;
					for (int i = 0; i < original.length(); i++) {
						char x = original.charAt(i);
						if (x == 32) {
							if (!word.equalsIgnoreCase("Server:") && !word.equalsIgnoreCase("incomingfile:")
									&& !word.equalsIgnoreCase("filesize:")) {
								newstring += word + " ";
							}
							word = "";
						} else {
							word += x;
						}
					}
					word = "";
					for (int i = 0; i < newstring.length(); i++) {
						char x = newstring.charAt(i);
						if (x == 32) {
							filenamex += word + " ";
							word = "";
						} else {
							if (x >= 48 && x <= 57) {
								filesizex += x;
							} else {
								word += x;
							}
						}
					}
					System.out.println(filenamex);
					filesender(filenamex, filesizex);
					c = 1;
				}
			} catch (ClassNotFoundException e) {
				showmessage("\nsomething happened..");
			} catch (OptionalDataException e) {
				filesender(filenamex, filesizex);
				c = 1;
			}
		} while (!msg.equals("Client End "));// System.out.println("filename "+file;name)
	}

	private void showmessage(final String string) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				chatwindow.append("\n" + string);
			}
		});
	}

	private void typepermissions(final boolean tof) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				usertext.setEditable(tof);
			}
		});
	}

	private void streams() throws IOException {
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showmessage("\nconnection green.");
	}

	private void connecttoserver() throws IOException {
		showmessage("connecting....");
		connection = new Socket(InetAddress.getByName(serverIP), 1234);
		clientinfo.setText("Client 1: " + connection.getInetAddress().getHostName());
		showmessage("\nconneted to:" + connection.getInetAddress().getHostName());
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == fileselection) {
			JFileChooser filechoice = new JFileChooser();
			int response = filechoice.showOpenDialog(clientframe.getParent());
			if (response == JFileChooser.APPROVE_OPTION) {
				String filename = filechoice.getSelectedFile().getAbsolutePath();
				{
					try {
						File choosenfile = new File(filename);
						int filesize = (int) choosenfile.length();
						PrintWriter pr = new PrintWriter(output, true);
						BufferedInputStream bin = new BufferedInputStream(new FileInputStream(filename));
						sendmessage("Incomingfile: " + choosenfile.getName() + " filesize: "
								+ (long) choosenfile.length() + " Bytes");
						byte[] bytesize = new byte[filesize];
						bin.read(bytesize, 0, bytesize.length);
						output.write(bytesize, 0, bytesize.length);
						output.flush();
						bin.close();
						pr.close();
					} catch (IOException ioexception) {
						ioexception.printStackTrace();
					}
				}
			}
		}
		if (e.getSource() == bradley) {
			chatwindow.setFont(new Font("Bradley Hand ITC", Font.ITALIC, 18));
		}
		if (e.getSource() == californian) {
			chatwindow.setFont(new Font("Californian FB", Font.ITALIC, 18));
		}
		if (e.getSource() == comic) {
			chatwindow.setFont(new Font("Comic Sans MS", Font.ITALIC, 18));
		}
		if (e.getSource() == logout) {
			int option = JOptionPane.showConfirmDialog(null, "Do you want to logout?", "Logout",
					JOptionPane.YES_NO_OPTION);
			if (option == 0) {
				closethingsup();
			}
		}
		if (e.getSource() == send) {
			String message = usertext.getText();
			sendmessage(message);
			usertext.setText("");

		}
		if (e.getSource() == bred) {
			chatwindow.setBackground(Color.red);
			panel1.setBackground(Color.red);
		}
		if (e.getSource() == bblack) {
			chatwindow.setBackground(Color.black);
			panel1.setBackground(Color.black);
		}
		if (e.getSource() == bblue) {
			chatwindow.setBackground(Color.blue);
			panel1.setBackground(Color.blue);
		}
		if (e.getSource() == bmagenta) {
			chatwindow.setBackground(Color.magenta);
			panel1.setBackground(Color.magenta);
		}
		if (e.getSource() == bcyan) {
			chatwindow.setBackground(Color.cyan);
			panel1.setBackground(Color.cyan);
		}
		if (e.getSource() == bwhite) {
			chatwindow.setBackground(Color.white);
			panel1.setBackground(Color.white);
		}
		if (e.getSource() == bgray) {
			chatwindow.setBackground(Color.gray);
			panel1.setBackground(Color.gray);
		}
		if (e.getSource() == bpink) {
			chatwindow.setBackground(Color.pink);
			panel1.setBackground(Color.pink);
		}
		if (e.getSource() == fred) {
			chatwindow.setForeground(Color.red);
		}
		if (e.getSource() == fblack) {
			chatwindow.setForeground(Color.black);
		}
		if (e.getSource() == fblue) {
			chatwindow.setForeground(Color.blue);
		}
		if (e.getSource() == fmagenta) {
			chatwindow.setForeground(Color.magenta);
		}
		if (e.getSource() == fcyan) {
			chatwindow.setForeground(Color.cyan);
		}
		if (e.getSource() == fwhite) {
			chatwindow.setForeground(Color.white);
		}
		if (e.getSource() == fgray) {
			chatwindow.setForeground(Color.gray);
		}
		if (e.getSource() == fpink) {
			chatwindow.setForeground(Color.pink);

		}
	}

	private void filesender(String filename, String filesize) {
		try {
			long longfilesize = Long.parseLong(filesize);
			filename = "C:\\project for sis\\filetransfer tests\\" + filename;
			File file = new File(filename);
			System.out.println(filename);
			FileOutputStream fos = new FileOutputStream(file, true);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			if (!file.exists()) {
				file.createNewFile();
			}
			long filelength;
			byte[] filebyte = new byte[1024];
			filelength = input.read(filebyte, 0, filebyte.length);
			bos.write(filebyte, 0, filebyte.length);
			if (filelength == longfilesize) {
				showmessage("successful");
				bos.close();
			} else {
				showmessage("corrupt");
				bos.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

package assets;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MP3PlayerGui extends JFrame {

	static class TwoOptionsDialog extends JDialog {
		private JTextField textField1;
		private JTextField textField2;
		private boolean confirmed = false;

		public TwoOptionsDialog(Frame parent, String msg1, String msg2) {
			super(parent, "Enter Values", true);
			JPanel panel = new JPanel(new GridLayout(2, 2));

			textField1 = new JTextField();
			textField2 = new JTextField();

			panel.add(new JLabel(msg1));
			panel.add(textField1);
			panel.add(new JLabel(msg2));
			panel.add(textField2);

			JButton okButton = new JButton("OK");
			okButton.addActionListener(e -> {
				confirmed = true;
				dispose();
			});

			JButton cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(e -> {
				dispose();
			});

			JPanel buttonPanel = new JPanel();
			buttonPanel.add(okButton);
			buttonPanel.add(cancelButton);

			getContentPane().setLayout(new BorderLayout());
			getContentPane().add(panel, BorderLayout.CENTER);
			getContentPane().add(buttonPanel, BorderLayout.SOUTH);

			pack();
			setLocationRelativeTo(parent);
		}

		public boolean isConfirmed() {
			return confirmed;
		}

		public String getValue1() {
			return textField1.getText();
		}

		public String getValue2() {
			return textField2.getText();
		}
	}

	public static final Color BACKGROUND_COLOR = new Color(0x121212);
	public static final Color MAIN_TEXT_COLOR = new Color(0xFFFFFF);
	public static final Color SUB_TEXT_COLOR = new Color(0xA7A7A7);
	public static final Color PLAYBACK_COLOR = new Color(0x000000);
	
	private Player player;
	private JFileChooser jFileChooser;
	private JLabel trackTitle, trackArtist;
	private JTextArea infoTextArea;
	private JButton playButton, pauseButton;
	private JSlider playbackSlider;
	
	public MP3PlayerGui() {
		super("MP3 Player");
		
		setSize(800, 600);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(false);
		setLayout(null);
		getContentPane().setBackground(BACKGROUND_COLOR);
		
		player = new Player(this);
		jFileChooser = new JFileChooser();
		jFileChooser.setCurrentDirectory(new File("src/assets"));
		jFileChooser.setFileFilter(new FileNameExtensionFilter("MP3", "mp3"));
		
		addGuiComponents();
	}
	
	private void addGuiComponents() {
		addToolbar();
		addInfoPanel();
		addPlaybackPanel();
		showTracklist();

		if (player.getCurrentTrack() != null) {
			updateText();
			updatePlaybackSlider();
			updatePlaybackSlider();
			updatePlayPauseButtons(false);
		}
	}

	private void addToolbar() {
		JToolBar toolbar = new JToolBar();
		toolbar.setBounds(0, 0, getWidth(), 20);
		toolbar.setFloatable(false);

		JMenuBar menuBar = new JMenuBar();
		toolbar.add(menuBar);


		// file flow control
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);

		JMenuItem createPlaylist = new JMenuItem("Create Playlist");
		fileMenu.add(createPlaylist);

		createPlaylist.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String userInput = JOptionPane.showInputDialog("Enter playlist name", "Playlist #1");
				if (userInput != null) {
					if (player.createPlaylist(userInput))
						showPlaylist(player.getPlaylists().getLast());
					else
						showMessage("Error: Couldn't create playlist with this name");
				}
			}
		});

		JMenuItem deletePlaylist = new JMenuItem("Delete Playlist");
		fileMenu.add(deletePlaylist);

		deletePlaylist.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String userInput = JOptionPane.showInputDialog("Enter playlist name", "Playlist #1");
				if (userInput != null) {
					if (player.deletePlaylist(userInput))
						showPlaylistsList();
					else
						showMessage("Error: Couldn't delete playlist with this name");
				}
			}
		});

		JMenuItem addSong = new JMenuItem("Add Song To Library");
		fileMenu.add(addSong);

		addSong.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int ret = jFileChooser.showOpenDialog(MP3PlayerGui.this);

				if (ret == JFileChooser.APPROVE_OPTION) {
					File selectedFile = jFileChooser.getSelectedFile();
					Track track = new Track(selectedFile.getPath());
					player.addTrack(track);
					showTracklist();
				} else {
					showMessage("Error: No file was selected");
				}
			}
		});


		JMenu playlistMenu = new JMenu("Playlist");
		menuBar.add(playlistMenu);

		JMenuItem addToPlaylist =  new JMenuItem("Add To A Playlist");
		playlistMenu.add(addToPlaylist);

		addToPlaylist.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TwoOptionsDialog dialog = new TwoOptionsDialog(MP3PlayerGui.this, "Enter Playlist Number", "Enter Track Number");
				dialog.setVisible(true);

				if (dialog.isConfirmed()) {
					String pIndex = dialog.getValue1();
					String tIndex = dialog.getValue2();

					if (pIndex != null && tIndex != null &&
							pIndex.matches("\\d+") && tIndex.matches("\\d+")) {
						if (player.addToPlaylist(Integer.parseInt(pIndex), Integer.parseInt(tIndex)))
							showPlaylist(player.getPlaylistAtIndex(Integer.parseInt(pIndex)));
						else
							showMessage("Error: Couldn't add track to playlist");
					}
				}
			}
		});


		JMenuItem removeFromPlaylist =  new JMenuItem("Remove From A Playlist");
		playlistMenu.add(removeFromPlaylist);

		removeFromPlaylist.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TwoOptionsDialog dialog = new TwoOptionsDialog(MP3PlayerGui.this, "Enter Playlist Number", "Enter Track Number");
				dialog.setVisible(true);

				if (dialog.isConfirmed()) {
					String pIndex = dialog.getValue1();
					String tIndex = dialog.getValue2();

					if (pIndex != null && tIndex != null &&
							pIndex.matches("\\d+") && tIndex.matches("\\d+")) {
						if (player.removeFromPlaylist(Integer.parseInt(pIndex), Integer.parseInt(tIndex)))
							showPlaylist(player.getPlaylistAtIndex(Integer.parseInt(pIndex)));
						else
							showMessage("Error: Couldn't add track to playlist");
					}
				}
			}
		});


		JMenuItem playPlaylist = new JMenuItem("Play Playlist");
		playlistMenu.add(playPlaylist);

		playPlaylist.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String userInput = JOptionPane.showInputDialog("Enter playlist number", "1");
				if (userInput != null && userInput.matches("\\d+")) {
					int playlistNumber = Integer.parseInt(userInput);

					if (player.playPlaylist(playlistNumber)) {
						showPlaylist(player.getPlaylistAtIndex(playlistNumber));
						updateText();
						updatePlayPauseButtons(true);
						updatePlaybackSlider();
					} else {
						showMessage("Error: No playlist with this number");
					}
				} else {
					showMessage("Error: No number was provided");
				}
			}
		});

		JMenuItem playLibrary = new JMenuItem("Play From Library");
		playlistMenu.add(playLibrary);

		playLibrary.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				player.playPlaylist(0);
				showTracklist();
			}
		});


		JMenu viewMenu = new JMenu("View");
		menuBar.add(viewMenu);

		JMenuItem showPlaylist = new JMenuItem("Show A Playlist");
		viewMenu.add(showPlaylist);

		showPlaylist.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String userInput = JOptionPane.showInputDialog("Enter playlist index", "1");
				if (userInput != null && userInput.matches("\\d+")) {
					int playlistNumber = Integer.parseInt(userInput);

					if (playlistNumber > 0 && playlistNumber < player.getPlaylists().size())
						showPlaylist(player.getPlaylistAtIndex(playlistNumber));
					else
						showMessage("Error: No playlist with this number");
				} else {
					showMessage("Error: No number was provided");
				}
			}
		});

		JMenuItem showTracklist = new JMenuItem("Show Tracklist");
		viewMenu.add(showTracklist);

		showTracklist.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showTracklist();
			}
		});

		JMenuItem showPlaylists = new JMenuItem("Show Playlists");
		viewMenu.add(showPlaylists);

		showPlaylists.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showPlaylistsList();
			}
		});

		add(toolbar);
	}

	private void addInfoPanel() {
		infoTextArea = new JTextArea(10, 20);
		infoTextArea.setEditable(false);
		infoTextArea.setBackground(BACKGROUND_COLOR);
		infoTextArea.setForeground(MAIN_TEXT_COLOR);
		infoTextArea.setFont(new Font("Arial", Font.PLAIN, 18));

		JScrollPane scrollPane = new JScrollPane(infoTextArea);
		scrollPane.setBounds(20, 45, getWidth() - 40, getHeight() - 225);
		scrollPane.setBorder(null);

		add(scrollPane);
	}

	private void addPlaybackPanel() {
		JPanel playbackPanel = new JPanel();
		playbackPanel.setBounds(0, getHeight() - 155, getWidth(), 130);
		playbackPanel.setBackground(PLAYBACK_COLOR);
		playbackPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));


		playbackSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
		playbackSlider.setPreferredSize(new Dimension(785, 40));
		playbackSlider.setBorder(null);
		playbackSlider.setBackground(null);
		playbackPanel.add(playbackSlider);

		playbackSlider.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				player.pauseTrack();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				JSlider source = (JSlider) e.getSource();

				int frame = source.getValue();

				if (player.getCurrentTrack() != null) {
					player.setCurrentFrame(frame);
					player.setCurrentTime((int) (frame / (2.05 * player.getCurrentTrack().getFramerate())));

					if (player.playCurrentTrack())
						updatePlayPauseButtons(true);
				}
			}
		});


		JPanel playbackControls = new JPanel();
		playbackControls.setPreferredSize(new Dimension(800, 80));
		playbackControls.setLayout(new BoxLayout(playbackControls, BoxLayout.X_AXIS));
		playbackControls.setBorder(new EmptyBorder(0, 10, 0, 0));
		playbackControls.setBackground(null);
		playbackPanel.add(playbackControls);


		JPanel trackInfo = new JPanel();
		trackInfo.setLayout(new BoxLayout(trackInfo, BoxLayout.Y_AXIS));
		trackInfo.setBackground(null);
		playbackControls.add(trackInfo);

		trackTitle = new JLabel("N/A");
		trackTitle.setFont(new Font("Dialog", Font.BOLD, 20));
		trackTitle.setForeground(MAIN_TEXT_COLOR);
		trackTitle.setHorizontalAlignment(SwingConstants.LEFT);
		trackInfo.add(trackTitle);

		trackArtist = new JLabel("No Artist");
		trackArtist.setFont(new Font("Dialog", Font.PLAIN, 16));
		trackArtist.setForeground(SUB_TEXT_COLOR);
		trackArtist.setHorizontalAlignment(SwingConstants.LEFT);
		trackInfo.add(trackArtist);


		JPanel playbackButtons = new JPanel();
		playbackButtons.setLayout(new BoxLayout(playbackButtons, BoxLayout.X_AXIS));
		playbackButtons.setBackground(null);
		playbackControls.add(Box.createHorizontalStrut(257 - trackInfo.getPreferredSize().width));
		playbackControls.add(playbackButtons);

		JButton prevButton = new JButton(loadImage("src/assets/prev.png", 40, 40));
		prevButton.setBorderPainted(false);
		prevButton.setBackground(null);
		playbackButtons.add(prevButton);

		prevButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				player.playPrevious();

				if (player.getCurrentTrack() != null) {
					updatePlayPauseButtons(true);
					updateText();
					updatePlaybackSlider();
				}
			}
		});


		playButton = new JButton(loadImage("src/assets/play.png", 40, 40));
		playButton.setBorderPainted(false);
		playButton.setBackground(null);
		playbackButtons.add(playButton);

		playButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {


				player.playCurrentTrack();

				if (player.getCurrentTrack() != null) {
					updatePlayPauseButtons(true);
				}
			}
		});


		pauseButton = new JButton(loadImage("src/assets/pause.png", 40, 40));
		pauseButton.setBorderPainted(false);
		pauseButton.setVisible(false);
		pauseButton.setBackground(null);
		playbackButtons.add(pauseButton);

		pauseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				player.pauseTrack();

				if (player.getCurrentTrack() != null) {
					updatePlayPauseButtons(false);
				}
			}
		});


		JButton nextButton = new JButton(loadImage("src/assets/next.png", 40, 40));
		nextButton.setBorderPainted(false);
		nextButton.setBackground(null);
		playbackButtons.add(nextButton);

		nextButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				player.playNext();

				if (player.getCurrentTrack() != null) {
					updatePlayPauseButtons(true);
					updateText();
					updatePlaybackSlider();
				}
			}
		});


		add(playbackPanel);
	}

	void showPlaylist(Playlist playlist) {
		StringBuilder builder = new StringBuilder();

		builder.append(playlist.getTitle()).append("\n");
		builder.append(playlist.getTracklist().size()).append(" Tracks\n\n");

		for (int i = 0; i < playlist.getTracklist().size(); i++) {
			Track track = playlist.getTrackAtIndex(i);
			builder.append(i + 1).append(". ");
			builder.append(track.getTitle()).append(" - ").append(track.getArtist());
			builder.append(" · ").append(track.getFormatedLength()).append("\n");
		}

		if (playlist.getTracklist().isEmpty())
			builder.append("No tracks were added yet");

		infoTextArea.setText(builder.toString());
	}

	void showTracklist() {
		StringBuilder builder = new StringBuilder();

		Playlist playlist = player.getPlaylistAtIndex(0);
		builder.append(playlist.getTracklist().size()).append(" Tracks\n\n");

		for (int i = 0; i < playlist.getTracklist().size(); i++) {
			Track track = playlist.getTrackAtIndex(i);
			builder.append(i + 1).append(". ");
			builder.append(track.getTitle()).append(" - ").append(track.getArtist());
			builder.append(" · ").append(track.getFormatedLength()).append("\n");
		}

		if (playlist.getTracklist().isEmpty())
			builder.append("No tracks were added yet");

		infoTextArea.setText(builder.toString());
	}

	void showPlaylistsList() {
		StringBuilder builder = new StringBuilder();

		builder.append(player.getPlaylists().size() - 1).append(" Playlists\n\n");

		for (int i = 1; i < player.getPlaylists().size(); i++) {
			Playlist playlist = player.getPlaylistAtIndex(i);
			builder.append(i).append(". ").append(playlist.getTitle());
			builder.append(" · ").append(playlist.getTracklist().size()).append(" Tracks\n");
		}

		if (player.getPlaylists().size() == 1)
			builder.append("No playlists were created yet");

		infoTextArea.setText(builder.toString());
	}

	void showMessage(String message) {
		infoTextArea.setText(message);
	}

	public void updateText() {
		trackTitle.setText(player.getCurrentTrack().getTitle());
		trackArtist.setText(player.getCurrentTrack().getArtist());
	}

	public void updatePlayPauseButtons(Boolean play) {
		playButton.setVisible(!play);
		playButton.setEnabled(!play);
		pauseButton.setVisible(play);
		pauseButton.setEnabled(play);
	}

	public void setPlaybackSliderValue(int frame) {
		playbackSlider.setValue(frame);
	}

	public void updatePlaybackSlider() {
		Track track = player.getCurrentTrack();
		playbackSlider.setMaximum(track.getMp3File().getFrameCount());

		Hashtable<Integer, JLabel>  labelTable = new Hashtable<>();

		JLabel labelBeginning = new JLabel("00:00");
		labelBeginning.setFont(new Font("Dialog", Font.BOLD, 18));
		labelBeginning.setForeground(MAIN_TEXT_COLOR);


		JLabel labelEnd = new JLabel(track.getFormatedLength());
		labelEnd.setFont(new Font("Dialog", Font.BOLD, 18));
		labelEnd.setForeground(MAIN_TEXT_COLOR);

		labelTable.put(0, labelBeginning);
		labelTable.put(track.getMp3File().getFrameCount(), labelEnd);

		playbackSlider.setLabelTable(labelTable);
		playbackSlider.setPaintLabels(true);
	}

	private ImageIcon loadImage(String path) {
		try {
			BufferedImage image = ImageIO.read(new File(path));

			return new ImageIcon(image);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private ImageIcon loadImage(String path, Integer sizeX, Integer sizeY) {
		try {
			BufferedImage image = ImageIO.read(new File(path));
			Image resizedImage = image.getScaledInstance(sizeX, sizeY, Image.SCALE_SMOOTH);
			return new ImageIcon(resizedImage);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}


//	private void addPlaylistPanel() {
//		JPanel playlistPanel = new JPanel();
//		playlistPanel.setBounds(0, 20, getWidth(), getHeight() - 145);
//		playlistPanel.setBackground(null);
//		playlistPanel.setLayout(new BoxLayout(playlistPanel, BoxLayout.Y_AXIS));
//		playlistPanel.setBorder(BorderFactory.createEmptyBorder(30, 70, 0, 70));
//
//
//		JPanel playlistInfo = new JPanel();
//		playlistInfo.setBackground(null);
//		playlistInfo.setLayout(new BoxLayout(playlistInfo, BoxLayout.Y_AXIS));
//		playlistInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
//		playlistPanel.add(playlistInfo);
//		playlistPanel.add(Box.createVerticalStrut(12));
//
//		JLabel playlistTitle = new JLabel("Playlist Title");
//		playlistTitle.setFont(new Font("Dialog", Font.BOLD, 20));
//		playlistTitle.setForeground(MAIN_TEXT_COLOR);
//		playlistTitle.setHorizontalAlignment(SwingConstants.LEFT);
//		playlistInfo.add(playlistTitle);
//
//		JLabel playlistNumber = new JLabel("N Tracks");
//		playlistNumber.setFont(new Font("Dialog", Font.PLAIN, 16));
//		playlistNumber.setForeground(MAIN_TEXT_COLOR);
//		playlistNumber.setHorizontalAlignment(SwingConstants.LEFT);
//		playlistInfo.add(playlistNumber);
//
//
//		JPanel playlistActions = new JPanel();
//		playlistActions.setBackground(null);
//		playlistActions.setLayout(new BoxLayout(playlistActions, BoxLayout.X_AXIS));
//		playlistActions.setAlignmentX(Component.LEFT_ALIGNMENT);
//		playlistPanel.add(playlistActions);
//		playlistPanel.add(Box.createVerticalStrut(12));
//
//		JButton playButton = new JButton(loadImage("src/assets/play.png", 32, 32));
//		playButton.setBorderPainted(false);
//		playButton.setBackground(null);
//		playlistActions.add(playButton);
//
//		JButton addButton = new JButton(loadImage("src/assets/plus.png"));
//		addButton.setBorderPainted(false);
//		addButton.setBackground(null);
//		playlistActions.add(addButton);
//
//		JButton moreButton = new JButton(loadImage("src/assets/more.png"));
//		moreButton.setBorderPainted(false);
//		moreButton.setBackground(null);
//		playlistActions.add(moreButton);
//
//
//		JPanel tracksDetails = new JPanel();
//		tracksDetails.setBackground(null);
//		tracksDetails.setMaximumSize(new Dimension(getWidth(), 20));
//		tracksDetails.setLayout(new BoxLayout(tracksDetails, BoxLayout.X_AXIS));
//		tracksDetails.setAlignmentX(Component.LEFT_ALIGNMENT);
//		playlistPanel.add(tracksDetails);
//		playlistPanel.add(Box.createVerticalStrut(8));
//		playlistPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
//
//		JLabel detailNumber = new JLabel("#");
//		detailNumber.setFont(new Font("Dialog", Font.PLAIN, 16));
//		detailNumber.setForeground(SUB_TEXT_COLOR);
//		detailNumber.setHorizontalAlignment(SwingConstants.CENTER);
//		tracksDetails.add(Box.createHorizontalStrut(15));
//		tracksDetails.add(detailNumber);
//		tracksDetails.add(Box.createHorizontalStrut(15));
//
//		JLabel detailTitle = new JLabel("Title");
//		detailTitle.setFont(new Font("Dialog", Font.PLAIN, 16));
//		detailTitle.setForeground(SUB_TEXT_COLOR);
//		detailTitle.setHorizontalAlignment(SwingConstants.CENTER);
//		tracksDetails.add(detailTitle);
//		tracksDetails.add(Box.createHorizontalStrut(525));
//
//		JLabel detailLength = new JLabel("Length");
//		detailLength.setFont(new Font("Dialog", Font.PLAIN, 16));
//		detailLength.setForeground(SUB_TEXT_COLOR);
//		detailLength.setHorizontalAlignment(SwingConstants.CENTER);
//		tracksDetails.add(detailLength);
//
//		playlistPanel.add(getTracksPanelForPlaylist(0));
//
//		add(playlistPanel);
//	}
//
//	private JPanel getTracksPanelForPlaylist(int index) {
//		JPanel playlistTracks = new JPanel();
//		playlistTracks.setBackground(null);
//		playlistTracks.setLayout(new BoxLayout(playlistTracks, BoxLayout.Y_AXIS));
//
//		Integer i = 1;
//		for (Track t : player.getPlaylistAtIndex(index).getTracklist()) {
//			playlistTracks.add(createTrackPanelForPlaylist(i, t));
//			i++;
//		}
//
//		return playlistTracks;
//	}
//
//	private JPanel createTrackPanelForPlaylist(Integer number, Track t) {
//		JPanel trackPanel = new JPanel();
//		trackPanel.setBackground(null);
//		trackPanel.setLayout(new BoxLayout(trackPanel, BoxLayout.X_AXIS));
//		trackPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
//
//
//		JLabel numberLabel = new JLabel(number.toString());
//		numberLabel.setFont(new Font("Dialog", Font.PLAIN, 16));
//		numberLabel.setForeground(SUB_TEXT_COLOR);
//		numberLabel.setHorizontalAlignment(SwingConstants.CENTER);
//		trackPanel.add(numberLabel);
//
//
//		JPanel infoPanel = new JPanel();
//		infoPanel.setBackground(null);
//		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
//		trackPanel.add(infoPanel);
//
//		JLabel titleLabel = new JLabel(t.getTitle());
//		titleLabel.setFont(new Font("Dialog", Font.PLAIN, 16));
//		titleLabel.setForeground(MAIN_TEXT_COLOR);
//		titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
//		infoPanel.add(titleLabel);
//
//		JLabel artistLabel = new JLabel(t.getArtist());
//		artistLabel.setFont(new Font("Dialog", Font.PLAIN, 16));
//		artistLabel.setForeground(SUB_TEXT_COLOR);
//		artistLabel.setHorizontalAlignment(SwingConstants.LEFT);
//		infoPanel.add(artistLabel);
//
//
//		JLabel lengthLabel = new JLabel(t.getFormatedLength());
//		lengthLabel.setFont(new Font("Dialog", Font.PLAIN, 16));
//		lengthLabel.setForeground(SUB_TEXT_COLOR);
//		lengthLabel.setHorizontalAlignment(SwingConstants.LEFT);
//		trackPanel.add(Box.createHorizontalStrut(250));
//		trackPanel.add(lengthLabel);
//
//
//		return trackPanel;
//	}
//
//	private void addPlaybackButtons() {
//		playButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				if (player.playCurrentTrack()) {
//					updatePlayPauseButtons(true);
//				}
//			}
//		});
//
//		pauseButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				player.pauseTrack();
//				updatePlayPauseButtons(false);
//			}
//		});
//	}
}
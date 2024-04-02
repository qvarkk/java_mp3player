package assets;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MP3PlayerGui extends JFrame {
	public static final Color BACKGROUND_COLOR = new Color(0x121212);
	public static final Color MAIN_TEXT_COLOR = new Color(0xFFFFFF);
	public static final Color SUB_TEXT_COLOR = new Color(0xA7A7A7);
	public static final Color PLAYBACK_COLOR = new Color(0x000000);
	
	private Player player;
	private JFileChooser jFileChooser;
	private JLabel trackTitle, trackArtist;
	private JButton playButton, pauseButton;
	
	public MP3PlayerGui() {
		super("MP3 Player");
		
		setSize(800, 600);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(false);
		setLayout(null);
		getContentPane().setBackground(BACKGROUND_COLOR);
		
		player = new Player();
		jFileChooser = new JFileChooser();
		jFileChooser.setCurrentDirectory(new File("src/assets"));
		jFileChooser.setFileFilter(new FileNameExtensionFilter("MP3", "mp3"));
		
		addGuiComponents();
	}
	
	private void addGuiComponents() {
		addToolbar();
		addPlaylistPanel();
		addPlaybackPanel();
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

		JMenuItem addSong = new JMenuItem("Add Song To Library");
		fileMenu.add(addSong);

//		addToPlaylist.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				jFileChooser.showOpenDialog(MP3PlayerGui.this);
//				File selectedFile = jFileChooser.getSelectedFile();
//
//				if (selectedFile != null) {
//					Track track = new Track(selectedFile.getPath());
//					player.loadTrack(track);
//					updateText(track);
//					updatePlayPauseButtons(true);
//				}
//			}
//		});


		JMenu playbackMenu = new JMenu("Playback");
		menuBar.add(playbackMenu);

		JMenuItem previousTrack = new JMenuItem("Previous Track");
		playbackMenu.add(previousTrack);

		JMenuItem nextTrack = new JMenuItem("Next Track");
		playbackMenu.add(nextTrack);

		JMenuItem repeatTrack = new JMenuItem("Repeat Track");
		playbackMenu.add(repeatTrack);


		JMenu viewMenu = new JMenu("View");
		menuBar.add(viewMenu);

		JMenuItem showTracklist = new JMenuItem("Show Tracklist");
		viewMenu.add(showTracklist);

		JMenuItem showPlaylist = new JMenuItem("Show Playlists");
		viewMenu.add(showPlaylist);

		add(toolbar);
	}

	private void addPlaylistPanel() {
		JPanel playlistPanel = new JPanel();
		playlistPanel.setBounds(0, 20, getWidth(), getHeight() - 145);
		playlistPanel.setBackground(null);
		playlistPanel.setLayout(new BoxLayout(playlistPanel, BoxLayout.Y_AXIS));
		playlistPanel.setBorder(BorderFactory.createEmptyBorder(30, 70, 0, 70));


		JPanel playlistInfo = new JPanel();
		playlistInfo.setBackground(null);
		playlistInfo.setLayout(new BoxLayout(playlistInfo, BoxLayout.Y_AXIS));
		playlistInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
		playlistPanel.add(playlistInfo);
		playlistPanel.add(Box.createVerticalStrut(12));

		JLabel playlistTitle = new JLabel("Playlist Title");
		playlistTitle.setFont(new Font("Dialog", Font.BOLD, 20));
		playlistTitle.setForeground(MAIN_TEXT_COLOR);
		playlistTitle.setHorizontalAlignment(SwingConstants.LEFT);
		playlistInfo.add(playlistTitle);

		JLabel playlistNumber = new JLabel("N Tracks");
		playlistNumber.setFont(new Font("Dialog", Font.PLAIN, 16));
		playlistNumber.setForeground(MAIN_TEXT_COLOR);
		playlistNumber.setHorizontalAlignment(SwingConstants.LEFT);
		playlistInfo.add(playlistNumber);


		JPanel playlistActions = new JPanel();
		playlistActions.setBackground(null);
		playlistActions.setLayout(new BoxLayout(playlistActions, BoxLayout.X_AXIS));
		playlistActions.setAlignmentX(Component.LEFT_ALIGNMENT);
		playlistPanel.add(playlistActions);
		playlistPanel.add(Box.createVerticalStrut(12));

		JButton playButton = new JButton(loadImage("src/assets/play.png", 32, 32));
		playButton.setBorderPainted(false);
		playButton.setBackground(null);
		playlistActions.add(playButton);

		JButton addButton = new JButton(loadImage("src/assets/plus.png"));
		addButton.setBorderPainted(false);
		addButton.setBackground(null);
		playlistActions.add(addButton);

		JButton moreButton = new JButton(loadImage("src/assets/more.png"));
		moreButton.setBorderPainted(false);
		moreButton.setBackground(null);
		playlistActions.add(moreButton);


		JPanel tracksDetails = new JPanel();
		tracksDetails.setBackground(null);
		tracksDetails.setMaximumSize(new Dimension(getWidth(), 20));
		tracksDetails.setLayout(new BoxLayout(tracksDetails, BoxLayout.X_AXIS));
		tracksDetails.setAlignmentX(Component.LEFT_ALIGNMENT);
		playlistPanel.add(tracksDetails);
		playlistPanel.add(Box.createVerticalStrut(8));
		playlistPanel.add(new JSeparator(SwingConstants.HORIZONTAL));

		JLabel detailNumber = new JLabel("#");
		detailNumber.setFont(new Font("Dialog", Font.PLAIN, 16));
		detailNumber.setForeground(SUB_TEXT_COLOR);
		detailNumber.setHorizontalAlignment(SwingConstants.CENTER);
		tracksDetails.add(Box.createHorizontalStrut(15));
		tracksDetails.add(detailNumber);
		tracksDetails.add(Box.createHorizontalStrut(15));

		JLabel detailTitle = new JLabel("Title");
		detailTitle.setFont(new Font("Dialog", Font.PLAIN, 16));
		detailTitle.setForeground(SUB_TEXT_COLOR);
		detailTitle.setHorizontalAlignment(SwingConstants.CENTER);
		tracksDetails.add(detailTitle);
		tracksDetails.add(Box.createHorizontalStrut(525));

		JLabel detailLength = new JLabel("Length");
		detailLength.setFont(new Font("Dialog", Font.PLAIN, 16));
		detailLength.setForeground(SUB_TEXT_COLOR);
		detailLength.setHorizontalAlignment(SwingConstants.CENTER);
		tracksDetails.add(detailLength);


//		JPanel playlistTracks = new JPanel();
//		playlistTracks.setBackground(null);
//		playlistTracks.setLayout(new BoxLayout(playlistTracks, BoxLayout.Y_AXIS));
//		playlistPanel.add(Box.createVerticalGlue());
//		playlistPanel.add(playlistTracks);
//
//
//		JPanel trackPanel = new JPanel();
//		trackPanel.setBackground(Color.DARK_GRAY);
//		trackPanel.setLayout(new BoxLayout(trackPanel, BoxLayout.X_AXIS));
//		playlistPanel.add(trackPanel);
//
//		JLabel numberLabel = new JLabel("1");
//		numberLabel.setFont(new Font("Dialog", Font.PLAIN, 16));
//		numberLabel.setForeground(SUB_TEXT_COLOR);
//		numberLabel.setHorizontalAlignment(SwingConstants.CENTER);
//		trackPanel.add(numberLabel);

		playlistPanel.add(getTracksPanelForPlaylist(0));

		add(playlistPanel);
	}

	private JPanel getTracksPanelForPlaylist(int index) {
		JPanel playlistTracks = new JPanel();
		playlistTracks.setBackground(null);
		playlistTracks.setLayout(new BoxLayout(playlistTracks, BoxLayout.Y_AXIS));

		Integer i = 1;
		for (Track t : player.getPlaylistAtIndex(index).getTracklist()) {
			playlistTracks.add(createTrackPanelForPlaylist(i, t));
			i++;
		}

		return playlistTracks;
	}

	private JPanel createTrackPanelForPlaylist(Integer number, Track t) {
		JPanel trackPanel = new JPanel();
		trackPanel.setBackground(null);
		trackPanel.setLayout(new BoxLayout(trackPanel, BoxLayout.X_AXIS));
		trackPanel.setAlignmentX(Component.LEFT_ALIGNMENT);


		JLabel numberLabel = new JLabel(number.toString());
		numberLabel.setFont(new Font("Dialog", Font.PLAIN, 16));
		numberLabel.setForeground(SUB_TEXT_COLOR);
		numberLabel.setHorizontalAlignment(SwingConstants.CENTER);
		trackPanel.add(numberLabel);


		JPanel infoPanel = new JPanel();
		infoPanel.setBackground(null);
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
		trackPanel.add(infoPanel);

		JLabel titleLabel = new JLabel(t.getTitle());
		titleLabel.setFont(new Font("Dialog", Font.PLAIN, 16));
		titleLabel.setForeground(MAIN_TEXT_COLOR);
		titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
		infoPanel.add(titleLabel);

		JLabel artistLabel = new JLabel(t.getArtist());
		artistLabel.setFont(new Font("Dialog", Font.PLAIN, 16));
		artistLabel.setForeground(SUB_TEXT_COLOR);
		artistLabel.setHorizontalAlignment(SwingConstants.LEFT);
		infoPanel.add(artistLabel);


		JLabel lengthLabel = new JLabel(t.getFormatedLength());
		lengthLabel.setFont(new Font("Dialog", Font.PLAIN, 16));
		lengthLabel.setForeground(SUB_TEXT_COLOR);
		lengthLabel.setHorizontalAlignment(SwingConstants.LEFT);
		trackPanel.add(Box.createHorizontalStrut(250));
		trackPanel.add(lengthLabel);


		return trackPanel;
	}

	private void addPlaybackPanel() {
		JPanel playbackPanel = new JPanel();
		playbackPanel.setBounds(0, getHeight() - 125, getWidth(), 87);
		playbackPanel.setBackground(PLAYBACK_COLOR);
		playbackPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));


		JSlider playbackSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
		playbackSlider.setPreferredSize(new Dimension(785, 7));
		playbackSlider.setBorder(null);
		playbackSlider.setBackground(null);
		playbackPanel.add(playbackSlider);


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

		trackTitle = new JLabel("Track Title");
		trackTitle.setFont(new Font("Dialog", Font.BOLD, 20));
		trackTitle.setForeground(MAIN_TEXT_COLOR);
		trackTitle.setHorizontalAlignment(SwingConstants.LEFT);
		trackInfo.add(trackTitle);

		trackArtist = new JLabel("Track Title");
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

		playButton = new JButton(loadImage("src/assets/play.png", 40, 40));
		playButton.setBorderPainted(false);
		playButton.setBackground(null);
		playbackButtons.add(playButton);

		pauseButton = new JButton(loadImage("src/assets/pause.png", 40, 40));
		pauseButton.setBorderPainted(false);
		pauseButton.setVisible(false);
		pauseButton.setBackground(null);
		playbackButtons.add(pauseButton);

		JButton nextButton = new JButton(loadImage("src/assets/next.png", 40, 40));
		nextButton.setBorderPainted(false);
		nextButton.setBackground(null);
		playbackButtons.add(nextButton);


		add(playbackPanel);
	}
	
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
	
	private void updateText(Track track) {
		trackTitle.setText(track.getTitle());
		trackArtist.setText(track.getArtist());
	}
	
	private void updatePlayPauseButtons(Boolean play) {
		playButton.setVisible(!play);
		playButton.setEnabled(!play);
		pauseButton.setVisible(play);
		pauseButton.setEnabled(play);
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
}
package panels;

import application.JournalService;
import frames.JournalFrame;
import models.Resources;
import themes.Colors;
import themes.Fonts;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

public class BookmarkPanel extends JPanel {
    private JPanel topPanel;
    private JPanel journalsPanel;
    private JournalFrame journalWindow;
    private JournalService journalService;

    public BookmarkPanel(JournalService journalService) throws IOException {
        this.journalService = journalService;

        setLayout(new BorderLayout());
        setOpaque(false);

        initTopPanel();
        initTitle();
        initJournalsPanel();
    }

    private void initTopPanel() {
        topPanel = new JPanel();
        topPanel.setLayout(new GridLayout());
        topPanel.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(150, 150, 150)),
                BorderFactory.createEmptyBorder(15, 15, 15, 10)));
        topPanel.setOpaque(false);

        add(topPanel, BorderLayout.PAGE_START);
    }

    private void initTitle() {
        JLabel label = new JLabel("매매 일지");
        label.setForeground(Colors.FONT);
        label.setFont(Fonts.LARGE_BOLD);
        topPanel.add(label);
    }

    private JScrollPane createJournalsPanel() {
        JPanel panel = new JPanel();

        if (journalService.getStarredIds().size() == 0) {
            panel = new JPanel();
            panel.setLayout(new BorderLayout());
            panel.setBorder(null);

            JPanel messagePanel = new JPanel();

            messagePanel.add(new JLabel("자주 보고 싶은 일지를 즐겨찾기 해주세요!"));

            messagePanel.setOpaque(false);

            panel.add(messagePanel, BorderLayout.PAGE_START);

            panel.setOpaque(false);

            JScrollPane scrollPane = new JScrollPane(panel);
            scrollPane.setBorder(null);

            return scrollPane;
        }

        panel.setLayout(new GridLayout(0, 1));
        panel.setBorder(new EmptyBorder(10, 0, 0, 0));

        for (UUID id : journalService.getStarredIds()) {
            panel.add(createJournalPanel(id));
        }

        panel.setOpaque(false);

        JPanel wrapperPanel = new JPanel();
        wrapperPanel.setLayout(new BorderLayout());

        wrapperPanel.add(panel, BorderLayout.PAGE_START);

        wrapperPanel.setOpaque(false);

        JScrollPane jScrollPane = new JScrollPane(wrapperPanel);
        jScrollPane.setPreferredSize(new Dimension(300, 530));
        jScrollPane.setBorder(null);

        jScrollPane.setOpaque(false);

        return jScrollPane;
    }

    private JPanel createJournalPanel(UUID journalId) {
        LocalDate date = journalService.date((UUID) journalId);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0;

        JButton button = new JButton(
                "<html>" + date.getDayOfMonth() + " " + date.getMonth() + "<br>" + journalService.title((UUID) journalId) + "</html>");
        button.setFont(Fonts.MEDIUM);
        button.setHorizontalAlignment(JButton.LEFT);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addActionListener(event -> {
            if (journalWindow != null && journalWindow.isDisplayable()) {
                return;
            }

            journalWindow = new JournalFrame(journalService, (UUID) journalId);

            journalWindow.setVisible(true);

            journalWindow.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    try {
                        updateJournalsPanel();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
        });

        panel.add(button, gridBagConstraints);

        Image starImage = Resources.STAR_LINED_IMAGE;

        if (journalService.starred((UUID) journalId)) {
            starImage = Resources.STAR_FILLED_IMAGE;
        }

        Icon starIcon = new ImageIcon(starImage);

        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(0, 0, 10, 0);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 0.0;

        JButton starButton = new JButton(starIcon);
        starButton.setBorderPainted(false);
        starButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        starButton.addActionListener(event -> {
            journalService.toggleStar((UUID) journalId);

            try {
                updateJournalsPanel();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        panel.add(starButton, gridBagConstraints);

        Icon xIcon = new ImageIcon(Resources.X_IMAGE);

        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;

        JButton deleteButton = new JButton(xIcon);
        deleteButton.setFont(Fonts.LARGE_BOLD);
        deleteButton.setBorderPainted(false);
        deleteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        deleteButton.addActionListener(event -> {
            try {
                journalService.remove((UUID) journalId);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try {
                updateJournalsPanel();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        panel.add(deleteButton, gridBagConstraints);

        panel.setOpaque(false);

        return panel;
    }

    private void initJournalsPanel() {
        journalsPanel = new JPanel();
        journalsPanel.setLayout(new BorderLayout());

        journalsPanel.add(createJournalsPanel(), BorderLayout.PAGE_START);

        journalsPanel.setOpaque(false);

        add(journalsPanel, BorderLayout.CENTER);
    }

    private void updateJournalsPanel() throws IOException {
        journalsPanel.removeAll();
        journalsPanel.add(createJournalsPanel(), BorderLayout.PAGE_START);
        journalsPanel.setVisible(false);
        journalsPanel.setVisible(true);
    }
}

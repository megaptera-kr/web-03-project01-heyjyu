package panels;

import frames.JournalingFrame;
import models.InvestGod;
import models.Journal;
import themes.Colors;
import themes.Fonts;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class JournalsPanel extends JPanel {
    private InvestGod investGod;
    private List<Journal> journals;

    private JPanel topPanel;
    private JournalingFrame journalingWindow;
    private JPanel journalsPanel;

    public JournalsPanel(InvestGod investGod) throws IOException {
        this.investGod = investGod;
        this.journals = investGod.journals();
        setLayout(new BorderLayout());
        setOpaque(false);

        initTopPanel();
        initAddButton();
        initTitle();
        initJournalsPanel();
    }

    private void initTopPanel() {
        topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(0, 1));
        topPanel.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(150, 150, 150)),
                BorderFactory.createEmptyBorder(5, 15, 5, 10)));
        topPanel.setOpaque(false);

        add(topPanel, BorderLayout.PAGE_START);
    }

    private void initAddButton() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        JButton button = new JButton("+");
        button.setForeground(Colors.FONT);
        button.setFont(new Font("Verbose", Font.PLAIN, 26));
        button.setBorder(null);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(event -> {
            if (journalingWindow != null && journalingWindow.isDisplayable()) {
                return;
            }

            journalingWindow = new JournalingFrame(investGod);

            journalingWindow.setVisible(true);

            journalingWindow.addWindowListener(new WindowAdapter() {
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

        buttonPanel.add(button);
        topPanel.add(buttonPanel);
    }

    private void initTitle() {
        JLabel label = new JLabel("매매 일지");
        label.setForeground(Colors.FONT);
        label.setFont(Fonts.BOLD);
        topPanel.add(label);
    }

    private JPanel createJournalsPanel() throws IOException {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));
        panel.setBorder(new EmptyBorder(20, 0, 0, 0));

        for (Journal journal : journals) {
            panel.add(createJournalPanel(journal));
        }

        panel.setOpaque(false);

        return panel;
    }

    private JPanel createJournalPanel(Journal journal) throws IOException {
        LocalDate date = journal.date();

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(0, 0, 15, 0);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0;

        JButton button = new JButton(
                "<html>" + date.getDayOfMonth() + " " + date.getMonth() + "<br>" + journal.title() + "</html>");
        button.setFont(Fonts.MEDIUM);
        button.setHorizontalAlignment(JButton.LEFT);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addActionListener(event -> {
            //TODO: 내용 보기 프레임 열기
        });

        panel.add(button, gridBagConstraints);

        Image image = ImageIO.read(new File("src/main/resources/images/iconmonstr-star-lined-32.png"));

        if (journal.starred()) {
            image = ImageIO.read(new File("src/main/resources/images/iconmonstr-star-filled-32.png"));
        }

        Icon icon = new ImageIcon(image);

        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(0, 0, 0, 10);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 0.0;

        JButton iconButton = new JButton(icon);
        iconButton.setBorderPainted(false);
        iconButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        iconButton.addActionListener(event -> {
            journal.toggleStar();

            try {
                updateJournalsPanel();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        panel.add(iconButton, gridBagConstraints);

        panel.setOpaque(false);

        return panel;
    }

    private void initJournalsPanel() throws IOException {
        journalsPanel = new JPanel();
        journalsPanel.setLayout(new BorderLayout());
        System.out.println(journals.size());

        if (journals.size() == 0) {
            JPanel messagePanel = new JPanel();

            messagePanel.add(new JLabel("일지를 작성해주세요!"));

            messagePanel.setOpaque(false);

            journalsPanel.add(messagePanel, BorderLayout.PAGE_START);

            journalsPanel.setOpaque(false);

            add(journalsPanel, BorderLayout.CENTER);

            return;
        }

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
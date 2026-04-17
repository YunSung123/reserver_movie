package com.tenco.view;

import com.tenco.dto.*;
import com.tenco.service.ReserveService;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

public class MovieReserveSwingApp extends JFrame {

    private final ReserveService reserveService = new ReserveService();

    private final DefaultListModel<String> bookingInfoModel = new DefaultListModel<>();
    private final JPanel movieListPanel = new JPanel();
    private final JPanel seatGridPanel = new JPanel();
    private final JLabel posterPreviewLabel = new JLabel();
    private final JLabel movieTitleLabel = new JLabel("현재 선택중인 영화");
    private final JLabel movieMetaLabel = new JLabel(" ");
    private final JLabel selectedSeatLabel = new JLabel("선택 좌석: 없음");
    private final JLabel statusLabel = new JLabel("준비 완료");

    private Movies selectedMovie;
    private Room selectedRoom;
    private User loginUser;
    private JLabel loginStatusLabel;

    private final List<SeatButton> seatButtons = new ArrayList<>();
    private final Set<Integer> selectedSeatNumbers = new HashSet<>();

    private static final int ROWS = 5;
    private static final int COLS = 8;

    public MovieReserveSwingApp() {
        setTitle("영화 예매 시스템");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1500, 850);
        setLocationRelativeTo(null);
        setContentPane(buildRootPanel());

        buildSeatGrid();
        loadMovies();
    }

    private JPanel buildRootPanel() {
        JPanel root = new JPanel(new BorderLayout(18, 18));
        root.setBorder(new EmptyBorder(18, 18, 18, 18));
        root.setBackground(new Color(18, 18, 24));

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildCenter(), BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);

        return root;
    }

    private JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("MOVIE RESERVE SYSTEM");
        title.setFont(new Font("맑은 고딕", Font.BOLD, 28));
        title.setForeground(Color.WHITE);

        JLabel sub = new JLabel("영화 선택 → 좌석 선택 → 예매 완료");
        sub.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        sub.setForeground(new Color(180, 180, 190));

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);
        left.add(title);
        left.add(Box.createVerticalStrut(4));
        left.add(sub);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        loginStatusLabel = new JLabel("로그인 상태: 비로그인");
        loginStatusLabel.setForeground(new Color(220, 220, 230));
        loginStatusLabel.setFont(new Font("맑은 고딕", Font.BOLD, 13));

        JButton signUpButton = createAccentButton("회원가입");
        signUpButton.addActionListener(e -> showSignUpDialog());

        JButton loginButton = createAccentButton("로그인");
        loginButton.addActionListener(e -> showLoginDialog());

        JButton logoutButton = createGrayButton("로그아웃");
        logoutButton.addActionListener(e -> logoutAction());

        JButton addMovieButton = createAccentButton("영화 등록");
        addMovieButton.addActionListener(e -> showAddMovieDialog());

        JButton updateMovieButton = createGrayButton("영화 수정");
        updateMovieButton.addActionListener(e -> showUpdateMovieDialog());

        JButton deleteMovieButton = createGrayButton("영화 삭제");
        deleteMovieButton.addActionListener(e -> deleteSelectedMovie());

        right.add(loginStatusLabel);
        right.add(signUpButton);
        right.add(loginButton);
        right.add(logoutButton);
        right.add(addMovieButton);
        right.add(updateMovieButton);
        right.add(deleteMovieButton);

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    private JComponent buildCenter() {
        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                buildMovieListSection(),
                buildReservationSection()
        );
        splitPane.setDividerLocation(500);
        splitPane.setBorder(null);
        splitPane.setOpaque(false);
        return splitPane;
    }

    private JComponent buildMovieListSection() {
        JPanel wrapper = createCardPanel();
        wrapper.setLayout(new BorderLayout(0, 14));

        JLabel sectionTitle = createSectionTitle("상영 영화 목록");
        JLabel guideLabel = new JLabel("영화를 클릭해서 선택하세요");
        guideLabel.setForeground(new Color(180, 180, 190));
        guideLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));

        JButton refreshButton = createAccentButton("새로고침");
        refreshButton.addActionListener(e -> loadMovies());

        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        headerRow.add(sectionTitle, BorderLayout.WEST);
        headerRow.add(refreshButton, BorderLayout.EAST);

        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setOpaque(false);
        top.add(headerRow);
        top.add(Box.createVerticalStrut(4));
        top.add(guideLabel);

        movieListPanel.setLayout(new BoxLayout(movieListPanel, BoxLayout.Y_AXIS));
        movieListPanel.setBackground(new Color(28, 28, 35));

        JScrollPane scrollPane = new JScrollPane(movieListPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        wrapper.add(top, BorderLayout.NORTH);
        wrapper.add(scrollPane, BorderLayout.CENTER);
        return wrapper;
    }

    private JComponent buildReservationSection() {
        JPanel wrapper = new JPanel(new BorderLayout(14, 14));
        wrapper.setOpaque(false);

        wrapper.add(buildMoviePreviewCard(), BorderLayout.NORTH);
        wrapper.add(buildSeatSelectionCard(), BorderLayout.CENTER);
        wrapper.add(buildBookingInfoCard(), BorderLayout.EAST);

        return wrapper;
    }

    private JComponent buildMoviePreviewCard() {
        JPanel panel = createCardPanel();
        panel.setLayout(new BorderLayout(16, 0));
        panel.setPreferredSize(new Dimension(0, 240));

        posterPreviewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        posterPreviewLabel.setPreferredSize(new Dimension(180, 220));
        posterPreviewLabel.setOpaque(true);
        posterPreviewLabel.setBackground(new Color(40, 40, 48));
        posterPreviewLabel.setBorder(new LineBorder(new Color(70, 70, 82), 1, true));
        posterPreviewLabel.setText("POSTER");
        posterPreviewLabel.setForeground(new Color(190, 190, 200));

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        movieTitleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 26));
        movieTitleLabel.setForeground(Color.WHITE);

        movieMetaLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        movieMetaLabel.setForeground(new Color(190, 190, 200));

        JTextArea guide = new JTextArea(
                "선택한 영화의 포스터와 기본 정보가 표시됩니다. " +
                        "좌석은 클릭으로 선택/해제할 수 있으며, 예매 버튼을 누르면 ReserveService와 연결됩니다."
        );
        guide.setEditable(false);
        guide.setLineWrap(true);
        guide.setWrapStyleWord(true);
        guide.setOpaque(false);
        guide.setForeground(new Color(180, 180, 190));
        guide.setFont(new Font("맑은 고딕", Font.PLAIN, 13));

        info.add(movieTitleLabel);
        info.add(Box.createVerticalStrut(10));
        info.add(movieMetaLabel);
        info.add(Box.createVerticalStrut(18));
        info.add(guide);

        panel.add(posterPreviewLabel, BorderLayout.WEST);
        panel.add(info, BorderLayout.CENTER);
        return panel;
    }

    private JComponent buildSeatSelectionCard() {
        JPanel panel = createCardPanel();
        panel.setLayout(new BorderLayout(0, 12));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel title = createSectionTitle("좌석 선택");
        selectedSeatLabel.setForeground(new Color(255, 210, 120));
        selectedSeatLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));

        top.add(title, BorderLayout.WEST);
        top.add(selectedSeatLabel, BorderLayout.EAST);

        seatGridPanel.setOpaque(false);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);

        JButton resetButton = createGrayButton("선택 초기화");
        resetButton.addActionListener(e -> clearSeatSelection());

        JButton reserveButton = createAccentButton("예매하기");
        reserveButton.addActionListener(e -> reserveSelectedSeats());

        bottom.add(resetButton);
        bottom.add(reserveButton);

        panel.add(top, BorderLayout.NORTH);
        panel.add(seatGridPanel, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    private JComponent buildBookingInfoCard() {
        JPanel panel = createCardPanel();
        panel.setLayout(new BorderLayout(0, 12));
        panel.setPreferredSize(new Dimension(260, 0));

        JLabel title = createSectionTitle("예매 정보");

        JList<String> bookingInfoList = new JList<>(bookingInfoModel);
        bookingInfoList.setBackground(new Color(28, 28, 35));
        bookingInfoList.setForeground(Color.WHITE);
        bookingInfoList.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        bookingInfoList.setSelectionBackground(new Color(60, 90, 150));

        bookingInfoModel.addElement("로그인 사용자: 없음");
        bookingInfoModel.addElement("영화: 선택 안됨");
        bookingInfoModel.addElement("상영관: 없음");
        bookingInfoModel.addElement("좌석: 없음");
        bookingInfoModel.addElement("총 금액: 0원");

        panel.add(title, BorderLayout.NORTH);
        panel.add(new JScrollPane(bookingInfoList), BorderLayout.CENTER);
        return panel;
    }

    private JComponent buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);

        statusLabel.setForeground(new Color(170, 170, 180));
        statusLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));

        JLabel legend = new JLabel("매우 어두운 회색: 예약불가   |   파랑: 선택됨   |   밝은 회색: 예약가능");
        legend.setForeground(new Color(170, 170, 180));
        legend.setFont(new Font("맑은 고딕", Font.PLAIN, 12));

        footer.add(statusLabel, BorderLayout.WEST);
        footer.add(legend, BorderLayout.EAST);
        return footer;
    }

    private JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(28, 28, 35));
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(58, 58, 70), 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));
        return panel;
    }

    private JLabel createSectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        label.setForeground(Color.WHITE);
        return label;
    }

    private JButton createAccentButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(76, 110, 245));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setBorder(new EmptyBorder(10, 16, 10, 16));
        return button;
    }

    private JButton createGrayButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(72, 72, 82));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setBorder(new EmptyBorder(10, 16, 10, 16));
        return button;
    }

    private void buildSeatGrid() {
        seatGridPanel.removeAll();
        seatGridPanel.setLayout(new GridLayout(ROWS + 1, COLS + 1, 8, 8));
        seatButtons.clear();

        seatGridPanel.add(new JLabel(""));
        for (int c = 1; c <= COLS; c++) {
            JLabel colLabel = new JLabel(String.valueOf(c), SwingConstants.CENTER);
            colLabel.setForeground(Color.WHITE);
            seatGridPanel.add(colLabel);
        }

        int roomNumber = (selectedRoom == null) ? 1 : selectedRoom.getRoomNumber();

        for (int r = 1; r <= ROWS; r++) {
            char rowChar = (char) ('A' + r - 1);
            JLabel rowLabel = new JLabel(String.valueOf(rowChar), SwingConstants.CENTER);
            rowLabel.setForeground(Color.WHITE);
            seatGridPanel.add(rowLabel);

            for (int c = 1; c <= COLS; c++) {
                int seatNumber = (r - 1) * COLS + c;

                SeatButton btn = new SeatButton(seatNumber, rowChar + "-" + c);
                btn.setPreferredSize(new Dimension(70, 48));
                btn.setAvailableSeat(true);
                btn.addActionListener(e -> toggleSeat(btn));
                seatButtons.add(btn);
                seatGridPanel.add(btn);
            }
        }

        seatGridPanel.revalidate();
        seatGridPanel.repaint();
    }

    public void updateLoginStatusLabel() {
        if (loginStatusLabel == null) {
            return;
        }

        if (loginUser == null) {
            loginStatusLabel.setText("로그인 상태: 비로그인");
        } else {
            loginStatusLabel.setText("로그인 상태: " + loginUser.getName());
        }
    }

    private void showSignUpDialog() {
        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField nameField = new JTextField();
        JTextField ageField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 1, 8, 8));
        panel.add(new JLabel("이메일"));
        panel.add(emailField);
        panel.add(new JLabel("비밀번호"));
        panel.add(passwordField);
        panel.add(new JLabel("이름"));
        panel.add(nameField);
        panel.add(new JLabel("나이"));
        panel.add(ageField);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "회원가입",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String name = nameField.getText().trim();
        String ageText = ageField.getText().trim();

        if (email.isEmpty() || password.isEmpty() || name.isEmpty() || ageText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "모든 항목을 입력해주세요.");
            return;
        }

        try {
            int age = Integer.parseInt(ageText);

            Customer customer = new Customer();
            customer.setEmail(email);
            customer.setPassword(password);
            customer.setName(name);
            customer.setAge(age);

            Customer resultCustomer = reserveService.signUp(customer);

            if (resultCustomer != null) {
                JOptionPane.showMessageDialog(this, "회원가입이 완료되었습니다.");
                statusLabel.setText("회원가입 완료: " + name);
            } else {
                JOptionPane.showMessageDialog(this, "회원가입에 실패했습니다.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "나이는 숫자로 입력해주세요.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "회원가입 중 오류: " + e.getMessage());
        }
    }

    private void showLoginDialog() {
        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        JPanel panel = new JPanel(new GridLayout(0, 1, 8, 8));
        panel.add(new JLabel("이메일"));
        panel.add(emailField);
        panel.add(new JLabel("비밀번호"));
        panel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "로그인",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "이메일과 비밀번호를 입력해주세요.");
            return;
        }

        try {
            User user = reserveService.login(email, password);

            if (user != null) {
                loginUser = user;
                updateLoginStatusLabel();
                refreshBookingInfo();

                if (user instanceof Customer) {
                    loginUser = (Customer) user;
                    statusLabel.setText("로그인 성공 (고객): " + user.getName());
                    JOptionPane.showMessageDialog(this, user.getName() + "님 로그인되었습니다.");

                } else if (user instanceof Admin) {
                    statusLabel.setText("관리자 로그인: " + user.getName());
                    JOptionPane.showMessageDialog(this, "관리자 " + user.getName() + "님 로그인");

                }

            } else {
                JOptionPane.showMessageDialog(this, "로그인에 실패했습니다. 계정을 확인해주세요.");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "로그인 중 오류: " + e.getMessage());
        }
    }

    private void logoutAction() {
        try {
            reserveService.logout();
        } catch (Exception ignored) {
        }

        loginUser = null;
        updateLoginStatusLabel();
        refreshBookingInfo();
        statusLabel.setText("로그아웃 되었습니다.");
        JOptionPane.showMessageDialog(this, "로그아웃 되었습니다.");
    }

    private void showAddMovieDialog() {
        if (!(loginUser instanceof Admin)) {
            JOptionPane.showMessageDialog(
                    this,
                    "관리자만 영화 등록이 가능합니다.",
                    "권한 없음",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        JTextField titleField = new JTextField();
        JTextField gradeField = new JTextField();
        JTextField priceField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 1, 8, 8));
        panel.add(new JLabel("영화 제목"));
        panel.add(titleField);
        panel.add(new JLabel("영화 등급"));
        panel.add(gradeField);
        panel.add(new JLabel("영화 가격"));
        panel.add(priceField);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "영화 등록",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String title = titleField.getText().trim();
        String grade = gradeField.getText().trim();
        String priceText = priceField.getText().trim();

        if (title.isEmpty() || grade.isEmpty() || priceText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "모든 항목을 입력해주세요.");
            return;
        }

        try {
            BigDecimal price = new BigDecimal(priceText);

            Movies movie = new Movies();
            movie.setTitle(title);
            movie.setGrade(grade);
            movie.setPrice(price);
            movie.setViewCount(0);
            movie.setAvailable(true);

            boolean success = reserveService.insert(movie);
            if (success) {
                JOptionPane.showMessageDialog(this, "영화가 등록되었습니다.");
                statusLabel.setText("영화 등록 완료: " + title);
                loadMovies();
            } else {
                JOptionPane.showMessageDialog(this, "영화 등록에 실패했습니다.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "가격은 숫자로 입력해주세요.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "영화 등록 중 오류: " + e.getMessage());
        }
    }

    private void showUpdateMovieDialog() {

        if (!(loginUser instanceof Admin)) {
            JOptionPane.showMessageDialog(
                    this,
                    "관리자만 영화 수정이 가능합니다.",
                    "권한 없음",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }


        if (selectedMovie == null) {
            JOptionPane.showMessageDialog(this, "수정할 영화를 먼저 선택해주세요.");
            return;
        }

        JTextField titleField = new JTextField(selectedMovie.getTitle());
        JTextField gradeField = new JTextField(selectedMovie.getGrade());
        JTextField priceField = new JTextField(String.valueOf(selectedMovie.getPrice().intValue()));

        JPanel panel = new JPanel(new GridLayout(0, 1, 8, 8));
        panel.add(new JLabel("영화 제목"));
        panel.add(titleField);
        panel.add(new JLabel("영화 등급"));
        panel.add(gradeField);
        panel.add(new JLabel("영화 가격"));
        panel.add(priceField);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "영화 수정",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String title = titleField.getText().trim();
        String grade = gradeField.getText().trim();
        String priceText = priceField.getText().trim();

        if (title.isEmpty() || grade.isEmpty() || priceText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "모든 항목을 입력해주세요.");
            return;
        }

        try {
            selectedMovie.setTitle(title);
            selectedMovie.setGrade(grade);
            selectedMovie.setPrice(new BigDecimal(priceText));

            reserveService.movieUpdate(selectedMovie);

            JOptionPane.showMessageDialog(this, "영화가 수정되었습니다.");
            statusLabel.setText("영화 수정 완료: " + selectedMovie.getTitle());
            loadMovies();
            updateSelectedMoviePreview();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "가격은 숫자로 입력해주세요.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "영화 수정 중 오류: " + e.getMessage());
        }
    }

    private void deleteSelectedMovie() {

        if (!(loginUser instanceof Admin)) {
            JOptionPane.showMessageDialog(
                    this,
                    "관리자만 영화 삭제가 가능합니다.",
                    "권한 없음",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (selectedMovie == null) {
            JOptionPane.showMessageDialog(this, "삭제할 영화를 먼저 선택해주세요.");
            return;
        }

        int result = JOptionPane.showConfirmDialog(
                this,
                selectedMovie.getTitle() + " 영화를 삭제하시겠습니까?",
                "영화 삭제",
                JOptionPane.YES_NO_OPTION
        );

        if (result != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            reserveService.movieDelete(selectedMovie);

            JOptionPane.showMessageDialog(this, "영화가 삭제되었습니다.");
            statusLabel.setText("영화 삭제 완료: " + selectedMovie.getTitle());

            selectedMovie = null;
            selectedRoom = null;
            clearSeatSelection();
            resetMoviePreview();
            loadMovies();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "영화 삭제 중 오류: " + e.getMessage());
        }
    }

    private boolean ensureLoggedIn() {
        if (loginUser != null) {
            return true;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "로그인이 필요합니다. 지금 로그인하시겠습니까?",
                "로그인 필요",
                JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {
            showLoginDialog();
        }
        return loginUser != null;
    }

    private void loadMovies() {
        movieListPanel.removeAll();

        List<Movies> movies = new ArrayList<>();
        try {
            List<Movies> serviceMovies = reserveService.allMovieList();
            if (serviceMovies != null) {
                movies = serviceMovies;
            }
            statusLabel.setText("영화 목록을 불러왔습니다.");
        } catch (Exception e) {
            movies = createDummyMovies();
            statusLabel.setText("DB 연결 실패 - 더미 영화 데이터 표시 중");
        }

        movies.sort(Comparator.comparing(Movies::isAvailable).reversed());

        if (movies.isEmpty()) {
            JLabel empty = new JLabel("등록된 영화가 없습니다.");
            empty.setForeground(Color.WHITE);
            movieListPanel.add(empty);
        } else {
            for (Movies movie : movies) {
                movieListPanel.add(createMovieCard(movie));
                movieListPanel.add(Box.createVerticalStrut(12));
            }
        }

        movieListPanel.revalidate();
        movieListPanel.repaint();
    }

    private JPanel createMovieCard(Movies movie) {
        JPanel card = new JPanel(new BorderLayout(14, 0));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        card.setPreferredSize(new Dimension(420, 150));
        card.setBackground(new Color(36, 36, 45));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(70, 70, 86), 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.putClientProperty("movieId", movie.getId());

        JLabel posterLabel = new JLabel();
        posterLabel.setBounds(0, 0, 90, 126);
        posterLabel.setHorizontalAlignment(SwingConstants.CENTER);

        ImageIcon icon = loadPosterByMovieId(movie.getId(), 90, 126);
        if (icon != null) {
            posterLabel.setIcon(icon);
        } else {
            posterLabel.setOpaque(true);
            posterLabel.setBackground(new Color(50, 50, 60));
            posterLabel.setForeground(Color.WHITE);
            posterLabel.setText("IMAGE 없음");
        }

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(90, 126));
        layeredPane.setMinimumSize(new Dimension(90, 126));
        layeredPane.setMaximumSize(new Dimension(90, 126));
        layeredPane.add(posterLabel, Integer.valueOf(0));

        if (!movie.isAvailable()) {
            JPanel overlay = new JPanel(new GridBagLayout());
            overlay.setBounds(0, 0, 90, 126);
            overlay.setBackground(new Color(0, 0, 0, 120));
            overlay.setOpaque(true);

            JLabel overlayText = new JLabel("예매 마감");
            overlayText.setForeground(Color.WHITE);
            overlayText.setFont(new Font("맑은 고딕", Font.BOLD, 13));
            overlay.add(overlayText);

            layeredPane.add(overlay, Integer.valueOf(1));
        }

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titleRow.setOpaque(false);
        titleRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        titleRow.setPreferredSize(new Dimension(0, 30));
        titleRow.setMinimumSize(new Dimension(0, 30));
        titleRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel(movie.getTitle());
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        titleRow.add(titleLabel);

        if ("청불".equals(movie.getGrade())) {
            titleRow.add(Box.createHorizontalStrut(8));
            JLabel adultMark = new JLabel("🔞");
            adultMark.setForeground(new Color(255, 70, 70));
            adultMark.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            titleRow.add(adultMark);
        }

        JLabel gradeLabel = new JLabel("등급: " + movie.getGrade());
        gradeLabel.setForeground(new Color(200, 200, 210));
        gradeLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        gradeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel priceLabel = new JLabel("가격: " + movie.getPrice().intValue() + "원");
        priceLabel.setForeground(new Color(255, 210, 120));
        priceLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel viewLabel = new JLabel("누적 관객 수: " + movie.getViewCount());
        viewLabel.setForeground(new Color(170, 170, 180));
        viewLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        viewLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel availableLabel = new JLabel(movie.isAvailable() ? "예매 가능" : "예매 마감");
        availableLabel.setForeground(movie.isAvailable() ? new Color(120, 220, 150) : new Color(240, 110, 110));
        availableLabel.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        availableLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        info.add(titleRow);
        info.add(Box.createVerticalStrut(8));
        info.add(gradeLabel);
        info.add(Box.createVerticalStrut(4));
        info.add(priceLabel);
        info.add(Box.createVerticalStrut(4));
        info.add(viewLabel);
        info.add(Box.createVerticalStrut(8));
        info.add(availableLabel);

        MouseAdapter cardClick = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (movie.isAvailable()) {
                    selectMovie(movie);
                } else {
                    JOptionPane.showMessageDialog(MovieReserveSwingApp.this, "예매가 마감된 영화입니다.");
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (!isSelectedMovieCard(movie.getId())) {
                    card.setBackground(new Color(46, 46, 58));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!isSelectedMovieCard(movie.getId())) {
                    card.setBackground(new Color(36, 36, 45));
                }
            }
        };

        card.addMouseListener(cardClick);
        layeredPane.addMouseListener(cardClick);
        posterLabel.addMouseListener(cardClick);
        info.addMouseListener(cardClick);
        titleLabel.addMouseListener(cardClick);
        gradeLabel.addMouseListener(cardClick);
        priceLabel.addMouseListener(cardClick);
        viewLabel.addMouseListener(cardClick);
        availableLabel.addMouseListener(cardClick);

        card.add(layeredPane, BorderLayout.WEST);
        card.add(info, BorderLayout.CENTER);

        return card;
    }

    private void selectMovie(Movies movie) {
        this.selectedMovie = movie;

        try {
            this.selectedRoom = reserveService.findRoomByMovie(movie);

            if (this.selectedRoom == null) {
                JOptionPane.showMessageDialog(this, "해당 영화의 상영관 정보를 찾을 수 없습니다.");
                statusLabel.setText("상영관 조회 실패");
                return;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "상영관 정보를 불러오지 못했습니다: " + e.getMessage());
            statusLabel.setText("상영관 조회 실패");
            return;
        }

        System.out.println("[DEBUG] selectedMovie = " + selectedMovie);
        System.out.println("[DEBUG] selectedRoom  = " + selectedRoom);

        clearSeatSelection();
        updateSelectedMoviePreview();
        highlightSelectedMovieCard(movie.getId());
        refreshSeatGridState();
        refreshBookingInfo();

        statusLabel.setText("영화 선택 완료: " + movie.getTitle());
    }

    private void updateSelectedMoviePreview() {
        if (selectedMovie == null) {
            resetMoviePreview();
            return;
        }

        movieTitleLabel.setText("현재 선택중인 영화");
        movieMetaLabel.setText(String.format(
                "%s  |  등급: %s  |  가격: %d원  |  관객 수: %d",
                selectedMovie.getTitle(),
                selectedMovie.getGrade(),
                selectedMovie.getPrice().intValue(),
                selectedMovie.getViewCount()
        ));

        ImageIcon poster = loadPosterByMovieId(selectedMovie.getId(), 180, 220);
        if (poster != null) {
            posterPreviewLabel.setText(null);
            posterPreviewLabel.setIcon(poster);
        } else {
            posterPreviewLabel.setIcon(null);
            posterPreviewLabel.setText("IMAGE 없음");
        }

        refreshBookingInfo();
    }

    private void resetMoviePreview() {
        posterPreviewLabel.setIcon(null);
        posterPreviewLabel.setText("POSTER");
        movieTitleLabel.setText("현재 선택중인 영화");
        movieMetaLabel.setText(" ");
        refreshBookingInfo();
    }

    private void refreshSeatGridState() {

        if (selectedRoom == null) {
            for (SeatButton btn : seatButtons) {
                btn.setSelectedSeat(false);
                btn.setAvailableSeat(false);
            }
            seatGridPanel.revalidate();
            seatGridPanel.repaint();
            return;
        }

        try {
            // ⭐ 모든 좌석 조회
            List<Seat> seatList = reserveService.findAllSeatsByRoom(selectedRoom);

            // ⭐ seatNumber → isAvailable 매핑
            Map<Integer, Boolean> seatMap = new HashMap<>();
            for (Seat seat : seatList) {
                seatMap.put(seat.getSeatNumber(), seat.isAvailable());
            }

            // ⭐ 버튼 상태 반영
            for (SeatButton btn : seatButtons) {
                btn.setSelectedSeat(false);

                boolean isAvailable = seatMap.getOrDefault(btn.getSeatNumber(), true);

                if (isAvailable) {
                    btn.setAvailableSeat(true);   // 예약 가능
                } else {
                    btn.setAvailableSeat(false);  // 예약됨
                }
            }

            seatGridPanel.revalidate();
            seatGridPanel.repaint();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "좌석 정보를 불러오지 못했습니다.");
        }
    }

    private void toggleSeat(SeatButton btn) {
        if (!ensureLoggedIn()) {
            return;
        }

        if (selectedMovie == null) {
            JOptionPane.showMessageDialog(this, "먼저 영화를 선택해주세요.");
            return;
        }

        if (!btn.isAvailableSeat()) {
            JOptionPane.showMessageDialog(this, "이미 예약된 좌석입니다.");
            return;
        }

        int seatNumber = btn.getSeatNumber();
        if (selectedSeatNumbers.contains(seatNumber)) {
            selectedSeatNumbers.remove(seatNumber);
            btn.setSelectedSeat(false);
        } else {
            selectedSeatNumbers.add(seatNumber);
            btn.setSelectedSeat(true);
        }

        refreshSelectedSeatLabel();
        refreshBookingInfo();
    }

    private void clearSeatSelection() {
        selectedSeatNumbers.clear();
        for (SeatButton btn : seatButtons) {
            btn.setSelectedSeat(false);
        }
        refreshSelectedSeatLabel();
        refreshBookingInfo();
    }

    private void refreshSelectedSeatLabel() {
        if (selectedSeatNumbers.isEmpty()) {
            selectedSeatLabel.setText("선택 좌석: 없음");
            return;
        }

        List<String> labels = new ArrayList<>();
        for (SeatButton btn : seatButtons) {
            if (selectedSeatNumbers.contains(btn.getSeatNumber())) {
                labels.add(btn.getSeatLabel());
            }
        }
        selectedSeatLabel.setText("선택 좌석: " + String.join(", ", labels));
    }

    public void refreshBookingInfo() {
        bookingInfoModel.clear();
        bookingInfoModel.addElement("로그인 사용자: " + (loginUser == null ? "비로그인" : loginUser.getName()));
        bookingInfoModel.addElement("영화: " + (selectedMovie == null ? "선택 안됨" : selectedMovie.getTitle()));
        bookingInfoModel.addElement("상영관: " + (selectedRoom == null ? "없음" : selectedRoom.getRoomNumber() + "관"));

        if (selectedSeatNumbers.isEmpty()) {
            bookingInfoModel.addElement("좌석: 없음");
        } else {
            List<String> labels = new ArrayList<>();
            for (SeatButton btn : seatButtons) {
                if (selectedSeatNumbers.contains(btn.getSeatNumber())) {
                    labels.add(btn.getSeatLabel());
                }
            }
            bookingInfoModel.addElement("좌석: " + String.join(", ", labels));
        }

        int total = 0;
        if (selectedMovie != null) {
            total = selectedMovie.getPrice().intValue() * selectedSeatNumbers.size();
        }
        bookingInfoModel.addElement("총 금액: " + total + "원");
    }

    private void reserveSelectedSeats() {
        System.out.println("=== reserveSelectedSeats() 시작 ===");

        if (!ensureLoggedIn()) {
            System.out.println("[DEBUG] ensureLoggedIn() 실패");
            return;
        }

        if (selectedMovie == null) {
            System.out.println("[DEBUG] selectedMovie == null");
            JOptionPane.showMessageDialog(this, "영화를 먼저 선택해주세요.");
            return;
        }

        if (selectedRoom == null) {
            System.out.println("[DEBUG] selectedRoom == null");
            JOptionPane.showMessageDialog(this, "상영관 정보가 없습니다.");
            return;
        }

        if (selectedSeatNumbers.isEmpty()) {
            System.out.println("[DEBUG] selectedSeatNumbers.isEmpty() == true");
            JOptionPane.showMessageDialog(this, "좌석을 한 개 이상 선택해주세요.");
            return;
        }

        if (loginUser == null) {
            System.out.println("[DEBUG] loginCustomer == null");
            JOptionPane.showMessageDialog(this, "로그인 후 이용 가능합니다.");
            return;
        }

        System.out.println("[DEBUG] 로그인 사용자: " + loginUser.getName());
        System.out.println("[DEBUG] 선택 영화: " + selectedMovie.getTitle());
        System.out.println("[DEBUG] 선택 상영관: " + selectedRoom.getRoomNumber());
        System.out.println("[DEBUG] 선택 좌석 번호들: " + selectedSeatNumbers);

        int successCount = 0;
        List<Integer> selectedSeatCopy = new ArrayList<>(selectedSeatNumbers);

        for (Integer seatNumber : selectedSeatCopy) {
            System.out.println("\n--- 좌석 예약 시도 ---");
            System.out.println("[DEBUG] seatNumber = " + seatNumber);

            try {
                Seat seat = Seat.builder()
                        .seatNumber(seatNumber)
                        .roomId(selectedRoom.getId())
                        .isAvailable(true)
                        .build();

                System.out.println("[DEBUG] 생성된 seat 객체 = " + seat);
                System.out.println("[DEBUG] reserveService.reserve() 호출 직전");
                System.out.println("        customer = " + loginUser);
                System.out.println("        movie    = " + selectedMovie);
                System.out.println("        room     = " + selectedRoom);
                System.out.println("        seatNo   = " + seatNumber);

                boolean result = reserveService.reserve(
                        seat,
                        (Customer) loginUser,
                        selectedMovie,
                        selectedRoom,
                        seatNumber
                );

                System.out.println("[DEBUG] reserve 결과 = " + result);

                if (result) {
                    successCount++;
                    System.out.println("[DEBUG] successCount 증가 -> " + successCount);
                } else {
                    System.out.println("[DEBUG] 예약 실패 반환(false) - seatNumber: " + seatNumber);
                }

            } catch (SQLException ex) {
                System.out.println("[ERROR] SQLException 발생");
                ex.printStackTrace();
                statusLabel.setText("DB 예매 실패: " + ex.getMessage());
            } catch (Exception ex) {
                System.out.println("[ERROR] Exception 발생");
                ex.printStackTrace();
                statusLabel.setText("DTO/DAO 연동 확인 필요");
            }
        }

        System.out.println("\n=== 반복문 종료 ===");
        System.out.println("[DEBUG] 최종 successCount = " + successCount);

        if (successCount > 0) {
            clearSeatSelection();
            refreshSeatGridState();
            refreshBookingInfo();

            JOptionPane.showMessageDialog(this, successCount + "개 좌석 예매가 완료되었습니다.");
            statusLabel.setText("예매 완료");
            System.out.println("[DEBUG] 예매 완료 처리");
        } else {
            JOptionPane.showMessageDialog(this, "예매에 실패했습니다.");
            statusLabel.setText("예매 실패");
            System.out.println("[DEBUG] successCount <= 0 이므로 예매 실패 메시지 출력");
        }

        System.out.println("=== reserveSelectedSeats() 종료 ===");
    }

    private ImageIcon loadPosterByMovieId(int movieId, int width, int height) {
        String path = "img/Post_" + String.format("%02d", movieId) + ".png";
        File file = new File(path);

        if (!file.exists()) {
            return null;
        }

        try {
            BufferedImage image = ImageIO.read(file);
            Image scaled = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (IOException e) {
            return null;
        }
    }

    private List<Movies> createDummyMovies() {
        List<Movies> list = new ArrayList<>();

        list.add(Movies.builder()
                .id(1)
                .title("스파이더맨")
                .grade("12세")
                .price(new BigDecimal("12500"))
                .viewCount(0)
                .isAvailable(true)
                .build());

        list.add(Movies.builder()
                .id(2)
                .title("조커")
                .grade("청불")
                .price(new BigDecimal("13500"))
                .viewCount(0)
                .isAvailable(true)
                .build());

        list.add(Movies.builder()
                .id(3)
                .title("라라랜드")
                .grade("12세")
                .price(new BigDecimal("11500"))
                .viewCount(0)
                .isAvailable(false)
                .build());

        list.add(Movies.builder()
                .id(4)
                .title("기생충")
                .grade("15세")
                .price(new BigDecimal("11000"))
                .viewCount(0)
                .isAvailable(false)
                .build());

        return list;
    }

    private boolean isSelectedMovieCard(int movieId) {
        return selectedMovie != null && selectedMovie.getId() == movieId;
    }

    private void highlightSelectedMovieCard(int selectedMovieId) {
        for (Component comp : movieListPanel.getComponents()) {
            if (comp instanceof JPanel panel) {
                Object value = panel.getClientProperty("movieId");
                if (value instanceof Integer movieId) {
                    boolean selected = movieId == selectedMovieId;
                    panel.setBackground(selected ? new Color(52, 60, 92) : new Color(36, 36, 45));
                    panel.setBorder(BorderFactory.createCompoundBorder(
                            new LineBorder(selected ? new Color(76, 110, 245) : new Color(70, 70, 86), selected ? 2 : 1, true),
                            new EmptyBorder(10, 10, 10, 10)
                    ));
                }
            }
        }
        movieListPanel.repaint();
    }

    static class SeatButton extends JButton {
        private final int seatNumber;
        private final String seatLabel;

        private boolean availableSeat = true;
        private boolean selectedSeat = false;

        private static ImageIcon seatAvailableIcon;
        private static ImageIcon seatReservedIcon;

        static {
            seatAvailableIcon = loadSeatIcon("img/Seat_01.png", 70, 48);
            seatReservedIcon = loadSeatIcon("img/Seat_02.png", 70, 48);
        }

        public SeatButton(int seatNumber, String seatLabel) {
            super(seatLabel);
            this.seatNumber = seatNumber;
            this.seatLabel = seatLabel;

            setHorizontalAlignment(SwingConstants.CENTER);
            setVerticalAlignment(SwingConstants.CENTER);
            setHorizontalTextPosition(SwingConstants.CENTER);
            setVerticalTextPosition(SwingConstants.CENTER);

            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setMargin(new Insets(0, 0, 0, 0));

            setForeground(Color.BLACK);
            setFont(new Font("맑은 고딕", Font.BOLD, 12));

            updateStyle();
        }

        private static ImageIcon loadSeatIcon(String path, int width, int height) {
            ImageIcon icon = new ImageIcon(path);
            Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        }

        public int getSeatNumber() {
            return seatNumber;
        }

        public String getSeatLabel() {
            return seatLabel;
        }

        public boolean isAvailableSeat() {
            return availableSeat;
        }

        public void setAvailableSeat(boolean availableSeat) {
            this.availableSeat = availableSeat;
            if (!availableSeat) {
                selectedSeat = false;
            }
            updateStyle();
        }

        public void setSelectedSeat(boolean selectedSeat) {
            if (availableSeat) {
                this.selectedSeat = selectedSeat;
            }
            updateStyle();
        }

        private void updateStyle() {
            if (!availableSeat) {
                setIcon(seatReservedIcon);
                setForeground(Color.WHITE);
            } else if (selectedSeat) {
                setIcon(seatAvailableIcon);
                setForeground(new Color(255, 80, 80));
            } else {
                setIcon(seatAvailableIcon);
                setForeground(Color.BLACK);
            }

            setText(seatLabel);
        }
    }
}
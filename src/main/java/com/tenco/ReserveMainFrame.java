package com.tenco;

import com.tenco.dto.Customer;
import com.tenco.dto.Movies;
import com.tenco.service.ReserveService;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class ReserveMainFrame extends JFrame {

    private final ReserveService reserveService = new ReserveService();
    private Customer loginCustomer;

    private JTextArea outputArea;
    private JLabel loginStatusLabel;

    public ReserveMainFrame() {
        initData();
        initUI();
        initLayout();
        initListener();
    }

    private void initData() {
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("맑은 고딕", Font.PLAIN, 14));

        loginStatusLabel = new JLabel("현재 상태: 비로그인");
    }

    private void initUI() {
        setTitle("영화 예매 시스템");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void initLayout() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(loginStatusLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 4, 10, 10));

        JButton signUpButton = new JButton("회원가입");
        JButton loginButton = new JButton("로그인");
        JButton logoutButton = new JButton("로그아웃");
        JButton allMovieButton = new JButton("전체 영화조회");
        JButton searchMovieButton = new JButton("영화검색");
        JButton insertMovieButton = new JButton("영화등록");
        JButton updateMovieButton = new JButton("영화수정");
        JButton deleteMovieButton = new JButton("영화삭제");

        buttonPanel.add(signUpButton);
        buttonPanel.add(loginButton);
        buttonPanel.add(logoutButton);
        buttonPanel.add(allMovieButton);
        buttonPanel.add(searchMovieButton);
        buttonPanel.add(insertMovieButton);
        buttonPanel.add(updateMovieButton);
        buttonPanel.add(deleteMovieButton);

        JScrollPane scrollPane = new JScrollPane(outputArea);

        add(topPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);
        scrollPane.setPreferredSize(new Dimension(850, 350));

        signUpButton.addActionListener(e -> signUp());
        loginButton.addActionListener(e -> login());
        logoutButton.addActionListener(e -> logout());
        allMovieButton.addActionListener(e -> allMovieList());
        searchMovieButton.addActionListener(e -> findMovie());
        insertMovieButton.addActionListener(e -> insertMovie());
        updateMovieButton.addActionListener(e -> updateMovie());
        deleteMovieButton.addActionListener(e -> deleteMovie());
    }

    private void initListener() {
    }

    private void signUp() {
        try {
            String email = JOptionPane.showInputDialog(this, "이메일 입력");
            if (email == null || email.trim().isEmpty()) return;

            String password = JOptionPane.showInputDialog(this, "비밀번호 입력");
            if (password == null || password.trim().isEmpty()) return;

            String name = JOptionPane.showInputDialog(this, "이름 입력");
            if (name == null || name.trim().isEmpty()) return;

            String ageStr = JOptionPane.showInputDialog(this, "나이 입력");
            if (ageStr == null || ageStr.trim().isEmpty()) return;

            int age;
            try {
                age = Integer.parseInt(ageStr);
            } catch (NumberFormatException e) {
                showMessage("나이는 숫자로 입력해주세요.");
                return;
            }

            Customer customer = Customer.builder()
                    .email(email)
                    .password(password)
                    .name(name)
                    .age(age)
                    .build();

            Customer result = reserveService.signUp(customer);

            if (result != null) {
                showMessage("회원가입 성공");
                appendText("회원가입 완료: " + result.getEmail());
            } else {
                showMessage("회원가입 실패");
            }

        } catch (SQLException e) {
            showMessage("회원가입 중 오류: " + e.getMessage());
        }
    }

    private void login() {
        try {
            String email = JOptionPane.showInputDialog(this, "이메일 입력");
            if (email == null || email.trim().isEmpty()) return;

            String password = JOptionPane.showInputDialog(this, "비밀번호 입력");
            if (password == null || password.trim().isEmpty()) return;

            Customer customer = reserveService.login(email, password);

            if (customer != null) {
                loginCustomer = customer;
                loginStatusLabel.setText("현재 상태: 로그인됨 / " + customer.getName());
                showMessage("로그인 성공");
                appendText("로그인: " + customer.getEmail());
            } else {
                showMessage("로그인 실패");
            }

        } catch (SQLException e) {
            showMessage("로그인 중 오류: " + e.getMessage());
        }
    }

    private void logout() {
        reserveService.logout();
        loginCustomer = null;
        loginStatusLabel.setText("현재 상태: 비로그인");
        showMessage("로그아웃 완료");
        appendText("로그아웃");
    }

    private void allMovieList() {
        try {
            List<Movies> moviesList = reserveService.allmovieList();
            outputArea.setText("");

            if (moviesList == null || moviesList.isEmpty()) {
                appendText("등록된 영화가 없습니다.");
                return;
            }

            appendText("=== 전체 영화 목록 ===");
            int i = 0;
            for (Movies movie : moviesList) {
                System.out.println(i++);
                appendText(formatMovie(movie));
            }
            System.out.println("완료");

        } catch (SQLException e) {
            showMessage("전체 영화 조회 중 오류: " + e.getMessage());
        }
    }

    private void findMovie() {
        try {
            String title = JOptionPane.showInputDialog(this, "검색할 영화 제목 입력");
            if (title == null || title.trim().isEmpty()) return;

            List<Movies> moviesList = reserveService.findByMovieTitle(title);
            outputArea.setText("");

            if (moviesList == null || moviesList.isEmpty()) {
                appendText("검색 결과가 없습니다.");
                return;
            }

            appendText("=== 검색 결과 ===");
            for (Movies movie : moviesList) {
                appendText(formatMovie(movie));
            }

        } catch (SQLException e) {
            showMessage("영화 검색 중 오류: " + e.getMessage());
        }
    }

    private void insertMovie() {
        try {
            String title = JOptionPane.showInputDialog(this, "영화 제목");
            if (title == null || title.trim().isEmpty()) return;

            String grade = JOptionPane.showInputDialog(this, "관람 등급");
            if (grade == null || grade.trim().isEmpty()) return;

            String priceStr = JOptionPane.showInputDialog(this, "가격");
            if (priceStr == null || priceStr.trim().isEmpty()) return;

            BigDecimal price;
            try {
                price = new BigDecimal(priceStr);
            } catch (NumberFormatException e) {
                showMessage("가격은 숫자로 입력해주세요.");
                return;
            }

            Movies movie = Movies.builder()
                    .title(title)
                    .grade(grade)
                    .price(price)
                    .viewCount(0)
                    .build();

            boolean result = reserveService.insert(movie);

            if (result) {
                showMessage("영화 등록 성공");
                appendText("등록 완료: " + title);
            } else {
                showMessage("영화 등록 실패");
            }

        } catch (SQLException e) {
            showMessage("영화 등록 중 오류: " + e.getMessage());
        }
    }

    private void updateMovie() {
        try {
            String idStr = JOptionPane.showInputDialog(this, "수정할 영화 ID");
            if (idStr == null || idStr.trim().isEmpty()) return;

            int id;
            try {
                id = Integer.parseInt(idStr);
            } catch (NumberFormatException e) {
                showMessage("ID는 숫자로 입력해주세요.");
                return;
            }

            String title = JOptionPane.showInputDialog(this, "변경할 영화 제목");
            if (title == null || title.trim().isEmpty()) return;

            String grade = JOptionPane.showInputDialog(this, "변경할 관람 등급");
            if (grade == null || grade.trim().isEmpty()) return;

            String priceStr = JOptionPane.showInputDialog(this, "변경할 가격");
            if (priceStr == null || priceStr.trim().isEmpty()) return;

            BigDecimal price;
            try {
                price = new BigDecimal(priceStr);
            } catch (NumberFormatException e) {
                showMessage("가격은 숫자로 입력해주세요.");
                return;
            }

            Movies movie = Movies.builder()
                    .id(id)
                    .title(title)
                    .grade(grade)
                    .price(price)
                    .build();

            reserveService.movieUpdate(movie);
            showMessage("영화 수정 완료");
            appendText("수정 완료: " + title);

        } catch (SQLException e) {
            showMessage("영화 수정 중 오류: " + e.getMessage());
        }
    }

    private void deleteMovie() {
        try {
            String idStr = JOptionPane.showInputDialog(this, "삭제할 영화 ID");
            if (idStr == null || idStr.trim().isEmpty()) return;

            int id;
            try {
                id = Integer.parseInt(idStr);
            } catch (NumberFormatException e) {
                showMessage("ID는 숫자로 입력해주세요.");
                return;
            }

            String title = JOptionPane.showInputDialog(this, "삭제할 영화 제목");
            if (title == null || title.trim().isEmpty()) return;

            Movies movie = Movies.builder()
                    .id(id)
                    .title(title)
                    .build();

            reserveService.movieDelete(movie);
            showMessage("영화 삭제 요청 완료");
            appendText("삭제 처리: " + title);

        } catch (SQLException e) {
            showMessage("영화 삭제 중 오류: " + e.getMessage());
        }
    }

    private String formatMovie(Movies movie) {
        return String.format(
                "ID: %d | 제목: %s | 등급: %s | 가격: %s | 조회수: %d",
                movie.getId(),
                movie.getTitle(),
                movie.getGrade(),
                movie.getPrice(),
                movie.getViewCount()
        );
    }

    private void appendText(String message) {
        outputArea.append(message + "\n");
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ReserveMainFrame().setVisible(true));
    }
}